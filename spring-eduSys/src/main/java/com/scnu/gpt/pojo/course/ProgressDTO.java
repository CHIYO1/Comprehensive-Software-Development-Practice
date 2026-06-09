package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学习进度 DTO
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "学习进度")
public record ProgressDTO(
        @JsonProperty("completed_lessons")
        @Schema(description = "已完成课时数")
        int completedLessons,

        @JsonProperty("total_lessons")
        @Schema(description = "总课时数")
        int totalLessons
) {}
