package com.scnu.gpt.pojo.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户注册请求数据")
public record UserRegisterRequest(
        @Schema(description = "登录账号", example = "student003")
        String account,

        @Schema(description = "密码", example = "123456abc")
        String password,

        @Schema(description = "用户名", example = "张三")
        String username,

        @Schema(description = "用户角色：Student/Teacher（Admin不允许注册）", example = "Student")
        String role) {
}
