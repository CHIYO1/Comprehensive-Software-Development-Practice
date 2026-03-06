package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnu.gpt.config.ApiConfig;
import com.scnu.gpt.entity.Course;
import com.scnu.gpt.entity.RecordSet;
import com.scnu.gpt.entity.Section;
import com.scnu.gpt.entity.Subsection;
import com.scnu.gpt.mapper.*;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.CourseDetailDTO;
import com.scnu.gpt.pojo.course.CourseStatisticsDTO;
import com.scnu.gpt.pojo.course.SectionDTO;
import com.scnu.gpt.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(course.getCoverPath()!=null && !course.getCoverPath().isEmpty()){
            course.setCoverPath(NGINX_UPLOAD_URL+course.getCoverPath());
        }
        QueryWrapper<Section> queryWrapper = new QueryWrapper<Section>();
        queryWrapper.eq("course_id", courseId);
        ArrayList<Section> sections = (ArrayList<Section>)sectionMapper.selectList(queryWrapper);
        //查询并重构DTO
        ArrayList<SectionDTO> sectionDTOList = new ArrayList<>();
        for (Section section : sections) {
            //章节下小节
            QueryWrapper<Subsection> subsectionQueryWrapper = new QueryWrapper<Subsection>();
            subsectionQueryWrapper.eq("section_id", section.getSectionId());
            ArrayList<Subsection> subsections = (ArrayList<Subsection>)subsectionMapper.selectList(subsectionQueryWrapper);
            //章节相关知识点
            ArrayList<String> knowledges = (ArrayList<String>)knowledgeMapper.selectBySectionId(section.getSectionId());
            sectionDTOList.add(new SectionDTO(section,knowledges,subsections,false));
        }
        return new CourseDetailDTO(course,sectionDTOList);
    }

    @Override
    public ArrayList<CourseStatisticsDTO> queryCourseByTeacherId(int userId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
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
        ArrayList<String> knowledges = (ArrayList<String>)knowledgeMapper.selectBySectionId(section.getSectionId());
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
                course.getCourseDesc(),
                section.getSectionName(),
                section.getSectionDesc(),
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
    public void joinCourse(int userId, int courseId) {
        courseMapper.joinCourse(userId,courseId);
        //获取课程下所有试题小节
        QueryWrapper<Section> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        ArrayList<Section> sections = (ArrayList<Section>)sectionMapper.selectList(queryWrapper);
        for (Section section : sections) {
            QueryWrapper<Subsection> subsectionQueryWrapper = new QueryWrapper<>();
            subsectionQueryWrapper.eq("section_id", section.getSectionId());
            ArrayList<Subsection> subsections = (ArrayList<Subsection>)subsectionMapper.selectList(subsectionQueryWrapper);
            for (Subsection subsection : subsections) {
                //为用户批量创建空做题记录
                if(subsection.getSubsectionType().equals("questions")){
                    RecordSet recordSet = new RecordSet();
                    recordSet.setSubsectionId(subsection.getSubsectionId());
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
        if(course.getCoverPath()!=null && !course.getCoverPath().isEmpty()){
            course.setCoverPath(NGINX_UPLOAD_URL+course.getCoverPath());
        }
        int studentNum = courseMapper.getStudentNum(courseId);
        int sectionNum = courseMapper.getSectionNum(courseId);
        String teacherName = userMapper.selectById(course.getUserId()).getUserName();
        if(userId<=0){
            return new CourseStatisticsDTO(course,studentNum,sectionNum,teacherName,0);
        }else{
            return new CourseStatisticsDTO(course,studentNum,sectionNum,teacherName,courseMapper.getIfJoin(courseId,userId));
        }
    }

}
