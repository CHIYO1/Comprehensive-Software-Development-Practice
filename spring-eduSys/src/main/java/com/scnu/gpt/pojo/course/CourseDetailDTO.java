package com.scnu.gpt.pojo.course;

import com.scnu.gpt.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "课程完整详情（包括课程下的章节DTO列表）")
public record CourseDetailDTO(
        @Schema(description = "课程基础信息")
        Course course,
        @Schema(description = "本课程下的章节列表")
        ArrayList<SectionDTO> sectionList
) {
}
