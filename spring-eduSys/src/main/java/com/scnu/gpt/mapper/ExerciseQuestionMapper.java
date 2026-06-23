package com.scnu.gpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnu.gpt.entity.ExerciseQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 练习题 Mapper
 */
@Mapper
public interface ExerciseQuestionMapper extends BaseMapper<ExerciseQuestion> {

    /**
     * 根据课程ID和题型查询题目列表
     */
    @Select("SELECT * FROM t_exercise_question WHERE course_id = #{courseId} " +
            "AND question_type = #{questionType} " +
            "AND is_active = 1 " +
            "ORDER BY created_at DESC")
    List<ExerciseQuestion> selectByCourseAndType(@Param("courseId") Integer courseId,
                                                  @Param("questionType") String questionType);

    /**
     * 根据课程ID查询所有题目
     */
    @Select("SELECT * FROM t_exercise_question WHERE course_id = #{courseId} " +
            "AND is_active = 1 " +
            "ORDER BY created_at DESC")
    List<ExerciseQuestion> selectByCourse(@Param("courseId") Integer courseId);
}