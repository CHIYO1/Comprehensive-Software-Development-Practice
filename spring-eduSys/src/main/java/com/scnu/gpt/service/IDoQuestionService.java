package com.scnu.gpt.service;

import com.scnu.gpt.entity.RecordQuestion;
import com.scnu.gpt.entity.RecordSet;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.question.RecordQuestionDTO;

import java.util.ArrayList;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ldw
 * @since 2025-07-03
 */
public interface IDoQuestionService {
    ApiResponse<String> runCode(String code);

    String addRecoderSet(RecordSet recordSet);

    String addRecoderQuestion(RecordQuestion recordQuestion);

    RecordSet queryRecordSet(int setRecordId);

    void updateRecordSet(RecordSet recordSet);


    RecordSet queryRecordSetBySubsection(int i, int i1);

    ArrayList<RecordQuestionDTO> queryRecordQuestions(int setRecordId);
}
