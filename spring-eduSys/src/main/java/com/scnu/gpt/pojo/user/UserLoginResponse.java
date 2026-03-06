package com.scnu.gpt.pojo.user;
import com.scnu.gpt.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户登录返回数据")
public record UserLoginResponse(
        @Schema(description = "用户id")
        int userId,
        @Schema(description = "用户姓名")
        String userName,
        @Schema(description = "用户类型",example = "TEACHER、STUDENT、ADMIN")
        String role,
        @Schema(description = "认证信息")
        String token
        ) {}
