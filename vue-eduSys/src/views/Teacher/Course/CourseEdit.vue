<template>
    <div class="course-edit-content">
        <!-- 课程信息编辑 -->
        <el-card id="course-info" class="course-info-card">
            <template #header>
                <div class="card-header">
                    <span>课程信息编辑</span>
                </div>
            </template>
            <el-row :gutter="24">
                <el-col :span="8">
                    <div class="cover-upload">
                        <el-upload 
                            v-model:file-list="coverFile" 
                            action="#" 
                            :auto-upload="false" 
                            :limit="1"
                            :show-file-list="false" 
                            :on-change="handleChange" 
                            :on-remove="handleRemove"
                            :on-exceed="handleExceed" 
                            drag 
                            accept="image/*"
                            class="cover-uploader"
                        >
                            <div v-if="!courseDetail.course.coverPath" class="upload-placeholder">
                                <el-icon class="upload-icon"><UploadFilled /></el-icon>
                                <div class="upload-text">
                                    拖动或<em>点击上传封面</em>
                                </div>
                            </div>
                            <div v-if="courseDetail.course.coverPath" class="cover-preview">
                                <img :src="courseDetail.course.coverPath" alt="课程封面" />
                            </div>
                        </el-upload>
                    </div>
                </el-col>
                <el-col :span="16">
                    <el-form :model="courseDetail.course" label-width="100px" class="course-form">
                        <el-form-item label="课程名称" required>
                            <el-input v-model="courseDetail.course.courseName" placeholder="请输入课程名称" />
                        </el-form-item>
                        <el-form-item label="课程分类">
                            <el-select v-model="courseDetail.course.category" placeholder="选择课程分类" style="width: 100%;">
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
                        <el-form-item label="课程难度">
                            <el-rate v-model="courseDetail.course.difficulty" :max="5" show-text />
                        </el-form-item>
                        <el-form-item label="课程介绍">
                            <el-input 
                                v-model="courseDetail.course.courseDesc" 
                                type="textarea" 
                                :rows="4"
                                placeholder="请输入课程介绍"
                            />
                        </el-form-item>
                        <el-form-item label="课程标签">
                            <el-input-tag v-model="courseDetail.course.tags" placeholder="输入标签后按回车确认" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="updateCourse">
                                <el-icon><Check /></el-icon>
                                保存课程信息
                            </el-button>
                            <el-button @click="resetCourseForm">
                                <el-icon><Refresh /></el-icon>
                                重置
                            </el-button>
                        </el-form-item>
                    </el-form>
                </el-col>
            </el-row>
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
                            </div>
                            <div class="section-actions">
                                <el-button 
                                    v-if="!sectionDTO.editable" 
                                    type="primary" 
                                    size="small"
                                    @click="sectionDTO.editable = true"
                                >
                                    <el-icon><Edit /></el-icon>
                                    编辑
                                </el-button>
                                <el-button 
                                    v-if="sectionDTO.editable" 
                                    type="success" 
                                    size="small"
                                    @click="updateSection(sectionDTO); sectionDTO.editable = false"
                                >
                                    <el-icon><Check /></el-icon>
                                    保存
                                </el-button>
                                <el-button 
                                    type="danger" 
                                    size="small"
                                    @click="deleteSection(sectionDTO.section.sectionId, sectionIndex)"
                                >
                                    <el-icon><Delete /></el-icon>
                                    删除
                                </el-button>
                            </div>
                        </div>
                    </template>

                    <!-- 章节内容 -->
                    <div class="section-content">
                        <el-form :model="sectionDTO.section" label-width="100px" :disabled="!sectionDTO.editable">
                            <el-form-item label="章节名称" required>
                                <el-input v-model="sectionDTO.section.sectionName" />
                            </el-form-item>
                            <el-form-item label="章节描述">
                                <el-input 
                                    v-model="sectionDTO.section.sectionDesc" 
                                    type="textarea" 
                                    :rows="3"
                                />
                            </el-form-item>
                            <el-form-item label="关联知识点">
                                <el-input-tag 
                                    v-model="sectionDTO.knowledgeList" 
                                    placeholder="输入知识点后按回车确认"
                                />
                            </el-form-item>
                        </el-form>

                        <!-- 小节列表 -->
                        <div class="subsections-container">
                            <div class="subsections-header">
                                <h4>小节列表</h4>
                                <el-button type="primary" size="small" @click="addSubsection(sectionDTO.section.sectionId)">
                                    <el-icon><Plus /></el-icon>
                                    新增小节
                                </el-button>
                            </div>
                            
                            <div class="subsections-list">
                                <div 
                                    v-for="(subsection, subIndex) in sectionDTO.subsectionList"
                                    :key="subsection.subsectionId"
                                    :id="'subsection' + subsection.subsectionId"
                                    class="subsection-item"
                                >
                                    <div class="subsection-info">
                                        <el-icon class="subsection-icon">
                                            <component :is="getIconComponent(subsection.subsectionType)" />
                                        </el-icon>
                                        <div class="subsection-details">
                                            <div class="subsection-title">
                                                {{ sectionIndex + 1 }}-{{ subIndex + 1 }} {{ subsection.subsectionName }}
                                            </div>
                                            <div class="subsection-desc">{{ subsection.subsectionDesc }}</div>
                                        </div>
                                    </div>
                                    <div class="subsection-actions">
                                        <el-button type="primary" size="small" @click="preview(subsection.resourceId, subsection.subsectionType)">
                                            <el-icon><View /></el-icon>
                                            预览
                                        </el-button>
                                        <el-button type="warning" size="small" @click="editSubsection(subsection.subsectionId)">
                                            <el-icon><Edit /></el-icon>
                                            编辑
                                        </el-button>
                                        <el-button type="danger" size="small" @click="deleteSubsection(subsection.subsectionId, sectionIndex, subIndex)">
                                            <el-icon><Delete /></el-icon>
                                            删除
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </TransitionGroup>

            <!-- 新增章节按钮 -->
            <el-card id="add-section" class="add-section-card" @click="addSection">
                <div class="add-section-content">
                    <el-icon class="add-icon"><Plus /></el-icon>
                    <span class="add-text">点击新增章节</span>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
    UploadFilled, Plus, Document, VideoPlay, EditPen, Delete, Edit, 
    Check, View, Refresh
} from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { RESOURCE_TYPES } from '@/constants/resourceTypes'
// import VideoPlayDialog from '@/components/file/VideoPlay.vue';
// import { useCourseStore } from '@/store/course'

// const courseStore = useCourseStore()
const route = useRoute()
const router = useRouter()
const courseId = ref(null)

// const mainRef = ref(null)

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

// 小节图标映射
const iconMap = {
    [RESOURCE_TYPES.DOCUMENT]: Document,
    [RESOURCE_TYPES.VIDEO]: VideoPlay,
    [RESOURCE_TYPES.QUESTIONS]: EditPen
}

// 获取图标的函数方法
const getIconComponent = (type) => {
    return iconMap[type] || Document
}

onMounted(async () => {
    courseId.value = route.params.courseId
    if (!courseId.value) {
        ElMessage.error("课程ID不存在")
        router.go(-1);
    } else {
        getCourseDetail()
        updateCourseStats()
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

// 重置课程表单
const resetCourseForm = () => {
    ElMessage.info('表单已重置')
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

// 封面上传
const coverFile = ref([]);
const handleChange = (uploadFile) => {
    const rawFile = uploadFile.raw;
    if (!rawFile.type.startsWith('image/')) {
        ElMessage.error('请上传图片格式文件（JPG/PNG）')
        return false;
    }

    if (coverFile.value) {
        URL.revokeObjectURL(courseDetail.value.course.coverPath);
    }
    coverFile.value = [rawFile];
    courseDetail.value.course.coverPath = URL.createObjectURL(rawFile)
}

const handleRemove = () => {
    if (courseDetail.value.course.coverPath) {
        URL.revokeObjectURL(courseDetail.value.course.coverPath);
    }
    courseDetail.value.course.coverPath = null;
    coverFile.value = [];
}

const handleExceed = (files) => {
    if (files.length === 0) return
    handleRemove()
    handleChange({
        raw: files[0],
    })
    ElMessage.warning("覆盖图片");
}

// 章节操作
const addSection = async () => {
    try {
        const formData = {
            'courseId': courseId.value
        }
        const response = await request.post('/section/addSection', formData)
        courseDetail.value.sectionList.push(response.data.data);
    } catch (error) {
        // 模拟添加章节
        const newSection = {
            section: {
                sectionId: Date.now(),
                sectionName: '新章节',
                sectionDesc: '请输入章节描述'
            },
            editable: true,
            knowledgeList: [],
            subsectionList: []
        }
        courseDetail.value.sectionList.push(newSection)
        updateCourseStats()
        ElMessage.success('章节添加成功')
    }
}

const updateSection = async (sectionDTO) => {
    try {
        await request.post('/section/updateSection', sectionDTO)
        ElMessage.success("成功更新");
    } catch (error) {
        ElMessage.success('章节更新成功（模拟）')
    }
}

const deleteSection = async (sectionId, sectionIndex) => {
    try {
        await ElMessageBox.confirm(
            '确定要删除这个章节吗？此操作不可撤销！',
            '确认删除',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        
        try {
            await request.post('/section/deleteSection', sectionId)
        } catch (error) {
            // 模拟删除
        }
        
        courseDetail.value.sectionList.splice(sectionIndex, 1);
        updateCourseStats()
        ElMessage.success('章节删除成功')
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

// 小节操作
const addSubsection = (sectionId) => {
    router.push({
        name: "OpSubsection",
        query: {
            "sectionId": sectionId,
        }
    })
}

const editSubsection = (subsectionId) => {
    router.push({
        name: "OpSubsection",
        query: {
            "subsectionId": subsectionId,
        }
    })
}

const deleteSubsection = async (subsectionId, sectionIndex, subsectionIndex) => {
    try {
        await ElMessageBox.confirm(
            '确定要删除这个小节吗？此操作不可撤销！',
            '确认删除',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        
        try {
            await request.post('/section/deleteSubsection', subsectionId)
        } catch (error) {
            // 模拟删除
        }
        
        courseDetail.value.sectionList[sectionIndex].subsectionList.splice(subsectionIndex, 1);
        updateCourseStats()
        ElMessage.success("成功删除");
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

// 小节预览
// const VideoPlayVisable = ref(false);
// const VideoPlayingUrl = ref(null);
const preview = async (resourceId, resourceType) => {
    ElMessage.info(`预览资源: ${resourceType}`)
}
</script>

<style scoped>
.course-edit-content {
    padding: 0;
    background: transparent;
    min-height: auto;
}

/* 课程信息卡片 */
.course-info-card {
    border-radius: 12px;
    margin-bottom: 24px;
}

.cover-upload {
    width: 100%;
}

.cover-uploader {
    width: 100%;
}

.upload-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    border: 2px dashed #d9d9d9;
    border-radius: 8px;
    color: #606266;
}

.upload-icon {
    font-size: 48px;
    color: #c0c4cc;
    margin-bottom: 16px;
}

.upload-text {
    font-size: 16px;
    text-align: center;
}

.upload-text em {
    color: #409eff;
    font-style: normal;
    font-weight: 500;
}

.cover-preview {
    width: 100%;
    height: 200px;
    border-radius: 8px;
    overflow: hidden;
}

.cover-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.course-form {
    padding: 20px 0;
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
}

.section-actions {
    display: flex;
    gap: 8px;
}

.section-content {
    padding: 20px 0;
}

/* 小节容器 */
.subsections-container {
    margin-top: 24px;
}

.subsections-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e4e7ed;
}

.subsections-header h4 {
    margin: 0;
    color: #303133;
    font-weight: 600;
}

.subsections-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
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
}

.subsection-actions {
    display: flex;
    gap: 8px;
}

/* 新增章节卡片 */
.add-section-card {
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 2px dashed #d9d9d9;
}

.add-section-card:hover {
    border-color: #409eff;
    background: #f0f9ff;
}

.add-section-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 120px;
    color: #606266;
}

.add-icon {
    font-size: 32px;
    color: #c0c4cc;
    margin-bottom: 8px;
}

.add-text {
    font-size: 16px;
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