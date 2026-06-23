package com.scnu.gpt.service;

import com.scnu.gpt.entity.ExerciseQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.exercise.ExerciseQuestionDTO;

import java.util.List;

/**
 * 练习题服务接口
 */
public interface IExerciseService extends IService<ExerciseQuestion> {

    /**
     * 根据课程ID和题型查询题目列表（包含选项）
     */
    ApiResponse<List<ExerciseQuestionDTO>> queryByCourseAndType(Integer courseId, String questionType);

    /**
     * 新增题目（包含选项）
     */
    ApiResponse<Void> addExerciseQuestion(ExerciseQuestionDTO exerciseQuestionDTO);


    /**
     * 批量新增题目（包含选项）
     */
    ApiResponse<Void> addExerciseQuestions(List<ExerciseQuestionDTO> exerciseQuestionDTOList);
}