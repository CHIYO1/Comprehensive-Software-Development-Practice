<template>
    <div class="course-info-page">
        <el-card class="course-info-card">
            <template #header>
                <div class="card-header">
                    <el-icon class="header-icon"><Notebook /></el-icon>
                    <span>课程介绍</span>
                </div>
            </template>
            
            <div class="course-info-content">
                <div class="course-cover" v-if="courseDetail.course.coverPath">
                    <img :src="courseDetail.course.coverPath" alt="课程封面" />
                </div>
                <div class="course-desc">
                    <p>{{ courseDetail.course.courseDesc }}</p>
                    <div class="course-meta">
                        <el-tag v-for="tag in courseDetail.course.tags" :key="tag" size="small" style="margin: 4px;">
                            {{ tag }}
                        </el-tag>
                    </div>
                </div>
            </div>
        </el-card>

        <!-- 章节列表 -->
        <div class="sections-container">
            <TransitionGroup name="fade">
                <el-card 
                    v-for="(sectionDTO, sectionIndex) in courseDetail.sectionList" 
                    :key="sectionDTO.section.sectionId"
                    :id="'section' + sectionDTO.section.sectionId"
                    class="section-card"
                >
                    <template #header>
                        <div class="section-header">
                            <div class="section-info">
                                <span class="section-number">{{ sectionIndex + 1 }}</span>
                                <span class="section-name">{{ sectionDTO.section.sectionName }}</span>
                                <el-progress :percentage="getSectionProgress(sectionDTO)" :show-text="false" style="width: 100px; margin-left: 16px;" />
                                <span class="progress-text">{{ getSectionProgress(sectionDTO) }}%</span>
                            </div>
                        </div>
                    </template>

                    <!-- 章节描述 -->
                    <div class="section-desc">
                        {{ sectionDTO.section.sectionDesc }}
                    </div>

                    <!-- 小节列表 -->
                    <div class="subsections-container">
                        <div 
                            v-for="(subsection, subIndex) in sectionDTO.subsectionList"
                            :key="subsection.subsectionId"
                            :id="'subsection' + subsection.subsectionId"
                            class="subsection-item"
                        >
                            <div class="subsection-info">
                                <el-icon class="subsection-icon" :class="getSubsectionIconClass(subsection)">
                                    <component :is="getIconComponent(subsection.subsectionType)" />
                                </el-icon>
                                <div class="subsection-details">
                                    <div class="subsection-title">
                                        {{ sectionIndex + 1 }}-{{ subIndex + 1 }} {{ subsection.subsectionName }}
                                    </div>
                                    <div class="subsection-desc">{{ subsection.subsectionDesc }}</div>
                                    <div class="subsection-meta">
                                        <el-tag v-if="subsection.subsectionType == RESOURCE_TYPES.QUESTIONS" :type="typeMap[subsection.state]" size="small">
                                            {{ subsection.state }}
                                        </el-tag>
                                        <el-tag v-else type="info" size="small">
                                            {{ getResourceTypeText(subsection.subsectionType) }}
                                        </el-tag>
                                    </div>
                                </div>
                            </div>
                            <div class="subsection-actions">
                                <el-button 
                                    v-if="subsection.subsectionType != RESOURCE_TYPES.QUESTIONS"
                                    type="primary" 
                                    size="small" 
                                    @click="preview(subsection)"
                                >
                                    <el-icon><View /></el-icon>
                                    在线预览
                                </el-button>
                                <el-button 
                                    v-if="subsection.subsectionType == RESOURCE_TYPES.QUESTIONS"
                                    type="primary" 
                                    size="small" 
                                    @click="doQuestion(subsection)"
                                >
                                    <el-icon><EditPen /></el-icon>
                                    {{ subsection.state == RECORD_QUESTION_TYPES.UNSUBMIT ? "开始练习" : "查看结果" }}
                                </el-button>
                                <el-button 
                                    type="success" 
                                    size="small"
                                    @click="downloadFileWithFetch(subsection.resourceId, subsection.subsectionType)"
                                >
                                    <el-icon><Download /></el-icon>
                                    导出
                                </el-button>
                                <el-button 
                                    type="warning" 
                                    size="small"
                                    @click="addNote(subsection)"
                                >
                                    <el-icon><Memo /></el-icon>
                                    笔记
                                </el-button>
                            </div>
                        </div>
                    </div>
                </el-card>
            </TransitionGroup>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
    Document, VideoPlay, EditPen, Notebook, Memo, 
    View, Download 
} from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { RESOURCE_TYPES } from '@/constants/resourceTypes'
import { RECORD_QUESTION_TYPES } from '@/constants/recordQuestionTypes'

const router = useRouter()

// 小节图标映射
const iconMap = {
    [RESOURCE_TYPES.DOCUMENT]: Document,
    [RESOURCE_TYPES.VIDEO]: VideoPlay,
    [RESOURCE_TYPES.QUESTIONS]: EditPen
}

const typeMap = {
    [RECORD_QUESTION_TYPES.UNSUBMIT]: 'info',
    [RECORD_QUESTION_TYPES.GRADING]: 'warning',
    [RECORD_QUESTION_TYPES.GRADED]: 'success'
}

// 获取图标的函数方法
const getIconComponent = (type) => {
    return iconMap[type] || Document
}

// 课程完整信息 - 从父组件传入
// eslint-disable-next-line no-undef
const props = defineProps({
    courseDetail: {
        type: Object,
        default: () => ({
            'course': {
                'courseName': '',
                'courseDesc': '',
                'coverPath': null,
                'tags': []
            },
            'sectionList': []
        })
    }
})

const courseDetail = computed(() => props.courseDetail)

// 获取章节进度
const getSectionProgress = (section) => {
    const total = section.subsectionList.length
    const completed = section.subsectionList.filter(sub => getSubsectionStatus(sub) === 'completed').length
    return Math.round((completed / total) * 100)
}

// 获取小节状态
const getSubsectionStatus = (subsection) => {
    if (subsection.subsectionType === RESOURCE_TYPES.QUESTIONS) {
        if (subsection.state === RECORD_QUESTION_TYPES.GRADED) return 'completed'
        if (subsection.state === RECORD_QUESTION_TYPES.GRADING) return 'in-progress'
        return 'not-started'
    }
    return 'completed' // 非题目类型默认为已完成
}

// 获取小节图标样式
const getSubsectionIconClass = (subsection) => {
    const status = getSubsectionStatus(subsection)
    return {
        'icon-completed': status === 'completed',
        'icon-in-progress': status === 'in-progress',
        'icon-not-started': status === 'not-started'
    }
}

// 获取资源类型文本
const getResourceTypeText = (type) => {
    const typeMap = {
        [RESOURCE_TYPES.DOCUMENT]: '文档',
        [RESOURCE_TYPES.VIDEO]: '视频',
        [RESOURCE_TYPES.QUESTIONS]: '练习题'
    }
    return typeMap[type] || '未知'
}

// 小节预览
const preview = async (subsection) => {
    if (subsection.subsectionType == RESOURCE_TYPES.DOCUMENT) {
        router.push({
            name: "filePreview",
            query: {
                'fileId': subsection.resourceId
            }
        })
    } else if (subsection.subsectionType == RESOURCE_TYPES.VIDEO) {
        try {
            await request.post('/file/queryFile', {
                "fileId": subsection.resourceId,
                "fileType": RESOURCE_TYPES.VIDEO,
            })
            // 这里需要触发父组件的视频播放
            ElMessage.info('视频播放功能需要父组件处理')
        } catch (error) {
            ElMessage.error('视频加载失败')
        }
    }
}

const doQuestion = (subsection) => {
    router.push({
        name: "DoQuestions",
        query: {
            'subsectionId': subsection.subsectionId,
            'setRecordId': subsection.setRecordId
        }
    })
}

const downloadFileWithFetch = async (resourceId, resourceType) => {
    console.log('下载文件:', resourceId, resourceType)
    ElMessage.success('文件下载功能开发中...')
}

const addNote = () => {
    ElMessage.info('添加笔记功能需要父组件处理')
}

// 暴露方法给父组件
// defineExpose({
//     courseDetail
// })
</script>

<style scoped>
.course-info-page {
    padding: 20px;
}

.course-info-card {
    border-radius: 12px;
    margin-bottom: 24px;
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

.course-info-content {
    display: flex;
    gap: 24px;
}

.course-cover {
    width: 200px;
    height: 150px;
    border-radius: 8px;
    overflow: hidden;
    flex-shrink: 0;
}

.course-cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.course-desc {
    flex: 1;
}

.course-desc p {
    margin: 0 0 16px 0;
    line-height: 1.6;
    color: #606266;
}

.course-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

/* 章节容器 */
.sections-container {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.section-card {
    border-radius: 12px;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.section-info {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 1;
}

.section-number {
    width: 32px;
    height: 32px;
    background: #409eff;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    font-size: 14px;
}

.section-name {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    flex: 1;
}

.section-desc {
    padding: 16px 0;
    color: #606266;
    line-height: 1.6;
    border-bottom: 1px solid #e4e7ed;
    margin-bottom: 20px;
}

/* 小节容器 */
.subsections-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.subsection-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background: #fafafa;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
    transition: all 0.3s ease;
}

.subsection-item:hover {
    background: #f0f9ff;
    border-color: #409eff;
}

.subsection-info {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 1;
}

.subsection-icon {
    font-size: 20px;
    color: #409eff;
}

.subsection-icon.icon-completed {
    color: #67c23a;
}

.subsection-icon.icon-in-progress {
    color: #e6a23c;
}

.subsection-icon.icon-not-started {
    color: #c0c4cc;
}

.subsection-details {
    flex: 1;
}

.subsection-title {
    font-weight: 500;
    color: #303133;
    margin-bottom: 4px;
}

.subsection-desc {
    font-size: 13px;
    color: #909399;
    margin-bottom: 8px;
}

.subsection-meta {
    display: flex;
    gap: 8px;
}

.subsection-actions {
    display: flex;
    gap: 8px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
    transition: all 0.3s ease;
}

.fade-enter-from {
    opacity: 0;
    transform: translateY(20px);
}

.fade-leave-to {
    opacity: 0;
    transform: translateY(-20px);
}
</style> 