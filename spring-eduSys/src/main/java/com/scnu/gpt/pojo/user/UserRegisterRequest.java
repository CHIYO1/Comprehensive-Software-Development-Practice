package com.scnu.gpt.pojo.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户注册请求数据")
public record UserRegisterRequest(
        @Schema(description = "登录账号", example = "student001")
        String account,

        @Schema(description = "用户密码", example = "123456abc")
        String password,

        @Schema(description = "用户名", example = "张三")
        String username,

        @Schema(description = "用户角色", example = "Student")
        String role) {
}
