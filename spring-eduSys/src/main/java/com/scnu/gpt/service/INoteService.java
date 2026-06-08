package com.scnu.gpt.service;

import com.scnu.gpt.entity.Note;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnu.gpt.pojo.note.NoteRequest;

import java.util.Map;

/**
 * <p>
 * 笔记服务接口
 * </p>
 *
 * @author Yusheng
 * @since 2025-06-04
 */
public interface INoteService extends IService<Note> {

    /**
     * 创建笔记
     * @param studentId 当前登录学生ID
     * @param request   创建请求
     * @return 创建的笔记对象
     */
    Note createNote(int studentId, NoteRequest request);

    /**
     * 获取笔记列表
     * @param studentId 学生ID
     * @param courseId  课程ID（可选，为null时查全部）
     * @return 包含 total 和 notes 的 Map
     */
    Map<String, Object> listNotes(int studentId, Integer courseId);

    /**
     * 更新笔记（仅允许笔记所有者操作）
     * @param studentId 当前登录学生ID
     * @param request   更新请求
     * @return 更新后的笔记
     */
    Note updateNote(int studentId, NoteRequest request);

    /**
     * 删除笔记（仅允许笔记所有者操作）
     * @param studentId 当前登录学生ID
     * @param noteId    笔记ID
     */
    void deleteNote(int studentId, int noteId);
}
