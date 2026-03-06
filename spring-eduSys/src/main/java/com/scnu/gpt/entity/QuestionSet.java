package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author ldw
 * @since 2025-06-12
 */
@Getter
@Setter
@TableName("t_question_set")
public class QuestionSet {

    /**
     * 试题集
     */
    @TableId(value = "set_id", type = IdType.AUTO)
    private Integer setId;

    /**
     * 试题集名
     */
    private String setName="";

    /**
     * 试题集描述
     */
    private String setDesc="";

    /**
     * 所属教师id
     */
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Integer userId;
}
