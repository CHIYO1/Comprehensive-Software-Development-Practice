package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 章节 DTO — 包含章节下的内容列表
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "章节信息（含内容列表）")
public record ChapterDTO(
        @JsonProperty("chapter_id")
        @Schema(description = "章节ID")
        Integer chapterId,

        @JsonProperty("chapter_name")
        @Schema(description = "章节名称")
        String chapterName,

        @JsonProperty("chapter_description")
        @Schema(description = "章节描述")
        String chapterDescription,

        @JsonProperty("chapter_order")
        @Schema(description = "章节顺序")
        Integer chapterOrder,

        @JsonProperty("contents")
        @Schema(description = "章节下的内容列表")
        List<ContentDTO> contents
) {}
