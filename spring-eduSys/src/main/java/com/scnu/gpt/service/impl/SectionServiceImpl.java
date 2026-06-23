package com.scnu.gpt.service.impl;

import com.scnu.gpt.entity.Section;
import com.scnu.gpt.entity.Subsection;
import com.scnu.gpt.mapper.KnowledgeMapper;
import com.scnu.gpt.mapper.SectionMapper;
import com.scnu.gpt.mapper.SubsectionMapper;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.SectionDTO;
import com.scnu.gpt.pojo.knowledge.SectionKnowledgeDTO;
import com.scnu.gpt.service.ISectionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ldw
 * @since 2025-06-17
 */
@Service
public class SectionServiceImpl extends ServiceImpl<SectionMapper, Section> implements ISectionService {
    @Autowired
    private SubsectionMapper subsectionMapper;
    @Autowired
    private SectionMapper sectionMapper;
    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Override
    public SectionDTO addSection(Section section) {
        sectionMapper.insert(section);
        return new SectionDTO(section,new ArrayList<>(),new ArrayList<>(),true);
    }
    @Override
    public void addSectionKnowledge(int sectionId, ArrayList<String> KnowledgeList) {
        if(KnowledgeList!=null && !KnowledgeList.isEmpty()){
            List<SectionKnowledgeDTO> sectionKnowledgeDTOS = new ArrayList<>();
            for(String knowledge : KnowledgeList){
                sectionKnowledgeDTOS.add(new SectionKnowledgeDTO(sectionId,knowledge));
            }
            knowledgeMapper.insertSectionKnowledges(sectionKnowledgeDTOS);
        }
    }
    @Override
    public ApiResponse<Void> updateSection(SectionDTO sectionDTO) {
        Section section = sectionDTO.section();
        sectionMapper.updateById(section);
        //更新本章节关联的知识点列表（先删后增）
        knowledgeMapper.deleteBySectionId(section.getChapterId());
        addSectionKnowledge(section.getChapterId(),sectionDTO.knowledgeList());
        return new ApiResponse<>("200","成功",null);
    }

    @Override
    public ApiResponse<Void> deleteSection(Integer sectionId) {
        sectionMapper.deleteById(sectionId);
        return new ApiResponse<>("200","成功",null);
    }

    @Override
    public ApiResponse<Void> deleteSubsection(String subsectionId) {
        subsectionMapper.deleteById(subsectionId);
        return new ApiResponse<>("200","成功",null);
    }

    @Override
    public ApiResponse<Void> updateSubsection(Subsection subsection) {
        subsectionMapper.updateById(subsection);
        return new ApiResponse<>("200","成功",null);
    }

    @Override
    public Subsection addSubsection(Subsection subsection) {
        subsectionMapper.insert(subsection);
        return subsection;
    }

    @Override
    public ArrayList<String> querySectionKnowledges(int sectionId) {
        ArrayList<String> result = (ArrayList<String>) knowledgeMapper.selectBySectionId(sectionId);
        return result;
    }

    @Override
    public Subsection querySubSection(int subsectionId) {
        return subsectionMapper.selectById(subsectionId);
    }

    @Override
    public List<Map<String, Object>> querySectionsByCourseId(Integer courseId) {
        // 1. 根据课程ID查询所有章节，按章节顺序排序
        QueryWrapper<Section> sectionWrapper = new QueryWrapper<>();
        sectionWrapper.eq("course_id", courseId)
                    .orderByAsc("chapter_order");
        List<Section> sections = sectionMapper.selectList(sectionWrapper);
        
        // 2. 对每个章节，查询其下的小节列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Section section : sections) {
            // 查询该章节下的所有小节，按内容顺序排序
            QueryWrapper<Subsection> subsectionWrapper = new QueryWrapper<>();
            subsectionWrapper.eq("chapter_id", section.getChapterId())
                        .orderByAsc("content_order");
            List<Subsection> subsections = subsectionMapper.selectList(subsectionWrapper);
            
            // 构建返回的Map
            Map<String, Object> sectionMap = new HashMap<>();
            sectionMap.put("section", section);
            sectionMap.put("subsections", subsections);
            
            resultList.add(sectionMap);
        }
        
        return resultList;
    }
}
