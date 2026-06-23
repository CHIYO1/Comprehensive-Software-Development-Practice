<template>
    <div class="auto-design-content">
        <!-- 主要内容区域 -->
        <el-row :gutter="24" class="main-content">
            <!-- 左侧配置区域 -->
            <el-col :span="8">
                <el-card class="config-card">
                    <template #header>
                        <div class="card-header">
                            <span>备课配置</span>
                        </div>
                    </template>
                    
                    <el-form :model="designForm" label-width="100px" class="design-form">
                        <!-- <el-form-item label="课程大纲:">
                            <el-upload 
                                v-model:file-list="designForm.files" 
                                :limit="3" 
                                :auto-upload="false" 
                                drag
                                multiple
                                accept=".pdf,.doc,.docx,.txt"
                            >
                                <div class="upload-placeholder">
                                    <el-icon class="upload-icon"><Upload /></el-icon>
                                    <div>拖动或<em>点击上传课程大纲</em></div>
                                    <div class="upload-hint">支持PDF、DOC、DOCX、TXT格式，最多3个文件</div>
                                </div>
                            </el-upload>
                        </el-form-item> -->

                        <el-form-item label="教学需求:">
                            <el-input 
                                v-model="designForm.demand" 
                                type="textarea" 
                                :rows="4"
                                placeholder="请输入具体的教学需求，如：重点难点、教学方法、时间安排、学生特点等"
                            />
                        </el-form-item>

                        <el-form-item label="学科类型:">
                            <el-select v-model="designForm.subject" placeholder="选择学科类型" style="width: 100%;">
                                <el-option label="编程开发" value="编程开发" />
                                <el-option label="软件工程" value="软件工程" />
                                <el-option label="理论教学" value="理论教学" />
                                <el-option label="综合实训" value="课程实训" />
                            </el-select>
                        </el-form-item>

                        <el-form-item label="课程难度:">
                            <el-rate v-model="designForm.difficulty" :max="5" show-text />
                        </el-form-item>

                        <el-form-item label="课时安排:">
                            <el-input-number v-model="designForm.totalHours" :min="1" :max="100" />
                            <span style="margin-left: 8px;">课时</span>
                        </el-form-item>

                        <el-form-item label="教学目标:">
                            <el-input-tag v-model="designForm.objectives" placeholder="输入目标后按回车确认" />
                        </el-form-item>

                        <el-form-item>
                            <el-button type="primary" @click="generateDesign" :loading="generating" style="width: 100%;">
                                <el-icon><MagicStick /></el-icon>
                                {{ generating ? '生成中...' : '开始生成备课方案' }}
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
                            <span>备课方案</span>
                            <div class="header-actions">
                                <el-button size="small" @click="clearResult">
                                    <el-icon><Delete /></el-icon>
                                    清空
                                </el-button>

                                <el-button size="small" type="primary" @click="exportDesign">
                                    <el-icon><Download /></el-icon>
                                    导出文档
                                </el-button>

                                <!-- <el-button size="small" type="primary" @click="applyToCourse">
                                    <el-icon><Check /></el-icon>
                                    应用到课程
                                </el-button> -->
                            </div>
                        </div>
                    </template>

                    <div v-if="!designResult && !generating" class="empty-result">
                        <el-icon class="empty-icon"><Document /></el-icon>
                        <div class="empty-text">请配置备课参数并点击生成按钮</div>
                        <div class="empty-hint">AI将根据您的需求生成个性化的备课方案</div>
                    </div>

                    <div v-if="generating" class="loading-result">
                        <el-icon class="loading-icon is-loading"><Loading /></el-icon>
                        <div class="loading-text">AI正在生成课程计划，请稍候...</div>
                    </div>

                    <div v-else class="design-result" v-html="renderedContent">
                        
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'

import { ElMessage } from 'element-plus'
import { 
    MagicStick, Delete, Document, 
    Download,Loading 
} from '@element-plus/icons-vue'

import { generateCoursePlan } from '@/api/ai'
import { marked } from 'marked'

// 渲染Markdown内容
const renderedContent = computed(() => {
    if (!designResult.value) return ''
    return marked(designResult.value)
})

const generating = ref(false)

// 备课表单
const designForm = ref({
    files: [],
    courseName: '',      // 添加课程名称
    description: '',     // 添加课程描述
    demand: '',
    subject: '',
    difficulty: 3,
    totalHours: 32,
    objectives: [],
    outline: ''          // 添加大纲字段
})


// 生成结果
const designResult = ref(null)



const generateDesign = async () => {
    if (!designForm.value.subject) {
        ElMessage.warning('请选择学科类型')
        return
    }

    generating.value = true
    designResult.value = null

    try {
        // 用已有字段构造课程名称
        const courseName = `${designForm.value.subject}课程`
        
        const result = await generateCoursePlan({
            courseName: courseName,
            description: designForm.value.demand || '',
            subject: designForm.value.subject,
            difficulty: designForm.value.difficulty,
            totalHours: designForm.value.totalHours,
            objectives: designForm.value.objectives,
            demand: designForm.value.demand,
            outline: ''
        })

        

        if (result.success) {
            designResult.value = result.content
            ElMessage.success('课程计划生成成功')
        } else {
            ElMessage.error(`生成失败: ${result.error}`)
        }
    } catch (error) {
        ElMessage.error('生成课程计划失败')
        console.error(error)
    } finally {
        generating.value = false
    }
}

const clearResult = () => {
    designResult.value = null
    ElMessage.info('已清空备课方案')
}

// 导出文档
const exportDesign = () => {
    if (!designResult.value) {
        ElMessage.warning('请先生成备课方案')
        return
    }

    const content = `
# ${designForm.value.courseName} 课程计划

生成时间：${new Date().toLocaleString()}
学科类型：${designForm.value.subject}
难度等级：${designForm.value.difficulty}/5
总课时：${designForm.value.totalHours} 课时

---

${designResult.value}
`

    const blob = new Blob([content], { type: 'text/markdown;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${designForm.value.courseName}_课程计划.md`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    
    ElMessage.success('课程计划导出成功')
}



// const applyToCourse = () => {
//     if (!designResult.value) {
//         ElMessage.warning('请先生成备课方案')
//         return
//     }
//     ElMessage.success('备课方案已应用到课程')
// }

onMounted(() => {
    // 初始化页面
})
</script>

<style scoped>
.auto-design-content {
    padding: 0;
    background: transparent;
    min-height: auto;
}

/* 滚动条美化 */
.auto-design-content::-webkit-scrollbar {
    width: 6px;
}

.auto-design-content::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.auto-design-content::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
}

.auto-design-content::-webkit-scrollbar-thumb:hover {
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

.design-form {
    padding: 20px 0;
}

.upload-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 120px;
    color: #606266;
}

.upload-icon {
    font-size: 32px;
    color: #c0c4cc;
    margin-bottom: 8px;
}

.upload-hint {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
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

.design-result {
    padding: 20px 0;
}

.result-section {
    margin-bottom: 32px;
}

.section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 2px solid #409eff;
}

.outline-content {
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
}

.chapter-item {
    margin-bottom: 20px;
}

.chapter-item:last-child {
    margin-bottom: 0;
}

.chapter-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;
}

.chapter-number {
    width: 24px;
    height: 24px;
    background: #409eff;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 600;
}

.chapter-title {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
    flex: 1;
}

.chapter-hours {
    font-size: 12px;
    color: #409eff;
    background: #ecf5ff;
    padding: 4px 8px;
    border-radius: 4px;
}

.chapter-content {
    margin-left: 36px;
}

.section-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 4px 0;
}

.section-number {
    font-size: 12px;
    color: #909399;
    min-width: 30px;
}

.section-title {
    font-size: 14px;
    color: #606266;
}

.suggestions-content {
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
}

.suggestion-item {
    margin-bottom: 16px;
}

.suggestion-item:last-child {
    margin-bottom: 0;
}

.suggestion-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}

.suggestion-icon {
    color: #409eff;
}

.suggestion-title {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
}

.suggestion-content {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
    margin-left: 24px;
}

.resources-content {
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
}

.resource-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: white;
    border-radius: 6px;
    margin-bottom: 12px;
    transition: box-shadow 0.3s;
}

.resource-item:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.resource-icon {
    font-size: 24px;
    color: #409eff;
}

.resource-title {
    font-size: 14px;
    color: #303133;
    margin-bottom: 4px;
}

.resource-type {
    font-size: 12px;
    color: #909399;
}

.assessment-content {
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
}

.assessment-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 12px 0;
    border-bottom: 1px solid #e4e7ed;
}

.assessment-item:last-child {
    border-bottom: none;
}

.assessment-title {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 4px;
}

.assessment-weight {
    font-size: 14px;
    color: #409eff;
    font-weight: 600;
}

.assessment-desc {
    font-size: 12px;
    color: #909399;
    flex: 1;
    margin-right: 16px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
    .main-content .el-col {
        margin-bottom: 24px;
    }
}

@media (max-width: 768px) {
    .auto-design-page {
        padding: 16px;
    }
    
    .header-content {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }
    
    .resources-content .el-col {
        margin-bottom: 12px;
    }
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

</style> 