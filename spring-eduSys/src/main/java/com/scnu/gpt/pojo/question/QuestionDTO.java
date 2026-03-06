package com.scnu.gpt.pojo.question;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "前端接收的单个试题实体的结构(前端也是以该形式发送数据到后端)")
public record QuestionDTO(
        @Schema(description = "试题ID")
        String questionId,
        @Schema(description = "试题题干")
        String questionStem,
        @Schema(description = "试题答案")
        String questionAnswer,
        @Schema(description = "试题解析")
        String questionParse,
        @Schema(description = "代码期望输出")
        String codeOutput,
        @Schema(description = "试题类型")
        String questionType,
        @Schema(description = "试题所属试题集Id")
        String setId,
        @Schema(description = "选择题选项List(仅当试题类型为'选择题'时有效)")
        ArrayList<String> choiceList,
        @Schema(description = "知识点List")
        ArrayList<String> knowledgeList,
        @Schema(description = "是否可编辑")
        Boolean editable
) {
}
