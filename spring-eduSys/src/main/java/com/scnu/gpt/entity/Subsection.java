package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author ldw
 * @since 2025-06-03
 */
@Getter
@Setter
@TableName("t_subsection")
@Schema(description = "章内小节")
public class Subsection {

    /**
     * 章节小节
     */
    @TableId(value = "subsection_id", type = IdType.AUTO)
    private Integer subsectionId;

    /**
     * 小节标题
     */
    @Schema(description = "小节名")
    private String subsectionName="";

    /**
     * 小节描述
     */
    @Schema(description = "小节描述")
    private String subsectionDesc="";

    /**
     * 内容类型（questions、video、document）
     */
    @Schema(description = "内容类型",example = "questions/video/document")
    private String subsectionType;

    /**
     * 该内容对应的id
     */
    @Schema(description = "内容对应资源id")
    private Integer resourceId;

    /**
     * 外键-该小节所属章节
     */
    @Schema(description = "所属章节")
    private Integer sectionId;
}
