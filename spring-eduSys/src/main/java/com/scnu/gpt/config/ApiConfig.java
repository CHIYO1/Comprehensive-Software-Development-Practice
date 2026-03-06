package com.scnu.gpt.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api")
public record ApiConfig(NginxConfig nginx, DifyConfig dify) {
    public record DifyConfig(
            String url,
            @JsonProperty("file-url")String fileUrl,
            @JsonProperty("key-question-generate") String keyQuestionGenerate,
            @JsonProperty("key-question-grade") String keyQuestionGrade,
            @JsonProperty("key-document-generate") String keyDocumentGenerate,
            @JsonProperty("key-course-generate") String keyCourseGenerate) {}
    public record NginxConfig(String url) {}
}