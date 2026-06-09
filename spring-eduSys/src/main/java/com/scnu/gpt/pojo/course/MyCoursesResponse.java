package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 我的课程列表响应 — 包裹 total + courses 数组
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "我的课程列表响应")
public record MyCoursesResponse(
        @JsonProperty("total")
        @Schema(description = "课程总数")
        int total,

        @JsonProperty("courses")
        @Schema(description = "课程列表")
        List<MyCourseDTO> courses
) {}
