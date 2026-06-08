package com.scnu.gpt.service;

import com.scnu.gpt.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnu.gpt.entity.Section;
import com.scnu.gpt.entity.Subsection;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.CourseDetailDTO;
import com.scnu.gpt.pojo.course.CourseStatisticsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ldw
 * @since 2025-06-09
 */
public interface ICourseService extends IService<Course> {

    int addCourse(Course course);

    ApiResponse<Void> updateCourse(Course course);

    CourseDetailDTO queryCourseDetail(int courseId);

    ArrayList<CourseStatisticsDTO> queryCourseByTeacherId(int userId);

    void deleteCourse(int courseId);

    String queryCourseDetailStr(int sectionId);

    ArrayList<CourseStatisticsDTO> queryAllCourse(int i);

    void joinCourse(int userId, int courseId);

    /**
     * 退出课程
     * @param userId 学生ID
     * @param courseId 课程ID
     * @return 受影响行数（0表示未加入该课程）
     */
    int dropCourse(int userId, int courseId);
}
