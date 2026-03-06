package com.scnu.gpt.service;

import com.scnu.gpt.entity.Section;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnu.gpt.entity.Subsection;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.SectionDTO;

import java.util.ArrayList;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ldw
 * @since 2025-06-17
 */
public interface ISectionService extends IService<Section> {
    SectionDTO addSection(Section section);

    void addSectionKnowledge(int sectionId, ArrayList<String> KnowledgeList);

    ApiResponse<Void> updateSection(SectionDTO sectionDTO);

    ApiResponse<Void> deleteSection(Integer sectionId);

    ApiResponse<Void> deleteSubsection(String subsectionId);

    ApiResponse<Void> updateSubsection(Subsection subsection);

    Subsection addSubsection(Subsection subsection);

    ArrayList<String> querySectionKnowledges(int sectionId);

    Subsection querySubSection(int subsectionId);
}
