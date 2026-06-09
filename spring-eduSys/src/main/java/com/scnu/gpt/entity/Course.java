package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程实体 — 对应 finalDB t_course 表
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
     * 课程ID
     */
    @TableId(value = "course_id", type = IdType.AUTO)
    private Integer courseId;

    /**
     * 课程名称
     */
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private String courseName = "";

    /**
     * 课程描述
     */
    private String description = "";

    /**
     * 关键词JSON数组
     */
    private String keywordsJson;

    /**
     * 教师用户ID (FK -> t_user.user_id)
     */
    @TableField(value = "teacher_id", updateStrategy = FieldStrategy.NOT_NULL)
    private Integer teacherId;

    /**
     * 选课人数 (冗余字段，由DB触发器/维护脚本更新)
     */
    private Integer studentCount;

    /**
     * 开课日期
     */
    private LocalDate startDate;

    /**
     * 课程时长(周)
     */
    private Integer weeks;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 难度 (1-5)
     */
    private Integer difficulty;

    /**
     * 课程评分 (DECIMAL 3,1)
     */
    private BigDecimal score;

    /**
     * 封面图片相对路径
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 创建时间 (由数据库 DEFAULT CURRENT_TIMESTAMP 管理，应用层不写入)
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;

    /**
     * 更新时间 (由数据库 ON UPDATE CURRENT_TIMESTAMP 管理，应用层不写入)
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updatedAt;
}
