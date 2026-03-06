package com.scnu.gpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnu.gpt.entity.Question;
import com.scnu.gpt.pojo.question.QuestionChoiceDTO;
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
 * @since 2025-06-13
 */
public interface QuestionMapper extends BaseMapper<Question> {
    //    查询某选择题下的所有选项
    @Select("SELECT choice FROM question_choice WHERE question_id = #{questionId} ")
    List<String> selectChoices(@Param("questionId") int questionId);

    //    删除某选择题的所有选项
    @Delete("DELETE FROM question_choice WHERE question_id = #{questionId}")
    void deleteChoices(@Param("questionId") int questionId);
    //    插入某选择题选项
    void insertChoices(@Param("list") List<QuestionChoiceDTO> QCDTO);

}
