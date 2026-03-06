<!-- 做题界面 -->
<template>
  <el-row v-if="ifSubmit === true">
    <!-- 侧边栏 -->
    <el-col :span="8">
      <el-card style="height: 660px;">
        <template #header>
          <el-row>
            <el-col :span="16">
              <span style="font-size: large;font-weight: bold;">{{ setInfor.setName }}</span>
            </el-col>
            <el-col :span="8">
              <el-button type="success" @click="submitAnswer">提交答案</el-button>
            </el-col>
          </el-row>
          <el-row>
            {{ setInfor.setDesc }}
          </el-row>
        </template>
        <!-- 侧边锚点跳转 -->
        <span style="font-size: large;font-weight: bold;margin-left: 10px;">题目列表</span>
        <el-anchor style="height: 90%;width: 80%;" :container="mainRef" direction="vertical" type="default"
          :offset="30">
          <el-popover v-for="(question, index) in questionsData" :key="question.questionId" placement="left-start"
            :width="200" trigger="hover" :content="question.questionStem">
            <template #reference>
              <el-anchor-link style="font-weight: bold;" :href="'#question' + question.questionId"
                :title="`[${question.questionType.slice(0, 2)}] ${index + 1}、${truncatedContent(question.questionStem, 25)}`" />
            </template>
          </el-popover>
        </el-anchor>
      </el-card>
    </el-col>
    <!-- 题目列表 -->
    <el-col :span="16" style="height: 660px">
      <div ref="mainRef" style="height: 100%;overflow: auto;">
        <div :id="'question' + question.questionId" class="questionDiv" v-for="(question, index) in questionsData"
          :key="question.questionId">
          <!-- 头部信息 -->
          <el-row>
            <el-col :span="4">
              <span style="color: #409eff;font-size: large;font-weight: bold;margin-right: 10px;">题目 {{ index + 1
              }}</span>
              <el-tag :type="typeMap[question.questionType]">{{
                question.questionType
              }}</el-tag>
            </el-col>
            <el-col :span="20">
              <el-tag style="float: right;margin-left: 5px;" v-for="(knowledge, kindex) in question.knowledgeList"
                :key="kindex" type="primary" effect="plain">
                {{ knowledge }}
              </el-tag>
            </el-col>
          </el-row>

          <!-- 题干 -->
          <div style="margin-top: 10px;background: #f8fbff;border-radius: 6px;line-height: 1.6;padding: 10px 15px;">
            {{ question.questionStem }}
          </div>
          <div v-if="question.questionType == QUESTION_TYPE.PROGRAM"
            style="margin-top: 10px;background: #f8fbff;border-radius: 6px;line-height: 1.6;padding: 10px 15px;white-space: pre-wrap;">
            <span>程序期望输出：</span>
            <div style="color: #aaa;font-weight: bold; padding-left: 20px;">{{ '\n' + question.codeOutput }}</div>
          </div>
          <el-divider />
          <!-- 填写答案区-选择题 -->
          <div class="questionForm" v-if="question.questionType == QUESTION_TYPE.CHOICE" :model="question"
            style="padding:0 50px;">

            <!-- 答案选项 -->
            <div style="width: 100%;">
              <el-radio-group v-model="answerList[index].studentAnswer" :min="1" :max="1" style="width: 100%;">
                <el-row class="choiceRow" v-for="(_, choiceIndex) in question.choiceList" :key="choiceIndex">
                  <el-col :span="2">
                    <el-radio :value="indexToLetter(choiceIndex)">{{
                      indexToLetter(choiceIndex)
                    }}.</el-radio>
                  </el-col>
                  <el-col :span="19" style="align-content: center;">
                    <span style="font-size: medium;">{{ question.choiceList[choiceIndex] }}</span>
                  </el-col>
                </el-row>
              </el-radio-group>
            </div>
          </div>
          <!-- 主观题 -->
          <div class="questionForm" v-else-if="question.questionType == QUESTION_TYPE.SUBJECTIVE" :model="question"
            style="padding:0 50px;">
            <div>
              <el-input v-model="answerList[index].studentAnswer" :autosize="{ minRows: 3, maxRows: 12 }"
                type="textarea" placeholder="请输入答案" />
            </div>
          </div>
          <!-- 编程题 -->
          <div class="questionForm" v-else-if="question.questionType == QUESTION_TYPE.PROGRAM" :model="question"
            style="padding:0 50px;">
            <ProgrammingQuestion 
              :question-id="question.questionId"
              :question-stem="question.questionStem"
              :code-output="question.codeOutput"
              :knowledge-list="question.knowledgeList"
              v-model="answerList[index].studentAnswer"
              @code-executed="handleCodeExecuted"
              @answer-submitted="handleAnswerSubmitted"
            />
          </div>
        </div>
      </div>
    </el-col>
  </el-row>
  <el-result v-if="ifSubmit === false" icon="success" title="提醒" sub-title="试题批改中，请稍后再来">
    <template #extra>
      <el-button type="primary" @click="returnPage">返回</el-button>
    </template>
  </el-result>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue';
import ProgrammingQuestion from '@/components/ProgrammingQuestion.vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request.js'
import { useRoute, useRouter } from 'vue-router';
import { QUESTION_TYPE } from '@/constants/questionTypes';
import { RECORD_QUESTION_TYPES } from '@/constants/recordQuestionTypes';

const route = useRoute()
const router = useRouter()

const ifSubmit = ref(null);  //是否已提交
const setId = ref('')
const setRecordId = ref('')
const questionsData = ref([])       //questionDTO，各试题详情
const answerList = ref([])    //回答
const setInfor = ref({    //试题集基本信息
  "setName": '',
  "setDesc": ''
})
const typeMap = {
  [QUESTION_TYPE.CHOICE]: 'primary',
  [QUESTION_TYPE.SUBJECTIVE]: 'success',
  [QUESTION_TYPE.PROGRAM]: 'info'
};

const mainRef = ref(null)

onMounted(async () => {
  // 从 URL query 获取参数
  setRecordId.value = route.query.setRecordId;
  if (!setRecordId.value || setRecordId.value == '') {
    ElMessage.error("id不存在")
    return;
  } else {
    //获取做题记录信息
    {
      const response = await request.post('/doQuestion/queryRecordSet', setRecordId.value)
      setId.value = response.data.data.setId;
      if (response.data.data.state !== RECORD_QUESTION_TYPES.UNSUBMIT) {
        ifSubmit.value = (false);
        return;
      } else {
        ifSubmit.value = (true);
      }
    }
    //获取试题集信息
    {
      const response = await request.post('/question/querySet', setId.value)
      setInfor.value = response.data.data;
    }
    //获取该实体集下具体的各题目信息
    {
      const response = await request.post('/question/queryQuestions', setId.value)
      questionsData.value = response.data.data;
      //初始化答案数组
      answerList.value = questionsData.value.map(question => ({
        questionId: question.questionId,
        studentAnswer: ''
      }))
    }
  }
})

const returnPage = () => {
  router.go(-1);
}

//提交答案
const submitAnswer = async () => {
  // 检查是否有未答题目
  const unansweredCount = answerList.value.filter(answer => !answer.studentAnswer.trim()).length;
  if (unansweredCount > 0) {
    ElMessage.warning(`还有 ${unansweredCount} 道题目未作答，请检查后提交`);
    return;
  }

  answerList.value = answerList.value.map(answer => ({
    questionId: answer.questionId,
    studentAnswer: answer.studentAnswer,
    setRecordId: setRecordId.value
  }))
  
  try {
    //gpt智能评卷 
    await request.post('/gpt/gradeAnswers', answerList.value)
    ElMessage.success("提交成功");
    // 跳转到结果页面
    setTimeout(() => {
      router.push({
        name: "QuestionResult",
        query: { setRecordId: setRecordId.value }
      });
    }, 1500);
  } catch (error) {
    ElMessage.error("提交失败，请重试");
  }
}

const indexToLetter = computed(() => (index) => {
  return String.fromCharCode(65 + index);
});

// 截断文本内容
const truncatedContent = (text, maxLength) => {
  if (!text || typeof text !== 'string') return '';
  if (text.length > maxLength) {
    return text.slice(0, maxLength) + "...";
  }
  return text;
};

// 处理编程题代码执行事件
const handleCodeExecuted = (data) => {
  console.log('编程题代码执行:', data);
  // 可以在这里添加代码执行记录逻辑
};

// 处理编程题答案提交事件
const handleAnswerSubmitted = (data) => {
  console.log('编程题答案提交:', data);
  // 可以在这里添加答案提交记录逻辑
};
</script>

<style scoped>
/* 选择题 */
.choiceRow {
  width: 100%;
  display: flex;
  align-content: center;
  justify-self: center;
}

.questionDiv {
  width: 80%;
  padding: 20px;
  padding-bottom: 5px;
  padding-top: 15px;
  margin-bottom: 10px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.questionForm {
  margin-top: 20px;
  margin-bottom: 20px;
}
</style>