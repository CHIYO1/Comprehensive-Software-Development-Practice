package com.scnu.gpt.controller;

import com.scnu.gpt.entity.RecordQuestion;
import com.scnu.gpt.entity.RecordSet;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.question.RecordQuestionDTO;
import com.scnu.gpt.service.IDoQuestionService;
import com.scnu.gpt.service.IUserService;
import com.scnu.gpt.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ldw
 * @since 2025-07-03
 */
@CrossOrigin
@Tag(name = "做题相关", description = "做题记录增删改查、评测答案....") // API分组
@RestController
@RequestMapping("/doQuestion")
public class DoQuestionController {
    private final IDoQuestionService doQuestionService;

    // 构造函数依赖注入
    public DoQuestionController(IDoQuestionService doQuestionService) {
        this.doQuestionService = doQuestionService;
    }

    @Operation(
            summary = "执行python代码文件,返回执行结果",
            description = "如题") // 接口注解
    @PostMapping("/runCode")
    public ApiResponse<String> runCode(@RequestBody String code) {
        try {
            return doQuestionService.runCode(code);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "创建做题记录,返回该记录的Id。所属用户为当前登录角色",
            description = "如题") // 接口注解
    @PostMapping("/addRecordSet")
    public ApiResponse<String> addRecordSet(@RequestBody RecordSet recordSet) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            recordSet.setUserId(Integer.parseInt(userId));

            return new ApiResponse<>("200","成功",doQuestionService.addRecoderSet(recordSet));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "查询做题记录基本信息(不包括题目题干等信息)",
            description = "如题") // 接口注解
    @PostMapping("/queryRecordSet")
    public ApiResponse<RecordSet> queryRecordSet(@RequestBody String setRecordId) {
        try {
            return new ApiResponse<>("200","成功",doQuestionService.queryRecordSet(Integer.parseInt(setRecordId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "查询当前登录用户特定试题小节的做题记录基本信息(不包括题目题干等信息)",
            description = "如题") // 接口注解
    @PostMapping("/queryRecordSetBySubsection")
    public ApiResponse<RecordSet> queryRecordSetBySubsection(@RequestBody String subsectionId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ApiResponse<>("200","成功",doQuestionService.queryRecordSetBySubsection(Integer.parseInt(subsectionId),Integer.parseInt(userId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "查询某做题记录中各个题目的改卷结果（包括题干等信息）",
            description = "如题") // 接口注解
    @PostMapping("/queryRecordQuestions")
    public ApiResponse<ArrayList<RecordQuestionDTO>> queryRecordQuestions(@RequestBody String setRecordId) {
        try {
            return new ApiResponse<>("200","成功",doQuestionService.queryRecordQuestions(Integer.parseInt(setRecordId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

}
