package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 课程卡片 DTO — 用于 /courses/all 列表展示，扁平结构 + 下划线命名
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "课程卡片信息（用于列表展示）")
public record CourseCardDTO(
        @JsonProperty("course_id")
        @Schema(description = "课程ID")
        Integer courseId,

        @JsonProperty("course_name")
        @Schema(description = "课程名称")
        String courseName,

        @JsonProperty("description")
        @Schema(description = "课程描述")
        String description,

        @JsonProperty("teacher")
        @Schema(description = "授课教师信息")
        TeacherBriefDTO teacher,

        @JsonProperty("student_count")
        @Schema(description = "选课人数")
        Integer studentCount,

        @JsonProperty("start_date")
        @Schema(description = "开课日期")
        LocalDate startDate,

        @JsonProperty("weeks")
        @Schema(description = "课程时长（周）")
        Integer weeks,

        @JsonProperty("course_type")
        @Schema(description = "课程类型")
        String courseType,

        @JsonProperty("difficulty")
        @Schema(description = "难度 (1-5)")
        Integer difficulty,

        @JsonProperty("score")
        @Schema(description = "课程评分")
        BigDecimal score,

        @JsonProperty("cover_image")
        @Schema(description = "封面图片URL")
        String coverImage
) {}
