package com.scnu.gpt.pojo.course;

import com.scnu.gpt.entity.Section;
import com.scnu.gpt.entity.Subsection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "章节DTO，包括章节下的小节DTO的List")
public record SectionDTO(
        @Schema(description = "章节信息")
        Section section,
        @Schema(description = "本章节涉及的知识点")
        ArrayList<String> knowledgeList,
        @Schema(description = "章节下的小节")
        ArrayList<Subsection> subsectionList,
        @Schema(description = "是否可编辑")
        Boolean editable
) {
}
