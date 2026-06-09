package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 教师简要信息 DTO — 用于课程列表/详情中的 teacher 嵌套对象
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "教师简要信息")
public record TeacherBriefDTO(
        @JsonProperty("teacher_id")
        @Schema(description = "教师ID")
        Integer teacherId,

        @JsonProperty("teacher_name")
        @Schema(description = "教师姓名")
        String teacherName
) {}
