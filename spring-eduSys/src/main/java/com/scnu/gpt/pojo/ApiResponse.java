package com.scnu.gpt.pojo;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "标准api返回")
public record ApiResponse<T>(
        @Schema(description = "状态码，200表成功，400表示前端参数传入有误，500表示后端处理错误", example = "200")
        String code,

        @Schema(description = "附带信息", example = "成功")
        String message,

        @Schema(description = "返回结果")
        T data){
}