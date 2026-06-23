<template>
    <div class="question-generate-content">
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
            </el-col>

            <!-- 右侧结果区域 -->
            <el-col :span="16">
                <el-card class="result-card">
                    <template #header>
                        <div class="card-header">
                            <span>生成结果</span>
                            <div class="header-actions">
                                <el-button size="small" @click="clearResult" :disabled="!generatedContent && !generating">
                                    <el-icon><Delete /></el-icon>
                                    清空
                                </el-button>
                                <el-button size="small" type="primary" @click="exportQuestions" :disabled="!generatedContent || generating">
                                    <el-icon><Download /></el-icon>
                                    导出Markdown
                                </el-button>
                            </div>
                        </div>
                    </template>

                    <!-- 空状态 -->
                    <div v-if="!generatedContent && !generating" class="empty-result">
                        <el-icon class="empty-icon"><EditPen /></el-icon>
                        <div class="empty-text">请配置生成参数并点击生成按钮</div>
                        <div class="empty-hint">AI将根据您的需求生成高质量的题目</div>
                    </div>

                    <!-- 加载状态 -->
                    <div v-if="generating" class="loading-result">
                        <el-icon class="loading-icon is-loading"><Loading /></el-icon>
                        <div class="loading-text">AI正在生成题目，请稍候...</div>
                        <div class="loading-hint">预计需要10-30秒，请耐心等待</div>
                    </div>

                    <!-- 结果显示 -->
                    <div v-else-if="generatedContent" class="design-result" v-html="renderedContent"></div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

import { ElMessage } from 'element-plus'
import { 
    EditPen, Delete, Loading, Download
} from '@element-plus/icons-vue'
import { generateExercises } from '@/api/ai'
import { marked } from 'marked'

const generating = ref(false)

// 生成表单
const generateForm = ref({
    questionTypes: ['choice'],
    difficulty: 'medium',
    count: 10,
    knowledgePoints: [],
    requirements: ''
})

// 生成的题目内容（Markdown 格式）
const generatedContent = ref('')

// 计算属性：渲染 Markdown 为 HTML
const renderedContent = computed(() => {
    if (!generatedContent.value) return ''
    return marked(generatedContent.value)
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

// 生成题目
const generateQuestions = async () => {
    if (!generateForm.value.questionTypes.length) {
        ElMessage.warning('请选择题目类型')
        return
    }

    generating.value = true
    generatedContent.value = ''
    
    try {       
        const result = await generateExercises({
            courseId: 1,
            chapterId: null,
            questionTypes: generateForm.value.questionTypes,
            difficulty: generateForm.value.difficulty,
            count: generateForm.value.count,
            knowledgePoints: generateForm.value.knowledgePoints,
            requirements: generateForm.value.requirements,
            courseName: '当前课程名称',
        })

        if (result.success) {
            generatedContent.value = result.content
            ElMessage.success('题目生成成功')
        } else {
            ElMessage.error(`生成失败: ${result.error}`)
        }
    } catch (error) {
        ElMessage.error('生成题目失败')
        console.error(error)
    } finally {
        generating.value = false
    }
}

// 清空结果
const clearResult = () => {
    generatedContent.value = ''
    ElMessage.info('已清空生成结果')
}

// 导出 Markdown 文档
const exportQuestions = () => {
    if (!generatedContent.value) {
        ElMessage.warning('请先生成题目')
        return
    }

    const content = `# 练习题

生成时间：${new Date().toLocaleString()}
题目类型：${generateForm.value.questionTypes.map(t => getQuestionTypeText(t)).join('、')}
难度等级：${getDifficultyText(generateForm.value.difficulty)}
题目数量：${generateForm.value.count}

---

${generatedContent.value}
`

    const blob = new Blob([content], { type: 'text/markdown;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `练习题_${new Date().toLocaleDateString()}.md`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    
    ElMessage.success('题目导出成功')
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

.main-content {
    margin-bottom: 24px;
}

.config-card, .result-card {
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

.loading-result {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 400px;
    padding: 40px;
}

.loading-icon {
    font-size: 48px;
    color: #409eff;
    margin-bottom: 16px;
}

.loading-icon.is-loading {
    animation: rotating 2s linear infinite;
}

@keyframes rotating {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}

.loading-text {
    font-size: 16px;
    color: #606266;
    margin-bottom: 8px;
}

.loading-hint {
    font-size: 13px;
    color: #909399;
}

.design-result {
    padding: 20px 0;
    max-height: 600px;
    overflow-y: auto;
}

.design-result :deep(h1) {
    font-size: 24px;
    color: #303133;
    margin: 20px 0 16px;
    border-bottom: 2px solid #409eff;
    padding-bottom: 8px;
}

.design-result :deep(h2) {
    font-size: 20px;
    color: #303133;
    margin: 16px 0 12px;
}

.design-result :deep(h3) {
    font-size: 16px;
    color: #303133;
    margin: 12px 0 8px;
}

.design-result :deep(p) {
    line-height: 1.8;
    color: #606266;
    margin: 8px 0;
}

.design-result :deep(ul), 
.design-result :deep(ol) {
    padding-left: 24px;
    margin: 8px 0;
}

.design-result :deep(li) {
    line-height: 1.8;
    color: #606266;
}

.design-result :deep(hr) {
    border: none;
    border-top: 1px solid #e4e7ed;
    margin: 16px 0;
}

.design-result :deep(strong) {
    color: #303133;
}

.design-result :deep(code) {
    background: #f4f6f9;
    padding: 2px 6px;
    border-radius: 3px;
    font-family: monospace;
}

.design-result :deep(pre) {
    background: #f4f6f9;
    padding: 12px;
    border-radius: 6px;
    overflow-x: auto;
    margin: 8px 0;
}

.design-result :deep(pre code) {
    background: transparent;
    padding: 0;
}

.design-result :deep(table) {
    width: 100%;
    border-collapse: collapse;
    margin: 12px 0;
}

.design-result :deep(th),
.design-result :deep(td) {
    border: 1px solid #dcdfe6;
    padding: 8px 12px;
    text-align: left;
}

.design-result :deep(th) {
    background: #f5f7fa;
    font-weight: 600;
}

.design-result :deep(blockquote) {
    border-left: 4px solid #409eff;
    padding: 8px 16px;
    margin: 8px 0;
    background: #f4f6f9;
    color: #606266;
}

/* 响应式设计 */
@media (max-width: 1200px) {
    .main-content .el-col {
        margin-bottom: 24px;
    }
}

@media (max-width: 768px) {
    .header-content {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }
}
</style>