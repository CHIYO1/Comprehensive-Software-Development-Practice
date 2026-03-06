package com.scnu.gpt.pojo.file;

import com.scnu.gpt.entity.Course;
import com.scnu.gpt.pojo.course.SectionDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "文件（图片、文档）查询")
public record FileQueryDTO(
        @Schema(description = "资源id")
        int fileId,
        @Schema(description = "资源类型(document、video)")
        String fileType,
        @Schema(description = "资源名")
        String fileName,
        @Schema(description = "资源url")
        String fileUrl
) {
}
