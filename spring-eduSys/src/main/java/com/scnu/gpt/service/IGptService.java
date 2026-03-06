package com.scnu.gpt.service;


import com.scnu.gpt.entity.Question;
import com.scnu.gpt.entity.RecordQuestion;
import com.scnu.gpt.pojo.course.CourseDetailDTO;
import com.scnu.gpt.pojo.gpt.DifyBlockResponse;
import com.scnu.gpt.pojo.gpt.DifyRequest;
import com.scnu.gpt.pojo.gpt.DocumentGenerateRequest;
import com.scnu.gpt.pojo.gpt.QuestionGradeRequest;
import com.scnu.gpt.pojo.question.QuestionDTO;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ldw
 * @since 2025-06-23
 */
public interface IGptService {

    QuestionDTO questionGenerate(QuestionDTO questionDTO, String demand, String userId);

    Flux<String> documentGenerate(DocumentGenerateRequest documentGenerateRequest,String userId);


    CourseDetailDTO courseGenerate(MultipartFile file, String demand,String userId,int courseId);

    RecordQuestion gradeAnswers(Question question,RecordQuestion recordQuestio,String userId);
}
