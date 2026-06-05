package com.scnu.gpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnu.gpt.entity.Note;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 笔记 Mapper 接口
 * </p>
 *
 * @author Yusheng
 * @since 2025-06-04
 */
@Mapper
public interface NoteMapper extends BaseMapper<Note> {
}
