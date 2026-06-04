package com.scnu.gpt.pojo.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户登录返回数据")
public record UserLoginResponse(
        @Schema(description = "用户id")
        int userId,
        @Schema(description = "用户名")
        String username,
        @Schema(description = "用户角色", example = "Student、Teacher、Admin")
        String role,
        @Schema(description = "JWT认证令牌")
        String token) {
}
