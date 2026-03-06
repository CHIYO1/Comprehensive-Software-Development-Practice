package com.scnu.gpt.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.scnu.gpt.config.ApiConfig;
import com.scnu.gpt.entity.Course;
import com.scnu.gpt.entity.Question;
import com.scnu.gpt.entity.RecordQuestion;
import com.scnu.gpt.entity.Section;
import com.scnu.gpt.mapper.QuestionMapper;
import com.scnu.gpt.pojo.course.CourseDetailDTO;
import com.scnu.gpt.pojo.course.SectionDTO;
import com.scnu.gpt.pojo.gpt.*;
import com.scnu.gpt.pojo.question.QuestionDTO;
import com.scnu.gpt.service.IGptService;
import com.scnu.gpt.util.CodeUtil;
import com.scnu.gpt.util.DifyApiUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Service
public class GptServiceImpl implements IGptService {
    private static final Logger log = LoggerFactory.getLogger(GptServiceImpl.class);
    //各任务类型的智能体key
    private final String DIFY_KEY_DocumentGenerate;     //教案等文档生成
    private final String DIFY_KEY_QuestionGenerate;     //试题生成补全
    private final String DIFY_KEY_CourseGenerate;       //课程架构生成
    private final String DIFY_KEY_QuestionGrade;       //试题智能评分

    private final DifyApiUtil difyApiUtil;
    private final CodeUtil codeUtil;

    public GptServiceImpl(ApiConfig apiConfig, DifyApiUtil difyApiUtil, CodeUtil codeUtil) {
        this.DIFY_KEY_DocumentGenerate = apiConfig.dify().keyDocumentGenerate();
        this.DIFY_KEY_QuestionGenerate = apiConfig.dify().keyQuestionGenerate();
        this.DIFY_KEY_CourseGenerate = apiConfig.dify().keyCourseGenerate();
        this.DIFY_KEY_QuestionGrade = apiConfig.dify().keyQuestionGrade();
        this.difyApiUtil = difyApiUtil;
        this.codeUtil = codeUtil;
    }
    @Getter
    public enum ResponseType {//响应模式
        BLOCK("blocking"), //阻塞。（全部处理完后返回完整结果）
        STREAM("streaming");   //流式。
        private final String value;
        ResponseType(String value) {
            this.value = value;
        }
    }

    @Override
    public QuestionDTO questionGenerate(QuestionDTO questionDTO,String demand,String userId){
        String questionId = questionDTO.questionId();
        String questionType = questionDTO.questionType();
        ArrayList<String> knowledgeList = questionDTO.knowledgeList();
        ArrayList<String> choiceList = questionDTO.choiceList();
        String setId = questionDTO.setId();
        //构造dify请求体
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("demand", demand);
        inputs.put("questionStem", questionDTO.questionStem());
        inputs.put("questionAnswer", questionDTO.questionAnswer());
        inputs.put("questionParse", questionDTO.questionParse());
        inputs.put("questionType", questionType);

        StringBuilder sb = new StringBuilder();//将Knowledge字符串数组拼接成单个字符串以对接dify参数类型(dify接收不了数组)
        if(knowledgeList != null) {
            for (int i = 0; i < knowledgeList.size(); i++) {
                if (i > 0) sb.append("、");
                sb.append(String.format("'%s'", knowledgeList.get(i)));
            }
        }
        inputs.put("knowledges",sb.toString());
        if(questionType.equals("选择题")){//选项数组也要拼接成单个字符串
            sb = new StringBuilder();
            if(choiceList != null){
                char prefix = 'A';
                for (int i = 0; i < choiceList.size(); i++) {
                    if (i > 0) {
                        sb.append("、");
                    }
                    sb.append(String.format("'%s、%s'", (char)(prefix + i), choiceList.get(i)));
                }
            }
            inputs.put("questionChoices",sb.toString());
        }else {
            inputs.put("questionChoices","");
        }
        DifyRequest<Map<String, Object>> request = new DifyRequest<>(inputs, ResponseType.BLOCK.getValue(), userId);
        DifyBlockResponse response = difyApiUtil.getDifyBlockResponse(request,DIFY_KEY_QuestionGenerate);
        JsonNode jsonOutput = response.data().outputs().jsonOutput();
        //解析知识点、选项数组
        ObjectMapper mapper = new ObjectMapper();
        JsonNode knowledgeNode = jsonOutput.get("knowledges");
        knowledgeList = mapper.convertValue(knowledgeNode, new TypeReference<>() {
        });
        ArrayList<String> questionChoices = new ArrayList<>();
        String codeOutput="";   //期望输出
        if (questionType.equals("选择题")) {//读取选项字符串数组
            JsonNode choiceNode = jsonOutput.get("questionChoices");
            questionChoices = mapper.convertValue(choiceNode,
                    new TypeReference<>() {
                    });
        }
        else if (questionType.equals("编程题")) {
            codeOutput = codeUtil.runCode(jsonOutput.get("questionAnswer").asText()).data();
        }
        return new QuestionDTO(questionId,
                jsonOutput.get("questionStem").asText(),
                jsonOutput.get("questionAnswer").asText(),
                jsonOutput.get("questionParse").asText(),
                codeOutput,
                questionType,setId,questionChoices,knowledgeList,true
        );
    }

    @Override
    public Flux<String> documentGenerate(DocumentGenerateRequest documentGenerateRequest,String userId) {
        DifyRequest<DocumentGenerateRequest> difyRequest = new DifyRequest<>(documentGenerateRequest,ResponseType.STREAM.getValue(), userId);
        return difyApiUtil.streamDifyWorkflow(difyRequest,DIFY_KEY_DocumentGenerate);
    }

    @Override
    public CourseDetailDTO courseGenerate(MultipartFile file, String demand,String userId,int courseId) {
        String fileId = difyApiUtil.uploadFile(file,DIFY_KEY_CourseGenerate);
        //构造文件参数
        DifyFileInput difyFileInput = new DifyFileInput(fileId,"local_file","document");
        ArrayList<DifyFileInput> files = new ArrayList<>();
        files.add(difyFileInput);
        //构造dify工作流请求体
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("demand", demand);
        inputs.put("outlineFile", files);
        DifyRequest<Map<String, Object>> request = new DifyRequest<>(inputs, ResponseType.BLOCK.getValue(), userId);
        DifyBlockResponse response = difyApiUtil.getDifyBlockResponse(request,DIFY_KEY_CourseGenerate);
        JsonNode jsonOutput = response.data().outputs().jsonOutput();
        //解析返回值
        ObjectMapper mapper = new ObjectMapper();
        Course course = new Course();
        course.setCourseId(courseId);
        ArrayList<SectionDTO> sections = new ArrayList<>();

        course.setCourseName(jsonOutput.get("courseName").asText());
        course.setCourseDesc(jsonOutput.get("courseDesc").asText());
        ArrayNode arrayNode = (ArrayNode) jsonOutput.get("sectionList");
        for (JsonNode element : arrayNode) {
            Section section = new Section();
            section.setCourseId(courseId);
            section.setSectionName(element.get("sectionName").asText());
            section.setSectionDesc(element.get("sectionDesc").asText());
            ArrayList<String> knowledges = mapper.convertValue(element.get("knowledgeList"),
                    new TypeReference<>() {
                    });
            sections.add(new SectionDTO(section,knowledges,new ArrayList<>(),false));
        }
        return new CourseDetailDTO(course,sections);
    }

    //主观题、编程题智能评分
    @Override
    public RecordQuestion gradeAnswers(Question question, RecordQuestion recordQuestion,String userId) {
        //构造dify工作流请求体
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("questionStem", question.getQuestionStem());
        inputs.put("questionAnswer", question.getQuestionAnswer());
        inputs.put("studentAnswer", recordQuestion.getStudentAnswer());
        inputs.put("questionParse", question.getQuestionParse());
        inputs.put("questionType", question.getQuestionType());
        String studentOutput = "";  //程序运行结果
        if(question.getQuestionType().equals("编程题")){//编程题还要获取代码的运行结果
            studentOutput = codeUtil.runCode(question.getQuestionAnswer()).data();
            inputs.put("codeOutput", question.getCodeOutput());
            inputs.put("studentOutput", studentOutput);
        }
        DifyRequest<Map<String, Object>> request = new DifyRequest<>(inputs, ResponseType.BLOCK.getValue(), userId);
        DifyBlockResponse response = difyApiUtil.getDifyBlockResponse(request,DIFY_KEY_QuestionGrade);
        JsonNode jsonOutput = response.data().outputs().jsonOutput();
        //解析返回值
        recordQuestion.setQuestionScore(jsonOutput.get("questionScore").asInt());
        recordQuestion.setScoreParse(jsonOutput.get("scoreParse").asText());
        recordQuestion.setErrorParse(jsonOutput.get("errorParse").asText());
        recordQuestion.setStudentOutput(studentOutput);
        return recordQuestion;
    }
}
