package com.scnu.gpt.controller;

import com.scnu.gpt.entity.User;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.EnrollRequest;
import com.scnu.gpt.pojo.course.MyCoursesResponse;
import com.scnu.gpt.service.ICourseService;
import com.scnu.gpt.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 学生端控制器 — 退课、我的课程
 * </p>
 *
 * @author Yusheng
 * @since 2025-06-05
 */
@RestController
@CrossOrigin
@RequestMapping("/students")
@Tag(name = "学生端", description = "退课、我的课程等学生专属操作")
public class StudentController {

    private final ICourseService courseService;
    private final IUserService userService;

    public StudentController(ICourseService courseService, IUserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    /**
     * 获取当前登录学生的 userId
     */
    private int getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String account = authentication.getName();
        User user = userService.getByAccount(account);
        return user.getUserId();
    }

    /**
     * 我的课程 — 获取当前登录学生已加入的课程列表，包含学习进度和作业情况
     */
    @Operation(summary = "我的课程", description = "获取当前登录学生已加入的课程列表，包含学习进度和作业情况")
    @GetMapping("/my-courses")
    public ApiResponse<MyCoursesResponse> getMyCourses() {
        try {
            int studentId = getCurrentUserId();
            MyCoursesResponse result = courseService.getMyCourses(studentId);
            return new ApiResponse<>("200", "获取成功", result);
        } catch (Exception e) {
            System.out.println("获取我的课程异常: " + e.getMessage());
            return new ApiResponse<>("500", "获取我的课程失败，请稍后重试", null);
        }
    }

    /**
     * 选课 — 当前登录学生加入指定课程
     */
    @Operation(summary = "学生选课", description = "当前登录学生加入指定课程")
    @PostMapping("/enroll")
    public ApiResponse<Void> enroll(@RequestBody EnrollRequest request) {
        try {
            int studentId = getCurrentUserId();
            courseService.joinCourse(studentId, request.courseId());
            return new ApiResponse<>("200", "加入课程成功", null);
        } catch (Exception e) {
            System.out.println("选课异常: " + e.getMessage());
            return new ApiResponse<>("500", "选课失败，请稍后重试", null);
        }
    }

    /**
     * 退课 — 当前登录学生退出指定课程
     */
    @Operation(summary = "退课", description = "当前登录学生退出指定课程")
    @DeleteMapping("/drop")
    public ApiResponse<Void> dropCourse(@RequestBody EnrollRequest request) {
        try {
            int studentId = getCurrentUserId();
            int rows = courseService.dropCourse(studentId, request.courseId());
            if (rows > 0) {
                return new ApiResponse<>("200", "退课成功", null);
            } else {
                return new ApiResponse<>("500", "未加入该课程", null);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("退课失败: " + e.getMessage());
            return new ApiResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("退课异常: " + e.getMessage());
            return new ApiResponse<>("500", "退课失败，请稍后重试", null);
        }
    }
}
