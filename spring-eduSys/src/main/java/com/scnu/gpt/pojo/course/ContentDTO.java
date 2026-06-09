package com.scnu.gpt.pojo.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 章节内容/小节 DTO — 对应 t_chapter_content 表
 *
 * @author verobz
 * @since 2026-06-09
 */
@Schema(description = "章节内容/小节信息")
public record ContentDTO(
        @JsonProperty("content_id")
        @Schema(description = "内容ID")
        Integer contentId,

        @JsonProperty("content_name")
        @Schema(description = "内容名称")
        String contentName,

        @JsonProperty("content_description")
        @Schema(description = "内容描述")
        String contentDescription,

        @JsonProperty("content_type")
        @Schema(description = "内容类型 (video/document/exercise/quiz/assignment)")
        String contentType,

        @JsonProperty("content_order")
        @Schema(description = "内容顺序")
        Integer contentOrder,

        @JsonProperty("video_url")
        @Schema(description = "视频地址")
        String videoUrl,

        @JsonProperty("document_url")
        @Schema(description = "文档地址")
        String documentUrl
) {}
