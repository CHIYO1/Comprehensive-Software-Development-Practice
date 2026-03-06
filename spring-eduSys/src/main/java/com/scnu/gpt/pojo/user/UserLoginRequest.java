package com.scnu.gpt.pojo.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户登录请求数据")
public record UserLoginRequest(
        @Schema(description = "账户名/用户id", example = "2022")
        String userId,

        @Schema(description = "用户密码", example = "123")
        String password) {
}
