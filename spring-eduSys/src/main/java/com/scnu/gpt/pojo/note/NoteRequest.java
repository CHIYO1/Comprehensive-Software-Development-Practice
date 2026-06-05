package com.scnu.gpt.pojo.note;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "笔记创建/更新请求数据")
public record NoteRequest(
        @Schema(description = "笔记ID（创建时为空，更新时必填）", example = "1")
        Integer noteId,

        @Schema(description = "课程ID", example = "1")
        Integer courseId,

        @Schema(description = "笔记标题", example = "Python变量命名规则总结")
        String title,

        @Schema(description = "笔记内容", example = "变量名只能包含字母、数字和下划线...")
        String content) {
}
