package com.scnu.gpt.controller;

import com.scnu.gpt.entity.Note;
import com.scnu.gpt.entity.User;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.note.NoteRequest;
import com.scnu.gpt.service.INoteService;
import com.scnu.gpt.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 笔记控制器 — 创建、列表、更新、删除
 * </p>
 *
 * @author Yusheng
 * @since 2025-06-04
 */
@RestController
@CrossOrigin
@RequestMapping("/notes")
@Tag(name = "笔记管理", description = "学生笔记的增删改查")
public class NoteController {

    private final INoteService noteService;
    private final IUserService userService;

    public NoteController(INoteService noteService, IUserService userService) {
        this.noteService = noteService;
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
     * 创建笔记
     */
    @Operation(summary = "创建笔记", description = "为当前登录学生创建一条笔记")
    @PostMapping("/create")
    public ApiResponse<Note> createNote(@RequestBody NoteRequest request) {
        try {
            int studentId = getCurrentUserId();
            Note note = noteService.createNote(studentId, request);
            return new ApiResponse<>("200", "笔记创建成功", note);
        } catch (IllegalArgumentException e) {
            System.out.println("创建笔记失败: " + e.getMessage());
            return new ApiResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("创建笔记异常: " + e.getMessage());
            return new ApiResponse<>("500", "创建失败，请稍后重试", null);
        }
    }

    /**
     * 获取笔记列表
     */
    @Operation(summary = "获取笔记列表", description = "获取当前学生的笔记列表，可按课程筛选")
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> listNotes(
            @RequestParam(value = "course_id", required = false) Integer courseId) {
        try {
            int studentId = getCurrentUserId();
            Map<String, Object> result = noteService.listNotes(studentId, courseId);
            return new ApiResponse<>("200", "获取成功", result);
        } catch (IllegalArgumentException e) {
            System.out.println("获取笔记列表失败: " + e.getMessage());
            return new ApiResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("获取笔记列表异常: " + e.getMessage());
            return new ApiResponse<>("500", "获取失败，请稍后重试", null);
        }
    }

    /**
     * 更新笔记
     */
    @Operation(summary = "更新笔记", description = "更新笔记标题或内容，仅笔记所有者可操作")
    @PutMapping("/update")
    public ApiResponse<Note> updateNote(@RequestBody NoteRequest request) {
        try {
            int studentId = getCurrentUserId();
            Note note = noteService.updateNote(studentId, request);
            return new ApiResponse<>("200", "笔记更新成功", note);
        } catch (IllegalArgumentException e) {
            System.out.println("更新笔记失败: " + e.getMessage());
            return new ApiResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("更新笔记异常: " + e.getMessage());
            return new ApiResponse<>("500", "更新失败，请稍后重试", null);
        }
    }

    /**
     * 删除笔记
     */
    @Operation(summary = "删除笔记", description = "删除指定笔记，仅笔记所有者可操作")
    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteNote(@RequestParam("note_id") Integer noteId) {
        try {
            int studentId = getCurrentUserId();
            noteService.deleteNote(studentId, noteId);
            return new ApiResponse<>("200", "笔记删除成功", null);
        } catch (IllegalArgumentException e) {
            System.out.println("删除笔记失败: " + e.getMessage());
            return new ApiResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("删除笔记异常: " + e.getMessage());
            return new ApiResponse<>("500", "删除失败，请稍后重试", null);
        }
    }
}
