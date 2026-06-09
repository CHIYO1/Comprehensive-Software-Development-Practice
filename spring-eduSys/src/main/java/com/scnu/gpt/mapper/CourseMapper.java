package com.scnu.gpt.mapper;

import com.scnu.gpt.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
    @Select("SELECT COUNT(sc.student_id) " +
            "FROM t_student_course sc " +
            "WHERE sc.course_id = #{courseId}")
    int getStudentNum(@Param("courseId") int courseId);

    //查询某学生是否有参加本课程
    @Select("SELECT COUNT(sc.student_id) " +
            "FROM t_student_course sc " +
            "WHERE sc.course_id = #{courseId} and sc.student_id = #{userId}")
    int getIfJoin(@Param("courseId") int courseId,@Param("userId") int userId);

    //学生加入课程
    @Insert("INSERT INTO t_student_course (student_id, course_id) VALUES (#{userId},#{courseId})")
    void joinCourse(@Param("userId") int userId, @Param("courseId") int courseId);

    //学生退出课程
    @Delete("DELETE FROM t_student_course WHERE student_id = #{userId} AND course_id = #{courseId}")
    int dropCourse(@Param("userId") int userId, @Param("courseId") int courseId);

    //统计章节总数
    @Select("SELECT COUNT(tc.chapter_id) " +
            "FROM t_chapter tc " +
            "WHERE tc.course_id = #{courseId}")
    int getSectionNum(@Param("courseId") int courseId);

    // === 以下为 /courses/all、/courses/detail、/students/my-courses 新增查询方法 ===

    //查询学生已选课程 ID 列表
    @Select("SELECT course_id FROM t_student_course WHERE student_id = #{studentId}")
    List<Integer> selectEnrolledCourseIds(@Param("studentId") int studentId);

    //查询学生在某课程中的得分
    @Select("SELECT COALESCE(score, 0) FROM t_student_course WHERE student_id = #{studentId} AND course_id = #{courseId}")
    int getStudentCourseScore(@Param("studentId") int studentId, @Param("courseId") int courseId);

    //统计课程下所有小节（内容）总数
    @Select("SELECT COUNT(*) FROM t_chapter_content cc " +
            "JOIN t_chapter ch ON cc.chapter_id = ch.chapter_id " +
            "WHERE ch.course_id = #{courseId}")
    int countSubsectionsByCourseId(@Param("courseId") int courseId);

    //统计学生在某课程中已完成的小节数（通过 t_record_set 记录判断）
    @Select("SELECT COUNT(DISTINCT cc.content_id) FROM t_chapter_content cc " +
            "JOIN t_chapter ch ON cc.chapter_id = ch.chapter_id " +
            "JOIN t_record_set rs ON rs.subsection_id = cc.content_id " +
            "WHERE ch.course_id = #{courseId} AND rs.user_id = #{studentId}")
    int countCompletedSubsections(@Param("courseId") int courseId, @Param("studentId") int studentId);

    //统计课程下作业（questions 类型内容）总数
    @Select("SELECT COUNT(*) FROM t_chapter_content cc " +
            "JOIN t_chapter ch ON cc.chapter_id = ch.chapter_id " +
            "WHERE ch.course_id = #{courseId} AND cc.content_type = 'questions'")
    int countHomeworkTotal(@Param("courseId") int courseId);

    //统计学生在某课程中已完成的作业数（状态不为"未完成"）
    @Select("SELECT COUNT(DISTINCT cc.content_id) FROM t_chapter_content cc " +
            "JOIN t_chapter ch ON cc.chapter_id = ch.chapter_id " +
            "JOIN t_record_set rs ON rs.subsection_id = cc.content_id " +
            "WHERE ch.course_id = #{courseId} AND rs.user_id = #{studentId} " +
            "AND cc.content_type = 'questions' AND rs.state != '未完成'")
    int countHomeworkCompleted(@Param("courseId") int courseId, @Param("studentId") int studentId);

}
