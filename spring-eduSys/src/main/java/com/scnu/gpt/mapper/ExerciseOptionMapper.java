package com.scnu.gpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnu.gpt.entity.ExerciseOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 练习题选项 Mapper
 */
@Mapper
public interface ExerciseOptionMapper extends BaseMapper<ExerciseOption> {

    /**
     * 根据题目ID查询所有选项
     */
    @Select("SELECT * FROM t_exercise_option WHERE question_id = #{questionId} ORDER BY option_key")
    List<ExerciseOption> selectByQuestionId(@Param("questionId") Integer questionId);
}