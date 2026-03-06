<template>
    <div class="practice-page">
        <el-card class="practice-card">
            <template #header>
                <div class="card-header">
                    <el-icon class="header-icon"><EditPen /></el-icon>
                    <span>实时练习评测</span>
                </div>
            </template>
            
            <div class="practice-content">
                <div v-if="showPracticeForm" class="practice-form">
                    <el-form :model="practiceForm" label-width="100px">
                        <el-form-item label="选择章节:">
                            <el-select v-model="practiceForm.sectionId" placeholder="选择要练习的章节" style="width: 100%;">
                                <el-option 
                                    v-for="section in courseDetail.sectionList" 
                                    :key="section.section.sectionId"
                                    :label="section.section.sectionName"
                                    :value="section.section.sectionId"
                                />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="题目类型:">
                            <el-select v-model="practiceForm.questionType" placeholder="选择题目类型" style="width: 100%;">
                                <el-option label="选择题" value="choice" />
                                <el-option label="填空题" value="fill" />
                                <el-option label="判断题" value="judge" />
                                <el-option label="简答题" value="short" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="题目数量:">
                            <el-input-number v-model="practiceForm.count" :min="1" :max="20" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="startPractice">
                                <el-icon><EditPen /></el-icon>
                                开始练习
                            </el-button>
                        </el-form-item>
                    </el-form>
                </div>
                
                <div v-if="!showPracticeForm && practiceQuestions.length > 0" class="practice-questions">
                    <div class="question-header">
                        <span>第 {{ currentPracticeIndex + 1 }} 题 / 共 {{ practiceQuestions.length }} 题</span>
                        <div class="question-actions">
                            <el-button type="info" @click="toggleAnswer" size="small">
                                {{ showAnswer ? '隐藏答案' : '查看答案' }}
                            </el-button>
                            <el-button type="primary" @click="submitPractice" size="small">提交练习</el-button>
                        </div>
                    </div>
                    
                    <div class="question-content">
                        <div class="question-stem">{{ practiceQuestions[currentPracticeIndex].questionStem }}</div>
                        
                        <div v-if="practiceQuestions[currentPracticeIndex].questionType === 'choice'" class="choices">
                            <el-radio-group v-model="practiceAnswers[currentPracticeIndex]">
                                <el-radio 
                                    v-for="(choice, index) in practiceQuestions[currentPracticeIndex].choiceList" 
                                    :key="index"
                                    :label="String.fromCharCode(65 + index)"
                                >
                                    {{ String.fromCharCode(65 + index) }}. {{ choice }}
                                </el-radio>
                            </el-radio-group>
                        </div>
                        
                        <div v-else-if="practiceQuestions[currentPracticeIndex].questionType === 'fill'" class="fill-answer">
                            <el-input 
                                v-model="practiceAnswers[currentPracticeIndex]" 
                                placeholder="请输入答案..."
                            />
                        </div>
                        
                        <div v-else-if="practiceQuestions[currentPracticeIndex].questionType === 'judge'" class="judge-answer">
                            <el-radio-group v-model="practiceAnswers[currentPracticeIndex]">
                                <el-radio :label="true">正确</el-radio>
                                <el-radio :label="false">错误</el-radio>
                            </el-radio-group>
                        </div>
                        
                        <div v-else-if="practiceQuestions[currentPracticeIndex].questionType === 'short'" class="short-answer">
                            <el-input 
                                v-model="practiceAnswers[currentPracticeIndex]" 
                                type="textarea" 
                                :rows="4"
                                placeholder="请输入你的答案..."
                            />
                        </div>
                        
                        <!-- 答案解释 -->
                        <div v-if="showAnswer" class="answer-explanation">
                            <el-divider content-position="left">答案解释</el-divider>
                            <div class="correct-answer">
                                <strong>正确答案：</strong>
                                <span v-if="practiceQuestions[currentPracticeIndex].questionType === 'choice'">
                                    {{ practiceQuestions[currentPracticeIndex].correctAnswer }}
                                </span>
                                <span v-else-if="practiceQuestions[currentPracticeIndex].questionType === 'judge'">
                                    {{ practiceQuestions[currentPracticeIndex].correctAnswer ? '正确' : '错误' }}
                                </span>
                                <span v-else>
                                    {{ practiceQuestions[currentPracticeIndex].correctAnswer }}
                                </span>
                            </div>
                            <div class="explanation">
                                <strong>解释：</strong>
                                {{ practiceQuestions[currentPracticeIndex].explanation }}
                            </div>
                        </div>
                    </div>
                    
                    <div class="question-navigation">
                        <el-button @click="prevPractice" :disabled="currentPracticeIndex === 0">上一题</el-button>
                        <el-button @click="nextPractice" :disabled="currentPracticeIndex === practiceQuestions.length - 1">下一题</el-button>
                    </div>
                </div>
                
                <!-- 练习结果 -->
                <div v-if="showPracticeResult" class="practice-result">
                    <div class="result-header">
                        <h3>练习完成！</h3>
                        <div class="score-info">
                            <span class="score">得分：{{ practiceScore }}</span>
                            <span class="total">总分：{{ practiceQuestions.length * 10 }}</span>
                        </div>
                    </div>
                    <div class="result-actions">
                        <el-button type="primary" @click="restartPractice">重新开始</el-button>
                        <el-button @click="backToForm">返回选择</el-button>
                    </div>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { EditPen } from '@element-plus/icons-vue'

// 实时练习相关
const practiceForm = ref({
    sectionId: '',
    questionType: 'choice',
    count: 5
})

const practiceQuestions = ref([])
const currentPracticeIndex = ref(0)
const practiceAnswers = ref([])
const showPracticeResult = ref(false)
const showPracticeForm = ref(true)
const practiceScore = ref(0)
const showAnswer = ref(false)

// 课程完整信息 - 从父组件传入
// eslint-disable-next-line no-undef
const props = defineProps({
    courseDetail: {
        type: Object,
        default: () => ({
            sectionList: [
                {
                    section: {
                        sectionId: 1,
                        sectionName: 'Vue.js 基础入门'
                    }
                },
                {
                    section: {
                        sectionId: 2,
                        sectionName: '组件化开发'
                    }
                }
            ]
        })
    }
})

const courseDetail = computed(() => props.courseDetail)

// 实时练习功能
const startPractice = async () => {
    if (!practiceForm.value.sectionId) {
        ElMessage.error('请选择要练习的章节')
        return
    }
    
    try {
        // 模拟获取练习题
        const questionBank = {
            choice: [
                {
                    id: 1,
                    questionStem: 'Vue.js中v-if和v-show的区别是什么？',
                    questionType: 'choice',
                    choiceList: [
                        'v-if是条件渲染，v-show是显示隐藏',
                        'v-if会销毁重建DOM，v-show只是切换display',
                        'v-if性能更好，v-show更灵活',
                        '没有区别'
                    ],
                    correctAnswer: 'B',
                    explanation: 'v-if是真正的条件渲染，会销毁和重建DOM元素；v-show只是切换元素的display属性，DOM元素始终存在。'
                },
                {
                    id: 2,
                    questionStem: 'Vue.js中computed和watch的区别是什么？',
                    questionType: 'choice',
                    choiceList: [
                        'computed用于计算属性，watch用于监听数据变化',
                        'computed有缓存，watch没有缓存',
                        'computed是同步的，watch是异步的',
                        '以上都是'
                    ],
                    correctAnswer: 'D',
                    explanation: 'computed用于计算属性且有缓存，watch用于监听数据变化，两者都是同步的。'
                },
                {
                    id: 3,
                    questionStem: 'Vue.js的生命周期钩子中，哪个钩子在组件挂载后执行？',
                    questionType: 'choice',
                    choiceList: [
                        'beforeCreate',
                        'created',
                        'beforeMount',
                        'mounted'
                    ],
                    correctAnswer: 'D',
                    explanation: 'mounted钩子在组件挂载到DOM后执行，此时可以访问DOM元素。'
                }
            ],
            fill: [
                {
                    id: 4,
                    questionStem: 'Vue.js中，使用_____指令可以实现双向数据绑定。',
                    questionType: 'fill',
                    correctAnswer: 'v-model',
                    explanation: 'v-model是Vue.js中实现双向数据绑定的指令。'
                },
                {
                    id: 5,
                    questionStem: 'Vue.js的响应式系统使用_____来劫持数据变化。',
                    questionType: 'fill',
                    correctAnswer: 'Object.defineProperty',
                    explanation: 'Vue 2.x使用Object.defineProperty，Vue 3.x使用Proxy来劫持数据变化。'
                }
            ],
            judge: [
                {
                    id: 6,
                    questionStem: 'Vue.js中的data函数必须返回一个对象。',
                    questionType: 'judge',
                    correctAnswer: true,
                    explanation: 'Vue.js中的data函数必须返回一个对象，这样每个组件实例都有自己独立的数据副本。'
                },
                {
                    id: 7,
                    questionStem: 'Vue.js中的methods可以访问data中的数据。',
                    questionType: 'judge',
                    correctAnswer: true,
                    explanation: 'Vue.js中的methods可以通过this访问data中的数据。'
                }
            ],
            short: [
                {
                    id: 8,
                    questionStem: '请简述Vue.js的响应式原理。',
                    questionType: 'short',
                    correctAnswer: 'Vue.js使用Object.defineProperty或Proxy来劫持数据变化，当数据发生变化时，会通知相关的依赖进行更新。',
                    explanation: 'Vue.js通过数据劫持和发布订阅模式实现响应式系统。'
                },
                {
                    id: 9,
                    questionStem: 'Vue.js中父子组件通信的方式有哪些？',
                    questionType: 'short',
                    correctAnswer: '父子组件通信主要通过props和emit实现，父组件通过props向子组件传递数据，子组件通过emit向父组件发送事件。',
                    explanation: '这是Vue.js中最常用的父子组件通信方式。'
                }
            ]
        }
        
        // 根据选择的题目类型获取题目
        const selectedQuestions = questionBank[practiceForm.value.questionType] || questionBank.choice
        practiceQuestions.value = selectedQuestions.slice(0, practiceForm.value.count)
        
        practiceAnswers.value = new Array(practiceQuestions.value.length).fill('')
        currentPracticeIndex.value = 0
        showPracticeResult.value = false
        showPracticeForm.value = false
    } catch (error) {
        ElMessage.error('获取练习题失败')
    }
}

const submitPractice = () => {
    // 计算得分
    let score = 0
    practiceQuestions.value.forEach((question, index) => {
        const answer = practiceAnswers.value[index]
        if (answer !== undefined && answer !== '') {
            if (question.questionType === 'choice' || question.questionType === 'judge') {
                if (answer === question.correctAnswer) {
                    score += 10
                }
            } else if (question.questionType === 'fill') {
                if (answer.toLowerCase().trim() === question.correctAnswer.toLowerCase().trim()) {
                    score += 10
                }
            } else if (question.questionType === 'short') {
                // 简答题简单判断是否包含关键词
                const keywords = question.correctAnswer.toLowerCase().split(' ')
                const userAnswer = answer.toLowerCase()
                const matchCount = keywords.filter(keyword => userAnswer.includes(keyword)).length
                if (matchCount >= keywords.length * 0.6) {
                    score += 10
                }
            }
        }
    })
    
    practiceScore.value = score
    showPracticeResult.value = true
    ElMessage.success(`练习完成！得分：${score}/${practiceQuestions.value.length * 10}`)
}

const nextPractice = () => {
    if (currentPracticeIndex.value < practiceQuestions.value.length - 1) {
        currentPracticeIndex.value++
        showAnswer.value = false
    }
}

const prevPractice = () => {
    if (currentPracticeIndex.value > 0) {
        currentPracticeIndex.value--
        showAnswer.value = false
    }
}

const toggleAnswer = () => {
    showAnswer.value = !showAnswer.value
}

const restartPractice = () => {
    showPracticeForm.value = true
    showPracticeResult.value = false
    practiceQuestions.value = []
    practiceAnswers.value = []
    currentPracticeIndex.value = 0
    practiceScore.value = 0
    showAnswer.value = false
}

const backToForm = () => {
    showPracticeForm.value = true
    showPracticeResult.value = false
    practiceQuestions.value = []
    practiceAnswers.value = []
    currentPracticeIndex.value = 0
    practiceScore.value = 0
    showAnswer.value = false
}
</script>

<style scoped>
.practice-page {
    padding: 20px;
}

.practice-card {
    border-radius: 12px;
}

.card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    color: #303133;
}

.header-icon {
    color: #409eff;
    font-size: 18px;
}

.practice-content {
    height: 600px;
    display: flex;
    flex-direction: column;
}

.practice-form {
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
    margin-bottom: 10px;
}

.practice-form .el-form-item {
    margin-bottom: 15px;
}

.practice-form .el-select,
.practice-form .el-input-number {
    width: 100%;
}

.practice-form .el-button {
    width: 100%;
}

.practice-questions {
    flex: 1;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
    overflow-y: auto;
}

.question-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.question-actions {
    display: flex;
    gap: 8px;
}

.question-header .el-button {
    flex-shrink: 0;
}

.question-content {
    padding: 15px;
    background: white;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
    margin-bottom: 15px;
}

.question-stem {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 15px;
}

.choices .el-radio-group {
    margin-top: 10px;
}

.choices .el-radio {
    margin-bottom: 10px;
}

.short-answer .el-textarea {
    margin-top: 10px;
}

.fill-answer .el-input {
    margin-top: 10px;
}

.judge-answer .el-radio-group {
    margin-top: 10px;
}

.answer-explanation {
    margin-top: 20px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 8px;
    border-left: 4px solid #409eff;
}

.correct-answer {
    margin-bottom: 10px;
    color: #67c23a;
}

.explanation {
    color: #606266;
    line-height: 1.6;
}

.practice-result {
    text-align: center;
    padding: 40px 20px;
}

.result-header h3 {
    color: #303133;
    margin-bottom: 20px;
}

.score-info {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-bottom: 30px;
}

.score {
    font-size: 18px;
    color: #67c23a;
    font-weight: 600;
}

.total {
    font-size: 18px;
    color: #909399;
}

.result-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
}

.question-navigation {
    display: flex;
    justify-content: space-between;
    margin-top: 15px;
}

.question-navigation .el-button {
    flex: 1;
    margin: 0 5px;
}

.question-navigation .el-button:first-child {
    margin-right: 5px;
}

.question-navigation .el-button:last-child {
    margin-left: 5px;
}
</style> 