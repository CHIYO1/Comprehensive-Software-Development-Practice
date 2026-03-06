package com.scnu.gpt.pojo.gpt;

import lombok.Data;

import java.util.List;

@Data
public class FastGptResponse {
    private String id;
    private String model;
    private Usage usage;
    private List<Choice> choices;

    // 嵌套结构定义
    @Data
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }

    @Data
    public static class Choice {
        private Message message;
        private String finish_reason;
        private int index;
    }
    @Data
    public static class Message {
        private String content;
        private String role;

    }
}