package com.scnu.gpt.pojo.question;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "教师查询试题集返回数据结构")
public record QuestionSetDTO(
        @Schema(description = "试题集ID")
        String setId,
        @Schema(description = "试题集名")
        String setName,
        @Schema(description = "试题集描述")
        String setDesc,
        @Schema(description = "总题数")
        Integer questionNum,
        @Schema(description = "包含的题型")
        ArrayList<String> questionTags
        ) {}
