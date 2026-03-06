package com.scnu.gpt.pojo.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "试题知识点关系类")
@Data
@AllArgsConstructor
public class QuestionKnowledgeDTO {
    private Integer questionId;
    private String knowledge;
}
