package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 练习题选项实体 - 对应 t_exercise_option
 */
@Getter
@Setter
@TableName("t_exercise_option")
public class ExerciseOption {

    @TableId(value = "option_id", type = IdType.AUTO)
    private Integer optionId;

    /**
     * 所属题目ID
     */
    private Integer questionId;

    /**
     * 选项标识: A, B, C, D
     */
    private String optionKey;

    /**
     * 选项内容
     */
    private String optionText;

    /**
     * 是否正确答案
     */
    private Boolean isCorrect;
}