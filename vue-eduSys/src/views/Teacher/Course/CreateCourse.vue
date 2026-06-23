<template>
  <div class="create-course-page">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ name: 'MyCourse' }">我的课程</el-breadcrumb-item>
        <el-breadcrumb-item>创建新课程</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 表单卡片 -->
    <el-card class="form-card" shadow="never">
      <h3 class="form-title">填写课程基本信息</h3>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        label-position="left"
        class="course-form"
      >
        <!-- 课程名称 -->
        <el-form-item label="课程名称" prop="courseName">
          <el-input
            v-model="form.courseName"
            placeholder="请输入课程名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <!-- 课程描述 -->
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <!-- 课程类型 & 难度 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="form.courseType" placeholder="请选择课程类型" style="width: 100%">
                <el-option label="编程开发" value="编程开发" />
                <el-option label="软件工程" value="软件工程" />
                <el-option label="理论教学" value="理论教学" />
                <el-option label="综合实训" value="课程实训" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度等级" prop="difficulty">
              <el-rate
                v-model="form.difficulty"
                :max="5"
                :texts="['非常简单', '简单', '中等', '较难', '非常难']"
                show-text
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 开课日期 & 课程时长 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开课日期" prop="startDate">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                placeholder="选择开课日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程时长" prop="weeks">
              <el-input-number
                v-model="form.weeks"
                :min="1"
                :max="24"
                :step="1"
                controls-position="right"
                style="width: 100%"
              />
              <span class="unit-text">周</span>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 关键词 -->
        <el-form-item label="课程关键词" prop="keywords">
          <el-tag
            v-for="(tag, index) in form.keywords"
            :key="index"
            closable
            :disable-transitions="false"
            @close="removeKeyword(index)"
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

        <!-- 封面图片（预留，暂不上传） -->
        <el-form-item label="封面图片">
          <div class="cover-placeholder">
            <el-icon :size="48"><Picture /></el-icon>
            <p>创建课程后可上传封面</p>
          </div>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 底部操作栏 -->
    <div class="footer-actions">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ submitting ? '创建中...' : '创建课程' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref(null)
const submitting = ref(false)

// 表单数据
const form = reactive({
  courseName: '',
  description: '',
  courseType: '',
  difficulty: 0,
  startDate: '',
  weeks: 16,
  keywords: []
})

// 表单校验规则
const rules = {
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '课程名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '课程描述不能超过 200 个字符', trigger: 'blur' }
  ],
  courseType: [
    { required: true, message: '请选择课程类型', trigger: 'change' }
  ],
  difficulty: [
    {
      validator: (rule, value, callback) => {
        if (value === 0) {
          callback(new Error('请设置课程难度'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  startDate: [
    { required: true, message: '请选择开课日期', trigger: 'change' }
  ],
  weeks: [
    { required: true, message: '请输入课程时长', trigger: 'blur' },
    { type: 'number', min: 1, max: 24, message: '课程时长在 1-24 周之间', trigger: 'blur' }
  ]
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

const removeKeyword = (index) => {
  form.keywords.splice(index, 1)
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch {
    ElMessage.warning('请完善必填信息')
    return
  }

  submitting.value = true
  try {
    const payload = {
      courseName: form.courseName,
      description: form.description,
      courseType: form.courseType,
      difficulty: form.difficulty,
      startDate: form.startDate,
      weeks: form.weeks,
      keywordsJson: JSON.stringify(form.keywords),
      teacherId: authStore.userId
    }

    const res = await request.post('/course/addCourse', payload)

    if (res.data.code === '200') {
      const courseId = res.data.data
      ElMessage.success('课程创建成功！')
      // 跳转到课程编辑详情页
      router.push({
        name: 'courseDetail',
        params: { courseId }
      })
    } else {
      ElMessage.error(res.data.msg || '创建失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 取消
const handleCancel = () => {
  router.push({ name: 'MyCourse' })
}
</script>

<style scoped>
.create-course-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px 16px 80px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.form-card {
  border-radius: 12px;
}

.form-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 28px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.course-form {
  max-width: 600px;
}

.unit-text {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}

/* 封面占位 */
.cover-placeholder {
  width: 100%;
  height: 160px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  background: #fafafa;
  cursor: not-allowed;
}

.cover-placeholder p {
  margin-top: 8px;
  font-size: 14px;
}

/* 底部操作栏 */
.footer-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 16px 24px;
  display: flex;
  justify-content: center;
  gap: 16px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  z-index: 10;
}
</style>