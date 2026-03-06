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
 * @since 2025-07-03
 */
@Getter
@Setter
@TableName("t_record_question")
public class RecordQuestion {

    /**
     * 单个题目做题记录id
     */
    @TableId(value = "question_record_id", type = IdType.AUTO)
    private Integer questionRecordId;

    /**
     * 单个题目得分
     */
    private Integer questionScore;

    /**
     * 学生回答
     */
    private String studentAnswer;

    /**
     * 程序运行结果（仅编程题有效）
     */
    private String studentOutput;
    /**
     * 评分依据
     */
    private String scoreParse;

    /**
     * 错误分析
     */
    private String errorParse;


    /**
     * 做的哪道题
     */
    private int questionId;

    /**
     * 所属记录id
     */
    private Integer setRecordId;
}
