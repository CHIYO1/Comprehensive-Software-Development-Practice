package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 选课/退课请求体
 * student_id 不在请求体中 — 由后端从 JWT token 提取，更安全
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "选课/退课请求体")
public record EnrollRequest(
        @JsonProperty("course_id")
        @Schema(description = "课程ID")
        Integer courseId
) {}
