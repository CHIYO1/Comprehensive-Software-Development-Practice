package com.scnu.gpt.mapper;

import com.scnu.gpt.pojo.knowledge.QuestionKnowledgeDTO;
import com.scnu.gpt.pojo.knowledge.SectionKnowledgeDTO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ldw
 * @since 2025-06-16
 */
public interface KnowledgeMapper {
//    查询某章节涉及的所有知识点
    @Select("SELECT knowledge FROM section_knowledge WHERE section_id = #{sectionId} ")
    List<String> selectBySectionId(@Param("sectionId") int sectionId);

//    删除某章节涉及的所有知识点
    @Delete("DELETE FROM section_knowledge WHERE section_id = #{sectionId}")
    void deleteBySectionId(@Param("sectionId") int sectionId);
//    插入某章节关联知识点
    void insertSectionKnowledges(@Param("list") List<SectionKnowledgeDTO> SKDTO);

    //    查询某试题涉及的所有知识点
    @Select("SELECT knowledge FROM question_knowledge WHERE question_id = #{questionId} ")
    List<String> selectByQuestionId(@Param("questionId") int questionId);

    //  删除某试题涉及的所有知识点
    @Delete("DELETE FROM question_knowledge WHERE question_id = #{questionId}")
    void deleteByQuestionId(@Param("questionId") int questionId);
    //    插入某试题关联知识点
    void insertQuestionKnowledges(@Param("list") List<QuestionKnowledgeDTO> QKDTO);

}
