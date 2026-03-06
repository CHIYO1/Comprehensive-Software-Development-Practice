package com.scnu.gpt.controller;

import com.scnu.gpt.entity.Question;
import com.scnu.gpt.entity.RecordQuestion;
import com.scnu.gpt.entity.RecordSet;
import com.scnu.gpt.entity.Section;
import com.scnu.gpt.mapper.CourseMapper;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.course.CourseDetailDTO;
import com.scnu.gpt.pojo.course.SectionDTO;
import com.scnu.gpt.pojo.gpt.DocumentGenerateRequest;
import com.scnu.gpt.pojo.gpt.QuestionGenerateRequest;
import com.scnu.gpt.pojo.gpt.QuestionGradeRequest;
import com.scnu.gpt.pojo.question.QuestionDTO;
import com.scnu.gpt.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.ArrayList;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ldw
 * @since 2025-06-23
 */
@RestController
@CrossOrigin
@RequestMapping("/gpt")
@Tag(name = "gpt操作", description = "访问gpt进行试题生成、教案生成....") // API分组
public class GptController {
    private final IGptService gptService;
    private final ISectionService sectionService;
    private final ICourseService courseService;
    private final IQuestionService questionService;
    private final IDoQuestionService doQuestionService;
    // 构造函数依赖注入
    public GptController(IGptService gptService, ISectionService sectionService,
                         ICourseService courseService, IQuestionService questionService,
                        IDoQuestionService doQuestionService) {
        this.gptService = gptService;
        this.sectionService = sectionService;
        this.courseService = courseService;
        this.questionService = questionService;
        this.doQuestionService = doQuestionService;
    }

    @Operation(
            summary = "试题生成",
            description = "参考输入的试题非空信息，补充完整其它空字段")
    @PostMapping("/questionGenerate")
    public ApiResponse<QuestionDTO> questionGenerate(@RequestBody QuestionGenerateRequest questionGenerateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            QuestionDTO resultQuestion = gptService.questionGenerate(questionGenerateRequest.questionDTO(),questionGenerateRequest.demand(),userId);
            return new ApiResponse<>("200","生成成功",resultQuestion);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "文档生成",
            description = "流式文档生成")
    @PostMapping("/documentGenerate")
    public Flux<String> documentGenerate(@RequestBody DocumentGenerateRequest documentGenerateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return gptService.documentGenerate(documentGenerateRequest,userId);
    }

    @Operation(
            summary = "根据课程提纲文件（doc、pdf）及用户要求生成课程架构)",
            description = "包括课程名、描述，章节名、描述和知识点。不包括小节")
    @PostMapping("/courseGenerate")
    public ApiResponse<CourseDetailDTO> courseGenerate(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("demand") String demand,
                                                       @RequestParam("courseId") String courseId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            CourseDetailDTO courseDetailDTO = gptService.courseGenerate(file,demand,userId,Integer.parseInt(courseId));
            //持久化更新
            courseService.updateCourse(courseDetailDTO.course());
            for (SectionDTO sectionDTO : courseDetailDTO.sectionList()){
                sectionService.addSection(sectionDTO.section());
                sectionService.addSectionKnowledge(sectionDTO.section().getSectionId(),sectionDTO.knowledgeList());
            }
            return new ApiResponse<>("200","上传成功",courseDetailDTO);
        }catch (Exception e){
            System.out.println("上传失败："+e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }

    @Operation(
            summary = "给做题记录对应的试题集下的多道题目答案智能打分",
            description = "如题") // 接口注解
    @PostMapping("/gradeAnswers")
    public ApiResponse<Void> gradeAnswers(@RequestBody ArrayList<RecordQuestion> recordQuestionList) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            //获取做题记录并修改状态
            RecordSet recordSet = doQuestionService.queryRecordSet(recordQuestionList.getFirst().getSetRecordId());
            recordSet.setState("评卷中");
            doQuestionService.updateRecordSet(recordSet);
            int score = 0;
            int i=0;
            for(RecordQuestion recordQuestion:recordQuestionList){
                Question question = questionService.queryQuestion(recordQuestion.getQuestionId()).data();
                if(question.getQuestionType().equals( "选择题")){//客观题直接评分
                    if (question.getQuestionAnswer().equals(recordQuestion.getStudentAnswer())){
                        recordQuestion.setQuestionScore(100);
                    }else {
                        recordQuestion.setQuestionScore(0);
                    }
                    recordQuestion.setErrorParse(question.getQuestionParse());
                }else{//主观题、编程题智能打分(获取分数以及评分依据、错误分析)
                    recordQuestion = gptService.gradeAnswers(question,recordQuestion,userId);
                }
                //持久化单个题目记录
                doQuestionService.addRecoderQuestion(recordQuestion);
                score+=recordQuestion.getQuestionScore();
                i++;
            }
            //修改做题记录状态及得分
            recordSet = doQuestionService.queryRecordSet(recordQuestionList.getFirst().getSetRecordId());
            recordSet.setState("已评分");
            recordSet.setSetScore(score/i);
            doQuestionService.updateRecordSet(recordSet);

            return new ApiResponse<>("200","成功",null);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ApiResponse<>("500","未知错误"+e.getMessage(),null);
        }
    }
}
