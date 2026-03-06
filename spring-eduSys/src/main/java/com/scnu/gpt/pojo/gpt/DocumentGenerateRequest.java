package com.scnu.gpt.pojo.gpt;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "文档生成请求格式")
public record DocumentGenerateRequest(
        @Schema(description = "操作模式",example = "add；edit")
        String opMode,
        @Schema(description = "课程及章节信息描述上下文")
        String courseDetailStr,
        @Schema(description = "用户要求")
        String demand,
        @Schema(description = "被选中的文本(仅当局部修改时有效)")
        String selectedText,
        @Schema(description = "选中文本上文(仅当局部修改时有效)")
        String beforeText,
        @Schema(description = "选中文本下文(仅当局部修改时有效)")
        String afterText
) {
}
