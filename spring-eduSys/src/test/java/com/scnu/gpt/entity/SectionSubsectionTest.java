package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Section & Subsection 实体单元测试 — 验证 finalDB t_chapter / t_chapter_content 对齐
 */
class SectionSubsectionTest {

    // ==================== Section (t_chapter) 测试 ====================

    @Test
    void testSectionTableName() {
        TableName tableName = Section.class.getAnnotation(TableName.class);
        assertNotNull(tableName, "Section 应有 @TableName 注解");
        assertEquals("t_chapter", tableName.value(), "表名应为 t_chapter");
    }

    @Test
    void testSectionFieldsExist() throws NoSuchFieldException {
        assertNotNull(Section.class.getDeclaredField("chapterId"));
        assertNotNull(Section.class.getDeclaredField("chapterName"));
        assertNotNull(Section.class.getDeclaredField("chapterDescription"));
        assertNotNull(Section.class.getDeclaredField("courseId"));
        assertNotNull(Section.class.getDeclaredField("chapterOrder"));
    }

    @Test
    void testSectionOldFieldsDoNotExist() {
        assertThrows(NoSuchFieldException.class,
                () -> Section.class.getDeclaredField("sectionId"));
        assertThrows(NoSuchFieldException.class,
                () -> Section.class.getDeclaredField("sectionName"));
        assertThrows(NoSuchFieldException.class,
                () -> Section.class.getDeclaredField("sectionDesc"));
    }

    @Test
    void testSectionFieldCount() {
        Field[] fields = Section.class.getDeclaredFields();
        assertEquals(5, fields.length, "Section 应有 5 个字段");
    }

    @Test
    void testSectionGettersAndSetters() {
        Section section = new Section();
        assertEquals("", section.getChapterName(), "chapterName 默认应为空字符串");

        section.setChapterId(1);
        section.setChapterName("第一章");
        section.setChapterDescription("Java基础入门");
        section.setCourseId(10);
        section.setChapterOrder(1);

        assertEquals(1, section.getChapterId());
        assertEquals("第一章", section.getChapterName());
        assertEquals("Java基础入门", section.getChapterDescription());
        assertEquals(10, section.getCourseId());
        assertEquals(1, section.getChapterOrder());
    }

    // ==================== Subsection (t_chapter_content) 测试 ====================

    @Test
    void testSubsectionTableName() {
        TableName tableName = Subsection.class.getAnnotation(TableName.class);
        assertNotNull(tableName, "Subsection 应有 @TableName 注解");
        assertEquals("t_chapter_content", tableName.value(), "表名应为 t_chapter_content");
    }

    @Test
    void testSubsectionFieldsExist() throws NoSuchFieldException {
        assertNotNull(Subsection.class.getDeclaredField("contentId"));
        assertNotNull(Subsection.class.getDeclaredField("contentName"));
        assertNotNull(Subsection.class.getDeclaredField("contentDescription"));
        assertNotNull(Subsection.class.getDeclaredField("contentType"));
        assertNotNull(Subsection.class.getDeclaredField("resourceId"));
        assertNotNull(Subsection.class.getDeclaredField("chapterId"));
        assertNotNull(Subsection.class.getDeclaredField("contentOrder"));
        assertNotNull(Subsection.class.getDeclaredField("videoUrl"));
        assertNotNull(Subsection.class.getDeclaredField("documentUrl"));
    }

    @Test
    void testSubsectionOldFieldsDoNotExist() {
        assertThrows(NoSuchFieldException.class,
                () -> Subsection.class.getDeclaredField("subsectionId"));
        assertThrows(NoSuchFieldException.class,
                () -> Subsection.class.getDeclaredField("subsectionName"));
        assertThrows(NoSuchFieldException.class,
                () -> Subsection.class.getDeclaredField("subsectionDesc"));
        assertThrows(NoSuchFieldException.class,
                () -> Subsection.class.getDeclaredField("subsectionType"));
        assertThrows(NoSuchFieldException.class,
                () -> Subsection.class.getDeclaredField("sectionId"));
    }

    @Test
    void testSubsectionFieldCount() {
        Field[] fields = Subsection.class.getDeclaredFields();
        assertEquals(9, fields.length, "Subsection 应有 9 个字段");
    }

    @Test
    void testResourceIdNotMappedToDb() throws NoSuchFieldException {
        Field field = Subsection.class.getDeclaredField("resourceId");
        TableField tableField = field.getAnnotation(TableField.class);
        assertNotNull(tableField, "resourceId 应有 @TableField 注解");
        assertFalse(tableField.exist(), "resourceId 应标记为 exist=false");
    }

    @Test
    void testSubsectionGettersAndSetters() {
        Subsection sub = new Subsection();
        assertEquals("", sub.getContentName(), "contentName 默认应为空字符串");

        sub.setContentId(1);
        sub.setContentName("1.1 Java概述");
        sub.setContentDescription("Java语言简介");
        sub.setContentType("video");
        sub.setResourceId(100);
        sub.setChapterId(5);
        sub.setContentOrder(1);
        sub.setVideoUrl("/videos/java-intro.mp4");
        sub.setDocumentUrl(null);

        assertEquals(1, sub.getContentId());
        assertEquals("1.1 Java概述", sub.getContentName());
        assertEquals("Java语言简介", sub.getContentDescription());
        assertEquals("video", sub.getContentType());
        assertEquals(100, sub.getResourceId());
        assertEquals(5, sub.getChapterId());
        assertEquals(1, sub.getContentOrder());
        assertEquals("/videos/java-intro.mp4", sub.getVideoUrl());
        assertNull(sub.getDocumentUrl());
    }
}
