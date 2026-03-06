package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnu.gpt.entity.Question;
import com.scnu.gpt.entity.RecordQuestion;
import com.scnu.gpt.entity.RecordSet;
import com.scnu.gpt.mapper.KnowledgeMapper;
import com.scnu.gpt.mapper.QuestionMapper;
import com.scnu.gpt.mapper.RecordQuestionMapper;
import com.scnu.gpt.mapper.RecordSetMapper;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.question.RecordQuestionDTO;
import com.scnu.gpt.service.IDoQuestionService;
import com.scnu.gpt.util.CodeUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ldw
 * @since 2025-07-03
 */
@Service
public class DoQuestionServiceImpl  implements IDoQuestionService {
    private final RecordQuestionMapper recordQuestionMapper;
    private final RecordSetMapper recordSetMapper;
    private final  CodeUtil codeUtil;
    private final QuestionMapper questionMapper;
    private final KnowledgeMapper knowledgeMapper;
    // 构造函数依赖注入
    public DoQuestionServiceImpl(RecordQuestionMapper recordQuestionMapper, RecordSetMapper recordSetMapper, QuestionMapper questionMapper, CodeUtil codeUtil, KnowledgeMapper knowledgeMapper) {
        this.recordQuestionMapper = recordQuestionMapper;
        this.recordSetMapper = recordSetMapper;
        this.codeUtil = codeUtil;
        this.questionMapper = questionMapper;
        this.knowledgeMapper = knowledgeMapper;
    }
    @Override
    public ApiResponse<String> runCode(String code) {
        return  codeUtil.runCode(code);
    }

    @Override
    public String addRecoderSet(RecordSet recordSet) {
        recordSetMapper.insert(recordSet);
        return recordSet.getSetRecordId().toString();
    }

    @Override
    public String addRecoderQuestion(RecordQuestion recordQuestion) {
        recordQuestionMapper.insert(recordQuestion);
        return recordQuestion.getQuestionRecordId().toString();
    }

    @Override
    public RecordSet queryRecordSet(int setRecordId){
        return recordSetMapper.selectById(setRecordId);

    }

    @Override
    public void updateRecordSet(RecordSet recordSet){
        recordSetMapper.updateById(recordSet);
    }

    @Override
    public RecordSet queryRecordSetBySubsection(int subsectionId, int userId) {
        QueryWrapper<RecordSet> wrapper = new QueryWrapper<>();
        wrapper.eq("subsection_id", subsectionId);
        wrapper.eq("user_id", userId);
        return recordSetMapper.selectOne(wrapper);
    }

    @Override
    public ArrayList<RecordQuestionDTO> queryRecordQuestions(int setRecordId) {
        QueryWrapper<RecordQuestion> wrapper = new QueryWrapper<>();
        wrapper.eq("set_record_id", setRecordId);
        ArrayList<RecordQuestion> recordQuestions = (ArrayList<RecordQuestion>) recordQuestionMapper.selectList(wrapper);
        ArrayList<RecordQuestionDTO> recordQuestionDTOS = new ArrayList<>();
        //构造DTO
        for (RecordQuestion recordQuestion : recordQuestions) {
            Question question = questionMapper.selectById(recordQuestion.getQuestionId());
            //获取知识点
            ArrayList<String> knowledges = (ArrayList<String>)knowledgeMapper.selectByQuestionId(question.getQuestionId());
            recordQuestionDTOS.add(RecordQuestionDTO.fromEntities(question,recordQuestion,knowledges));
        }
        return recordQuestionDTOS;
    }


}
