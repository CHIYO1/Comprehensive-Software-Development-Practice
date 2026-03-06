package com.scnu.gpt.controller;

import com.scnu.gpt.entity.Section;
import com.scnu.gpt.entity.Subsection;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.SectionDTO;
import com.scnu.gpt.service.ISectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ldw
 * @since 2025-06-17
 */
@RestController
@CrossOrigin
@Tag(name = "章节管理", description = "章节及小节的增删改查......")
@RequestMapping("/section")
public class SectionController {
    @Autowired
    private ISectionService sectionService;

    @Operation(
            summary = "新增章节",
            description = "如题")
    @PostMapping("/addSection")
    public ApiResponse<SectionDTO> addSection(@RequestBody Section section) {
        try {
            return new ApiResponse<>("200","成功",sectionService.addSection(section));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "更新章节（不包括小节）",
            description = "如题")
    @PostMapping("/updateSection")
    public ApiResponse<Void> updateSection(@RequestBody SectionDTO sectionDTO) {
        try {
            return sectionService.updateSection(sectionDTO);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "查询章节关联知识点",
            description = "如题")
    @PostMapping("/querySectionKnowledges")
    public ApiResponse<ArrayList<String>> querySectionKnowledges(@RequestBody String sectionId) {
        try {
            return new ApiResponse<>("200","成功",sectionService.querySectionKnowledges(Integer.parseInt(sectionId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "删除章节",
            description = "如题")
    @PostMapping("/deleteSection")
    public ApiResponse<Void> deleteSection(@RequestBody String sectionId) {
        try {
            return sectionService.deleteSection(Integer.parseInt(sectionId));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "新增小节",
            description = "如题")
    @PostMapping("/addSubsection")
    public ApiResponse<Subsection> addSubsection(@RequestBody Subsection subsection) {
        try {
            return new ApiResponse<>("200","成功",sectionService.addSubsection(subsection));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "更新小节",
            description = "如题")
    @PostMapping("/updateSubsection")
    public ApiResponse<Void> updateSubsection(@RequestBody Subsection subsection) {
        try {
            return sectionService.updateSubsection(subsection);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "删除小节",
            description = "如题")
    @PostMapping("/deleteSubsection")
    public ApiResponse<Void> deleteSubsection(@RequestBody String subsectionId) {
        try {
            return sectionService.deleteSubsection(subsectionId);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "查询小节",
            description = "如题")
    @PostMapping("/querySubSection")
    public ApiResponse<Subsection> querySubSection(@RequestBody String subsectionId) {
        try {
            return new ApiResponse<>("200","成功",sectionService.querySubSection(Integer.parseInt(subsectionId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }
}
