package com.scnu.gpt.pojo.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "选择题及其选项关系类")
@Data
@AllArgsConstructor
public class QuestionChoiceDTO {
    private Integer questionId;
    private String choice;
}
