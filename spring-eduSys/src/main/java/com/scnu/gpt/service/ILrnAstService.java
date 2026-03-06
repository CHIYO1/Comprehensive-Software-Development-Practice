package com.scnu.gpt.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface ILrnAstService {
    SseEmitter chat(String query,int userId);
    Map<String,Object> getAllConv(int userId);

    Map<String,Object> invertActivate(int convId);

    Map<String, Object> deleteConvs(int userId);

    Map<String, Object> setConvsActivate(int userId, int activateState);
}
