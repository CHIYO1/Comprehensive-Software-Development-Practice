package com.scnu.gpt.pojo.gpt;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Dify工作流api请求格式")
public record DifyRequest<T>(
        @Schema(description = "工作流中的输入参数")
        T inputs,

        @Schema(description = "响应模式", example = "streaming/blocking ")
        String response_mode,

        @Schema(description = "用户标识")
        String user){
}
