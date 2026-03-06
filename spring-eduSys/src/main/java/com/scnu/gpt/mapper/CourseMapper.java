package com.scnu.gpt.mapper;

import com.scnu.gpt.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ldw
 * @since 2025-06-09
 */
public interface CourseMapper extends BaseMapper<Course> {
    //统计学生总数
    @Select("SELECT COUNT(sc.user_id) " +
            "FROM std_crs sc " +
            "WHERE sc.course_id = #{courseId}")
    int getStudentNum(@Param("courseId") int courseId);

    //查询某学生是否有参加本课程
    @Select("SELECT COUNT(sc.user_id) " +
            "FROM std_crs sc " +
            "WHERE sc.course_id = #{courseId} and sc.user_id = #{userId}")
    int getIfJoin(@Param("courseId") int courseId,@Param("userId") int userId);

    //学生加入课程
    @Insert("INSERT INTO std_crs (user_id, course_id) VALUES (#{userId},#{courseId})")
    void joinCourse(@Param("userId") int userId, @Param("courseId") int courseId);

    //统计章节总数
    @Select("SELECT COUNT(ts.section_id) " +
            "FROM t_section ts " +
            "WHERE ts.course_id = #{courseId}")
    int getSectionNum(@Param("courseId") int courseId);


}
