package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnu.gpt.entity.Question;
import com.scnu.gpt.entity.QuestionSet;
import com.scnu.gpt.mapper.KnowledgeMapper;
import com.scnu.gpt.mapper.QuestionMapper;
import com.scnu.gpt.mapper.QuestionSetMapper;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.knowledge.QuestionKnowledgeDTO;
import com.scnu.gpt.pojo.question.QuestionChoiceDTO;
import com.scnu.gpt.pojo.question.QuestionDTO;
import com.scnu.gpt.pojo.question.QuestionSetDTO;
import com.scnu.gpt.service.IQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ldw
 * @since 2025-06-12
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionSetMapper, QuestionSet> implements IQuestionService {
    @Autowired
    QuestionSetMapper questionSetMapper;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    KnowledgeMapper knowledgeMapper;

//    private static final String[] questionTypes={"选择题","主观题","客观题"};
    @Override
    public String addSet(Integer userId) {
        QuestionSet questionSet = new QuestionSet();
        questionSet.setUserId(userId);
        
        questionSetMapper.insert(questionSet);
        return questionSet.getSetId().toString();
    }

    @Override
    public ArrayList<QuestionSetDTO> querySets(int userId) {
        QueryWrapper<QuestionSet> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("set_id");
        ArrayList<QuestionSet> queryResults = (ArrayList<QuestionSet>) questionSetMapper.selectList(wrapper);
        //重构数据库查询结果以适配前端需要的输出结构
        ArrayList<QuestionSetDTO> results = new ArrayList<>();
        for (QuestionSet questionSet : queryResults) {
            QueryWrapper<Question> questionWrapper = new QueryWrapper<>();
            questionWrapper.eq("set_id", questionSet.getSetId());
            int total = Math.toIntExact(questionMapper.selectCount(questionWrapper));//总题数
            // 查询去重后的type列（统计题型）
            QueryWrapper<Question> distinctQueryWrapper = new QueryWrapper<>();
            distinctQueryWrapper.select("DISTINCT question_type"); // 去重查询question_type列
            distinctQueryWrapper.eq("set_id", questionSet.getSetId());
            //映射到ArrayList<String>
            ArrayList<String> questionTags = questionMapper.selectObjs(distinctQueryWrapper).stream()
                    .map(obj -> (String) obj)
                    .collect(Collectors.toCollection(ArrayList::new));
            results.add(new QuestionSetDTO(
                    questionSet.getSetId().toString(),
                    questionSet.getSetName(),
                    questionSet.getSetDesc(),
                    total,questionTags
            ));
        }
        return results;
    }

    @Override
    public QuestionSet querySet(int setId) {
        return questionSetMapper.selectById(setId);
    }

    @Override
    public QuestionDTO addQuestion(Question question) {
        questionMapper.insert(question);
        //重构question结构以适配前端需要的结构
        return new QuestionDTO(
                question.getQuestionId().toString(),
                question.getQuestionStem(),
                question.getQuestionAnswer(),
                question.getQuestionParse(),
                question.getCodeOutput(),
                question.getQuestionType(),
                question.getSetId().toString(),
                new ArrayList<>(),new ArrayList<>(),false
        );
    }

    @Override
    public ArrayList<QuestionDTO> queryQuestions(int setId) {
        ArrayList<Question> questionList = (ArrayList<Question>) questionMapper.selectList(
                new QueryWrapper<Question>().eq("set_id", setId));

        //重构question结构以适配前端需要的结构
        ArrayList<QuestionDTO> results = new ArrayList<>();
        for(Question question : questionList){
            ArrayList<String> choiceList = new ArrayList<>();
            if(question.getQuestionType().equals("选择题")){
                choiceList = (ArrayList<String>) questionMapper.selectChoices(question.getQuestionId());
            }
            ArrayList<String> knowledges = (ArrayList<String>)knowledgeMapper.selectByQuestionId(question.getQuestionId());
            results.add(new QuestionDTO(
                    question.getQuestionId().toString(),
                    question.getQuestionStem(),
                    question.getQuestionAnswer(),
                    question.getQuestionParse(),
                    question.getCodeOutput(),
                    question.getQuestionType(),
                    question.getSetId().toString(),
                    choiceList,knowledges,false
            ));
        }
        return results;
    }


    @Override
    public ApiResponse<Void> deleteQuestion(int questionId) {
        questionMapper.deleteById(questionId);
        return new ApiResponse<>("200","删除成功",null);
    }

    @Override
    public ApiResponse<Void> updateQuestion(QuestionDTO questionDTO) {
        Question question = new Question();
        question.setQuestionId(Integer.parseInt(questionDTO.questionId()));
        question.setQuestionStem(questionDTO.questionStem());
        question.setQuestionAnswer(questionDTO.questionAnswer());
        question.setQuestionParse(questionDTO.questionParse());
        question.setCodeOutput(questionDTO.codeOutput());
        question.setQuestionType(questionDTO.questionType());
        question.setSetId(Integer.parseInt(questionDTO.setId()));

        if(question.getQuestionType().equals("选择题")){//选择题还要录入选项
            //先删后插入
            questionMapper.deleteChoices(question.getQuestionId());
            if(questionDTO.choiceList()!=null && !questionDTO.choiceList().isEmpty()){
                List<QuestionChoiceDTO> questionChoiceDTOS = new ArrayList<>();
                for(String choice : questionDTO.choiceList()){
                    questionChoiceDTOS.add(new QuestionChoiceDTO(question.getQuestionId(),choice));
                }
                questionMapper.insertChoices(questionChoiceDTOS);
            }
        }
        //更新试题关联的知识点列表（先删后增）
        knowledgeMapper.deleteByQuestionId(question.getQuestionId());
        if(questionDTO.knowledgeList()!=null && !questionDTO.knowledgeList().isEmpty()){
            List<QuestionKnowledgeDTO> questionKnowledgeDTOS = new ArrayList<>();
            for(String knowledge : questionDTO.knowledgeList()){
                questionKnowledgeDTOS.add(new QuestionKnowledgeDTO(question.getQuestionId(),knowledge));
            }
            knowledgeMapper.insertQuestionKnowledges(questionKnowledgeDTOS);
        }
        questionMapper.updateById(question);
        return new ApiResponse<>("200","成功更新",null);
    }

    @Override
    public ApiResponse<Void> updateSet(QuestionSet questionSet) {
        questionSetMapper.updateById(questionSet);
        return new ApiResponse<>("200","成功",null);
    }

    @Override
    public ApiResponse<Void> deleteSet(int setId) {
        questionSetMapper.deleteById(setId);
        return new ApiResponse<>("200","成功",null);
    }

    @Override
    public ApiResponse<Question> queryQuestion(int questionId) {
        return new ApiResponse<>("200","成功", questionMapper.selectById(questionId));
    }

}
