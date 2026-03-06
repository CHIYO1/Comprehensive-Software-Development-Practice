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
@TableName("t_record_set")
public class RecordSet {

    /**
     * 试题集做题记录id
     */
    @TableId(value = "set_record_id", type = IdType.AUTO)
    private Integer setRecordId;

    /**
     * 总得分(四舍五入)
     */
    private Integer setScore;

    /**
     * 试题集id
     */
    private Integer setId;
    /**
     * 试题小节Id
     */
    private Integer subsectionId;
    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 状态（“未完成”、“评卷中”、“已评分”）
     */
    private String state;
}
