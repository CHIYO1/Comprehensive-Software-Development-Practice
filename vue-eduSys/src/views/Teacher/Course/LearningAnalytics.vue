<template>
    <div class="learning-analytics-content">
        <!-- 页面头部 -->
        <div class="content-header">
            <div class="header-content">
                <div class="header-left">
                    <h2 class="page-title">学习分析</h2>
                </div>
                <div class="header-right">
                    <el-button type="primary" @click="exportAnalytics">
                        <el-icon><Download /></el-icon>
                        导出报告
                    </el-button>
                    <el-button type="success" @click="refreshData">
                        <el-icon><Refresh /></el-icon>
                        刷新数据
                    </el-button>
                </div>
            </div>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-section">
            <el-row :gutter="24">
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><User /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ analyticsData.totalStudents }}</div>
                                <div class="stat-label">总学生数</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><TrendCharts /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ analyticsData.avgProgress }}%</div>
                                <div class="stat-label">平均进度</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><Clock /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ analyticsData.avgStudyTime }}h</div>
                                <div class="stat-label">平均学习时长</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><Star /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ analyticsData.avgScore }}</div>
                                <div class="stat-label">平均成绩</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <!-- 主要内容区域 -->
        <el-row :gutter="24" class="main-content">
            <!-- 左侧图表 -->
            <el-col :span="16">
                <el-card class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>学习进度分布</span>
                            <el-select v-model="selectedChart" placeholder="选择图表类型" style="width: 150px;">
                                <el-option label="进度分布" value="progress" />
                                <el-option label="章节完成" value="sections" />
                                <el-option label="学习时长" value="time" />
                            </el-select>
                        </div>
                    </template>
                    <div class="chart-container">
                        <div class="chart-placeholder">
                            <el-icon class="chart-icon"><DataAnalysis /></el-icon>
                            <div class="chart-text">图表区域</div>
                            <div class="chart-hint">这里将显示学习进度分布图表</div>
                        </div>
                    </div>
                </el-card>

                <el-card class="chart-card" style="margin-top: 24px;">
                    <template #header>
                        <div class="card-header">
                            <span>章节完成情况</span>
                        </div>
                    </template>
                    <div class="section-progress">
                        <div v-for="section in sectionProgress" :key="section.id" class="section-item">
                            <div class="section-info">
                                <span class="section-name">{{ section.name }}</span>
                                <span class="section-progress-text">{{ section.completionRate }}%</span>
                            </div>
                            <el-progress 
                                :percentage="section.completionRate" 
                                :color="getProgressColor(section.completionRate)"
                                :stroke-width="8"
                            />
                        </div>
                    </div>
                </el-card>
            </el-col>

            <!-- 右侧详情 -->
            <el-col :span="8">
                <el-card class="detail-card">
                    <template #header>
                        <div class="card-header">
                            <span>学习概况</span>
                        </div>
                    </template>
                    <div class="overview-content">
                        <div class="overview-item">
                            <div class="overview-label">活跃学生</div>
                            <div class="overview-value">{{ analyticsData.activeStudents }}</div>
                        </div>
                        <div class="overview-item">
                            <div class="overview-label">完成课程</div>
                            <div class="overview-value">{{ analyticsData.completedStudents }}</div>
                        </div>
                        <div class="overview-item">
                            <div class="overview-label">平均登录次数</div>
                            <div class="overview-value">{{ analyticsData.avgLoginCount }}</div>
                        </div>
                        <div class="overview-item">
                            <div class="overview-label">课程评分</div>
                            <div class="overview-value">
                                <el-rate v-model="analyticsData.courseRating" disabled show-score />
                            </div>
                        </div>
                    </div>
                </el-card>

                <el-card class="detail-card" style="margin-top: 24px;">
                    <template #header>
                        <div class="card-header">
                            <span>学习排行榜</span>
                        </div>
                    </template>
                    <div class="ranking-list">
                        <div v-for="(student, index) in topStudents" :key="student.id" class="ranking-item">
                            <div class="ranking-number" :class="getRankingClass(index + 1)">{{ index + 1 }}</div>
                            <div class="student-info">
                                <div class="student-name">{{ student.name }}</div>
                                <div class="student-progress">进度: {{ student.progress }}%</div>
                            </div>
                            <div class="student-score">{{ student.score }}分</div>
                        </div>
                    </div>
                </el-card>

                <el-card class="detail-card" style="margin-top: 24px;">
                    <template #header>
                        <div class="card-header">
                            <span>学习建议</span>
                        </div>
                    </template>
                    <div class="suggestions">
                        <div v-for="suggestion in learningSuggestions" :key="suggestion.id" class="suggestion-item">
                            <el-icon class="suggestion-icon" :class="suggestion.type">
                                <component :is="suggestion.icon" />
                            </el-icon>
                            <div class="suggestion-content">
                                <div class="suggestion-title">{{ suggestion.title }}</div>
                                <div class="suggestion-desc">{{ suggestion.description }}</div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 详细数据表格 -->
        <el-card class="table-card" style="margin-top: 24px;">
            <template #header>
                <div class="card-header">
                    <span>学生详细数据</span>
                    <div class="header-actions">
                        <el-input
                            v-model="searchKeyword"
                            placeholder="搜索学生姓名"
                            style="width: 200px; margin-right: 16px;"
                            clearable
                        >
                            <template #prefix>
                                <el-icon><Search /></el-icon>
                            </template>
                        </el-input>
                        <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px;">
                            <el-option label="按进度排序" value="progress" />
                            <el-option label="按成绩排序" value="score" />
                            <el-option label="按学习时长排序" value="time" />
                        </el-select>
                    </div>
                </div>
            </template>

            <el-table :data="filteredStudentData" style="width: 80%" v-loading="loading">
                <el-table-column prop="name" label="学生姓名" width="120" />
                <el-table-column prop="studentId" label="学号" width="120" />
                <el-table-column prop="progress" label="学习进度" width="150">
                    <template #default="{ row }">
                        <el-progress :percentage="row.progress" :color="getProgressColor(row.progress)" />
                    </template>
                </el-table-column>
                <el-table-column prop="score" label="平均成绩" width="100" align="center" />
                <el-table-column prop="studyTime" label="学习时长(h)" width="120" align="center" />
                <el-table-column prop="loginCount" label="登录次数" width="100" align="center" />
                <el-table-column prop="lastLogin" label="最后登录" width="180" />
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" type="primary" @click="viewStudentDetail(row)">
                            <el-icon><View /></el-icon>
                            详情
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

import { ElMessage } from 'element-plus'
import { 
    User, TrendCharts, Clock, Star, Download, Refresh, DataAnalysis,
    Search, View
} from '@element-plus/icons-vue'




const loading = ref(false)
const searchKeyword = ref('')
const sortBy = ref('progress')
const selectedChart = ref('progress')

// 分析数据
const analyticsData = ref({
    totalStudents: 156,
    activeStudents: 142,
    avgProgress: 78,
    avgStudyTime: 12.5,
    avgScore: 85.6,
    completedStudents: 89,
    avgLoginCount: 15,
    courseRating: 4.8
})

// 章节进度
const sectionProgress = ref([
    { id: 1, name: '第一章：课程介绍', completionRate: 95 },
    { id: 2, name: '第二章：基础知识', completionRate: 87 },
    { id: 3, name: '第三章：进阶内容', completionRate: 72 },
    { id: 4, name: '第四章：实战项目', completionRate: 65 },
    { id: 5, name: '第五章：总结与展望', completionRate: 58 }
])

// 学习排行榜
const topStudents = ref([
    { id: 1, name: '张三', progress: 95, score: 92 },
    { id: 2, name: '李四', progress: 92, score: 89 },
    { id: 3, name: '王五', progress: 88, score: 87 },
    { id: 4, name: '赵六', progress: 85, score: 84 },
    { id: 5, name: '钱七', progress: 82, score: 81 }
])

// 学习建议
const learningSuggestions = ref([
    {
        id: 1,
        type: 'warning',
        icon: 'Warning',
        title: '进度较慢的学生',
        description: '有15名学生进度低于60%，建议重点关注并提供额外辅导'
    },
    {
        id: 2,
        type: 'success',
        icon: 'Success',
        title: '学习效果良好',
        description: '大部分学生能够按时完成学习任务，整体表现优秀'
    },
    {
        id: 3,
        type: 'info',
        icon: 'Info',
        title: '互动性提升',
        description: '建议增加更多互动环节，提高学生参与度'
    }
])

// 学生详细数据
const studentData = ref([
    { id: 1, name: '张三', studentId: '2021001', progress: 95, score: 92, studyTime: 15.2, loginCount: 20, lastLogin: '2024-01-15 10:30' },
    { id: 2, name: '李四', studentId: '2021002', progress: 92, score: 89, studyTime: 14.8, loginCount: 18, lastLogin: '2024-01-15 09:15' },
    { id: 3, name: '王五', studentId: '2021003', progress: 88, score: 87, studyTime: 13.5, loginCount: 16, lastLogin: '2024-01-14 16:20' },
    { id: 4, name: '赵六', studentId: '2021004', progress: 85, score: 84, studyTime: 12.8, loginCount: 15, lastLogin: '2024-01-14 14:30' },
    { id: 5, name: '钱七', studentId: '2021005', progress: 82, score: 81, studyTime: 11.9, loginCount: 14, lastLogin: '2024-01-14 11:45' }
])

// 计算属性
const filteredStudentData = computed(() => {
    let filtered = studentData.value

    if (searchKeyword.value) {
        filtered = filtered.filter(student => 
            student.name.includes(searchKeyword.value) || 
            student.studentId.includes(searchKeyword.value)
        )
    }

    // 排序
    filtered.sort((a, b) => {
        switch (sortBy.value) {
            case 'progress':
                return b.progress - a.progress
            case 'score':
                return b.score - a.score
            case 'time':
                return b.studyTime - a.studyTime
            default:
                return 0
        }
    })

    return filtered
})



const getProgressColor = (progress) => {
    if (progress >= 80) return '#67C23A'
    if (progress >= 60) return '#E6A23C'
    return '#F56C6C'
}

const getRankingClass = (rank) => {
    if (rank === 1) return 'rank-first'
    if (rank === 2) return 'rank-second'
    if (rank === 3) return 'rank-third'
    return 'rank-normal'
}

const loadAnalyticsData = async () => {
    loading.value = true
    try {
        // 这里应该调用实际的API
        // const response = await request.get(`/api/courses/${courseId.value}/analytics`)
        
        // 模拟数据加载
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success('数据加载完成')
    } catch (error) {
        ElMessage.error('加载分析数据失败')
    } finally {
        loading.value = false
    }
}

const refreshData = () => {
    loadAnalyticsData()
}

const exportAnalytics = () => {
    ElMessage.success('分析报告导出成功')
    // 这里可以实现报告导出功能
}

const viewStudentDetail = (student) => {
    ElMessage.info(`查看学生 ${student.name} 的详细数据`)
    // 这里可以跳转到学生详情页面
}

onMounted(() => {
    loadAnalyticsData()
})
</script>

<style scoped>
.learning-analytics-content {
    padding: 0;
    background: transparent;
    min-height: auto;
}

/* 滚动条美化 */
.learning-analytics-content::-webkit-scrollbar {
    width: 6px;
}

.learning-analytics-content::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.learning-analytics-content::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
}

.learning-analytics-content::-webkit-scrollbar-thumb:hover {
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

.stats-section {
    margin-bottom: 24px;
}

.stat-card {
    border-radius: 8px;
    transition: all 0.3s ease;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
    display: flex;
    align-items: center;
    gap: 16px;
}

.stat-icon {
    font-size: 32px;
    color: #409eff;
}

.stat-info {
    flex: 1;
}

.stat-number {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    line-height: 1;
}

.stat-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
}

.main-content {
    margin-bottom: 24px;
}

.chart-card, .detail-card, .table-card {
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
    align-items: center;
}

.chart-container {
    height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.chart-placeholder {
    text-align: center;
    color: #909399;
}

.chart-icon {
    font-size: 64px;
    color: #c0c4cc;
    margin-bottom: 16px;
}

.chart-text {
    font-size: 18px;
    margin-bottom: 8px;
}

.chart-hint {
    font-size: 14px;
    color: #c0c4cc;
}

.section-progress {
    padding: 20px 0;
}

.section-item {
    margin-bottom: 20px;
}

.section-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.section-name {
    font-size: 14px;
    color: #303133;
}

.section-progress-text {
    font-size: 14px;
    color: #409eff;
    font-weight: 500;
}

.overview-content {
    padding: 20px 0;
}

.overview-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
}

.overview-item:last-child {
    border-bottom: none;
}

.overview-label {
    font-size: 14px;
    color: #606266;
}

.overview-value {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
}

.ranking-list {
    padding: 20px 0;
}

.ranking-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
}

.ranking-item:last-child {
    border-bottom: none;
}

.ranking-number {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    margin-right: 12px;
}

.rank-first {
    background: #ffd700;
    color: #fff;
}

.rank-second {
    background: #c0c0c0;
    color: #fff;
}

.rank-third {
    background: #cd7f32;
    color: #fff;
}

.rank-normal {
    background: #f5f5f5;
    color: #606266;
}

.student-info {
    flex: 1;
}

.student-name {
    font-size: 14px;
    color: #303133;
    margin-bottom: 4px;
}

.student-progress {
    font-size: 12px;
    color: #909399;
}

.student-score {
    font-size: 16px;
    font-weight: 500;
    color: #409eff;
}

.suggestions {
    padding: 20px 0;
}

.suggestion-item {
    display: flex;
    align-items: flex-start;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
}

.suggestion-item:last-child {
    border-bottom: none;
}

.suggestion-icon {
    font-size: 20px;
    margin-right: 12px;
    margin-top: 2px;
}

.suggestion-icon.warning {
    color: #e6a23c;
}

.suggestion-icon.success {
    color: #67c23a;
}

.suggestion-icon.info {
    color: #409eff;
}

.suggestion-content {
    flex: 1;
}

.suggestion-title {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 4px;
}

.suggestion-desc {
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 1200px) {
    .main-content .el-col {
        margin-bottom: 24px;
    }
}

@media (max-width: 768px) {
    .learning-analytics-page {
        padding: 16px;
    }
    
    .header-content {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }
    
    .header-actions {
        flex-direction: column;
        gap: 12px;
        width: 100%;
    }
    
    .stats-section .el-col {
        margin-bottom: 16px;
    }
    
    .chart-container {
        height: 300px;
    }
}
</style> 