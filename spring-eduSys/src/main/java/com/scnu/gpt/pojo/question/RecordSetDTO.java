package com.scnu.gpt.pojo.question;

import com.scnu.gpt.entity.Question;
import com.scnu.gpt.entity.RecordQuestion;
import com.scnu.gpt.entity.RecordSet;
import com.scnu.gpt.entity.Subsection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "试题集做题记录基本信息（不包括其下具体题目信息）")
public record RecordSetDTO(
        @Schema(description = "id")
        int setRecordId,
        @Schema(description = "总得分")
        int setScore,
        @Schema(description = "试题集id")
        int setId,
        @Schema(description = "做题学生姓名")
        String userName,
        @Schema(description = "状态（未完成/评卷中/已评分）")
        String state
) {
//    public static RecordSetDTO fromEntities(
//            RecordSet recordSet,
//            String setId,
//            String userName
//    ){
//    }
}
