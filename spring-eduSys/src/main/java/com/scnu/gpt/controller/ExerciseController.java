package com.scnu.gpt.controller;

import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.exercise.ExerciseQuestionDTO;
import com.scnu.gpt.service.IExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 练习题管理 Controller
 */
@RestController
@CrossOrigin
@Tag(name = "练习题管理", description = "课程练习题的增删改查")
@RequestMapping("/exercise")
public class ExerciseController {

    @Autowired
    private IExerciseService exerciseService;

    @Operation(
            summary = "根据课程ID和题型查询题目列表",
            description = "查询某个课程下的所有题目，可按题型过滤"
    )
    @PostMapping("/queryByCourse")
    public ApiResponse<List<ExerciseQuestionDTO>> queryByCourseAndType(
            @RequestParam("courseId") Integer courseId,
            @RequestParam(value = "questionType", required = false) String questionType) {
        try {
            return exerciseService.queryByCourseAndType(courseId, questionType);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ApiResponse<>("500", "未知错误: " + e.getMessage(), null);
        }
    }

    @Operation(
            summary = "新增单个练习题",
            description = "新增一道练习题，包含题目信息和选项列表"
    )
    @PostMapping("/add")
    public ApiResponse<Void> addExerciseQuestion(@RequestBody ExerciseQuestionDTO exerciseQuestionDTO) {
        try {
            return exerciseService.addExerciseQuestion(exerciseQuestionDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ApiResponse<>("500", "未知错误: " + e.getMessage(), null);
        }
    }

    @Operation(
            summary = "批量新增练习题",
            description = "一次新增多道练习题，传入题目数组"
    )
    @PostMapping("/addBatch")
    public ApiResponse<Void> addExerciseQuestions(@RequestBody List<ExerciseQuestionDTO> exerciseQuestionDTOList) {
        try {
            return exerciseService.addExerciseQuestions(exerciseQuestionDTOList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ApiResponse<>("500", "未知错误: " + e.getMessage(), null);
        }
    }
}