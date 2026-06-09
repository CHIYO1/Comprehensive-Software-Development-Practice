package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 作业完成情况 DTO
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "作业完成情况")
public record HomeworkDTO(
        @JsonProperty("completed")
        @Schema(description = "已完成的作业数")
        int completed,

        @JsonProperty("total")
        @Schema(description = "作业总数")
        int total
) {}
