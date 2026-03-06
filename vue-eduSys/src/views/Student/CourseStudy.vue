<!-- 学生浏览课程详情界面 -->
<template>
    <div class="course-study-page">
        <!-- 页面头部 -->
        <div class="page-header">
            <div class="header-content">
                <div class="header-left">
                    <el-button @click="goBack" type="text" class="back-btn">
                        <el-icon><ArrowLeft /></el-icon>
                        返回课程列表
                    </el-button>
                    <h1 class="page-title">{{ courseDetail.course.courseName || '课程学习' }}</h1>
                </div>
            </div>
        </div>

        <!-- 学习进度卡片 -->
        <div class="progress-section">
            <el-row :gutter="24">
                <el-col :span="6">
                    <el-card class="progress-card" shadow="hover">
                        <div class="progress-content">
                            <el-icon class="progress-icon"><Clock /></el-icon>
                            <div class="progress-info">
                                <div class="progress-number">{{ studyProgress.totalTime }}</div>
                                <div class="progress-label">学习时长</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="progress-card" shadow="hover">
                        <div class="progress-content">
                            <el-icon class="progress-icon"><Document /></el-icon>
                            <div class="progress-info">
                                <div class="progress-number">{{ studyProgress.completedSections }}</div>
                                <div class="progress-label">已完成章节</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="progress-card" shadow="hover">
                        <div class="progress-content">
                            <el-icon class="progress-icon"><EditPen /></el-icon>
                            <div class="progress-info">
                                <div class="progress-number">{{ studyProgress.completedExercises }}</div>
                                <div class="progress-label">已完成练习</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="progress-card" shadow="hover">
                        <div class="progress-content">
                            <el-icon class="progress-icon"><Star /></el-icon>
                            <div class="progress-info">
                                <div class="progress-number">{{ studyProgress.avgScore }}</div>
                                <div class="progress-label">平均得分</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <!-- 主要内容区域 -->
        <el-container class="main-container">
            <!-- 左侧学习工具 -->
            <el-aside class="sidebar">
                <!-- 学习工具 -->
                <el-card class="learning-tools-card">
                    <template #header>
                        <div class="card-header">
                            <span>学习工具</span>
                        </div>
                    </template>
                    <div class="learning-tools">
                        <el-button 
                            type="default"
                            @click="goToCourseInfo" 
                            class="tool-btn"
                            :class="{ 'active': currentActiveTab === 'courseInfo' }"
                        >
                            <el-icon><Notebook /></el-icon>
                            课程详情
                        </el-button>
                        <el-button 
                            type="default"
                            @click="goToStudyNotes" 
                            class="tool-btn"
                            :class="{ 'active': currentActiveTab === 'studyNotes' }"
                        >
                            <el-icon><Memo /></el-icon>
                            学习笔记
                        </el-button>
                        <el-button 
                            type="default"
                            @click="goToCourseDiscussion" 
                            class="tool-btn"
                            :class="{ 'active': currentActiveTab === 'courseDiscussion' }"
                        >
                            <el-icon><ChatDotRound /></el-icon>
                            课程讨论
                        </el-button>
                        <el-button 
                            type="default"
                            @click="goToLearningAssistant" 
                            class="tool-btn"
                            :class="{ 'active': currentActiveTab === 'learningAssistant' }"
                        >
                            <el-icon><Service /></el-icon>
                            学习助手
                        </el-button>
                        <el-button 
                            type="default"
                            @click="goToPractice" 
                            class="tool-btn"
                            :class="{ 'active': currentActiveTab === 'practice' }"
                        >
                            <el-icon><EditPen /></el-icon>
                            实时练习
                        </el-button>
                        <el-button type="default" @click="exportNotes" class="tool-btn">
                            <el-icon><Download /></el-icon>
                            导出笔记
                        </el-button>
                    </div>
                </el-card>
            </el-aside>

            <!-- 右侧内容 -->
            <el-main class="main-content">
                <div ref="mainRef" class="content-scroll">
                    <!-- 子路由内容 -->
                    <router-view :courseDetail="courseDetail" />
                </div>
            </el-main>
        </el-container>

        <!-- 弹窗组件 -->
        <VideoPlayDialog v-model:visible="VideoPlayVisable" :videoUrl="VideoPlayingUrl" />
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
    Document, EditPen, Notebook, ArrowLeft, Memo, 
    ChatDotRound, Clock, Download, Star, Service 
} from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { RESOURCE_TYPES } from '@/constants/resourceTypes'
import VideoPlayDialog from '@/components/file/VideoPlay.vue';
import { RECORD_QUESTION_TYPES } from '@/constants/recordQuestionTypes'

const route = useRoute()
const router = useRouter()
const courseId = ref(null)
const currentActiveTab = ref('courseInfo') // 默认选中课程详情

const mainRef = ref(null)

// 学习进度
const studyProgress = ref({
    totalTime: '12小时30分',
    completedSections: 3,
    completedExercises: 8,
    avgScore: 85,
    overallProgress: 65
})

// 课程完整信息
const courseDetail = ref({
    'course': {
        'courseName': '',
        'courseDesc': '',
        'coverPath': null,
        'tags': []
    },
    'sectionList': []
})

// 小节图标映射 - 暂时注释掉，因为当前未使用
// const iconMap = {
//     [RESOURCE_TYPES.DOCUMENT]: Document,
//     [RESOURCE_TYPES.VIDEO]: VideoPlay,
//     [RESOURCE_TYPES.QUESTIONS]: EditPen
// }

// const typeMap = {
//     [RECORD_QUESTION_TYPES.UNSUBMIT]: 'info',
//     [RECORD_QUESTION_TYPES.GRADING]: 'warning',
//     [RECORD_QUESTION_TYPES.GRADED]: 'success'
// }

// 获取图标的函数方法 - 暂时注释掉，因为当前未使用
// const getIconComponent = (type) => {
//     return iconMap[type] || Document
// }

onMounted(async () => {
    courseId.value = route.params.courseId
    if (!courseId.value) {
        ElMessage.error("课程ID不存在")
        router.go(-1);
    } else {
        getCourseDetail()
        // 根据当前路由设置默认选中的标签
        updateActiveTab()
    }
})

// 监听路由变化，更新选中的标签
watch(() => route.name, () => {
    updateActiveTab()
})

// 更新当前选中的标签
const updateActiveTab = () => {
    const routeName = route.name
    if (routeName === 'courseInfo') {
        currentActiveTab.value = 'courseInfo'
    } else if (routeName === 'studyNotes') {
        currentActiveTab.value = 'studyNotes'
    } else if (routeName === 'courseDiscussion') {
        currentActiveTab.value = 'courseDiscussion'
    } else if (routeName === 'learningAssistant') {
        currentActiveTab.value = 'learningAssistant'
    } else if (routeName === 'practice') {
        currentActiveTab.value = 'practice'
    } else {
        // 默认选中课程详情
        currentActiveTab.value = 'courseInfo'
    }
}

// 获取课程信息
const getCourseDetail = async () => {
    try {
        const response = await request.post('/course/queryCourseDetail', courseId.value)
        courseDetail.value = response.data.data;
        courseDetail.value.sectionList.forEach(section => {
            section.subsectionList.forEach(subsection => {
                if (subsection.subsectionType == RESOURCE_TYPES.QUESTIONS) {
                    request.post('/doQuestion/queryRecordSetBySubsection', subsection.subsectionId).then(res => {
                        subsection.state = res.data.data.state;
                        subsection.setRecordId = res.data.data.setRecordId;
                    })
                }
            });
        });
    } catch (error) {
        // 使用模拟数据
        courseDetail.value = {
            course: {
                courseName: 'Vue.js 前端开发实战',
                courseDesc: '本课程将带领大家深入学习Vue.js框架，从基础语法到高级特性，通过实战项目掌握前端开发技能。课程内容涵盖Vue.js核心概念、组件化开发、路由管理、状态管理等重要知识点。',
                coverPath: null,
                tags: ['Vue.js', '前端开发', 'JavaScript', '组件化']
            },
            sectionList: [
                {
                    section: {
                        sectionId: 1,
                        sectionName: 'Vue.js 基础入门',
                        sectionDesc: '学习Vue.js的基本概念和核心特性，包括Vue实例、模板语法、响应式数据等基础知识。'
                    },
                    subsectionList: [
                        {
                            subsectionId: 1,
                            subsectionName: 'Vue.js 简介',
                            subsectionDesc: '了解Vue.js的发展历史和基本概念',
                            subsectionType: RESOURCE_TYPES.VIDEO,
                            resourceId: 1,
                            state: RECORD_QUESTION_TYPES.GRADED
                        },
                        {
                            subsectionId: 2,
                            subsectionName: 'Vue实例创建',
                            subsectionDesc: '学习如何创建和配置Vue实例',
                            subsectionType: RESOURCE_TYPES.DOCUMENT,
                            resourceId: 2,
                            state: RECORD_QUESTION_TYPES.GRADED
                        },
                        {
                            subsectionId: 3,
                            subsectionName: '基础语法练习',
                            subsectionDesc: '练习Vue.js基础语法和指令',
                            subsectionType: RESOURCE_TYPES.QUESTIONS,
                            resourceId: 3,
                            state: RECORD_QUESTION_TYPES.GRADED
                        }
                    ]
                },
                {
                    section: {
                        sectionId: 2,
                        sectionName: '组件化开发',
                        sectionDesc: '掌握Vue.js组件化开发的核心概念，包括组件注册、组件通信、生命周期等。'
                    },
                    subsectionList: [
                        {
                            subsectionId: 4,
                            subsectionName: '组件基础',
                            subsectionDesc: '学习Vue组件的基本用法',
                            subsectionType: RESOURCE_TYPES.VIDEO,
                            resourceId: 4,
                            state: RECORD_QUESTION_TYPES.IN_PROGRESS
                        },
                        {
                            subsectionId: 5,
                            subsectionName: '组件通信',
                            subsectionDesc: '学习父子组件间的通信方式',
                            subsectionType: RESOURCE_TYPES.DOCUMENT,
                            resourceId: 5,
                            state: RECORD_QUESTION_TYPES.UNSUBMIT
                        }
                    ]
                }
            ]
        }
    }
}

// 返回上一页
const goBack = () => {
    router.go(-1)
}

// 导出笔记
const exportNotes = () => {
    ElMessage.success('笔记导出功能开发中...')
}

// 跳转到子页面
const goToCourseInfo = () => {
    currentActiveTab.value = 'courseInfo'
    router.push(`/courseStudy/${courseId.value}/courseInfo`)
}

const goToStudyNotes = () => {
    currentActiveTab.value = 'studyNotes'
    router.push(`/courseStudy/${courseId.value}/studyNotes`)
}

const goToCourseDiscussion = () => {
    currentActiveTab.value = 'courseDiscussion'
    router.push(`/courseStudy/${courseId.value}/courseDiscussion`)
}

const goToLearningAssistant = () => {
    currentActiveTab.value = 'learningAssistant'
    router.push(`/courseStudy/${courseId.value}/learningAssistant`)
}

const goToPractice = () => {
    currentActiveTab.value = 'practice'
    router.push(`/courseStudy/${courseId.value}/practice`)
}

// 小节预览
const VideoPlayVisable = ref(false);
const VideoPlayingUrl = ref(null);
</script>

<style scoped>
.course-study-page {
    min-height: 100vh;
    background-color: #f5f7fa;
}

/* 页面头部 */
.page-header {
    background: white;
    padding: 20px 32px;
    border-bottom: 1px solid #e4e7ed;
    margin-bottom: 24px;
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
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0;
}

/* 进度卡片 */
.progress-section {
    padding: 0 32px 24px;
}

.progress-card {
    border-radius: 12px;
    transition: transform 0.3s ease;
}

.progress-card:hover {
    transform: translateY(-4px);
}

.progress-content {
    display: flex;
    align-items: center;
    padding: 20px;
}

.progress-icon {
    font-size: 32px;
    color: #409eff;
    margin-right: 16px;
}

.progress-info {
    flex: 1;
}

.progress-number {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    line-height: 1;
}

.progress-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
}

/* 主容器 */
.main-container {
    padding: 0 0 32px 32px;
    gap: 24px;
}

.sidebar {
    width: 280px;
}

.learning-tools-card {
    border-radius: 12px;
    margin-bottom: 16px;
}

.card-header {
    font-weight: 600;
    color: #303133;
}

.learning-tools {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.tool-btn {
    justify-content: flex-start;
    text-align: left;
    transition: all 0.3s ease;
    border-radius: 8px;
    height: 48px;
    font-weight: 500;
    width: 100%;
    box-sizing: border-box;
    margin: 0;
    padding: 0 16px;
}

.tool-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.tool-btn.active {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
    font-weight: 600;
    background-color: #f0f9ff;
    border-color: #409eff;
}

/* 主内容区域 */
.main-content {
    height: calc(100vh - 36px);
    overflow-y: auto;
}

.content-scroll {
    min-height: 100%;
    height: 100%;
    overflow-y: auto;
}

/* 子页面滚动条美化 */
.content-scroll::-webkit-scrollbar {
    width: 6px;
}

.content-scroll::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.content-scroll::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
}

.content-scroll::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}

/* 子页面内容样式 */
.content-scroll > div {
    padding: 0;
    background: transparent;
    min-height: auto;
}

/* 响应式设计 */
@media (max-width: 1200px) {
    .main-container {
        flex-direction: column;
    }
    
    .sidebar {
        width: 100%;
    }
    
    .progress-section .el-col {
        margin-bottom: 16px;
    }
}

@media (max-width: 768px) {
    .page-header {
        padding: 16px;
    }
    
    .main-container {
        padding: 0 16px 16px;
    }
    
    .progress-section {
        padding: 0 16px 16px;
    }
    
    .header-content {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }
}
</style>