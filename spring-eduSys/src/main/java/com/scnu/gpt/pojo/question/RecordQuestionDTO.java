package com.scnu.gpt.pojo.question;

import com.scnu.gpt.entity.Question;
import com.scnu.gpt.entity.RecordQuestion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "具体某道题的改卷结果即题目信息")
public record RecordQuestionDTO(
        @Schema(description = "id")
        int questionRecordId,
        @Schema(description = "题目类型(编程题/主观题/选择题)")
        String questionType,
        @Schema(description = "本题题干")
        String questionStem,
        @Schema(description = "正确答案")
        String questionAnswer,
        @Schema(description = "程序正确运行结果")
        String codeOutput,
        @Schema(description = "学生回答（代码/主观题回答/选项）")
        String studentAnswer,
        @Schema(description = "学生代码运行结果（仅编程题有效）")
        String studentOutput,
        @Schema(description = "题目得分")
        int questionScore,
        @Schema(description = "评分依据")
        String scoreParse,
        @Schema(description = "错误分析")
        String erroParse,
        @Schema(description = "涉及知识点")
        ArrayList<String> knowledgeList
) {
    public static RecordQuestionDTO fromEntities(
            Question question,
            RecordQuestion recordQuestion,
            ArrayList<String> knowledgeList
    ){
        return new RecordQuestionDTO(
                recordQuestion.getQuestionRecordId(),
                question.getQuestionType(),
                question.getQuestionStem(),
                question.getQuestionAnswer(),
                question.getCodeOutput(),
                recordQuestion.getStudentAnswer(),
                recordQuestion.getStudentOutput(),
                recordQuestion.getQuestionScore(),
                recordQuestion.getScoreParse(),
                recordQuestion.getErrorParse(),
                knowledgeList
        );
    }
}
