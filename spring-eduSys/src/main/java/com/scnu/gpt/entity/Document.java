package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
@TableName("t_document")
public class Document {

    /**
     * 文档
     */
    @TableId(value = "document_id", type = IdType.AUTO)
    private Integer documentId;

    /**
     * 文件名
     */
    private String documentName;

    /**
     * 文件描述
     */
    private String documentDesc;

    /**
     * 文件大小
     */
    private String documentSize;

    /**
     * 存储相对路径
     */
    private String path;

    /**
     * 创建日期
     */
    private LocalDate createdDate;

    /**
     * 所属教师Id
     */
    private Integer userId;
}
