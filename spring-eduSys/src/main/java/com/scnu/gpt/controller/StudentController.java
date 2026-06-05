package com.scnu.gpt.controller;

import com.scnu.gpt.entity.User;
import com.scnu.gpt.pojo.ApiResponse;
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
     * 退课
     */
    @Operation(summary = "退课", description = "当前登录学生退出指定课程")
    @DeleteMapping("/drop")
    public ApiResponse<Void> dropCourse(@RequestParam("course_id") Integer courseId) {
        try {
            int studentId = getCurrentUserId();
            int rows = courseService.dropCourse(studentId, courseId);
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
