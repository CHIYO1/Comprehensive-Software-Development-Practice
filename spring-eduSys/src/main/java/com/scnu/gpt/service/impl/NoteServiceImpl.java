package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnu.gpt.entity.Note;
import com.scnu.gpt.mapper.NoteMapper;
import com.scnu.gpt.pojo.note.NoteRequest;
import com.scnu.gpt.service.INoteService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 笔记服务实现类
 * </p>
 *
 * @author Yusheng
 * @since 2025-06-04
 */
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements INoteService {

    private final NoteMapper noteMapper;

    public NoteServiceImpl(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    @Override
    public Note createNote(int studentId, NoteRequest request) {
        Note note = new Note();
        note.setStudentId(studentId);
        note.setCourseId(request.courseId());
        note.setTitle(request.title());
        note.setContent(request.content());
        noteMapper.insert(note);
        return note;
    }

    @Override
    public Map<String, Object> listNotes(int studentId, Integer courseId) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getStudentId, studentId);
        if (courseId != null) {
            wrapper.eq(Note::getCourseId, courseId);
        }
        wrapper.orderByDesc(Note::getUpdatedAt);

        List<Note> notes = noteMapper.selectList(wrapper);

        // 构建返回结构：note_id, title, content_preview（截取前200字符）
        List<Map<String, Object>> noteList = notes.stream().map(note -> {
            Map<String, Object> item = new HashMap<>();
            item.put("note_id", note.getNoteId());
            item.put("title", note.getTitle());
            String preview = note.getContent();
            if (preview != null && preview.length() > 200) {
                preview = preview.substring(0, 200) + "...";
            }
            item.put("content_preview", preview);
            return item;
        }).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", noteList.size());
        result.put("notes", noteList);
        return result;
    }

    @Override
    public Note updateNote(int studentId, NoteRequest request) {
        if (request.noteId() == null) {
            throw new IllegalArgumentException("更新笔记时noteId不能为空");
        }
        Note note = verifyOwner(studentId, request.noteId());
        if (request.title() != null) {
            note.setTitle(request.title());
        }
        if (request.content() != null) {
            note.setContent(request.content());
        }
        noteMapper.updateById(note);
        return note;
    }

    @Override
    public void deleteNote(int studentId, int noteId) {
        verifyOwner(studentId, noteId);
        noteMapper.deleteById(noteId);
    }

    /**
     * 校验笔记归属：笔记存在且属于当前学生，否则抛异常
     */
    private Note verifyOwner(int studentId, int noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new IllegalArgumentException("笔记不存在");
        }
        if (!Objects.equals(note.getStudentId(), studentId)) {
            throw new IllegalArgumentException("无权操作此笔记");
        }
        return note;
    }
}
