package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.*;

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
@TableName("t_course")
public class Course {

    /**
     * 课程
     */
    @TableId(value = "course_id", type = IdType.AUTO)
    private Integer courseId;

    /**
     * 课程名
     */
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private String courseName="";

    /**
     * 课程介绍
     */
    private String courseDesc="";

    /**
     * 封面相对路径
     */
    private String coverPath;

    /**
     * 外键-创建课程的教师id
     */
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Integer userId;
}
