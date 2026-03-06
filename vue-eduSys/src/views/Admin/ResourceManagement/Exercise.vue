<template>
  <div class="exercise-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">练习题资源管理</h2>
        <div class="stats-cards">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Memo /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ exerciseStats.totalExercises }}</div>
                <div class="stat-label">总练习题</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Folder /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ exerciseStats.totalCategories }}</div>
                <div class="stat-label">题目分类</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><User /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ exerciseStats.totalAttempts }}</div>
                <div class="stat-label">总答题次数</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Star /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ exerciseStats.avgScore }}</div>
                <div class="stat-label">平均得分</div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-content">
        <div class="filter-left">
          <el-select v-model="selectedCategory" placeholder="题目分类" style="width: 150px;" @change="handleCategoryChange">
            <el-option label="全部分类" value="" />
            <el-option label="选择题" value="choice" />
            <el-option label="填空题" value="fill" />
            <el-option label="判断题" value="judge" />
            <el-option label="简答题" value="short" />
            <el-option label="编程题" value="programming" />
          </el-select>
          <el-select v-model="selectedDifficulty" placeholder="难度等级" style="width: 150px;" @change="handleDifficultyChange">
            <el-option label="全部难度" value="" />
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
          <el-select v-model="selectedStatus" placeholder="审核状态" style="width: 150px;" @change="handleStatusChange">
            <el-option label="全部状态" value="" />
            <el-option label="已通过" value="approved" />
            <el-option label="审核中" value="pending" />
            <el-option label="未通过" value="rejected" />
          </el-select>
        </div>
        <div class="filter-right">
          <el-input 
            v-model="searchQuery" 
            placeholder="搜索题目内容、标签或关键词" 
            style="width: 300px;"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="action-section">
      <div class="action-content">
        <div class="left-actions">
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增题目
          </el-button>
          <el-button type="success" @click="batchApprove" :disabled="selectedExercises.length === 0">
            <el-icon><Check /></el-icon>
            批量通过
          </el-button>
          <el-button type="warning" @click="batchReject" :disabled="selectedExercises.length === 0">
            <el-icon><Close /></el-icon>
            批量拒绝
          </el-button>
          <el-button type="danger" @click="batchDelete" :disabled="selectedExercises.length === 0">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
        <div class="right-actions">
          <el-button @click="exportExercises">
            <el-icon><Download /></el-icon>
            导出题目
          </el-button>
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 题目列表 -->
    <div class="exercise-section">
      <el-table 
        :data="filteredExercises" 
        @selection-change="handleSelectionChange"
        style="width: 100%"
        v-loading="loading"
        stripe
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="题目内容" min-width="320">
          <template #default="{ row }">
            <div class="exercise-title">
              <el-icon class="exercise-icon" :class="getExerciseIconClass(row.type)">
                <component :is="getExerciseIcon(row.type)" />
              </el-icon>
              <div class="exercise-content">
                <div class="exercise-text">{{ row.title }}</div>
                <div class="exercise-tags">
                  <el-tag 
                    v-for="tag in row.tags" 
                    :key="tag" 
                    size="small"
                    style="margin: 2px;"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="题目分类" width="100" align="center" />
        <el-table-column prop="difficulty" label="难度等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getDifficultyTagType(row.difficulty)" size="small">
              {{ getDifficultyText(row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="80" align="center" />
        <el-table-column prop="attempts" label="答题次数" width="100" align="center" sortable>
          <template #default="{ row }">
            <span class="count-text">{{ row.attempts }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="correctRate" label="正确率" width="100" align="center" sortable>
          <template #default="{ row }">
            <el-progress :percentage="row.correctRate" :color="getProgressColor(row.correctRate)" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="审核状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creator" label="创建者" width="120" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" sortable>
          <template #default="{ row }">
            <span class="time-text">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="previewExercise(row)">
                <el-icon><View /></el-icon>
                预览
              </el-button>
              <el-button size="small" type="success" plain @click="editExercise(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button size="small" type="warning" plain @click="approveExercise(row)" v-if="row.status === 'pending'">
                <el-icon><Check /></el-icon>
                通过
              </el-button>
              <el-button size="small" type="danger" plain @click="deleteExercise(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination 
        v-model:current-page="currentPage" 
        v-model:page-size="pageSize" 
        :page-sizes="[10, 20, 50, 100]"
        background
        layout="total, sizes, prev, pager, next, jumper" 
        :total="filteredExercises.length"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange" 
      />
    </div>

    <!-- 新增题目弹窗 -->
    <el-dialog v-model="showAddDialog" title="新增练习题" width="800px" center>
      <el-form :model="exerciseForm" label-width="100px">
        <el-form-item label="题目类型" required>
          <el-select v-model="exerciseForm.type" placeholder="选择题目类型" style="width: 100%;">
            <el-option label="选择题" value="choice" />
            <el-option label="填空题" value="fill" />
            <el-option label="判断题" value="judge" />
            <el-option label="简答题" value="short" />
            <el-option label="编程题" value="programming" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目内容" required>
          <el-input v-model="exerciseForm.title" type="textarea" :rows="4" placeholder="请输入题目内容" />
        </el-form-item>
        <el-form-item label="题目分类" required>
          <el-select v-model="exerciseForm.category" placeholder="选择题目分类" style="width: 100%;">
            <el-option label="选择题" value="choice" />
            <el-option label="填空题" value="fill" />
            <el-option label="判断题" value="judge" />
            <el-option label="简答题" value="short" />
            <el-option label="编程题" value="programming" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级" required>
          <el-rate v-model="exerciseForm.difficulty" :max="3" show-text :texts="['简单', '中等', '困难']" />
        </el-form-item>
        <el-form-item label="分值" required>
          <el-input-number v-model="exerciseForm.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="题目标签">
          <el-input-tag v-model="exerciseForm.tags" placeholder="输入标签后按回车确认" />
        </el-form-item>
        <el-form-item label="题目解析">
          <el-input v-model="exerciseForm.analysis" type="textarea" :rows="3" placeholder="请输入题目解析" />
        </el-form-item>
        <el-form-item label="答案选项" v-if="exerciseForm.type === 'choice'">
          <div class="options-container">
            <div v-for="(option, index) in exerciseForm.options" :key="index" class="option-item">
              <el-input v-model="exerciseForm.options[index]" :placeholder="`选项 ${String.fromCharCode(65 + index)}`" />
              <el-checkbox v-model="exerciseForm.correctOptions[index]">正确答案</el-checkbox>
            </div>
            <el-button type="primary" @click="addOption">添加选项</el-button>
          </div>
        </el-form-item>
        <el-form-item label="正确答案" v-if="exerciseForm.type !== 'choice'">
          <el-input v-model="exerciseForm.answer" type="textarea" :rows="3" placeholder="请输入正确答案" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="addExercise">新增</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Memo, Folder, User, Star, Search, Plus, Check, Close, Delete, 
  Download, Refresh, View, Edit, Document, VideoPlay, EditPen 
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const selectedExercises = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedDifficulty = ref('')
const selectedStatus = ref('')
const showAddDialog = ref(false)

// 练习题统计数据
const exerciseStats = ref({
  totalExercises: 0,
  totalCategories: 5,
  totalAttempts: 0,
  avgScore: 0
})

// 练习题列表
const exerciseList = ref([])

// 新增题目表单
const exerciseForm = ref({
  type: '',
  title: '',
  category: '',
  difficulty: 2,
  score: 10,
  tags: [],
  analysis: '',
  options: ['', '', '', ''],
  correctOptions: [false, false, false, false],
  answer: ''
})

onMounted(() => {
  getExerciseList()
  updateStats()
})

// 获取练习题列表
const getExerciseList = () => {
  // 模拟数据
  exerciseList.value = [
    {
      id: 1,
      title: 'Vue.js中v-if和v-show的区别是什么？',
      category: '选择题',
      type: 'choice',
      difficulty: 'medium',
      score: 10,
      tags: ['Vue.js', '指令', '条件渲染'],
      attempts: 156,
      correctRate: 78,
      status: 'approved',
      creator: '张老师',
      createTime: '2024-01-15T10:30:00',
      analysis: 'v-if是真正的条件渲染，会销毁和重建DOM元素；v-show只是切换元素的display属性。'
    },
    {
      id: 2,
      title: '请解释JavaScript中的闭包概念，并举例说明。',
      category: '简答题',
      type: 'short',
      difficulty: 'hard',
      score: 20,
      tags: ['JavaScript', '闭包', '作用域'],
      attempts: 89,
      correctRate: 65,
      status: 'approved',
      creator: '李老师',
      createTime: '2024-01-20T14:20:00',
      analysis: '闭包是指有权访问另一个函数作用域中的变量的函数。'
    },
    {
      id: 3,
      title: 'HTML5的新特性包括哪些？',
      category: '填空题',
      type: 'fill',
      difficulty: 'easy',
      score: 15,
      tags: ['HTML5', '新特性'],
      attempts: 234,
      correctRate: 85,
      status: 'pending',
      creator: '王老师',
      createTime: '2024-01-25T09:15:00',
      analysis: 'HTML5的新特性包括语义化标签、表单控件、多媒体支持等。'
    },
    {
      id: 4,
      title: 'CSS中position属性的值有哪些？',
      category: '选择题',
      type: 'choice',
      difficulty: 'medium',
      score: 10,
      tags: ['CSS', '定位'],
      attempts: 178,
      correctRate: 72,
      status: 'approved',
      creator: '陈老师',
      createTime: '2024-02-01T16:45:00',
      analysis: 'position属性包括static、relative、absolute、fixed、sticky等值。'
    },
    {
      id: 5,
      title: '编写一个函数，实现数组去重功能。',
      category: '编程题',
      type: 'programming',
      difficulty: 'medium',
      score: 25,
      tags: ['JavaScript', '数组', '算法'],
      attempts: 67,
      correctRate: 58,
      status: 'rejected',
      creator: '刘老师',
      createTime: '2024-02-05T11:30:00',
      analysis: '可以使用Set、filter、reduce等方法实现数组去重。'
    },
    {
      id: 6,
      title: 'React和Vue.js的主要区别是什么？',
      category: '简答题',
      type: 'short',
      difficulty: 'hard',
      score: 20,
      tags: ['React', 'Vue.js', '框架对比'],
      attempts: 123,
      correctRate: 68,
      status: 'approved',
      creator: '赵老师',
      createTime: '2024-02-10T13:20:00',
      analysis: 'React使用JSX语法，Vue使用模板语法；React更灵活，Vue更易上手。'
    },
    {
      id: 7,
      title: 'HTTP状态码200、404、500分别表示什么？',
      category: '判断题',
      type: 'judge',
      difficulty: 'easy',
      score: 8,
      tags: ['HTTP', '状态码'],
      attempts: 345,
      correctRate: 92,
      status: 'approved',
      creator: '孙老师',
      createTime: '2024-02-15T15:30:00',
      analysis: '200表示成功，404表示未找到，500表示服务器内部错误。'
    },
    {
      id: 8,
      title: '请实现一个简单的Promise类。',
      category: '编程题',
      type: 'programming',
      difficulty: 'hard',
      score: 30,
      tags: ['JavaScript', 'Promise', '异步编程'],
      attempts: 45,
      correctRate: 42,
      status: 'pending',
      creator: '周老师',
      createTime: '2024-02-20T10:45:00',
      analysis: '需要实现then、catch、resolve、reject等方法。'
    }
  ]
}

// 更新统计数据
const updateStats = () => {
  exerciseStats.value.totalExercises = exerciseList.value.length
  exerciseStats.value.totalAttempts = exerciseList.value.reduce((sum, exercise) => sum + exercise.attempts, 0)
  exerciseStats.value.avgScore = Math.round(exerciseList.value.reduce((sum, exercise) => sum + exercise.correctRate, 0) / exerciseList.value.length)
}

// 过滤和搜索
const filteredExercises = computed(() => {
  let filtered = exerciseList.value
  
  // 搜索过滤
  if (searchQuery.value) {
    filtered = filtered.filter(exercise => 
      exercise.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      exercise.tags.some(tag => tag.toLowerCase().includes(searchQuery.value.toLowerCase()))
    )
  }
  
  // 分类过滤
  if (selectedCategory.value) {
    filtered = filtered.filter(exercise => exercise.type === selectedCategory.value)
  }
  
  // 难度过滤
  if (selectedDifficulty.value) {
    filtered = filtered.filter(exercise => exercise.difficulty === selectedDifficulty.value)
  }
  
  // 状态过滤
  if (selectedStatus.value) {
    filtered = filtered.filter(exercise => exercise.status === selectedStatus.value)
  }
  
  return filtered
})

// 事件处理
const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  searchQuery.value = ''
  selectedCategory.value = ''
  selectedDifficulty.value = ''
  selectedStatus.value = ''
  currentPage.value = 1
}

const handleCategoryChange = () => {
  currentPage.value = 1
}

const handleDifficultyChange = () => {
  currentPage.value = 1
}

const handleStatusChange = () => {
  currentPage.value = 1
}

const handleSelectionChange = (selection) => {
  selectedExercises.value = selection
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const refreshData = () => {
  getExerciseList()
  updateStats()
}

// 批量操作
const batchApprove = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要通过选中的 ${selectedExercises.value.length} 个题目吗？`, 
      '确认操作', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('批量通过成功')
    getExerciseList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量通过失败')
    }
  }
}

const batchReject = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要拒绝选中的 ${selectedExercises.value.length} 个题目吗？`, 
      '确认操作', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('批量拒绝成功')
    getExerciseList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量拒绝失败')
    }
  }
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedExercises.value.length} 个题目吗？此操作不可撤销！`, 
      '警告', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('批量删除成功')
    getExerciseList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const exportExercises = () => {
  ElMessage.success('题目导出功能开发中...')
}

// 单个题目操作
const previewExercise = (exercise) => {
  ElMessage.info(`预览题目: ${exercise.title}`)
}

const editExercise = (exercise) => {
  ElMessage.info(`编辑题目: ${exercise.title}`)
}

const approveExercise = async (exercise) => {
  try {
    await ElMessageBox.confirm(
      `确定要通过题目"${exercise.title}"吗？`, 
      '确认操作', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('题目通过成功')
    getExerciseList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const deleteExercise = async (exercise) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除题目"${exercise.title}"吗？此操作不可撤销！`, 
      '警告', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('删除成功')
    getExerciseList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const addExercise = () => {
  if (!exerciseForm.value.title || !exerciseForm.value.type || !exerciseForm.value.category) {
    ElMessage.error('请填写必填项')
    return
  }
  
  ElMessage.success('题目添加成功')
  showAddDialog.value = false
  exerciseForm.value = {
    type: '',
    title: '',
    category: '',
    difficulty: 2,
    score: 10,
    tags: [],
    analysis: '',
    options: ['', '', '', ''],
    correctOptions: [false, false, false, false],
    answer: ''
  }
  getExerciseList()
}

const addOption = () => {
  exerciseForm.value.options.push('')
  exerciseForm.value.correctOptions.push(false)
}

// 工具函数
const getExerciseIcon = (type) => {
  const iconMap = {
    choice: Document,
    fill: EditPen,
    judge: Check,
    short: Memo,
    programming: VideoPlay
  }
  return iconMap[type] || Document
}

const getExerciseIconClass = (type) => {
  const classMap = {
    choice: 'icon-choice',
    fill: 'icon-fill',
    judge: 'icon-judge',
    short: 'icon-short',
    programming: 'icon-programming'
  }
  return classMap[type] || 'icon-default'
}

const getDifficultyTagType = (difficulty) => {
  const typeMap = {
    easy: 'success',
    medium: 'warning',
    hard: 'danger'
  }
  return typeMap[difficulty] || 'default'
}

const getDifficultyText = (difficulty) => {
  const textMap = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return textMap[difficulty] || '未知'
}

const getStatusTagType = (status) => {
  const typeMap = {
    approved: 'success',
    pending: 'warning',
    rejected: 'danger'
  }
  return typeMap[status] || 'default'
}

const getStatusText = (status) => {
  const textMap = {
    approved: '已通过',
    pending: '审核中',
    rejected: '未通过'
  }
  return textMap[status] || '未知'
}

const getProgressColor = (rate) => {
  if (rate >= 80) return '#67c23a'
  if (rate >= 60) return '#e6a23c'
  return '#f56c6c'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}
</script>

<style scoped>
.exercise-page {
  padding: 32px;
  background-color: #fafafa;
  min-height: calc(100vh - 60px);
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.stats-cards {
  display: flex;
  gap: 24px;
}

.stat-card {
  width: 200px;
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 16px;
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

/* 筛选区域 */
.filter-section {
  margin-bottom: 24px;
}

.filter-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-left,
.filter-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 操作区域 */
.action-section {
  margin-bottom: 24px;
}

.action-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.left-actions,
.right-actions {
  display: flex;
  gap: 12px;
}

/* 题目列表 */
.exercise-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 24px;
}

.exercise-title {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.exercise-icon {
  font-size: 16px;
  color: #409eff;
  margin-top: 2px;
}

.exercise-content {
  flex: 1;
}

.exercise-text {
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.5;
}

.exercise-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.count-text {
  color: #606266;
  font-weight: 500;
}

.time-text {
  color: #909399;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 2px;
  flex-wrap: wrap;
  justify-content: center;
}

.action-buttons .el-button {
  transition: all 0.3s ease;
}

.action-buttons .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 新增题目弹窗 */
.options-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.option-item .el-input {
  flex: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exercise-page {
    padding: 16px;
  }
  
  .stats-cards {
    gap: 16px;
  }
  
  .stat-card {
    width: 150px;
  }
  
  .filter-content {
    flex-direction: column;
    gap: 16px;
  }
  
  .action-content {
    flex-direction: column;
    gap: 16px;
  }
  
  .action-buttons {
    justify-content: center;
  }
}
</style> 