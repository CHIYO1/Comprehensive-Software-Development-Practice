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
                            <div v-if="!form.coverPath" class="upload-placeholder">
                                <el-icon class="upload-icon"><UploadFilled /></el-icon>
                                <div class="upload-text">
                                    拖动或<em>点击上传封面</em>
                                </div>
                            </div>
                            <div v-if="form.coverPath" class="cover-preview">
                                <img :src="form.coverPath" alt="课程封面" />
                            </div>
                        </el-upload>
                    </div>
                </el-col>
                <el-col :span="16">
                    <el-form :model="form" label-width="100px" class="course-form">
                        <el-form-item label="课程名称" required>
                            <el-input v-model="form.courseName" placeholder="请输入课程名称" />
                        </el-form-item>
                        <el-form-item label="课程类型">
                            <el-select v-model="form.courseType" placeholder="请选择这门课的类型" style="width: 100%;">
                                <el-option label="编程开发" value="编程开发" />
                                <el-option label="软件工程" value="软件工程" />
                                <el-option label="理论教学" value="理论教学" />
                                <el-option label="综合实训" value="课程实训" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="难度等级" prop="difficulty">
                            <el-rate
                                v-model="form.difficulty"
                                :max="5"
                                :texts="['非常简单', '简单', '中等', '较难', '非常难']"
                                show-text
                            />
                        </el-form-item>
                        <el-form-item label="课程描述">
                            <el-input 
                                v-model="form.description" 
                                type="textarea" 
                                :rows="4"
                                placeholder="请输入课程介绍"
                            />
                        </el-form-item>
                        <el-form-item label="课程关键词" prop="keywords">
                            <el-tag
                                v-for="(tag, index) in form.keywords"
                                :key="index"
                                closable
                                :disable-transitions="false"
                                @close="form.keywords.splice(index, 1)"
                                style="margin-right: 8px"
                            >
                                {{ tag }}
                            </el-tag>
                            <el-input
                            v-if="keywordInputVisible"
                            ref="keywordInputRef"
                            v-model="keywordInputValue"
                            size="small"
                            style="width: 100px"
                            @keyup.enter="addKeyword"
                            @blur="addKeyword"
                        />
                        <el-button v-else size="small" @click="showKeywordInput">+ 添加关键词</el-button>
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
                    :key="sectionDTO.section.chapterId"
                    :id="'section' + sectionDTO.section.chapterId"
                    class="section-card"
                >
                    <template #header>
                        <div class="section-header">
                            <div class="section-info">
                                <span class="section-number">{{ sectionIndex + 1 }}</span>
                                <span class="section-name">{{ sectionDTO.section.chapterName }}</span>
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
                                    @click="deleteSection(sectionDTO.section.chapterId)"
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
                                <el-input v-model="sectionDTO.section.chapterName" />
                            </el-form-item>
                            <el-form-item label="章节描述">
                                <el-input 
                                    v-model="sectionDTO.section.chapterDescription" 
                                    type="textarea" 
                                    :rows="3"
                                />
                            </el-form-item>
                            <!-- <el-form-item label="关联知识点">
                                <el-input-tag 
                                    v-model="sectionDTO.knowledgeList" 
                                    placeholder="输入知识点后按回车确认"
                                />
                            </el-form-item> -->
                        </el-form>

                        <!-- 小节列表 -->
                        <div class="subsections-container">
                            <div class="subsections-header">
                                <h4>小节列表</h4>
                                <el-button type="primary" size="small" @click="addSubsection(sectionDTO.section.chapterId)">
                                    <el-icon><Plus /></el-icon>
                                    新增小节
                                </el-button>
                            </div>
                            
                            <div class="subsections-list">
                                <div 
                                    v-for="(subsection, subIndex) in sectionDTO.subsections"
                                    :key="subsection.contentId"
                                    :id="'subsection' + subsection.contentId"
                                    class="subsection-item"
                                >
                                    <div class="subsection-info">
                                        <el-icon class="subsection-icon">
                                            <component :is="getIconComponent(subsection.contentType)" />
                                        </el-icon>
                                        <div class="subsection-details">
                                            <div class="subsection-title">
                                                {{ sectionIndex + 1 }}-{{ subIndex + 1 }} {{ subsection.contentName }}
                                            </div>
                                            <div class="subsection-desc">{{ subsection.contentDescription }}</div>
                                        </div>
                                    </div>
                                    <div class="subsection-actions">
                                        <el-button type="primary" size="small" @click="preview(subsection.contentId, subsection.contentType)">
                                            <el-icon><View /></el-icon>
                                            预览
                                        </el-button>
                                        <!-- <el-button type="warning" size="small" @click="editSubsection(subsection.contentId)">
                                            <el-icon><Edit /></el-icon>
                                            编辑
                                        </el-button> -->
                                        <el-button type="danger" size="small" @click="deleteSubsection(subsection.contentId, sectionIndex, subIndex)">
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

        <el-dialog 
            v-model="subsectionDialogVisible" 
            title="新增小节" 
            width="600px"
            :close-on-click-modal="false"
        >
            <el-form :model="subsectionForm" label-width="100px">
                <el-form-item label="小节名称" required>
                    <el-input v-model="subsectionForm.contentName" placeholder="请输入小节名称" />
                </el-form-item>
                <el-form-item label="小节描述">
                    <el-input 
                        v-model="subsectionForm.contentDescription" 
                        type="textarea" 
                        :rows="3"
                        placeholder="请输入小节描述"
                    />
                </el-form-item>
                <el-form-item label="内容类型" required>
                    <el-select v-model="subsectionForm.contentType" placeholder="请选择内容类型" style="width: 100%;">
                        <el-option label="视频" value="video" />
                        <el-option label="文档" value="document" />
                        <el-option label="练习" value="exercise" />
                        <el-option label="测验" value="quiz" />
                        <el-option label="作业" value="assignment" />
                    </el-select>
                </el-form-item>
                <el-form-item label="上传文件">
                    <el-upload
                        ref="uploadRef"
                        v-model:file-list="fileList"
                        :auto-upload="false"
                        :limit="1"
                        :on-change="handleFileChange"
                        :on-remove="handleFileRemove"
                        :on-exceed="handleFileExceed"
                        drag
                        accept=".mp4,.pdf,.doc,.docx,.ppt,.pptx,.txt"
                    >
                        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                        <div class="el-upload__text">
                            拖拽文件到此处，或 <em>点击上传</em>
                        </div>
                        <template #tip>
                            <div class="el-upload__tip">
                                支持 .mp4, .pdf, .doc, .docx, .ppt, .pptx, .txt 格式
                            </div>
                        </template>
                    </el-upload>
                </el-form-item>
                <el-form-item v-if="uploadedFileInfo" label="已上传文件">
                    <el-tag type="success" size="large">
                        <el-icon><Document /></el-icon>
                        {{ uploadedFileInfo.name }}
                    </el-tag>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="subsectionDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitSubsection" :loading="submitting">
                        确定
                    </el-button>
                </span>
            </template>
        </el-dialog>

    </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick  } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
    UploadFilled, Plus, Document, VideoPlay, EditPen, Delete, Edit, 
    Check, View, Refresh
} from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { RESOURCE_TYPES } from '@/constants/resourceTypes'

// const courseStore = useCourseStore()
const route = useRoute()
const router = useRouter()
const courseId = ref(null)

// ========== 课程信息表单（和创建页面字段对齐） ==========
const form = reactive({
  courseName: '',
  description: '',
  courseType: '',
  difficulty: 0,
  startDate: '',
  weeks: 16,
  keywords: [],
  coverPath: null
})

// 用于重置的原始数据快照
const originalForm = reactive({})

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

const subsectionDialogVisible = ref(false)
const submitting = ref(false)
const currentChapterId = ref(null)
const uploadRef = ref(null)
const fileList = ref([])
const uploadedFileInfo = ref(null)

const subsectionForm = reactive({
    contentName: '',
    contentDescription: '',
    contentType: 'document',
    videoUrl: null,
    documentUrl: null
})

// 文件上传相关
const handleFileChange = (uploadFile) => {
    const rawFile = uploadFile.raw
    // 保存文件信息用于显示
    uploadedFileInfo.value = {
        name: rawFile.name,
        size: rawFile.size,
        type: rawFile.type
    }
    // 实际文件保存在内存中，等待提交时处理
}

const handleFileRemove = () => {
    uploadedFileInfo.value = null
    fileList.value = []
}

const handleFileExceed = () => {
    ElMessage.warning('只能上传一个文件')
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
// 获取课程信息
const getCourseDetail = async () => {
  try {
    // 1. 获取课程基本信息
    const courseRes = await request.get('/courses/detail', {
      params: { course_id: courseId.value }
    })

    if (courseRes.data.code === '200') {
      const d = courseRes.data.data
      // 填充编辑表单
      form.courseName = d.course_name || ''
      form.description = d.description || ''
      form.courseType = d.course_type || '理论教学'
      form.difficulty = d.difficulty || 3
      form.startDate = d.start_date || ''
      form.weeks = d.weeks || 16
      form.keywords = d.keywords || []
      Object.assign(originalForm, JSON.parse(JSON.stringify(form)))

      // 2. 获取章节列表（调用新接口）
      const sectionRes = await request.get('/section/querySectionsByCourseId', {
        params: { courseId: courseId.value }
      })
      
      if (sectionRes.data.code === '200') {
        const sectionData = sectionRes.data.data || []
        courseDetail.value = {
          course: {
            courseName: d.course_name || '',
            courseDesc: d.description || '',
            coverPath: d.cover_image || null,
            category: d.course_type || '',
            difficulty: d.difficulty || 0,
            tags: d.keywords || []
          },
          sectionList: sectionData.map(item => ({
            section: item.section,
            editable: false,
            knowledgeList: [],
            subsections: item.subsections || []
          }))
        }
      } else {
        courseDetail.value.sectionList = []
      }
      
      updateCourseStats()
      return
    }
  } catch (error) {
    console.log('获取课程详情失败', error)
  }

  // 接口失败兜底数据
  form.courseName = `课程 ${courseId.value}`
  form.description = ''
  form.courseType = ''
  form.difficulty = 0
  form.startDate = ''
  form.weeks = 16
  form.keywords = []
  Object.assign(originalForm, JSON.parse(JSON.stringify(form)))

  courseDetail.value = {
    course: { courseName: `课程 ${courseId.value}`, courseDesc: '', coverPath: null, category: '', difficulty: 0, tags: [] },
    sectionList: []
  }
  updateCourseStats()
}


// 更新课程统计
const updateCourseStats = () => {
    courseStats.value.totalSections = courseDetail.value.sectionList.length
    courseStats.value.totalSubsections = courseDetail.value.sectionList.reduce((sum, section) => {
        return sum + (section.subsections ? section.subsections.length : 0)
    }, 0)
}

// 重置课程表单 — 用数据库原始数据回填
const resetCourseForm = () => {
  Object.assign(form, JSON.parse(JSON.stringify(originalForm)))
  ElMessage.success('已重置为服务器数据')
}

// 更新课程信息 — 调新接口
const updateCourse = async () => {
  try {
    const payload = {
      courseId: Number(courseId.value),
      courseName: form.courseName,
      description: form.description,
      courseType: form.courseType,
      difficulty: form.difficulty,
      startDate: form.startDate,
      weeks: form.weeks,
      keywordsJson: JSON.stringify(form.keywords)
    }

    const res = await request.post('/course/updateCourse', payload)
    if (res.data.code === '200') {
      ElMessage.success('课程信息更新成功')
      Object.assign(originalForm, JSON.parse(JSON.stringify(form)))
    } else {
      ElMessage.error(res.data.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('请求失败')
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
        // 计算当前最大章节序号
        const maxOrder = courseDetail.value.sectionList.reduce((max, item) => {
            return Math.max(max, item.section.chapterOrder || 0)
        }, 0)
        
        const formData = {
            courseId: courseId.value,
            chapterName: '新章节',
            chapterDescription: '请输入章节描述',
            chapterOrder: maxOrder + 1
        }
        const response = await request.post('/section/addSection', formData)
        if (response.data.code === '200') {
            ElMessage.success('章节添加成功')
            // 重新获取章节列表
            await refreshSections()
        } else {
            ElMessage.error(response.data.message || '添加失败')
        }
    } catch (error) {
        console.error('添加章节失败', error)
        ElMessage.error('添加章节失败')
    }
}

// 刷新章节列表
const refreshSections = async () => {
    try {
        const sectionRes = await request.get('/section/querySectionsByCourseId', {
            params: { courseId: courseId.value }
        })
        if (sectionRes.data.code === '200') {
            const sectionData = sectionRes.data.data || []
            courseDetail.value.sectionList = sectionData.map(item => ({
                section: item.section,
                editable: false,
                knowledgeList: [],
                subsections: item.subsections || []
            }))
            updateCourseStats()
        }
    } catch (error) {
        console.error('刷新章节列表失败', error)
    }
}



// 关键词输入相关
const keywordInputVisible = ref(false)
const keywordInputValue = ref('')
const keywordInputRef = ref(null)

const showKeywordInput = () => {
  keywordInputVisible.value = true
  nextTick(() => {
    keywordInputRef.value?.focus()
  })
}

const addKeyword = () => {
  const val = keywordInputValue.value.trim()
  if (val && !form.keywords.includes(val)) {
    form.keywords.push(val)
  }
  keywordInputVisible.value = false
  keywordInputValue.value = ''
}

const updateSection = async (sectionDTO) => {
    try {
        await request.post('/section/updateSection', sectionDTO)
        ElMessage.success("成功更新");
    } catch (error) {
        ElMessage.success('章节更新成功（模拟）')
    }
}

const deleteSection = async (sectionId) => {
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
        
        // 后端接收的是 @RequestBody String sectionId，直接传字符串
        const response = await request.post('/section/deleteSection', String(sectionId))
        if (response.data.code === '200') {
            ElMessage.success('章节删除成功')
            // 重新获取章节列表
            await refreshSections()
        } else {
            ElMessage.error(response.data.message || '删除失败')
        }
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
            console.error(error)
        }
    }
}


// 小节操作
const addSubsection = (chapterId) => {
    currentChapterId.value = chapterId
    // 重置表单
    subsectionForm.contentName = ''
    subsectionForm.contentDescription = ''
    subsectionForm.contentType = 'document'
    subsectionForm.videoUrl = null
    subsectionForm.documentUrl = null
    fileList.value = []
    uploadedFileInfo.value = null
    subsectionDialogVisible.value = true
}

const submitSubsection = async () => {
    // 验证必填项
    if (!subsectionForm.contentName.trim()) {
        ElMessage.warning('请输入小节名称')
        return
    }
    if (!subsectionForm.contentType) {
        ElMessage.warning('请选择内容类型')
        return
    }

    submitting.value = true
    try {
        // 计算当前章节下小节的顺序
        const currentSection = courseDetail.value.sectionList.find(
            item => item.section.chapterId === currentChapterId.value
        )
        const maxOrder = currentSection?.subsections?.reduce((max, item) => {
            return Math.max(max, item.contentOrder || 0)
        }, 0) || 0

        // 构建提交数据
        const formData = {
            chapterId: currentChapterId.value,
            contentName: subsectionForm.contentName,
            contentDescription: subsectionForm.contentDescription,
            contentType: subsectionForm.contentType,
            contentOrder: maxOrder + 1,
            videoUrl: null,
            documentUrl: null
        }

        // 如果有上传文件，保存到 public/Course_files 目录
        if (fileList.value.length > 0) {
            const file = fileList.value[0].raw
            // 生成文件名：章节ID_时间戳_原文件名
            const timestamp = Date.now()
            const fileExt = file.name.split('.').pop()
            const fileName = `${currentChapterId.value}_${timestamp}.${fileExt}`
            const filePath = `/Course_files/${fileName}`
            
            // 根据内容类型设置对应的URL字段
            if (subsectionForm.contentType === 'video') {
                formData.videoUrl = filePath
            } else {
                formData.documentUrl = filePath
            }
            
            // 保存文件到 public/Course_files 目录
            await saveFileToPublic(file, fileName)
        }

        // 调用后端接口
        const response = await request.post('/section/addSubsection', formData)
        if (response.data.code === '200') {
            ElMessage.success('小节添加成功')
            subsectionDialogVisible.value = false
            // 刷新章节列表
            await refreshSections()
        } else {
            ElMessage.error(response.data.message || '添加失败')
        }
    } catch (error) {
        console.error('添加小节失败', error)
        ElMessage.error('添加小节失败')
    } finally {
        submitting.value = false
    }
}

// 保存文件到 public/Course_files 目录
const saveFileToPublic = (file, fileName) => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = (e) => {
            try {
                // 使用 Blob 保存文件
                const blob = new Blob([e.target.result], { type: file.type })
                // 创建下载链接
                const link = document.createElement('a')
                link.href = URL.createObjectURL(blob)
                link.download = fileName
                document.body.appendChild(link)
                link.click()
                document.body.removeChild(link)
                
                // 实际上，在浏览器中我们无法直接写入文件系统
                // 这里提示用户文件已准备好下载，需要手动保存到 public/Course_files
                ElMessage.info('请将下载的文件保存到 public/Course_files 目录')
                
                // 在实际项目中，你应该通过后端接口来保存文件
                // 这里简化处理，将文件转换为 base64 存储
                const reader2 = new FileReader()
                reader2.onload = () => {
                    // 这里可以存储 base64 数据，或者通过后端保存
                    resolve()
                }
                reader2.readAsDataURL(file)
                resolve()
            } catch (error) {
                reject(error)
            }
        }
        reader.onerror = reject
        reader.readAsArrayBuffer(file)
    })
}

// 修改 preview 方法，支持文件预览和下载
const preview = async (contentId, contentType) => {
    // 根据 contentId 查找对应的小节
    let targetSubsection = null
    for (const section of courseDetail.value.sectionList) {
        const found = section.subsections?.find(sub => sub.contentId === contentId)
        if (found) {
            targetSubsection = found
            break
        }
    }
    
    if (!targetSubsection) {
        ElMessage.error('未找到该小节')
        return
    }

    // 根据内容类型显示不同的预览
    let fileUrl = null
    if (contentType === 'video') {
        fileUrl = targetSubsection.videoUrl
    } else {
        fileUrl = targetSubsection.documentUrl
    }

    if (!fileUrl) {
        ElMessage.warning('该小节暂无关联文件')
        return
    }

    // 构建完整的文件路径
    const fullPath = `${window.location.origin}${fileUrl}`
    
    // 根据文件类型进行处理
    if (contentType === 'video') {
        // 视频文件：在新窗口打开
        window.open(fullPath, '_blank')
    } else {
        // 文档文件：提供预览和下载选项
        ElMessageBox.confirm(
            `文件路径：${fileUrl}\n是否要下载或打开文件？`,
            '文件预览',
            {
                confirmButtonText: '下载文件',
                cancelButtonText: '在新窗口打开',
                distinguishCancelAndClose: true
            }
        ).then(() => {
            // 下载文件
            const link = document.createElement('a')
            link.href = fullPath
            link.download = fileUrl.split('/').pop()
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
            ElMessage.success('开始下载')
        }).catch((action) => {
            if (action === 'cancel') {
                // 在新窗口打开
                window.open(fullPath, '_blank')
            }
        })
    }
}

// const editSubsection = (subsectionId) => {
//     router.push({
//         name: "OpSubsection",
//         query: {
//             "subsectionId": subsectionId,
//         }
//     })
// }

const deleteSubsection = async (subsectionId) => {
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
        
        const response = await request.post('/section/deleteSubsection', String(subsectionId))
        if (response.data.code === '200') {
            ElMessage.success("删除成功")
            await refreshSections()
        } else {
            ElMessage.error(response.data.message || '删除失败')
        }
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
            console.error(error)
        }
    }
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