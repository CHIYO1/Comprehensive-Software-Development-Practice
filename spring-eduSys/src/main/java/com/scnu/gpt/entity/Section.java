package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 章节实体 — 对应 finalDB t_chapter 表
 * </p>
 *
 * @author ldw
 * @since 2025-06-03
 */
@Getter
@Setter
@TableName("t_chapter")
public class Section {

    /**
     * 章节ID
     */
    @TableId(value = "chapter_id", type = IdType.AUTO)
    private Integer chapterId;

    /**
     * 章节名称
     */
    private String chapterName = "";

    /**
     * 章节描述
     */
    private String chapterDescription;

    /**
     * 外键-该章节所属的课程
     */
    private Integer courseId;

    /**
     * 章节顺序
     */
    private Integer chapterOrder;
}
