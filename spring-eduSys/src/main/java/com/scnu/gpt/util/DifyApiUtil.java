package com.scnu.gpt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnu.gpt.config.ApiConfig;
import com.scnu.gpt.pojo.gpt.DifyBlockResponse;
import com.scnu.gpt.pojo.gpt.DifyRequest;
import com.scnu.gpt.pojo.gpt.DifyStreamResponse;
import com.scnu.gpt.pojo.gpt.DifyTextChunk;
import com.scnu.gpt.service.impl.GptServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Dify访问工具
@Component
public class DifyApiUtil {
    private static final Logger log = LoggerFactory.getLogger(DifyApiUtil.class);
    private final String DIFY_URL;      //
    private final String FILE_URL;
    private final RestTemplate restTemplate;     //阻塞连接
    private final WebClient webClient;          //流式连接
    private final ObjectMapper objectMapper;

    public DifyApiUtil(ApiConfig apiConfig, ObjectMapper objectMapper) {
        this.DIFY_URL = apiConfig.dify().url();
        this.FILE_URL = apiConfig.dify().fileUrl();
        this.restTemplate = new RestTemplate();
        this.webClient = WebClient.builder()
                .baseUrl(this.DIFY_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = objectMapper;
    }

    //通用dify请求方法-阻塞模式
    public <T> DifyBlockResponse getDifyBlockResponse(DifyRequest<T> request,String apiKey) {
        //构造请求头并封装请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);    //自动添加Bear前缀？
        HttpEntity<DifyRequest<T>> httpEntity = new HttpEntity<>(request, headers);
        //访问dify
        ResponseEntity<DifyBlockResponse> response = restTemplate.exchange(
                DIFY_URL,
                HttpMethod.POST,
                httpEntity,
                DifyBlockResponse.class
        );
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        else{
            throw new RuntimeException("Dify'API访问失败:" + response.getStatusCode());
        }
    }
    //通用dify请求方法-流式模式(webFLux)-数据返回格式为json，不是SSE
    public <T> Flux<String> streamDifyWorkflow(DifyRequest<T> request, String apiKey) {
        try {
            System.out.println("Request body: " + objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return webClient.post().header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(sub -> log.info("开始连接流式响应"))
                .doOnError(e -> log.error("流式请求出错", e))
                .doOnComplete(() -> log.info("流式响应接收完成"))
                .flatMap(this::parseEvent)      //解析映射所有数据
                .filter(event -> "text_chunk".equals(event.event()))    //过滤出llm输出数据
                .map(event -> {//映射到有效数据类TextChunk
                    DifyTextChunk textChunk = objectMapper.convertValue(event.data(), DifyTextChunk.class);
                    return textChunk.text();
                });
    }
    private Mono<DifyStreamResponse> parseEvent(String eventStr) {
        try {
            // 直接解析JSON，不再检查"data: "前缀
            DifyStreamResponse response = objectMapper.readValue(eventStr, DifyStreamResponse.class);
            return Mono.just(response);
        } catch (JsonProcessingException e) {
            log.error("解析事件失败: {}", eventStr, e);
            return Mono.error(e);
        }
    }
    //上传文件到dify服务器,返回文件id(调用多模态工作流前必须调用该方法)
    public String uploadFile(MultipartFile file, String apikey) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + apikey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    FILE_URL,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.path("id").asText();
            } else {
                log.error("文件上传失败-dify响应: {}", response.getStatusCode());
                throw new RuntimeException("File upload failed");
            }
        } catch (Exception e) {
            log.error("后端文件uploadFile函数出错：", e);
            throw new RuntimeException("Error uploading file", e);
        }
    }
}
