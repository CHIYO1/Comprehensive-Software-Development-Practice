package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnu.gpt.config.ApiConfig;
import com.scnu.gpt.entity.Course;
import com.scnu.gpt.entity.RecordSet;
import com.scnu.gpt.entity.Section;
import com.scnu.gpt.entity.Subsection;
import com.scnu.gpt.entity.User;
import com.scnu.gpt.mapper.*;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.*;
import com.scnu.gpt.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ldw
 * @since 2025-06-09
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private SectionMapper sectionMapper;
    @Autowired
    private SubsectionMapper subsectionMapper;
    @Autowired
    private KnowledgeMapper knowledgeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RecordSetMapper recordSetMapper;

    // Nginx上传地址
    private final String NGINX_UPLOAD_URL;

    public CourseServiceImpl(ApiConfig apiConfig) {
        this.NGINX_UPLOAD_URL = apiConfig.nginx().url();
    }
    @Override
    public int addCourse(Course course) {
        courseMapper.insert(course);
        return course.getCourseId();
    }

    @Override
    public ApiResponse<Void> updateCourse(Course course) {
        if (course.getCourseId()==null){
            return new ApiResponse<>("400","缺少courseId",null);
        }
        courseMapper.updateById(course);
        return new ApiResponse<>("200","成功",null);
    }

    @Override
    public CourseDetailDTO queryCourseDetail(int courseId) {
        //查询课程信息
        Course course = courseMapper.selectById(courseId);
        if(course.getCoverImage()!=null && !course.getCoverImage().isEmpty()){
            course.setCoverImage(NGINX_UPLOAD_URL+course.getCoverImage());
        }
        QueryWrapper<Section> queryWrapper = new QueryWrapper<Section>();
        queryWrapper.eq("course_id", courseId);
        ArrayList<Section> sections = (ArrayList<Section>)sectionMapper.selectList(queryWrapper);
        //查询并重构DTO
        ArrayList<SectionDTO> sectionDTOList = new ArrayList<>();
        for (Section section : sections) {
            //章节下小节
            QueryWrapper<Subsection> subsectionQueryWrapper = new QueryWrapper<Subsection>();
            subsectionQueryWrapper.eq("chapter_id", section.getChapterId());
            ArrayList<Subsection> subsections = (ArrayList<Subsection>)subsectionMapper.selectList(subsectionQueryWrapper);
            //章节相关知识点
            ArrayList<String> knowledges = (ArrayList<String>)knowledgeMapper.selectBySectionId(section.getChapterId());
            sectionDTOList.add(new SectionDTO(section,knowledges,subsections,false));
        }
        return new CourseDetailDTO(course,sectionDTOList);
    }

    @Override
    public ArrayList<CourseStatisticsDTO> queryCourseByTeacherId(int userId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", userId);
        ArrayList<Course> courses = (ArrayList<Course>)courseMapper.selectList(queryWrapper);
        ArrayList<CourseStatisticsDTO> courseStatisticsDTOList = new ArrayList<>();
        for (Course course : courses) {
            //统计学生人数
            courseStatisticsDTOList.add(getCourseStatistics(course,-1));
        }
        return courseStatisticsDTOList;
    }

    @Override
    public void deleteCourse(int courseId) {
        courseMapper.deleteById(courseId);
    }

    @Override
    public String queryCourseDetailStr(int sectionId) {
        Section section = sectionMapper.selectById(sectionId);
        Course course = courseMapper.selectById(section.getCourseId());
        ArrayList<String> knowledges = (ArrayList<String>)knowledgeMapper.selectBySectionId(section.getChapterId());
        String knowledgeListStr = "";
        if (knowledges != null && !knowledges.isEmpty()) {
            knowledgeListStr = knowledges.stream()
                    .map(item -> "'" + item + "'")
                    .collect(Collectors.joining("、"));
        }

        return """
    [{课程名称：%s},
    {课程描述：%s},
    {章节名称：%s},
    {章节描述：%s},
    {知识点列表：%s}]""".formatted(
                course.getCourseName(),
                course.getDescription(),
                section.getChapterName(),
                section.getChapterDescription(),
                knowledgeListStr
        );
    }

    @Override
    public ArrayList<CourseStatisticsDTO> queryAllCourse(int userId) {
        ArrayList<Course> courses = (ArrayList<Course>)courseMapper.selectList(null);
        ArrayList<CourseStatisticsDTO> courseStatisticsDTOList = new ArrayList<>();
        for (Course course : courses) {
            //统计学生人数
            courseStatisticsDTOList.add(getCourseStatistics(course,userId));
        }
        return courseStatisticsDTOList;
    }

    @Override
    public int dropCourse(int userId, int courseId) {
        return courseMapper.dropCourse(userId, courseId);
    }

    @Override
    public void joinCourse(int userId, int courseId) {
        courseMapper.joinCourse(userId,courseId);
        //获取课程下所有试题小节
        QueryWrapper<Section> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        ArrayList<Section> sections = (ArrayList<Section>)sectionMapper.selectList(queryWrapper);
        for (Section section : sections) {
            QueryWrapper<Subsection> subsectionQueryWrapper = new QueryWrapper<>();
            subsectionQueryWrapper.eq("chapter_id", section.getChapterId());
            ArrayList<Subsection> subsections = (ArrayList<Subsection>)subsectionMapper.selectList(subsectionQueryWrapper);
            for (Subsection subsection : subsections) {
                //为用户批量创建空做题记录
                if(subsection.getContentType().equals("questions")){
                    RecordSet recordSet = new RecordSet();
                    recordSet.setSubsectionId(subsection.getContentId());
                    recordSet.setSetId(subsection.getResourceId());
                    recordSet.setUserId(userId);
                    recordSet.setState("未完成");
                    recordSetMapper.insert(recordSet);
                }
            }
        }
    }

    //获取课程统计信息,若userId不为-1顺带查询登录用户是否参加了本课程
    private CourseStatisticsDTO getCourseStatistics(Course course,int userId) {
        int courseId = course.getCourseId();
        if(course.getCoverImage()!=null && !course.getCoverImage().isEmpty()){
            course.setCoverImage(NGINX_UPLOAD_URL+course.getCoverImage());
        }
        int studentNum = courseMapper.getStudentNum(courseId);
        int sectionNum = courseMapper.getSectionNum(courseId);
        String teacherName = userMapper.selectById(course.getTeacherId()).getUsername();
        if(userId<=0){
            return new CourseStatisticsDTO(course,studentNum,sectionNum,teacherName,0);
        }else{
            return new CourseStatisticsDTO(course,studentNum,sectionNum,teacherName,courseMapper.getIfJoin(courseId,userId));
        }
    }

    // === 以下为 /courses/all、/courses/detail、/students/my-courses 新增方法 ===

    @Override
    public CourseAllResponse getAllCoursesWithTeacher() {
        List<Course> courses = courseMapper.selectList(null);
        List<CourseCardDTO> cardList = new ArrayList<>();
        for (Course course : courses) {
            // 处理封面图片 URL
            String coverImage = (course.getCoverImage() != null && !course.getCoverImage().isEmpty())
                    ? NGINX_UPLOAD_URL + course.getCoverImage() : course.getCoverImage();

            // 查询教师信息
            User teacher = userMapper.selectById(course.getTeacherId());
            TeacherBriefDTO teacherDTO = new TeacherBriefDTO(
                    teacher != null ? teacher.getUserId() : null,
                    teacher != null ? teacher.getUsername() : null
            );

            // 统计选课人数
            int studentCount = courseMapper.getStudentNum(course.getCourseId());

            cardList.add(new CourseCardDTO(
                    course.getCourseId(),
                    course.getCourseName(),
                    course.getDescription(),
                    teacherDTO,
                    studentCount,
                    course.getStartDate(),
                    course.getWeeks(),
                    course.getCourseType(),
                    course.getDifficulty(),
                    course.getScore(),
                    coverImage
            ));
        }
        return new CourseAllResponse(cardList.size(), cardList);
    }

    @Override
    public CourseDetailResponse getCourseDetailNew(int courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }

        // 解析 keywords_json → List<String>
        List<String> keywords = new ArrayList<>();
        if (course.getKeywordsJson() != null && !course.getKeywordsJson().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                keywords = objectMapper.readValue(course.getKeywordsJson(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                // 解析失败时返回空列表
            }
        }

        // 查询章节列表
        QueryWrapper<Section> sectionQuery = new QueryWrapper<>();
        sectionQuery.eq("course_id", courseId);
        sectionQuery.orderByAsc("chapter_order");
        List<Section> sections = sectionMapper.selectList(sectionQuery);

        List<ChapterDTO> chapterList = new ArrayList<>();
        for (Section section : sections) {
            // 查询章节下的内容列表
            QueryWrapper<Subsection> subQuery = new QueryWrapper<>();
            subQuery.eq("chapter_id", section.getChapterId());
            subQuery.orderByAsc("content_order");
            List<Subsection> subsections = subsectionMapper.selectList(subQuery);

            List<ContentDTO> contentList = new ArrayList<>();
            for (Subsection sub : subsections) {
                contentList.add(new ContentDTO(
                        sub.getContentId(),
                        sub.getContentName(),
                        sub.getContentDescription(),
                        sub.getContentType(),
                        sub.getContentOrder(),
                        sub.getVideoUrl(),
                        sub.getDocumentUrl()
                ));
            }

            chapterList.add(new ChapterDTO(
                    section.getChapterId(),
                    section.getChapterName(),
                    section.getChapterDescription(),
                    section.getChapterOrder(),
                    contentList
            ));
        }

        return new CourseDetailResponse(
                course.getCourseId(),
                course.getCourseName(),
                course.getDescription(),
                keywords,
                chapterList
        );
    }

    @Override
    public MyCoursesResponse getMyCourses(int studentId) {
        List<Integer> courseIds = courseMapper.selectEnrolledCourseIds(studentId);
        List<MyCourseDTO> courseList = new ArrayList<>();

        for (Integer cid : courseIds) {
            Course course = courseMapper.selectById(cid);
            if (course == null) continue;

            // 封面图片
            String coverImage = (course.getCoverImage() != null && !course.getCoverImage().isEmpty())
                    ? NGINX_UPLOAD_URL + course.getCoverImage() : course.getCoverImage();

            // 教师名
            User teacher = userMapper.selectById(course.getTeacherId());
            String teacherName = teacher != null ? teacher.getUsername() : null;

            // 统计信息
            int studentCount = courseMapper.getStudentNum(cid);
            int totalLessons = courseMapper.countSubsectionsByCourseId(cid);
            int completedLessons = courseMapper.countCompletedSubsections(cid, studentId);
            int homeworkTotal = courseMapper.countHomeworkTotal(cid);
            int homeworkCompleted = courseMapper.countHomeworkCompleted(cid, studentId);
            int score = courseMapper.getStudentCourseScore(studentId, cid);

            // 计算结课日期: startDate + weeks * 7
            LocalDate endDate = null;
            if (course.getStartDate() != null && course.getWeeks() != null) {
                endDate = course.getStartDate().plusWeeks(course.getWeeks());
            }

            courseList.add(new MyCourseDTO(
                    course.getCourseId(),
                    course.getCourseName(),
                    course.getDescription(),
                    studentCount,
                    course.getStartDate(),
                    endDate,
                    teacherName,
                    new ProgressDTO(completedLessons, totalLessons),
                    new HomeworkDTO(homeworkCompleted, homeworkTotal),
                    score,
                    coverImage
            ));
        }

        return new MyCoursesResponse(courseList.size(), courseList);
    }

}
