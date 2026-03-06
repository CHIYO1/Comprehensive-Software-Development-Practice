<template>
    <div class="question-generate-content">
        <!-- 页面头部 -->
        <div class="content-header">
            <div class="header-content">
                <div class="header-left">
                    <h2 class="page-title">智能题目生成</h2>
                </div>
                <div class="header-right">
                    <el-button type="primary" @click="saveQuestions">
                        <el-icon><Check /></el-icon>
                        保存题目
                    </el-button>
                    <el-button type="success" @click="exportQuestions">
                        <el-icon><Download /></el-icon>
                        导出题目
                    </el-button>
                </div>
            </div>
        </div>

        <!-- 主要内容区域 -->
        <el-row :gutter="24" class="main-content">
            <!-- 左侧配置区域 -->
            <el-col :span="8">
                <el-card class="config-card">
                    <template #header>
                        <div class="card-header">
                            <span>生成配置</span>
                        </div>
                    </template>
                    
                    <el-form :model="generateForm" label-width="100px" class="generate-form">
                        <el-form-item label="选择章节:">
                            <el-select v-model="generateForm.sectionId" placeholder="选择要生成题目的章节" style="width: 100%;">
                                <el-option 
                                    v-for="section in courseSections" 
                                    :key="section.sectionId"
                                    :label="section.sectionName"
                                    :value="section.sectionId"
                                />
                            </el-select>
                        </el-form-item>

                        <el-form-item label="题目类型:">
                            <el-checkbox-group v-model="generateForm.questionTypes">
                                <el-checkbox label="choice">选择题</el-checkbox>
                                <el-checkbox label="fill">填空题</el-checkbox>
                                <el-checkbox label="judge">判断题</el-checkbox>
                                <el-checkbox label="short">简答题</el-checkbox>
                                <el-checkbox label="programming">编程题</el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>

                        <el-form-item label="难度等级:">
                            <el-select v-model="generateForm.difficulty" placeholder="选择难度等级" style="width: 100%;">
                                <el-option label="简单" value="easy" />
                                <el-option label="中等" value="medium" />
                                <el-option label="困难" value="hard" />
                                <el-option label="混合难度" value="mixed" />
                            </el-select>
                        </el-form-item>

                        <el-form-item label="题目数量:">
                            <el-input-number v-model="generateForm.count" :min="1" :max="50" style="width: 100%;" />
                        </el-form-item>

                        <el-form-item label="知识点:">
                            <el-input-tag v-model="generateForm.knowledgePoints" placeholder="输入知识点后按回车确认" />
                        </el-form-item>

                        <el-form-item label="题目要求:">
                            <el-input 
                                v-model="generateForm.requirements" 
                                type="textarea" 
                                :rows="3"
                                placeholder="请输入具体的题目要求，如：重点考察、特殊要求等"
                            />
                        </el-form-item>

                        <el-form-item>
                            <el-button type="primary" @click="generateQuestions" :loading="generating" style="width: 100%;">
                                <el-icon><EditPen /></el-icon>
                                {{ generating ? '生成中...' : '开始生成题目' }}
                            </el-button>
                        </el-form-item>
                    </el-form>
                </el-card>

                <!-- 生成历史 -->
                <el-card class="history-card" style="margin-top: 24px;">
                    <template #header>
                        <div class="card-header">
                            <span>生成历史</span>
                        </div>
                    </template>
                    
                    <div class="history-list">
                        <div v-for="item in generateHistory" :key="item.id" class="history-item" @click="loadHistory(item)">
                            <div class="history-info">
                                <div class="history-title">{{ item.title }}</div>
                                <div class="history-meta">
                                    <span class="history-count">{{ item.count }}题</span>
                                    <span class="history-date">{{ item.createTime }}</span>
                                </div>
                            </div>
                            <el-button size="small" type="text" @click.stop="deleteHistory(item)">
                                <el-icon><Delete /></el-icon>
                            </el-button>
                        </div>
                    </div>
                </el-card>
            </el-col>

            <!-- 右侧结果区域 -->
            <el-col :span="16">
                <el-card class="result-card">
                    <template #header>
                        <div class="card-header">
                            <span>生成结果</span>
                            <div class="header-actions">
                                <el-button size="small" @click="clearResult">
                                    <el-icon><Delete /></el-icon>
                                    清空
                                </el-button>
                                <el-button size="small" type="primary" @click="previewQuestions">
                                    <el-icon><View /></el-icon>
                                    预览
                                </el-button>
                            </div>
                        </div>
                    </template>

                    <div v-if="!generatedQuestions.length" class="empty-result">
                        <el-icon class="empty-icon"><EditPen /></el-icon>
                        <div class="empty-text">请配置生成参数并点击生成按钮</div>
                        <div class="empty-hint">AI将根据您的需求生成高质量的题目</div>
                    </div>

                    <div v-else class="questions-result">
                        <div class="questions-header">
                            <div class="questions-info">
                                <span class="questions-count">共生成 {{ generatedQuestions.length }} 道题目</span>
                                <el-tag type="success">{{ getDifficultyText(generateForm.difficulty) }}</el-tag>
                            </div>
                            <div class="questions-actions">
                                <el-button size="small" @click="selectAll">全选</el-button>
                                <el-button size="small" @click="selectNone">取消全选</el-button>
                            </div>
                        </div>

                        <div class="questions-list">
                            <div v-for="(question, index) in generatedQuestions" :key="question.id" class="question-item">
                                <div class="question-header">
                                    <el-checkbox v-model="question.selected" />
                                    <span class="question-number">{{ index + 1 }}</span>
                                    <el-tag size="small" :type="getQuestionTypeColor(question.type)">
                                        {{ getQuestionTypeText(question.type) }}
                                    </el-tag>
                                    <el-tag size="small" type="info">{{ question.difficulty }}</el-tag>
                                    <div class="question-actions">
                                        <el-button size="small" type="text" @click="editQuestion(question)">
                                            <el-icon><Edit /></el-icon>
                                        </el-button>
                                        <el-button size="small" type="text" @click="deleteQuestion(question)">
                                            <el-icon><Delete /></el-icon>
                                        </el-button>
                                    </div>
                                </div>
                                
                                <div class="question-content">
                                    <div class="question-title">{{ question.title }}</div>
                                    
                                    <!-- 选择题选项 -->
                                    <div v-if="question.type === 'choice' && question.options" class="question-options">
                                        <div v-for="(option, optIndex) in question.options" :key="optIndex" class="option-item">
                                            <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                                            <span class="option-content">{{ option }}</span>
                                        </div>
                                    </div>
                                    
                                    <!-- 答案 -->
                                    <div class="question-answer">
                                        <div class="answer-label">答案：</div>
                                        <div class="answer-content">{{ question.answer }}</div>
                                    </div>
                                    
                                    <!-- 解析 -->
                                    <div v-if="question.explanation" class="question-explanation">
                                        <div class="explanation-label">解析：</div>
                                        <div class="explanation-content">{{ question.explanation }}</div>
                                    </div>
                                    
                                    <!-- 知识点 -->
                                    <div v-if="question.knowledgePoints && question.knowledgePoints.length" class="question-knowledge">
                                        <div class="knowledge-label">知识点：</div>
                                        <div class="knowledge-tags">
                                            <el-tag 
                                                v-for="point in question.knowledgePoints" 
                                                :key="point" 
                                                size="small" 
                                                type="info"
                                            >
                                                {{ point }}
                                            </el-tag>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 预览弹窗 -->
        <el-dialog v-model="showPreview" title="题目预览" width="800px">
            <div class="preview-content">
                <div v-for="(question, index) in selectedQuestions" :key="question.id" class="preview-question">
                    <div class="preview-header">
                        <span class="preview-number">{{ index + 1 }}.</span>
                        <el-tag size="small" :type="getQuestionTypeColor(question.type)">
                            {{ getQuestionTypeText(question.type) }}
                        </el-tag>
                    </div>
                    <div class="preview-title">{{ question.title }}</div>
                    
                    <div v-if="question.type === 'choice' && question.options" class="preview-options">
                        <div v-for="(option, optIndex) in question.options" :key="optIndex" class="preview-option">
                            <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                            <span class="option-content">{{ option }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

import { ElMessage, ElMessageBox } from 'element-plus'
import { 
    Check, Download, EditPen, Delete, View, Edit
} from '@element-plus/icons-vue'





const generating = ref(false)
const showPreview = ref(false)

// 生成表单
const generateForm = ref({
    sectionId: '',
    questionTypes: ['choice'],
    difficulty: 'medium',
    count: 10,
    knowledgePoints: [],
    requirements: ''
})

// 课程章节
const courseSections = ref([
    { sectionId: 1, sectionName: '第一章：课程介绍' },
    { sectionId: 2, sectionName: '第二章：基础知识' },
    { sectionId: 3, sectionName: '第三章：进阶内容' },
    { sectionId: 4, sectionName: '第四章：实战项目' }
])

// 生成历史
const generateHistory = ref([
    { id: 1, title: 'Vue.js基础选择题', count: 15, createTime: '2024-01-15 10:30' },
    { id: 2, title: 'React进阶题目', count: 20, createTime: '2024-01-14 15:20' },
    { id: 3, title: '前端工程化题目', count: 12, createTime: '2024-01-13 09:15' }
])

// 生成的题目
const generatedQuestions = ref([])

// 计算属性
const selectedQuestions = computed(() => {
    return generatedQuestions.value.filter(q => q.selected)
})



const getDifficultyText = (difficulty) => {
    const map = {
        easy: '简单',
        medium: '中等',
        hard: '困难',
        mixed: '混合难度'
    }
    return map[difficulty] || difficulty
}

const getQuestionTypeText = (type) => {
    const map = {
        choice: '选择题',
        fill: '填空题',
        judge: '判断题',
        short: '简答题',
        programming: '编程题'
    }
    return map[type] || type
}

const getQuestionTypeColor = (type) => {
    const map = {
        choice: 'primary',
        fill: 'success',
        judge: 'warning',
        short: 'info',
        programming: 'danger'
    }
    return map[type] || 'info'
}

const generateQuestions = async () => {
    if (!generateForm.value.sectionId || !generateForm.value.questionTypes.length) {
        ElMessage.warning('请选择章节和题目类型')
        return
    }

    generating.value = true
    try {
        // 这里应该调用实际的API
        // const response = await request.post('/api/question/generate', generateForm.value)
        
        // 模拟生成过程
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        // 模拟生成的题目
        const mockQuestions = [
            {
                id: 1,
                type: 'choice',
                difficulty: '中等',
                title: 'Vue.js中，以下哪个选项不是生命周期钩子？',
                options: ['created', 'mounted', 'updated', 'destroyed', 'computed'],
                answer: 'E. computed',
                explanation: 'computed是计算属性，不是生命周期钩子。',
                knowledgePoints: ['Vue.js', '生命周期'],
                selected: true
            },
            {
                id: 2,
                type: 'fill',
                difficulty: '简单',
                title: 'Vue.js中，用于声明响应式数据的选项是____。',
                answer: 'data',
                explanation: 'data选项用于声明组件的响应式数据。',
                knowledgePoints: ['Vue.js', '响应式数据'],
                selected: true
            },
            {
                id: 3,
                type: 'judge',
                difficulty: '简单',
                title: 'Vue.js中的v-model指令只能用于表单元素。',
                answer: '错误',
                explanation: 'v-model指令可以用于任何支持value属性的元素，包括自定义组件。',
                knowledgePoints: ['Vue.js', 'v-model'],
                selected: true
            },
            {
                id: 4,
                type: 'short',
                difficulty: '困难',
                title: '请简述Vue.js中computed和watch的区别。',
                answer: 'computed是计算属性，基于依赖缓存，只有依赖变化时才重新计算；watch是侦听器，用于监听数据变化并执行副作用。',
                explanation: 'computed适合用于数据的派生计算，watch适合用于数据变化时的异步操作或复杂逻辑。',
                knowledgePoints: ['Vue.js', 'computed', 'watch'],
                selected: true
            }
        ]
        
        generatedQuestions.value = mockQuestions
        ElMessage.success('题目生成成功')
    } catch (error) {
        ElMessage.error('生成题目失败')
    } finally {
        generating.value = false
    }
}

const saveQuestions = () => {
    if (!selectedQuestions.value.length) {
        ElMessage.warning('请选择要保存的题目')
        return
    }
    ElMessage.success('题目保存成功')
}

const exportQuestions = () => {
    if (!selectedQuestions.value.length) {
        ElMessage.warning('请选择要导出的题目')
        return
    }
    ElMessage.success('题目导出成功')
}

const clearResult = () => {
    generatedQuestions.value = []
    ElMessage.info('已清空生成结果')
}

const previewQuestions = () => {
    if (!selectedQuestions.value.length) {
        ElMessage.warning('请选择要预览的题目')
        return
    }
    showPreview.value = true
}

const selectAll = () => {
    generatedQuestions.value.forEach(q => q.selected = true)
}

const selectNone = () => {
    generatedQuestions.value.forEach(q => q.selected = false)
}

const editQuestion = (question) => {
    ElMessage.info(`编辑题目：${question.title}`)
    // 这里可以打开编辑弹窗
}

const deleteQuestion = async (question) => {
    try {
        await ElMessageBox.confirm(
            '确定要删除这道题目吗？',
            '确认删除',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        )
        
        const index = generatedQuestions.value.findIndex(q => q.id === question.id)
        if (index > -1) {
            generatedQuestions.value.splice(index, 1)
            ElMessage.success('删除成功')
        }
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

const loadHistory = (history) => {
    ElMessage.info(`加载历史：${history.title}`)
    // 这里可以加载历史配置
}

const deleteHistory = async (history) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除历史记录"${history.title}"吗？`,
            '确认删除',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        )
        
        const index = generateHistory.value.findIndex(h => h.id === history.id)
        if (index > -1) {
            generateHistory.value.splice(index, 1)
            ElMessage.success('删除成功')
        }
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

onMounted(() => {
    // 初始化页面
})
</script>

<style scoped>
.question-generate-content {
    padding: 0;
    background: transparent;
    min-height: auto;
}

/* 滚动条美化 */
.question-generate-content::-webkit-scrollbar {
    width: 6px;
}

.question-generate-content::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.question-generate-content::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
}

.question-generate-content::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}

.content-header {
    background: white;
    padding: 20px 24px;
    border-radius: 8px;
    margin-bottom: 24px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}

.back-btn {
    font-size: 14px;
    color: #606266;
}

.page-title {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
}

.header-right {
    display: flex;
    gap: 12px;
}

.main-content {
    margin-bottom: 24px;
}

.config-card, .result-card, .history-card {
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-actions {
    display: flex;
    gap: 8px;
}

.generate-form {
    padding: 20px 0;
}

.history-list {
    padding: 20px 0;
}

.history-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background-color 0.3s;
}

.history-item:hover {
    background-color: #f5f7fa;
}

.history-item:last-child {
    border-bottom: none;
}

.history-title {
    font-size: 14px;
    color: #303133;
    margin-bottom: 4px;
}

.history-meta {
    display: flex;
    gap: 12px;
}

.history-count {
    font-size: 12px;
    color: #409eff;
}

.history-date {
    font-size: 12px;
    color: #909399;
}

.empty-result {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 400px;
    color: #909399;
}

.empty-icon {
    font-size: 64px;
    color: #c0c4cc;
    margin-bottom: 16px;
}

.empty-text {
    font-size: 18px;
    margin-bottom: 8px;
}

.empty-hint {
    font-size: 14px;
    color: #c0c4cc;
}

.questions-result {
    padding: 20px 0;
}

.questions-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #e4e7ed;
}

.questions-info {
    display: flex;
    align-items: center;
    gap: 12px;
}

.questions-count {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
}

.questions-actions {
    display: flex;
    gap: 8px;
}

.questions-list {
    max-height: 600px;
    overflow-y: auto;
}

.question-item {
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    margin-bottom: 16px;
    background: white;
    transition: box-shadow 0.3s;
}

.question-item:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.question-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    background: #f8f9fa;
    border-bottom: 1px solid #e4e7ed;
    border-radius: 8px 8px 0 0;
}

.question-number {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    min-width: 30px;
}

.question-actions {
    margin-left: auto;
    display: flex;
    gap: 4px;
}

.question-content {
    padding: 16px;
}

.question-title {
    font-size: 14px;
    color: #303133;
    line-height: 1.6;
    margin-bottom: 12px;
}

.question-options {
    margin-bottom: 12px;
}

.option-item {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    padding: 4px 0;
}

.option-label {
    font-size: 14px;
    color: #409eff;
    font-weight: 500;
    min-width: 20px;
}

.option-content {
    font-size: 14px;
    color: #606266;
}

.question-answer, .question-explanation, .question-knowledge {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    margin-bottom: 8px;
}

.answer-label, .explanation-label, .knowledge-label {
    font-size: 12px;
    color: #909399;
    min-width: 40px;
}

.answer-content, .explanation-content {
    font-size: 14px;
    color: #303133;
    flex: 1;
}

.knowledge-tags {
    display: flex;
    gap: 4px;
    flex-wrap: wrap;
}

.preview-content {
    max-height: 500px;
    overflow-y: auto;
}

.preview-question {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #e4e7ed;
}

.preview-question:last-child {
    border-bottom: none;
}

.preview-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}

.preview-number {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
}

.preview-title {
    font-size: 14px;
    color: #303133;
    line-height: 1.6;
    margin-bottom: 12px;
}

.preview-options {
    margin-bottom: 12px;
}

.preview-option {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    padding: 4px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
    .main-content .el-col {
        margin-bottom: 24px;
    }
}

@media (max-width: 768px) {
    .question-generate-page {
        padding: 16px;
    }
    
    .header-content {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }
    
    .questions-header {
        flex-direction: column;
        gap: 12px;
        align-items: flex-start;
    }
    
    .question-header {
        flex-wrap: wrap;
        gap: 8px;
    }
}
</style> 