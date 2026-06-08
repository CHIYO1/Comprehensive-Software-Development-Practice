package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 学生笔记实体 — 映射 t_note 表
 * </p>
 *
 * @author Yusheng
 * @since 2025-06-04
 */
@Getter
@Setter
@TableName("t_note")
public class Note implements Serializable {

    @TableId(value = "note_id", type = IdType.AUTO)
    private Integer noteId;

    private Integer studentId;

    private Integer courseId;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
