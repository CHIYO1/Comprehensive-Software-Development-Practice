package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户实体 — 映射 t_user 表
 * </p>
 *
 * @author ldw
 * @since 2025-06-03
 */
@Getter
@Setter
@TableName("t_user")
public class User implements Serializable {

    @Schema(description = "用户ID（学号、职工号）", example = "2024001")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @Schema(description = "登录账号", example = "student001")
    private String account;

    @Schema(description = "密码（BCrypt加密）", example = "$2a$10$...")
    private String password;

    @Schema(description = "用户名", example = "张三")
    private String username;

    @Schema(description = "用户角色：Student/Teacher/Admin", example = "Student")
    private String role;

    @Schema(description = "头像相对路径", example = "/uploads/avatars/student1.jpg")
    private String avatar;

    @Schema(description = "状态：1正常 0禁用", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
