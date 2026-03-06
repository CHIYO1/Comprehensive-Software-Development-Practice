package com.scnu.gpt.controller;

import com.scnu.gpt.entity.Conv;
import com.scnu.gpt.service.ILrnAstService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/LrnAst")
public class LrnAstController {
    @Autowired
    ILrnAstService lrnAstService;
    //获取某用户所有对话
    @PostMapping("getAllConv")
    public Map<String,Object> getAllConv(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return lrnAstService.getAllConv(Integer.parseInt(userId));
    }
    //单轮对话
    @GetMapping("/chat")
    public SseEmitter chat(@RequestParam String query,@RequestParam String userId) {
        return lrnAstService.chat(query,Integer.parseInt(userId));
    }
    //逆转某轮对话隐藏状态
    @PostMapping("invertActivate")
    public Map<String,Object> invertActivate(@RequestBody String convId) {
        return lrnAstService.invertActivate(Integer.parseInt(convId));
    }
    //删除某用户所有对话
    @PostMapping("deleteConvs")
    public Map<String,Object> deleteConvs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return lrnAstService.deleteConvs(Integer.parseInt(userId));
    }
    //一键更改某用户的所有对话的隐藏状态
    @PostMapping("setConvsAct")
    public Map<String,Object> setConvsActivate(@RequestBody String actState) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return lrnAstService.setConvsActivate(Integer.parseInt(userId),Integer.parseInt(actState));
    }
}
