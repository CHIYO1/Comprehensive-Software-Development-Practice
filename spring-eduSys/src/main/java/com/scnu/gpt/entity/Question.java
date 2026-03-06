package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_question")
public class Question {

    /**
     * 题目id
     */
    @TableId(value = "question_id", type = IdType.AUTO)
    private Integer questionId;

    /**
     * 题干
     */
    private String questionStem;

    /**
     * 正确答案（题目类型为"选择题"时，该属性为选项号；题型为“编程题”时，该属性为程序标准输出，可为空）
     */
    private String questionAnswer;

    /**
     * 仅当题目类型为“编程题"时有效，编程题的期望输出
     */
    private String codeOutput;

    /**
     * 解析/评分标准
     */
    private String questionParse;

    /**
     * 题目类型（“主观题”、"选择题"、"编程题")
     */
    private String questionType;

    /**
     * 所属试题集
     */
    private Integer setId;
}
