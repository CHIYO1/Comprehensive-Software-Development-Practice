package com.scnu.gpt.pojo.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 练习题 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseQuestionDTO {
    private Integer questionId;
    private Integer courseId;
    private Integer chapterId;
    private String questionType;
    private String question;
    private String answer;
    private String analysis;
    private String sourceType;
    private Boolean isActive;
    private List<ExerciseOptionDTO> options;
}