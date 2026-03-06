package com.scnu.gpt.pojo.gpt;

import com.scnu.gpt.pojo.question.QuestionDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "试题智能生成请求格式")
public record QuestionGenerateRequest(
        @Schema(description = "试题基本属性")
        QuestionDTO questionDTO,
        @Schema(description = "试题生成的其它要求")
        String demand) {
}
