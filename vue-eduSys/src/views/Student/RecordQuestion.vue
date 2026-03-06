<!-- 做题评分结果 -->
<template>
    <el-row>
        <el-col :span="8">
            <el-card style="height: 660px;">
                <template #header>
                    <el-row>
                        <el-col :span="20">
                            <span style="font-size: large;font-weight: bold;">{{ setInfor.setName }}</span>
                        </el-col>
                        <el-col :span="4">
                            <el-tag :type="setScore >= 60 ? 'success' : 'danger'" size="large" effect="dark" style="font-weight: bold; " round>
                                总分: {{ setScore }}
                            </el-tag>
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
                    <el-popover v-for="(question, index) in recordQuestionDTOs" :key="question.questionRecordId"
                        placement="left-start" :width="200" trigger="hover" :content="question.questionStem">
                        <template #reference>
                            <el-anchor-link style="font-weight: bold;" :href="'#question' + question.questionRecordId"
                                :title="`[${question.questionType.slice(0, 2)}] ${index + 1}、${truncatedContent(question.questionStem, 22)} [${question.questionScore}分]`" />
                        </template>
                    </el-popover>
                </el-anchor>
            </el-card>
        </el-col>
        <el-col :span="16" v-if="ifGraded == true" style="height: 660px;">
            <div ref="mainRef" style="height: 100%;overflow: auto;">
                <div :id="'question' + question.questionRecordId" v-for="(question, index) in recordQuestionDTOs"
                    :key="index">

                    <el-card class="question-card">
                        <el-row class="card-header">
                            <el-col :span="4">
                                <span style="color: #409eff;font-size: large;font-weight: bold;margin-right: 10px;">题目
                                    {{
                                        index
                                        + 1
                                    }}</span>
                                <el-tag :type="typeMap[question.questionType]">{{
                                    question.questionType
                                }}</el-tag>
                            </el-col>
                            <el-col :span="4" :offset="16">
                                <el-tag :type="question.questionScore >= 60 ? 'success' : 'danger'" size="large" effect="plain"  style="font-weight: bold;">
                                    得分: {{ question.questionScore }}
                                </el-tag>
                            </el-col>
                        </el-row>

                        <div class="content-section">
                            <h4 class="section-title">题干</h4>
                            <div class="section-content">{{ question.questionStem }}</div>
                        </div>

                        <el-divider border-style="dashed" />

                        <div class="answer-container">
                            <div class="answer-column">
                                <h4 class="section-title">学生回答</h4>
                                <div class="section-content student-answer">{{ question.studentAnswer }}</div>
                            </div>

                            <div class="answer-column">
                                <h4 class="section-title">参考答案</h4>
                                <div class="section-content correct-answer">{{ question.questionAnswer }}</div>
                            </div>
                        </div>
                        <!-- 仅编程题有代码运行结果对比 -->
                        <div class="answer-container" v-if="question.questionType == QUESTION_TYPE.PROGRAM">
                            <div class="answer-column">
                                <h4 class="section-title">代码运行结果</h4>
                                <div class="section-content student-answer">{{ question.studentOutput }}</div>
                            </div>

                            <div class="answer-column">
                                <h4 class="section-title">正确运行结果</h4>
                                <div class="section-content correct-answer">{{ question.codeOutput }}</div>
                            </div>
                        </div>
                        <el-divider border-style="dashed" />

                        <div class="content-section" v-if="question.questionType != QUESTION_TYPE.CHOICE">
                            <h4 class="section-title">评分依据</h4>
                            <div class="section-content">{{ question.scoreParse }}</div>
                        </div>

                        <div class="content-section">
                            <h4 class="section-title">答案分析</h4>
                            <div class="section-content error-analysis">{{ question.erroParse }}</div>
                        </div>
                        <div class="content-section">
                            <h4 class="section-title">本题知识点</h4>
                            <div class="section-content" style="color: #0052d9;">{{ question.knowledgeList.join('、') }}
                            </div>
                        </div>
                    </el-card>
                </div>
            </div>
        </el-col>
    </el-row>
    <el-result v-if="ifGraded == false" icon="success" title="提醒" sub-title="试题批改中，请稍后再来">
        <template #extra>
            <el-button type="primary" @click="returnPage">返回</el-button>
        </template>
    </el-result>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request.js'
import { useRoute, useRouter } from 'vue-router';
import { QUESTION_TYPE } from '@/constants/questionTypes';
import { RECORD_QUESTION_TYPES } from '@/constants/recordQuestionTypes';
const route = useRoute()
const router = useRouter()

const ifGraded = ref(null);    //是否已批改
const setId = ref('')
const setRecordId = ref('')
const recordQuestionDTOs = ref([])       //各题目改卷结果
const setScore = ref(0)   //总得分
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
        console.log(typeMap)
        return;
    } else {
        //获取做题记录基本信息
        {
            const response = await request.post('/doQuestion/queryRecordSet', setRecordId.value)
            setId.value = response.data.data.setId;
            if (response.data.data.state == RECORD_QUESTION_TYPES.GRADED) {
                ifGraded.value = true;
            } else {
                ifGraded.value = false;
            }
            setScore.value = response.data.data.setScore
        }
        //获取试题集信息
        {
            const response = await request.post('/question/querySet', setId.value)
            setInfor.value = response.data.data;
        }
        //获取各题目改卷结果
        {
            const response = await request.post('/doQuestion/queryRecordQuestions', setRecordId.value)
            recordQuestionDTOs.value = response.data.data
        }
    }
})
const returnPage = () => {
    router.go(-1);
}
// 截断文本内容
const truncatedContent = (text, maxLength) => {
    if (!text || typeof text !== 'string') return '';
    if (text.length > maxLength) {
        return text.slice(0, maxLength) + "...";
    }

    return text;
};
</script>

<style scoped>
/* 改卷结果展示 */
.result-container {
    max-width: 1000px;
    margin: 20px auto;
    padding: 20px;
}

.question-card {
    margin-bottom: 25px;
    border-radius: 10px;
    box-shadow: 0 2px 12px rgba(100, 150, 220, 0.15);
    border: 1px solid #e6f0ff;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 15px;
    border-bottom: 1px solid #e6f0ff;
}

.question-title {
    color: #409eff;
    margin: 0;
}

.content-section {
    margin: 18px 0;
}

.section-title {
    color: #67a1ff;
    margin-bottom: 8px;
    font-weight: 500;
}

.section-content {
    padding: 10px 15px;
    background: #f8fbff;
    border-radius: 6px;
    line-height: 1.6;
    white-space: pre-wrap;
}

.answer-container {
    display: flex;
    gap: 20px;
}

.answer-column {
    flex: 1;
}

.student-answer {
    border-left: 3px solid #f56c6c;
}

.correct-answer {
    border-left: 3px solid #67c23a;
}

.error-analysis {
    background: #fef0f0;
    color: #f56c6c;
}

@media (max-width: 768px) {
    .answer-container {
        flex-direction: column;
        gap: 15px;
    }
}
</style>