package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnu.gpt.entity.Conv;
import com.scnu.gpt.mapper.ConvMapper;
import com.scnu.gpt.pojo.gpt.FastGptRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.scnu.gpt.config.AppConfig;
import com.scnu.gpt.service.ILrnAstService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class LrnAstServiceImpl implements ILrnAstService {
    //    private final RestTemplate restTemplate;
    private final AppConfig.GptConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final int numRounds = 5;   //对话轮数

    // 通过构造器注入
    public LrnAstServiceImpl(RestTemplate restTemplate,
                             AppConfig.GptConfig fastGptConfig) {
//        this.restTemplate = restTemplate;
        this.config = fastGptConfig;
    }

    @Autowired
    private ConvMapper convMapper;

    // 流式获取结果
    @Override
    public SseEmitter chat(String query, int userId) {
        // 查询某用户可作为上下文（activate_state字段为1）的numRounds轮历史对话（主键增序排列）
        QueryWrapper<Conv> wrapper = new QueryWrapper<>();
        wrapper.eq("activate_state", 1)
                .eq("user_id", userId)
                .orderByDesc("conv_id")
                .last("LIMIT " + numRounds);
        List<Conv> hisConList = convMapper.selectList(wrapper);
        hisConList.sort(Comparator.comparing(Conv::getConvId));

        SseEmitter emitter = new SseEmitter(120_000L); // 2分钟超时
        Thread.startVirtualThread(() -> {
            try {
                Conv conv = new Conv();
                conv.setUserId(userId);
                conv.setConvUser(query);

                // 构建 GPT 请求
                HttpRequest gptRequest = buildGptRequest(FastGptRequest.createRequest(UUID.randomUUID().toString(), hisConList, query));
                // 发送流式请求
                conv.setConvGpt(processStreamResponse(gptRequest, emitter));
                convMapper.insert(conv);
                emitter.send(SseEmitter.event()
                        .data("[DONE]").id(conv.getConvId().toString()));

            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitter.complete();
            }
        });

        return emitter;
    }
    // 临时结构，用于返回指定结构的数据给前端
    public record ConvDTO(String convId, String convUser, String convGpt,int activateState) {}
    @Override
    public Map<String, Object> getAllConv(int userId) {
        QueryWrapper<Conv> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("conv_id","conv_user", "conv_gpt", "activate_state")
                .eq("user_id", userId)
                .orderByAsc("conv_id");

        List<ConvDTO> results = convMapper.selectMaps(queryWrapper)
                .stream()
                .map(map -> new ConvDTO(
                        map.get("conv_id").toString(),
                        (String) map.get("conv_user"),
                        (String) map.get("conv_gpt"),
                        (int) map.get("activate_state")
                )).toList();

        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "success");
        map.put("results", results);
        return map;
    }

    @Override
    public Map<String, Object> invertActivate(int convId) {
        convMapper.update(null,
                new LambdaUpdateWrapper<Conv>()
                        .setSql("activate_state = 1 - activate_state")
                        .eq(Conv::getConvId, convId));

        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "success");
        return map;
    }

    @Override
    public Map<String, Object> deleteConvs(int userId) {
        LambdaQueryWrapper<Conv> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conv::getUserId, userId);
        int deletedRows = convMapper.delete(wrapper);
        
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "删除了 " + deletedRows + " 条记录");
        return map;
    }

    @Override
    public Map<String, Object> setConvsActivate(int userId, int activateState) {
        convMapper.update(null,
                new LambdaUpdateWrapper<Conv>()
                        .setSql("activate_state = " + activateState)
                        .eq(Conv::getUserId, userId));

        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "success");
        return map;
    }

    private HttpRequest buildGptRequest(FastGptRequest request) throws JsonProcessingException {
        System.out.println("建立sse连接：" + request);
        return HttpRequest.newBuilder()
                .uri(URI.create(config.url()))
                .header("Authorization", "Bearer " + config.key())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request)))
                .build();
    }
    //后端与FastGPT的sse连接交互。返回gpt回复内容。
    private String processStreamResponse(HttpRequest gptRequest, SseEmitter emitter) {
        String strResponse = "";
        try (HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)  // 强制使用 HTTP/1.1
                .build()) {

            HttpResponse<InputStream> response = client.send(gptRequest, HttpResponse.BodyHandlers.ofInputStream());

            // 验证状态码
            if (response.statusCode() != 200) {
                throw new IOException("SSE连接错误 " + response.statusCode());
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim(); // 提取 data字段
                        if ("[DONE]".equals(data)) {
                            System.out.println("SSE接收完毕:" + strResponse);
                            break;
                        }
                        strResponse = strResponse + processDataLine(data, emitter);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Stream processing failed:" + e);
            emitter.completeWithError(e);
        }
        return strResponse;
    }
    //后端与前端的SSE连接交互
    private String processDataLine(String json, SseEmitter emitter) {
        try {

            JsonNode node = objectMapper.readTree(json).at("/choices/0/delta/content");

            if (node.isNull()) {
                return "";
            }
            // 解析字段
            String content = node.asText();

            // 允许发送纯空格
            if (StringUtils.hasLength(content)) {
                emitter.send(SseEmitter.event()
                        .data(" " + content));    // 前端eventsource会自动删除首位空格（若存在）
            }
            return content;
        } catch (Exception e) {
            System.out.println("接收字符:" + json + "时，报错: " + e);
        }
        return "";
    }
    // 阻塞模式，等待执行完毕后返回结果
//    @Override
//    public String chat(String query) {
//        FastGptRequest request = FastGptRequest.createSimpleRequest(query);
//        FastGptResponse response = sendRequest(request);
//        System.out.println(response.getChoices().getFirst().getMessage().getContent());
//
//        return response.getChoices().getFirst().getMessage().getContent();
//    }
//
//    private FastGptResponse sendRequest(FastGptRequest request) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + config.key()); // 使用配置的key
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<FastGptRequest> entity = new HttpEntity<>(request, headers);
//        return restTemplate.postForObject(
//                config.url(), // 使用配置的URL
//                entity,
//                FastGptResponse.class
//        );
//    }
}
