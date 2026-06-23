package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 练习题实体 - 对应 t_exercise_question
 */
@Getter
@Setter
@TableName("t_exercise_question")
public class ExerciseQuestion {

    @TableId(value = "question_id", type = IdType.AUTO)
    private Integer questionId;

    /**
     * 课程ID
     */
    private Integer courseId;

    /**
     * 章节ID (可为空)
     */
    private Integer chapterId;

    /**
     * 题型: single_choice, multiple_choice, true_false, fill_blank, essay
     */
    private String questionType;

    /**
     * 题干
     */
    private String question;

    /**
     * 答案
     */
    private String answer;

    /**
     * 解析
     */
    private String analysis;

    /**
     * 来源: local, ai_generated
     */
    private String sourceType;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}