package com.scnu.gpt.controller;

import com.scnu.gpt.entity.Course;
import com.scnu.gpt.entity.Section;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.CourseDetailDTO;
import com.scnu.gpt.pojo.course.CourseStatisticsDTO;
import com.scnu.gpt.service.ICourseService;
import com.scnu.gpt.service.IDoQuestionService;
import com.scnu.gpt.service.IFileService;
import com.scnu.gpt.service.ISectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ldw
 * @since 2025-06-09
 */
@RestController
@CrossOrigin
@RequestMapping("/course")
@Tag(name = "课程管理", description = "包括课程增删改...") // API分组
public class CourseController {
    @Autowired
    private ICourseService courseService;


    @Autowired
    private IFileService fileService;

    @Operation(
            summary = "查询单个课程详情",
            description = "如题")
    @PostMapping("/queryCourseDetail")
    public ApiResponse<CourseDetailDTO> queryCourse(@RequestBody String courseId) {
        try {
            return new ApiResponse<>("200","成功",courseService.queryCourseDetail(Integer.parseInt(courseId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "新增教师课程(归属当前教师Id)。返回课程新增后的自增Id,下同。",
            description = "如题")
    @PostMapping("/addCourse")
    public ApiResponse<Integer> addCourse() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            Course course = new Course();
            course.setUserId(Integer.parseInt(userId));
            return new ApiResponse<>("200","成功",courseService.addCourse(course));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }
    @Operation(
            summary = "删除课程",
            description = "如题")
    @PostMapping("/deleteCourse")
    public ApiResponse<Void> deleteCourse(@RequestBody String courseId) {
        try {
            courseService.deleteCourse(Integer.parseInt(courseId));
            return new ApiResponse<>("200","成功",null);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "更新课程(仅更新单个课程实体，不包括其下的章节。下同)",
            description = "如题",
            parameters = {
                    @Parameter(
                            name = "coverFile",
                            description = "课程封面文件",
                            schema = @Schema(type = "MultipartFile")
                    )
            })
    @PostMapping("/updateCourse")
    public ApiResponse<Void> updateCourse(@ModelAttribute Course course, // 绑定表单数据到对象
                                          @RequestParam(value = "coverFile",required = false) MultipartFile coverFile) {
        try {
            //封面文件上传
            if(coverFile!=null && !coverFile.isEmpty()){
                ArrayList<MultipartFile> fileArrayList = new ArrayList<>();
                fileArrayList.add(coverFile);
                String coverPath = fileService.uploadFile(fileArrayList).getFirst();
                course.setCoverPath(coverPath);
            }
            return courseService.updateCourse(course);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "查询当前登录教师名下的所有课程信息",
            description = "如题")
    @PostMapping("/queryCourseByTeacherId")
    public ApiResponse<ArrayList<CourseStatisticsDTO>> queryCourseByTeacherId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ApiResponse<>("200","成功",courseService.queryCourseByTeacherId(Integer.parseInt(userId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "通过章节id获取该章节及其所属课程的上下文信息",
            description = "如题")
    @PostMapping("/queryCourseDetailStr")
    public ApiResponse<String> queryCourseDetailStr(@RequestBody String sectionId) {
        try {
            return new ApiResponse<>("200","成功",courseService.queryCourseDetailStr(Integer.parseInt(sectionId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "查询所有课程基础信息（不包括其下的章节列表）",
            description = "如题")
    @PostMapping("/queryAllCourse")
    public ApiResponse<ArrayList<CourseStatisticsDTO>> queryAllCourse() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ApiResponse<>("200","成功",courseService.queryAllCourse(Integer.parseInt(userId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "登录学生加入课程",
            description = "如题")
    @PostMapping("/joinCourse")
    public ApiResponse<Null> joinCourse(@RequestBody String courseId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            courseService.joinCourse(Integer.parseInt(userId),Integer.parseInt(courseId));

            return new ApiResponse<>("200","成功",null);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

}
