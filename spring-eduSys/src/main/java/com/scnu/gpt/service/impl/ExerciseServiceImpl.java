package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnu.gpt.entity.ExerciseOption;
import com.scnu.gpt.entity.ExerciseQuestion;
import com.scnu.gpt.mapper.ExerciseOptionMapper;
import com.scnu.gpt.mapper.ExerciseQuestionMapper;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.exercise.ExerciseOptionDTO;
import com.scnu.gpt.pojo.exercise.ExerciseQuestionDTO;
import com.scnu.gpt.service.IExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 练习题服务实现类
 */
@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseQuestionMapper, ExerciseQuestion> implements IExerciseService {

    @Autowired
    private ExerciseQuestionMapper exerciseQuestionMapper;

    @Autowired
    private ExerciseOptionMapper exerciseOptionMapper;

    @Override
    public ApiResponse<List<ExerciseQuestionDTO>> queryByCourseAndType(Integer courseId, String questionType) {
        List<ExerciseQuestion> questions;
        if (questionType != null && !questionType.isEmpty()) {
            questions = exerciseQuestionMapper.selectByCourseAndType(courseId, questionType);
        } else {
            questions = exerciseQuestionMapper.selectByCourse(courseId);
        }

        List<ExerciseQuestionDTO> resultList = new ArrayList<>();
        for (ExerciseQuestion question : questions) {
            // 查询该题目的所有选项
            List<ExerciseOption> options = exerciseOptionMapper.selectByQuestionId(question.getQuestionId());
            List<ExerciseOptionDTO> optionDTOs = new ArrayList<>();
            for (ExerciseOption option : options) {
                optionDTOs.add(new ExerciseOptionDTO(
                        option.getOptionKey(),
                        option.getOptionText(),
                        option.getIsCorrect()
                ));
            }

            ExerciseQuestionDTO dto = new ExerciseQuestionDTO(
                    question.getQuestionId(),
                    question.getCourseId(),
                    question.getChapterId(),
                    question.getQuestionType(),
                    question.getQuestion(),
                    question.getAnswer(),
                    question.getAnalysis(),
                    question.getSourceType(),
                    question.getIsActive(),
                    optionDTOs
            );
            resultList.add(dto);
        }

        return new ApiResponse<>("200", "查询成功", resultList);
    }

    @Override
    @Transactional
    public ApiResponse<Void> addExerciseQuestion(ExerciseQuestionDTO dto) {
        // 插入单条
        insertSingleQuestion(dto);
        return new ApiResponse<>("200", "新增成功", null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> addExerciseQuestions(List<ExerciseQuestionDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ApiResponse<>("400", "题目列表不能为空", null);
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMsg = new StringBuilder();

        for (int i = 0; i < dtoList.size(); i++) {
            try {
                insertSingleQuestion(dtoList.get(i));
                successCount++;
            } catch (Exception e) {
                failCount++;
                errorMsg.append("第").append(i + 1).append("题: ").append(e.getMessage()).append("; ");
            }
        }

        if (failCount == 0) {
            return new ApiResponse<>("200", "批量新增成功，共 " + successCount + " 题", null);
        } else {
            return new ApiResponse<>("200", 
                "批量新增完成，成功 " + successCount + " 题，失败 " + failCount + " 题。错误信息: " + errorMsg.toString(), 
                null);
        }
    }

    /**
     * 插入单道题目的私有方法（供单个和批量复用）
     */
    private void insertSingleQuestion(ExerciseQuestionDTO dto) {
        // 1. 插入题目
        ExerciseQuestion question = new ExerciseQuestion();
        question.setCourseId(dto.getCourseId());
        question.setChapterId(dto.getChapterId());
        question.setQuestionType(dto.getQuestionType());
        question.setQuestion(dto.getQuestion());
        question.setAnswer(dto.getAnswer());
        question.setAnalysis(dto.getAnalysis());
        question.setSourceType(dto.getSourceType() != null ? dto.getSourceType() : "local");
        question.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        question.setCreatedAt(LocalDateTime.now());

        exerciseQuestionMapper.insert(question);
        Integer questionId = question.getQuestionId();

        // 2. 插入选项（如果有）
        if (dto.getOptions() != null && !dto.getOptions().isEmpty()) {
            for (ExerciseOptionDTO optionDTO : dto.getOptions()) {
                ExerciseOption option = new ExerciseOption();
                option.setQuestionId(questionId);
                option.setOptionKey(optionDTO.getOptionKey());
                option.setOptionText(optionDTO.getOptionText());
                option.setIsCorrect(optionDTO.getIsCorrect());
                exerciseOptionMapper.insert(option);
            }
        }
    }
}