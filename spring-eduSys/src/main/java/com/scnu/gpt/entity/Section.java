package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
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
@TableName("t_section")
public class Section {

    /**
     * 课程章节
     */
    @TableId(value = "section_id", type = IdType.AUTO)
    private Integer sectionId;

    /**
     * 章节标题
     */
    private String sectionName="";

    /**
     * 章节描述
     */
    private String sectionDesc="";

    /**
     * 外键-该章节所属的课程
     */
    private Integer courseId;
}
