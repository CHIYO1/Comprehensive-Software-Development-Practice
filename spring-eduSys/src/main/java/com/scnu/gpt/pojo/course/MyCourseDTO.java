package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * 学生已选课程 DTO — 用于 /students/my-courses 列表
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "学生已选课程信息（含进度和作业）")
public record MyCourseDTO(
        @JsonProperty("course_id")
        @Schema(description = "课程ID")
        Integer courseId,

        @JsonProperty("course_name")
        @Schema(description = "课程名称")
        String courseName,

        @JsonProperty("description")
        @Schema(description = "课程描述")
        String description,

        @JsonProperty("student_count")
        @Schema(description = "选课人数")
        Integer studentCount,

        @JsonProperty("start_date")
        @Schema(description = "开课日期")
        LocalDate startDate,

        @JsonProperty("end_date")
        @Schema(description = "结课日期（由 start_date + weeks 计算）")
        LocalDate endDate,

        @JsonProperty("teacher_name")
        @Schema(description = "教师名字")
        String teacherName,

        @JsonProperty("progress")
        @Schema(description = "学习进度")
        ProgressDTO progress,

        @JsonProperty("homework")
        @Schema(description = "作业完成情况")
        HomeworkDTO homework,

        @JsonProperty("score")
        @Schema(description = "课程得分")
        int score,

        @JsonProperty("cover_image")
        @Schema(description = "封面图片URL")
        String coverImage
) {}
