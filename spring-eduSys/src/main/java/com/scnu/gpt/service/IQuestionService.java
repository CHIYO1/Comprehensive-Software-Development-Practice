package com.scnu.gpt.service;

import com.scnu.gpt.entity.Question;
import com.scnu.gpt.entity.QuestionSet;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.question.QuestionDTO;
import com.scnu.gpt.pojo.question.QuestionSetDTO;

import java.util.ArrayList;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ldw
 * @since 2025-06-12
 */
public interface IQuestionService extends IService<QuestionSet> {

    String addSet(Integer userId);

    ArrayList<QuestionSetDTO> querySets(int userId);

    QuestionSet querySet(int setId);

    QuestionDTO addQuestion(Question question);

    ArrayList<QuestionDTO> queryQuestions(int setId);

    ApiResponse<Void> deleteQuestion(int questionId);

    ApiResponse<Void> updateQuestion(QuestionDTO questionDTO);

    ApiResponse<Void> updateSet(QuestionSet questionSet);

    ApiResponse<Void> deleteSet(int setId);

    ApiResponse<Question> queryQuestion(int questionId);
}
