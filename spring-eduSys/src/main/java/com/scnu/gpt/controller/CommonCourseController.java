package com.scnu.gpt.controller;

import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.CourseAllResponse;
import com.scnu.gpt.pojo.course.CourseDetailResponse;
import com.scnu.gpt.service.ICourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 公共课程接口控制器 — 对应接口文档中的 /courses/* 路径
 * </p>
 *
 * @author verobz
 * @since 2026-06-09
 */
@RestController
@CrossOrigin
@RequestMapping("/courses")
@Tag(name = "公共课程接口", description = "供学生端和公共页面调用的课程查询接口")
public class CommonCourseController {

    @Autowired
    private ICourseService courseService;

    @Operation(summary = "获取全部课程", description = "返回所有课程的列表信息，包含总数和教师信息")
    @GetMapping("/all")
    public ApiResponse<CourseAllResponse> getAllCourses() {
        try {
            CourseAllResponse result = courseService.getAllCoursesWithTeacher();
            return new ApiResponse<>("200", "获取成功", result);
        } catch (Exception e) {
            System.out.println("获取全部课程失败: " + e.getMessage());
            return new ApiResponse<>("500", "获取课程列表失败: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "获取课程详情", description = "根据课程ID查询完整的课程详情，包含章节和内容列表")
    @GetMapping("/detail")
    public ApiResponse<CourseDetailResponse> getCourseDetail(
            @Parameter(description = "课程ID", required = true)
            @RequestParam("course_id") Integer courseId) {
        try {
            CourseDetailResponse result = courseService.getCourseDetailNew(courseId);
            return new ApiResponse<>("200", "获取成功", result);
        } catch (IllegalArgumentException e) {
            System.out.println("查询课程详情失败: " + e.getMessage());
            return new ApiResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("查询课程详情异常: " + e.getMessage());
            return new ApiResponse<>("500", "获取课程详情失败: " + e.getMessage(), null);
        }
    }
}
