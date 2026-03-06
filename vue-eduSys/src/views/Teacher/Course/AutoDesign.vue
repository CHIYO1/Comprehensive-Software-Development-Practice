<template>
    <div class="auto-design-content">
        <!-- 页面头部 -->
        <div class="content-header">
            <div class="header-content">
                <div class="header-left">
                    <h2 class="page-title">智能备课助手</h2>
                </div>
                <div class="header-right">
                    <el-button type="primary" @click="saveDesign">
                        <el-icon><Check /></el-icon>
                        保存方案
                    </el-button>
                    <el-button type="success" @click="exportDesign">
                        <el-icon><Download /></el-icon>
                        导出方案
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
                            <span>备课配置</span>
                        </div>
                    </template>
                    
                    <el-form :model="designForm" label-width="100px" class="design-form">
                        <el-form-item label="课程大纲:">
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
                        </el-form-item>

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
                                <el-option label="计算机科学" value="cs" />
                                <el-option label="数学" value="math" />
                                <el-option label="物理" value="physics" />
                                <el-option label="化学" value="chemistry" />
                                <el-option label="生物" value="biology" />
                                <el-option label="文学" value="literature" />
                                <el-option label="历史" value="history" />
                                <el-option label="艺术" value="art" />
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

                <!-- 历史方案 -->
                <el-card class="history-card" style="margin-top: 24px;">
                    <template #header>
                        <div class="card-header">
                            <span>历史方案</span>
                        </div>
                    </template>
                    
                    <div class="history-list">
                        <div v-for="item in historyDesigns" :key="item.id" class="history-item" @click="loadHistoryDesign(item)">
                            <div class="history-info">
                                <div class="history-title">{{ item.title }}</div>
                                <div class="history-date">{{ item.createTime }}</div>
                            </div>
                            <el-button size="small" type="text" @click.stop="deleteHistoryDesign(item)">
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
                            <span>备课方案</span>
                            <div class="header-actions">
                                <el-button size="small" @click="clearResult">
                                    <el-icon><Delete /></el-icon>
                                    清空
                                </el-button>
                                <el-button size="small" type="primary" @click="applyToCourse">
                                    <el-icon><Check /></el-icon>
                                    应用到课程
                                </el-button>
                            </div>
                        </div>
                    </template>

                    <div v-if="!designResult" class="empty-result">
                        <el-icon class="empty-icon"><Document /></el-icon>
                        <div class="empty-text">请配置备课参数并点击生成按钮</div>
                        <div class="empty-hint">AI将根据您的需求生成个性化的备课方案</div>
                    </div>

                    <div v-else class="design-result">
                        <!-- 课程大纲 -->
                        <div class="result-section">
                            <h3 class="section-title">
                                <el-icon><Document /></el-icon>
                                课程大纲
                            </h3>
                            <div class="outline-content">
                                <div v-for="(chapter, index) in designResult.outline" :key="index" class="chapter-item">
                                    <div class="chapter-header">
                                        <span class="chapter-number">{{ index + 1 }}</span>
                                        <span class="chapter-title">{{ chapter.title }}</span>
                                        <span class="chapter-hours">{{ chapter.hours }}课时</span>
                                    </div>
                                    <div class="chapter-content">
                                        <div v-for="(section, sIndex) in chapter.sections" :key="sIndex" class="section-item">
                                            <span class="section-number">{{ index + 1 }}.{{ sIndex + 1 }}</span>
                                            <span class="section-title">{{ section.title }}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 教学建议 -->
                        <div class="result-section">
                            <h3 class="section-title">
                                <el-icon><Lightbulb /></el-icon>
                                教学建议
                            </h3>
                            <div class="suggestions-content">
                                <div v-for="(suggestion, index) in designResult.suggestions" :key="index" class="suggestion-item">
                                    <div class="suggestion-header">
                                        <el-icon class="suggestion-icon"><InfoFilled /></el-icon>
                                        <span class="suggestion-title">{{ suggestion.title }}</span>
                                    </div>
                                    <div class="suggestion-content">{{ suggestion.content }}</div>
                                </div>
                            </div>
                        </div>

                        <!-- 教学资源 -->
                        <div class="result-section">
                            <h3 class="section-title">
                                <el-icon><Folder /></el-icon>
                                推荐资源
                            </h3>
                            <div class="resources-content">
                                <el-row :gutter="16">
                                    <el-col :span="8" v-for="resource in designResult.resources" :key="resource.id">
                                        <div class="resource-item">
                                            <div class="resource-icon">
                                                <el-icon><Document /></el-icon>
                                            </div>
                                            <div class="resource-info">
                                                <div class="resource-title">{{ resource.title }}</div>
                                                <div class="resource-type">{{ resource.type }}</div>
                                            </div>
                                        </div>
                                    </el-col>
                                </el-row>
                            </div>
                        </div>

                        <!-- 评估方案 -->
                        <div class="result-section">
                            <h3 class="section-title">
                                <el-icon><Star /></el-icon>
                                评估方案
                            </h3>
                            <div class="assessment-content">
                                <div v-for="(assessment, index) in designResult.assessments" :key="index" class="assessment-item">
                                    <div class="assessment-header">
                                        <span class="assessment-title">{{ assessment.title }}</span>
                                        <span class="assessment-weight">{{ assessment.weight }}%</span>
                                    </div>
                                    <div class="assessment-desc">{{ assessment.description }}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

import { ElMessage, ElMessageBox } from 'element-plus'
import { 
    Check, Download, Upload, MagicStick, Delete, Document, 
    Lightbulb, InfoFilled, Folder, Star 
} from '@element-plus/icons-vue'





const generating = ref(false)

// 备课表单
const designForm = ref({
    files: [],
    demand: '',
    subject: '',
    difficulty: 3,
    totalHours: 32,
    objectives: []
})

// 历史方案
const historyDesigns = ref([
    { id: 1, title: 'Vue.js基础教学方案', createTime: '2024-01-15 10:30' },
    { id: 2, title: 'React进阶课程设计', createTime: '2024-01-14 15:20' },
    { id: 3, title: '前端工程化实践', createTime: '2024-01-13 09:15' }
])

// 生成结果
const designResult = ref(null)



const generateDesign = async () => {
    if (!designForm.value.demand || !designForm.value.subject) {
        ElMessage.warning('请填写教学需求和选择学科类型')
        return
    }

    generating.value = true
    try {
        // 这里应该调用实际的API
        // const response = await request.post('/api/auto-design/generate', designForm.value)
        
        // 模拟生成过程
        await new Promise(resolve => setTimeout(resolve, 3000))
        
        // 模拟结果
        designResult.value = {
            outline: [
                {
                    title: '第一章：课程介绍与基础概念',
                    hours: 4,
                    sections: [
                        { title: '课程概述' },
                        { title: '开发环境搭建' },
                        { title: '基础语法介绍' }
                    ]
                },
                {
                    title: '第二章：核心概念深入',
                    hours: 8,
                    sections: [
                        { title: '组件化开发' },
                        { title: '数据绑定' },
                        { title: '生命周期' },
                        { title: '事件处理' }
                    ]
                },
                {
                    title: '第三章：实战项目',
                    hours: 12,
                    sections: [
                        { title: '项目需求分析' },
                        { title: '架构设计' },
                        { title: '功能实现' },
                        { title: '测试与优化' }
                    ]
                }
            ],
            suggestions: [
                {
                    title: '教学方法建议',
                    content: '建议采用项目驱动教学法，通过实际项目开发来巩固理论知识。'
                },
                {
                    title: '重点难点提示',
                    content: '组件通信和状态管理是学生容易困惑的地方，需要重点讲解。'
                },
                {
                    title: '时间分配建议',
                    content: '理论讲解占30%，实践操作占50%，项目实战占20%。'
                }
            ],
            resources: [
                { id: 1, title: 'Vue.js官方文档', type: '文档' },
                { id: 2, title: 'Vue CLI使用指南', type: '教程' },
                { id: 3, title: 'Vue Router实战', type: '视频' }
            ],
            assessments: [
                { title: '课堂参与度', weight: 20, description: '根据学生课堂提问和讨论参与情况评分' },
                { title: '作业完成质量', weight: 30, description: '评估学生课后作业的完成情况和质量' },
                { title: '项目实战', weight: 50, description: '通过实际项目开发来评估学生的综合能力' }
            ]
        }
        
        ElMessage.success('备课方案生成成功')
    } catch (error) {
        ElMessage.error('生成备课方案失败')
    } finally {
        generating.value = false
    }
}

const saveDesign = () => {
    if (!designResult.value) {
        ElMessage.warning('请先生成备课方案')
        return
    }
    ElMessage.success('备课方案保存成功')
}

const exportDesign = () => {
    if (!designResult.value) {
        ElMessage.warning('请先生成备课方案')
        return
    }
    ElMessage.success('备课方案导出成功')
}

const clearResult = () => {
    designResult.value = null
    ElMessage.info('已清空备课方案')
}

const applyToCourse = () => {
    if (!designResult.value) {
        ElMessage.warning('请先生成备课方案')
        return
    }
    ElMessage.success('备课方案已应用到课程')
}

const loadHistoryDesign = (design) => {
    ElMessage.info(`加载历史方案：${design.title}`)
    // 这里可以加载历史方案到表单中
}

const deleteHistoryDesign = async (design) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除方案"${design.title}"吗？`,
            '确认删除',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        )
        
        const index = historyDesigns.value.findIndex(item => item.id === design.id)
        if (index > -1) {
            historyDesigns.value.splice(index, 1)
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
</style> 