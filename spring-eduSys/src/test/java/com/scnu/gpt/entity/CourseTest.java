package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Course 实体单元测试 — 验证 finalDB t_course 表字段对齐
 */
class CourseTest {

    // ==================== 字段存在性测试 ====================

    @Test
    void testAll14FieldsExist() {
        // finalDB t_course 表有 14 列，实体应有 14 个字段
        Field[] fields = Course.class.getDeclaredFields();
        assertEquals(14, fields.length,
                "Course 实体应有 14 个字段对应 finalDB t_course 表的 14 列");
    }

    @Test
    void testRenamedFieldsExist() throws NoSuchFieldException {
        // 验证三个重命名的字段存在
        assertNotNull(Course.class.getDeclaredField("description"),
                "description 字段应存在");
        assertNotNull(Course.class.getDeclaredField("coverImage"),
                "coverImage 字段应存在");
        assertNotNull(Course.class.getDeclaredField("teacherId"),
                "teacherId 字段应存在");
    }

    @Test
    void testOldFieldNamesDoNotExist() {
        // 验证旧字段名已不存在
        assertThrows(NoSuchFieldException.class,
                () -> Course.class.getDeclaredField("courseDesc"),
                "courseDesc 字段不应再存在");
        assertThrows(NoSuchFieldException.class,
                () -> Course.class.getDeclaredField("coverPath"),
                "coverPath 字段不应再存在");
        assertThrows(NoSuchFieldException.class,
                () -> Course.class.getDeclaredField("userId"),
                "userId 字段不应再存在");
    }

    @Test
    void testNewFieldsExist() throws NoSuchFieldException {
        // 验证 9 个新字段存在
        assertNotNull(Course.class.getDeclaredField("keywordsJson"));
        assertNotNull(Course.class.getDeclaredField("studentCount"));
        assertNotNull(Course.class.getDeclaredField("startDate"));
        assertNotNull(Course.class.getDeclaredField("weeks"));
        assertNotNull(Course.class.getDeclaredField("courseType"));
        assertNotNull(Course.class.getDeclaredField("difficulty"));
        assertNotNull(Course.class.getDeclaredField("score"));
        assertNotNull(Course.class.getDeclaredField("createdAt"));
        assertNotNull(Course.class.getDeclaredField("updatedAt"));
    }

    // ==================== 注解测试 ====================

    @Test
    void testTableNameAnnotation() {
        TableName tableName = Course.class.getAnnotation(TableName.class);
        assertNotNull(tableName, "应有 @TableName 注解");
        assertEquals("t_course", tableName.value(), "表名应为 t_course");
    }

    @Test
    void testCourseIdAnnotation() throws NoSuchFieldException {
        Field field = Course.class.getDeclaredField("courseId");
        TableId tableId = field.getAnnotation(TableId.class);
        assertNotNull(tableId, "courseId 应有 @TableId 注解");
        assertEquals("course_id", tableId.value(), "列名应为 course_id");
    }

    @Test
    void testTeacherIdAnnotation() throws NoSuchFieldException {
        Field field = Course.class.getDeclaredField("teacherId");
        TableField tableField = field.getAnnotation(TableField.class);
        assertNotNull(tableField, "teacherId 应有 @TableField 注解");
        assertEquals("teacher_id", tableField.value(), "列名应为 teacher_id");
        assertEquals(FieldStrategy.NOT_NULL, tableField.updateStrategy(),
                "teacherId updateStrategy 应为 NOT_NULL");
    }

    @Test
    void testCoverImageAnnotation() throws NoSuchFieldException {
        Field field = Course.class.getDeclaredField("coverImage");
        TableField tableField = field.getAnnotation(TableField.class);
        assertNotNull(tableField, "coverImage 应有 @TableField 注解");
        assertEquals("cover_image", tableField.value(), "列名应为 cover_image");
    }

    @Test
    void testTimestampFieldStrategies() throws NoSuchFieldException {
        // createdAt: updateStrategy = NEVER（防止 UPDATE 覆盖 DB 默认值）
        Field createdAtField = Course.class.getDeclaredField("createdAt");
        TableField createdAtAnno = createdAtField.getAnnotation(TableField.class);
        assertNotNull(createdAtAnno, "createdAt 应有 @TableField 注解");
        assertEquals(FieldStrategy.NEVER, createdAtAnno.updateStrategy(),
                "createdAt updateStrategy 应为 NEVER");

        // updatedAt: updateStrategy = NEVER（防止 UPDATE 覆盖 DB ON UPDATE）
        Field updatedAtField = Course.class.getDeclaredField("updatedAt");
        TableField updatedAtAnno = updatedAtField.getAnnotation(TableField.class);
        assertNotNull(updatedAtAnno, "updatedAt 应有 @TableField 注解");
        assertEquals(FieldStrategy.NEVER, updatedAtAnno.updateStrategy(),
                "updatedAt updateStrategy 应为 NEVER");
    }

    // ==================== Getter/Setter 功能测试 ====================

    @Test
    void testCourseNameGetterSetter() {
        Course course = new Course();
        assertEquals("", course.getCourseName(), "默认值应为空字符串");

        course.setCourseName("Python编程");
        assertEquals("Python编程", course.getCourseName());
    }

    @Test
    void testDescriptionGetterSetter() {
        Course course = new Course();
        assertEquals("", course.getDescription(), "默认值应为空字符串");

        course.setDescription("一门Python编程课程");
        assertEquals("一门Python编程课程", course.getDescription());
    }

    @Test
    void testTeacherIdGetterSetter() {
        Course course = new Course();
        assertNull(course.getTeacherId());

        course.setTeacherId(2024001);
        assertEquals(2024001, course.getTeacherId());
    }

    @Test
    void testCoverImageGetterSetter() {
        Course course = new Course();
        assertNull(course.getCoverImage());

        course.setCoverImage("/uploads/covers/python.jpg");
        assertEquals("/uploads/covers/python.jpg", course.getCoverImage());
    }

    @Test
    void testNewFieldsGetterSetter() {
        Course course = new Course();

        // keywordsJson
        course.setKeywordsJson("[\"Python\",\"编程\"]");
        assertEquals("[\"Python\",\"编程\"]", course.getKeywordsJson());

        // studentCount
        course.setStudentCount(30);
        assertEquals(30, course.getStudentCount());

        // startDate
        LocalDate date = LocalDate.of(2026, 3, 1);
        course.setStartDate(date);
        assertEquals(date, course.getStartDate());

        // weeks
        course.setWeeks(16);
        assertEquals(16, course.getWeeks());

        // courseType
        course.setCourseType("必修");
        assertEquals("必修", course.getCourseType());

        // difficulty
        course.setDifficulty(3);
        assertEquals(3, course.getDifficulty());

        // score
        BigDecimal score = new BigDecimal("4.5");
        course.setScore(score);
        assertEquals(score, course.getScore());

        // createdAt
        LocalDateTime now = LocalDateTime.now();
        course.setCreatedAt(now);
        assertEquals(now, course.getCreatedAt());

        // updatedAt
        course.setUpdatedAt(now);
        assertEquals(now, course.getUpdatedAt());
    }

    // ==================== 字段类型测试 ====================

    @Test
    void testFieldTypes() throws NoSuchFieldException {
        assertEquals(Integer.class, Course.class.getDeclaredField("courseId").getType());
        assertEquals(String.class, Course.class.getDeclaredField("courseName").getType());
        assertEquals(String.class, Course.class.getDeclaredField("description").getType());
        assertEquals(String.class, Course.class.getDeclaredField("keywordsJson").getType());
        assertEquals(Integer.class, Course.class.getDeclaredField("teacherId").getType());
        assertEquals(Integer.class, Course.class.getDeclaredField("studentCount").getType());
        assertEquals(LocalDate.class, Course.class.getDeclaredField("startDate").getType());
        assertEquals(Integer.class, Course.class.getDeclaredField("weeks").getType());
        assertEquals(String.class, Course.class.getDeclaredField("courseType").getType());
        assertEquals(Integer.class, Course.class.getDeclaredField("difficulty").getType());
        assertEquals(BigDecimal.class, Course.class.getDeclaredField("score").getType());
        assertEquals(String.class, Course.class.getDeclaredField("coverImage").getType());
        assertEquals(LocalDateTime.class, Course.class.getDeclaredField("createdAt").getType());
        assertEquals(LocalDateTime.class, Course.class.getDeclaredField("updatedAt").getType());
    }

    // ==================== 完整实体构建测试 ====================

    @Test
    void testFullCourseConstruction() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseName("Java高级编程");
        course.setDescription("深入学习Java核心技术");
        course.setKeywordsJson("[\"Java\",\"Spring\"]");
        course.setTeacherId(2024001);
        course.setStudentCount(25);
        course.setStartDate(LocalDate.of(2026, 9, 1));
        course.setWeeks(18);
        course.setCourseType("专业选修");
        course.setDifficulty(4);
        course.setScore(new BigDecimal("4.8"));
        course.setCoverImage("/covers/java.jpg");

        // 验证所有设置的值
        assertEquals(1, course.getCourseId());
        assertEquals("Java高级编程", course.getCourseName());
        assertEquals("深入学习Java核心技术", course.getDescription());
        assertEquals("[\"Java\",\"Spring\"]", course.getKeywordsJson());
        assertEquals(2024001, course.getTeacherId());
        assertEquals(25, course.getStudentCount());
        assertEquals(LocalDate.of(2026, 9, 1), course.getStartDate());
        assertEquals(18, course.getWeeks());
        assertEquals("专业选修", course.getCourseType());
        assertEquals(4, course.getDifficulty());
        assertEquals(new BigDecimal("4.8"), course.getScore());
        assertEquals("/covers/java.jpg", course.getCoverImage());
    }
}
