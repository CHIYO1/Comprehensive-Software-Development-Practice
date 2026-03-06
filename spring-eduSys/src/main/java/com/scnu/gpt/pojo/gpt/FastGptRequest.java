package com.scnu.gpt.pojo.gpt;

import com.scnu.gpt.entity.Conv;
import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

@Data
public class FastGptRequest {
    private String chatId;
    private boolean stream;
    private boolean detail;
    private List<Message> messages;


    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    // 快速构建方法
    public static FastGptRequest createRequest(String ChartId, List<Conv> hisConList, String query) {
        FastGptRequest request = new FastGptRequest();
        request.setChatId(ChartId);
        request.setStream(true);
        request.setDetail(false);
        List<Message> messages = new java.util.ArrayList<>(hisConList.stream()
                .flatMap(conv -> Stream.of(
                        new Message("user", conv.getConvUser()),  // 第一个 Message
                        new Message("assistant", conv.getConvGpt())  // 第二个 Message
                ))
                .toList());
        messages.add(new Message("user", query));
        request.setMessages(messages);
        return request;
    }
}