package com.scnu.gpt.pojo.course;

import com.scnu.gpt.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "课程统计信息（不包括课程下章节详情）")
public record CourseStatisticsDTO(
        @Schema(description = "课程基础信息")
        Course course,
        @Schema(description = "课程总学生数")
        int studentNum,
        @Schema(description = "章节数")
        int sectionNum,
        @Schema(description = "教师名")
        String teacherName,
        @Schema(description = "是否参加了本课程(即>0)")
        int ifJoin){
}
