<template>
    <div class="course-detail-page">
        <!-- 页面头部 -->
        <div class="page-header">
            <div class="header-content">
                <div class="header-left">
                    <el-button @click="goBack" type="text" class="back-btn">
                        <el-icon><ArrowLeft /></el-icon>
                        返回课程列表
                    </el-button>
                    <h1 class="page-title">{{ courseDetail.course.courseName || '课程详情' }}</h1>
                </div>
                <div class="header-right">
                    <el-button type="primary" @click="updateCourse">
                        <el-icon><Check /></el-icon>
                        保存课程
                    </el-button>
                    <el-button type="danger" @click="deleteCourse">
                        <el-icon><Delete /></el-icon>
                        删除课程
                    </el-button>
                </div>
            </div>
        </div>

        <!-- 课程统计卡片 -->
        <div class="stats-section">
            <el-row :gutter="24">
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><User /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ courseStats.totalStudents }}</div>
                                <div class="stat-label">总学生数</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><Document /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ courseStats.totalSections }}</div>
                                <div class="stat-label">总章节数</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><VideoPlay /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ courseStats.totalSubsections }}</div>
                                <div class="stat-label">总小节数</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><Star /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ courseStats.avgRating }}</div>
                                <div class="stat-label">平均评分</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <!-- 主要内容区域 -->
        <el-container class="main-container">
            <!-- 左侧快速操作 -->
            <el-aside class="sidebar">
                <!-- 快速操作 -->
                <el-card class="quick-actions-card">
                    <template #header>
                        <div class="card-header">
                            <span>快速操作</span>
                        </div>
                    </template>
                    <div class="quick-actions">
                        <el-button 
                            type="default"
                            @click="goToCourseEdit" 
                            class="action-btn"
                            :class="{ 'active': currentActiveTab === 'courseEdit' }"
                        >
                            <el-icon><Edit /></el-icon>
                            编辑课程
                        </el-button>
                        <el-button 
                            type="default"
                            @click="goToStudentManagement" 
                            class="action-btn"
                            :class="{ 'active': currentActiveTab === 'studentManagement' }"
                        >
                            <el-icon><User /></el-icon>
                            学生管理
                        </el-button>
                        <el-button 
                            type="default"
                            @click="goToLearningAnalytics" 
                            class="action-btn"
                            :class="{ 'active': currentActiveTab === 'learningAnalytics' }"
                        >
                            <el-icon><DataAnalysis /></el-icon>
                            学习分析
                        </el-button>
                        <el-button 
                            type="default"
                            @click="goToAutoDesign" 
                            class="action-btn"
                            :class="{ 'active': currentActiveTab === 'autoDesign' }"
                        >
                            <el-icon><MagicStick /></el-icon>
                            智能备课
                        </el-button>
                        <el-button 
                            type="default"
                            @click="goToQuestionGenerate" 
                            class="action-btn"
                            :class="{ 'active': currentActiveTab === 'questionGenerate' }"
                        >
                            <el-icon><EditPen /></el-icon>
                            生成题目
                        </el-button>
                        <el-button type="default" @click="exportCourse" class="action-btn">
                            <el-icon><Download /></el-icon>
                            导出课程
                        </el-button>
                    </div>
                </el-card>
            </el-aside>

            <!-- 右侧内容 -->
            <el-main class="main-content">
                <div ref="mainRef" class="content-scroll">
                    <!-- 子路由内容 -->
                    <router-view />
                </div>
            </el-main>
        </el-container>

        <!-- 弹窗组件 -->
        <VideoPlayDialog v-model:visible="VideoPlayVisable" :videoUrl="VideoPlayingUrl" />
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter, onBeforeRouteUpdate } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
    Document, VideoPlay, EditPen, Delete, Edit, 
    ArrowLeft, Check, User, Star, DataAnalysis, Download, MagicStick 
} from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { RESOURCE_TYPES } from '@/constants/resourceTypes'
import VideoPlayDialog from '@/components/file/VideoPlay.vue';
import { useCourseStore } from '@/store/course'

const courseStore = useCourseStore()

const route = useRoute()
const router = useRouter()
const courseId = ref(null)
const currentRouteName = 'courseDetail'
const currentActiveTab = ref('courseEdit') // 默认选中编辑课程

const mainRef = ref(null)



// 课程统计数据
const courseStats = ref({
    totalStudents: 156,
    totalSections: 0,
    totalSubsections: 0,
    avgRating: 4.8
})







// 课程完整信息
const courseDetail = ref({
    'course': {
        'courseName': '',
        'courseDesc': '',
        'coverPath': null,
        'category': '',
        'difficulty': 3,
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
        updateCourseStats()
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
    if (routeName === 'courseEdit') {
        currentActiveTab.value = 'courseEdit'
    } else if (routeName === 'studentManagement') {
        currentActiveTab.value = 'studentManagement'
    } else if (routeName === 'learningAnalytics') {
        currentActiveTab.value = 'learningAnalytics'
    } else if (routeName === 'autoDesign') {
        currentActiveTab.value = 'autoDesign'
    } else if (routeName === 'questionGenerate') {
        currentActiveTab.value = 'questionGenerate'
    } else {
        // 默认选中编辑课程
        currentActiveTab.value = 'courseEdit'
    }
}

onBeforeRouteUpdate(async (to, from) => {
    if (to.name === currentRouteName) {
        await getCourseDetail();
        courseDetail.value.course = courseStore.getCourse();
        coverFile.value = courseStore.getCoverFile();
    }
    if (from.name === currentRouteName) {
        courseStore.cacheFormData(courseDetail.value.course, coverFile.value);
    }
})

// 获取课程信息
const getCourseDetail = async () => {
    try {
        const response = await request.post('/course/queryCourseDetail', courseId.value)
        courseDetail.value = response.data.data;
        updateCourseStats()
    } catch (error) {
        // 使用模拟数据
        courseDetail.value = {
            course: {
                courseName: 'Vue.js 前端开发实战',
                courseDesc: '本课程将带领大家深入学习Vue.js框架，从基础语法到高级特性，通过实战项目掌握前端开发技能。',
                coverPath: null,
                category: 'cs',
                difficulty: 4,
                tags: ['Vue.js', '前端开发', 'JavaScript']
            },
            sectionList: [
                {
                    section: {
                        sectionId: 1,
                        sectionName: 'Vue.js 基础入门',
                        sectionDesc: '学习Vue.js的基本概念和核心特性'
                    },
                    editable: false,
                    knowledgeList: ['Vue实例', '模板语法', '响应式数据'],
                    subsectionList: [
                        {
                            subsectionId: 1,
                            subsectionName: 'Vue.js 简介',
                            subsectionDesc: '了解Vue.js的发展历史和基本概念',
                            subsectionType: RESOURCE_TYPES.VIDEO,
                            resourceId: 1
                        },
                        {
                            subsectionId: 2,
                            subsectionName: 'Vue实例创建',
                            subsectionDesc: '学习如何创建和配置Vue实例',
                            subsectionType: RESOURCE_TYPES.DOCUMENT,
                            resourceId: 2
                        }
                    ]
                },
                {
                    section: {
                        sectionId: 2,
                        sectionName: '组件化开发',
                        sectionDesc: '掌握Vue.js组件化开发的核心概念'
                    },
                    editable: false,
                    knowledgeList: ['组件注册', '组件通信', '生命周期'],
                    subsectionList: [
                        {
                            subsectionId: 3,
                            subsectionName: '组件基础',
                            subsectionDesc: '学习Vue组件的基本用法',
                            subsectionType: RESOURCE_TYPES.VIDEO,
                            resourceId: 3
                        }
                    ]
                }
            ]
        }
        updateCourseStats()
    }
}

// 更新课程统计
const updateCourseStats = () => {
    courseStats.value.totalSections = courseDetail.value.sectionList.length
    courseStats.value.totalSubsections = courseDetail.value.sectionList.reduce((sum, section) => {
        return sum + section.subsectionList.length
    }, 0)
}

// 返回上一页
const goBack = () => {
    router.go(-1)
}

// 重置课程表单 - 暂时注释掉，因为当前未使用
// const resetCourseForm = () => {
//     ElMessage.info('表单已重置')
// }

// 导出课程
const exportCourse = () => {
    ElMessage.success('课程导出功能开发中...')
}

// 跳转到独立页面
const goToCourseEdit = () => {
    currentActiveTab.value = 'courseEdit'
    router.push(`/teacher/courseDetail/${courseId.value}/courseEdit`)
}

const goToStudentManagement = () => {
    currentActiveTab.value = 'studentManagement'
    router.push(`/teacher/courseDetail/${courseId.value}/studentManagement`)
}

const goToLearningAnalytics = () => {
    currentActiveTab.value = 'learningAnalytics'
    router.push(`/teacher/courseDetail/${courseId.value}/learningAnalytics`)
}

const goToAutoDesign = () => {
    currentActiveTab.value = 'autoDesign'
    router.push(`/teacher/courseDetail/${courseId.value}/autoDesign`)
}

const goToQuestionGenerate = () => {
    currentActiveTab.value = 'questionGenerate'
    router.push(`/teacher/courseDetail/${courseId.value}/questionGenerate`)
}



// 更新课程信息
const updateCourse = async () => {
    const formData = new FormData();
    formData.append('courseId', courseId.value);
    formData.append('courseName', courseDetail.value.course.courseName);
    formData.append('courseDesc', courseDetail.value.course.courseDesc);
    if (coverFile.value[0]) {
        formData.append('coverFile', coverFile.value[0].raw);
    } else {
        formData.append('coverFile', null);
    }

    try {
        const res = await request.post('/course/updateCourse', formData)
        if (res.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试");
            return;
        }
        ElMessage.success('更新成功');
        router.push({
            name: "MyCourse",
        })
    } catch (error) {
        ElMessage.success('课程信息更新成功（模拟）')
    }
}

// 删除课程
const deleteCourse = async () => {
    try {
        await ElMessageBox.confirm(
            '确定要删除这个课程吗？此操作不可撤销！',
            '确认删除',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        
        try {
            await request.post('/course/deleteCourse', courseId.value)
            ElMessage.success('成功删除');
        } catch (error) {
            ElMessage.success('课程删除成功（模拟）')
        }
        
        router.push({
            name: "MyCourse",
        })
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

// 封面上传
const coverFile = ref([]);
// const handleChange = (uploadFile) => {
//     const rawFile = uploadFile.raw;
//     if (!rawFile.type.startsWith('image/')) {
//         ElMessage.error('请上传图片格式文件（JPG/PNG）')
//         return false;
//     }

//     if (coverFile.value) {
//         URL.revokeObjectURL(courseDetail.value.course.coverPath);
//     }
//     coverFile.value = [rawFile];
//     courseDetail.value.course.coverPath = URL.createObjectURL(rawFile)
// }

// const handleRemove = () => {
//     if (courseDetail.value.course.coverPath) {
//         URL.revokeObjectURL(courseDetail.value.course.coverPath);
//     }
//     courseDetail.value.course.coverPath = null;
//     coverFile.value = [];
// }

// 文件超出限制处理 - 暂时注释掉，因为当前未使用
// const handleExceed = (files) => {
//     if (files.length === 0) return
//     handleRemove()
//     handleChange({
//         raw: files[0],
//     })
//     ElMessage.warning("覆盖图片");
// }

// 章节操作 - 暂时注释掉，因为当前未使用
// const addSection = async () => {
//     try {
//         const formData = {
//             'courseId': courseId.value
//         }
//         const response = await request.post('/section/addSection', formData)
//         courseDetail.value.sectionList.push(response.data.data);
//     } catch (error) {
//         // 模拟添加章节
//         const newSection = {
//             section: {
//                 sectionId: Date.now(),
//                 sectionName: '新章节',
//                 sectionDesc: '请输入章节描述'
//             },
//             editable: true,
//             knowledgeList: [],
//             subsectionList: []
//         }
//         courseDetail.value.sectionList.push(newSection)
//         updateCourseStats()
//         ElMessage.success('章节添加成功')
//     }
// }

// const updateSection = async (sectionDTO) => {
//     try {
//         await request.post('/section/updateSection', sectionDTO)
//         ElMessage.success("成功更新");
//     } catch (error) {
//         ElMessage.success('章节更新成功（模拟）')
//     }
// }

// const deleteSection = async (sectionId, sectionIndex) => {
//     try {
//         await ElMessageBox.confirm(
//             '确定要删除这个章节吗？此操作不可撤销！',
//             '确认删除',
//             {
//                 confirmButtonText: '确定',
//                 cancelButtonText: '取消',
//                 type: 'warning'
//             }
//         )
        
//         try {
//             await request.post('/section/deleteSection', sectionId)
//         } catch (error) {
//             // 模拟删除
//         }
        
//         courseDetail.value.sectionList.splice(sectionIndex, 1);
//         updateCourseStats()
//         ElMessage.success('章节删除成功')
//     } catch (error) {
//         if (error !== 'cancel') {
//             ElMessage.error('删除失败')
//         }
//     }
// }

// 小节操作 - 暂时注释掉，因为当前未使用
// const addSubsection = (sectionId) => {
//     router.push({
//         name: "OpSubsection",
//         query: {
//             "sectionId": sectionId,
//         }
//     })
// }

// const editSubsection = (subsectionId) => {
//     router.push({
//         name: "OpSubsection",
//         query: {
//             "subsectionId": subsectionId,
//         }
//     })
// }

// const deleteSubsection = async (subsectionId, sectionIndex, subsectionIndex) => {
//     try {
//         await ElMessageBox.confirm(
//             '确定要删除这个小节吗？此操作不可撤销！',
//             '确认删除',
//             {
//                 confirmButtonText: '确定',
//                 cancelButtonText: '取消',
//                 type: 'warning'
//             }
//         )
        
//         try {
//             await request.post('/section/deleteSubsection', subsectionId)
//             // 模拟删除
//         } catch (error) {
//             // 模拟删除
//         }
        
//         courseDetail.value.sectionList[sectionIndex].subsectionList.splice(subsectionIndex, 1);
//         updateCourseStats()
//         ElMessage.success("成功删除");
//     } catch (error) {
//         if (error !== 'cancel') {
//             ElMessage.error('删除失败')
//         }
//     }
// }

// 小节预览
const VideoPlayVisable = ref(false);
const VideoPlayingUrl = ref(null);
// const preview = async (resourceId, resourceType) => {
//     ElMessage.info(`预览资源: ${resourceType}`)
// }








</script>

<style scoped>
.course-detail-page {
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

.header-right {
    display: flex;
    gap: 12px;
}

/* 统计卡片 */
.stats-section {
    padding: 0 32px 24px;
}

.stat-card {
    border-radius: 12px;
    transition: transform 0.3s ease;
}

.stat-card:hover {
    transform: translateY(-4px);
}

.stat-content {
    display: flex;
    align-items: center;
    padding: 20px;
}

.stat-icon {
    font-size: 32px;
    color: #409eff;
    margin-right: 16px;
}

.stat-info {
    flex: 1;
}

.stat-number {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    line-height: 1;
}

.stat-label {
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

.quick-actions-card {
    border-radius: 12px;
    margin-bottom: 16px;
}

.card-header {
    font-weight: 600;
    color: #303133;
}

.quick-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.action-btn {
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

.action-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-btn.active {
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
    
    .stats-section .el-col {
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
    
    .stats-section {
        padding: 0 16px 16px;
    }
    
    .header-content {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }
    
    .section-header {
        flex-direction: column;
        gap: 12px;
        align-items: flex-start;
    }
    
    .subsection-item {
        flex-direction: column;
        gap: 12px;
        align-items: flex-start;
    }
    
    .subsection-actions {
        width: 100%;
        justify-content: flex-end;
    }
}
</style>