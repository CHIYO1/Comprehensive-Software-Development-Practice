package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 课程完整详情响应 DTO — 仅含文档规定的 5 个顶级字段
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "课程完整详情响应")
public record CourseDetailResponse(
        @JsonProperty("course_id")
        @Schema(description = "课程ID")
        Integer courseId,

        @JsonProperty("course_name")
        @Schema(description = "课程名称")
        String courseName,

        @JsonProperty("description")
        @Schema(description = "课程描述")
        String description,

        @JsonProperty("keywords")
        @Schema(description = "课程关键词列表")
        List<String> keywords,

        @JsonProperty("chapters")
        @Schema(description = "章节列表")
        List<ChapterDTO> chapters
) {}
