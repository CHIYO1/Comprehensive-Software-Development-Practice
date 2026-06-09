package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 章节内容实体 — 对应 finalDB t_chapter_content 表
 * </p>
 *
 * @author ldw
 * @since 2025-06-03
 */
@Getter
@Setter
@TableName("t_chapter_content")
@Schema(description = "章内小节/内容")
public class Subsection {

    /**
     * 内容ID
     */
    @TableId(value = "content_id", type = IdType.AUTO)
    private Integer contentId;

    /**
     * 内容名称
     */
    @Schema(description = "内容名")
    private String contentName = "";

    /**
     * 内容描述
     */
    @Schema(description = "内容描述")
    private String contentDescription;

    /**
     * 内容类型 (video/document/exercise/quiz/assignment)
     */
    @Schema(description = "内容类型", example = "video/document/exercise/quiz/assignment")
    private String contentType;

    /**
     * 内容对应资源id（在 finalDB 中无直接对应列，保留用于业务逻辑）
     */
    @Schema(description = "内容对应资源id")
    @TableField(exist = false)
    private Integer resourceId;

    /**
     * 外键-该内容所属章节
     */
    @Schema(description = "所属章节ID")
    private Integer chapterId;

    /**
     * 内容顺序
     */
    @Schema(description = "内容顺序")
    private Integer contentOrder;

    /**
     * 视频地址 (video 类型时)
     */
    @Schema(description = "视频地址")
    @TableField("video_url")
    private String videoUrl;

    /**
     * 文档地址 (document 类型时)
     */
    @Schema(description = "文档地址")
    @TableField("document_url")
    private String documentUrl;
}
