package com.scnu.gpt.pojo.gpt;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dify文件作为request的input参数时的输入格式")
public record DifyFileInput(
        @Schema(description = "文件Id，调用dify文件上传api获得")
        String upload_file_id,

        @Schema(description = "响应模式", example = "local_file")
        String transfer_method,

        @Schema(description = "文件类型", example = "document/image/video")
        String type){
}