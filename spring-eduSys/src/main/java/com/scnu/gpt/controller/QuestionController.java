package com.scnu.gpt.controller;

import com.scnu.gpt.entity.Question;
import com.scnu.gpt.entity.QuestionSet;
import com.scnu.gpt.entity.RecordQuestion;
import com.scnu.gpt.entity.RecordSet;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.question.QuestionDTO;
import com.scnu.gpt.pojo.question.QuestionSetDTO;
import com.scnu.gpt.service.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ldw
 * @since 2025-06-12
 */
@RestController
@CrossOrigin
@Tag(name = "试题管理", description = "试题集、试题的增删改查......") // API分组
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    IQuestionService questionService;

    @Operation(
            summary = "新增一个从属当前登录教师的空试题集,返回该试题集的id",
            description = "如题") // 接口注解
    @PostMapping("/addSet")
    public ApiResponse<String> addSet() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ApiResponse<>("200","成功",questionService.addSet(Integer.parseInt(userId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "查询当前登录教师名下的所有试题集（不包括题目本身）",
            description = "如题") // 接口注解
    @PostMapping("/querySets")
    public ApiResponse<ArrayList<QuestionSetDTO>> querySets() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ApiResponse<>("200","成功",questionService.querySets(Integer.parseInt(userId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "根据setId查询单个试题集（不包括题目本身）",
            description = "如题") // 接口注解
    @PostMapping("/querySet")
    public ApiResponse<QuestionSet> querySet(@RequestBody String setId) {
        try {
            return new ApiResponse<>("200","成功",questionService.querySet(Integer.parseInt(setId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "更新试题集",
            description = "如题") // 接口注解
    @PostMapping("/updateSet")
    public ApiResponse<Void> updateSet(@RequestBody QuestionSet questionSet) {
        try {
            return questionService.updateSet(questionSet);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "删除试题集",
            description = "如题") // 接口注解
    @PostMapping("/deleteSet")
    public ApiResponse<Void> deleteSet(@RequestBody String setId) {
        try {
            return questionService.deleteSet(Integer.parseInt(setId));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }
    @Operation(
            summary = "根据setId查询试题集下的所有题目（不包括试题集本身）",
            description = "如题") // 接口注解
    @PostMapping("/queryQuestions")
    public ApiResponse<ArrayList<QuestionDTO>> queryQuestions(@RequestBody String setId) {
        try {
            return new ApiResponse<>("200","成功",questionService.queryQuestions(Integer.parseInt(setId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "新建从属setId试题集的空题目，并返回该空实体",
            description = "如题") // 接口注解
    @PostMapping("/addQuestion")
    public ApiResponse<QuestionDTO> addQuestion(@RequestParam("setId") String setId,
                                   @RequestParam("questionType") String type) {
        try {
            Question question = new Question();
            question.setSetId(Integer.parseInt(setId));
            question.setQuestionType(type);
            return new ApiResponse<>("200","成功",questionService.addQuestion(question));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "删除题目",
            description = "如题") // 接口注解
    @PostMapping("/deleteQuestion")
    public ApiResponse<Void> deleteQuestion(@RequestBody String questionId) {
        try {
            return questionService.deleteQuestion(Integer.parseInt(questionId));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "更新单个试题",
            description = "如题") // 接口注解
    @PostMapping("/updateQuestion")
    public ApiResponse<Void> updateQuestion(@RequestBody QuestionDTO questionDTO) {
        try {
            return questionService.updateQuestion(questionDTO);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }
}
