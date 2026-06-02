package com.scnu.gpt.pojo.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户登录请求数据")
public record UserLoginRequest(
        @Schema(description = "登录账号", example = "student001")
        String account,

        @Schema(description = "用户密码", example = "123456abc")
        String password) {
}
