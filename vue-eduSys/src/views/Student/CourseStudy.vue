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
    Notebook, ArrowLeft, Memo, 
    ChatDotRound, Service 
} from '@element-plus/icons-vue'
// import request from '@/utils/request.js'
// import { RESOURCE_TYPES } from '@/constants/resourceTypes'
import VideoPlayDialog from '@/components/file/VideoPlay.vue';
// import { RECORD_QUESTION_TYPES } from '@/constants/recordQuestionTypes'

import { queryCourseDetail } from '@/api/course'

const route = useRoute()
const router = useRouter()
const courseId = ref(null)
const currentActiveTab = ref('courseInfo') // 默认选中课程详情

const mainRef = ref(null)

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
        const response = await queryCourseDetail(courseId.value)

        const data = response.data.data

        courseDetail.value = {
            course: {
                courseName: data.course_name,
                courseDesc: data.description,
                coverPath: null,
                tags: data.keywords || []
            },
            sectionList: (data.chapters || []).map(chapter => ({
                section: {
                    sectionId: chapter.chapter_id,
                    sectionName: chapter.chapter_name,
                    sectionDesc: chapter.chapter_description
                },
                subsectionList: (chapter.contents || []).map(content => ({
                    subsectionId: content.content_id,
                    subsectionName: content.content_name,
                    subsectionDesc: content.content_description,
                    subsectionType: content.content_type,
                    resourceId: content.content_id,
                    videoUrl: content.video_url,
                    documentUrl: content.document_url,
                    fileId: content.content_id,           // 新增：文件ID
                    fileUrl: content.video_url || content.document_url,  // 新增：文件URL
                    fileName: content.content_name,       // 新增：文件名
                    state: null
                }))
            }))
        }

    } catch (error) {
        console.error(error)

        courseDetail.value = {
            course: {
                courseName: '课程加载失败',
                courseDesc: '',
                coverPath: null,
                tags: []
            },
            sectionList: []
        }

        ElMessage.error('课程详情加载失败')
    }
}

// 返回上一页
const goBack = () => {
    router.push('/student/courses')
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

// 小节预览
const VideoPlayVisable = ref(false);
const VideoPlayingUrl = ref(null);
</script>

<style scoped>
.course-study-page {
    /* min-height: 100vh; */
    min-height: calc(100vh - 60px);
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