package com.scnu.gpt.pojo.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 练习题选项 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseOptionDTO {
    private String optionKey;
    private String optionText;
    private Boolean isCorrect;
}