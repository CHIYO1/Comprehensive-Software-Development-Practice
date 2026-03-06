package com.scnu.gpt.controller;

import com.scnu.gpt.entity.Document;
import com.scnu.gpt.entity.Video;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.file.FileQueryDTO;
import com.scnu.gpt.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * @since 2025-06-03
 */
@RestController
@CrossOrigin
@RequestMapping("/file")
@Tag(name = "文件管理", description = "图片、视频、文档等文件的增删改") // API分组
public class FileController {
    @Autowired
    private IFileService fileService;

//    @Operation(
//            summary = "文件上传",
//            description = "如题") // 接口注解
//    @PostMapping("/uploadFile")
//    public ArrayList<String> uploadFile(@RequestBody ArrayList<MultipartFile> files) {
//        return  fileService.uploadFile(files);
//    }

    @Operation(
            summary = "教师端视频资源上传",
            description = "如题") // 接口注解
    @PostMapping("/teacher/uploadVideo")
    public ApiResponse<Video> uploadTeacherVideo(@RequestParam("video") MultipartFile video,
                                          @RequestParam("cover") MultipartFile cover) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return fileService.uploadTeacherVideo(video,cover,Integer.parseInt(userId));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "查询某位教师上传的所有视频资源",
            description = "如题") // 接口注解
    @PostMapping("/teacher/queryVideos")
    public ApiResponse<ArrayList<Video>> queryTeacherVideos() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ApiResponse<>("200","成功",fileService.queryTeacherVideos(Integer.parseInt(userId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "视频删除",
            description = "如题") // 接口注解
    @PostMapping("/deleteVideo")
    public ApiResponse<Void> deleteVideo(@RequestBody String videoId ) {
        try {
            return fileService.deleteVideo(Integer.parseInt(videoId));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }


    @Operation(
            summary = "教师端文档资源上传",
            description = "如题") // 接口注解
    @PostMapping("/teacher/uploadDocument")
    public ApiResponse<Document> uploadTeacherDocument(@RequestParam("document") MultipartFile document,
                                             @RequestParam("documentDesc") String documentDesc ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return fileService.uploadTeacherDocument(document,documentDesc,Integer.parseInt(userId));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }
    @Operation(
            summary = "查询某位教师上传的所有文档资源",
            description = "如题") // 接口注解
    @PostMapping("/teacher/queryDocuments")
    public ApiResponse<ArrayList<Document>> queryTeacherDocuments() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ApiResponse<>("200","成功",fileService.queryTeacherDocuments(Integer.parseInt(userId)));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "文档删除",
            description = "如题") // 接口注解
    @PostMapping("/deleteDocument")
    public ApiResponse<Void> deleteDocument(@RequestBody String documentId ) {
        try {
            return fileService.deleteDocument(Integer.parseInt(documentId));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "资源（文档、图片）查询。需提供文件类型和Id",
            description = "如题") // 接口注解
    @PostMapping("/queryFile")
    public ApiResponse<FileQueryDTO> queryFile(@RequestBody FileQueryDTO fileQueryDTO ) {
        try {
            return fileService.queryFile(fileQueryDTO);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }
}
