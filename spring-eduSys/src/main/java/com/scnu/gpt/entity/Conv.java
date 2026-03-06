package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_conv")
//单轮对话
public class Conv {
    @TableId(type = IdType.AUTO)
    private Integer convId;
    private String convUser; //用户提问
    private String convGpt;    //gpt回答
    private Integer userId;     //所属用户
    private Integer activateState = 1; //是否作为上下文输入到gpt

}
