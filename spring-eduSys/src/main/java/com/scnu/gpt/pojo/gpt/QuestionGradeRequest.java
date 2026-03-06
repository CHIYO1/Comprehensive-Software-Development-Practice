package com.scnu.gpt.pojo.gpt;

import com.scnu.gpt.pojo.question.QuestionDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "试题智能评卷请求格式")
public record QuestionGradeRequest(
        @Schema(description = "试题题干")
        String questionStem,
        @Schema(description = "正确答案")
        String questionAnswer,
        @Schema(description = "学生回答")
        String studentAnswer,
        @Schema(description = "评分标准")
        String questionParse,
        @Schema(description = "试题类型(只支持主观题、编程题)")
        String questionType,
        @Schema(description = "正确代码输出(编程题有效)")
        String codeOutput,
        @Schema(description = "回答代码输出(编程题有效)")
        String studentOutput) {
}
