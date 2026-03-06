package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
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
@TableName("t_user")
public class User implements Serializable {

//    非自增
    @Schema(description = "（学号、职工号（登录账号)", example = "2024001")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @Schema(description = "用户类型（student、teacher）", example = "student")
    private String userType;

    @Schema(description = "用户昵称", example = "ldw")
    private String userName;


    @Schema(description = "密码", example = "123")
    private String password;
}
