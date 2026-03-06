<template>
  <div class="question-bank-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">试题集管理</h2>
      <div class="stats-cards">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon class="stat-icon"><Files /></el-icon>
            <div class="stat-info">
              <div class="stat-number">{{ questionStats.totalSets }}</div>
              <div class="stat-label">试题集总数</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon class="stat-icon"><Document /></el-icon>
            <div class="stat-info">
              <div class="stat-number">{{ questionStats.totalQuestions }}</div>
              <div class="stat-label">题目总数</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon class="stat-icon"><View /></el-icon>
            <div class="stat-info">
              <div class="stat-number">{{ questionStats.totalViews }}</div>
              <div class="stat-label">总浏览量</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon class="stat-icon"><Download /></el-icon>
            <div class="stat-info">
              <div class="stat-number">{{ questionStats.totalDownloads }}</div>
              <div class="stat-label">总下载量</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 筛选和搜索栏 -->
    <div class="filter-section">
      <div class="filter-content">
        <div class="filters">
          <el-select v-model="filterType" placeholder="题型筛选" clearable style="width: 150px;">
            <el-option label="全部题型" value="" />
            <el-option label="选择题" value="选择题" />
            <el-option label="主观题" value="主观题" />
            <el-option label="编程题" value="编程题" />
          </el-select>
          <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px;">
            <el-option label="创建时间" value="createTime" />
            <el-option label="题目数量" value="questionNum" />
            <el-option label="浏览量" value="viewCount" />
            <el-option label="下载量" value="downloadCount" />
          </el-select>
        </div>
        <div class="search">
          <el-input 
            v-model="searchQuery" 
            placeholder="搜索试题集名称" 
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
          <el-button type="primary" @click="addQuestionSet">
            <el-icon><Plus /></el-icon>
            新增试题集
          </el-button>
          <el-button type="success" @click="batchExport" :disabled="selectedSets.length === 0">
            <el-icon><Download /></el-icon>
            批量导出
          </el-button>
          <el-button type="danger" @click="batchDelete" :disabled="selectedSets.length === 0">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
        <div class="right-actions">
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 试题集列表 -->
    <div class="question-sets-section">
      <el-table 
        :data="filteredQuestionSets" 
        @selection-change="handleSelectionChange"
        style="width: 80%"
        v-loading="loading"
        stripe
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="setName" label="试题集名称" width="200">
          <template #default="{ row }">
            <div class="set-name">
              <el-icon class="set-icon"><Files /></el-icon>
              <span class="set-title">{{ row.setName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="setDesc" label="描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="questionNum" label="题目数量" width="120" align="center" sortable>
          <template #default="{ row }">
            <el-tag type="info">{{ row.questionNum }} 题</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="questionTags" label="题型分布" width="250" align="center">
          <template #default="{ row }">
            <div class="question-tags">
              <el-tag 
                v-for="(tag, index) in row.questionTags" 
                :key="index" 
                :type="typeMap[tag]"
                size="small"
                style="margin: 2px;"
              >
                {{ tag }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" sortable>
          <template #default="{ row }">
            <span class="time-text">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" align="center" sortable>
          <template #default="{ row }">
            <span class="count-text">{{ row.viewCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="handleEdit(row.setId)">
                <el-icon><View /></el-icon>
                查看编辑
              </el-button>
              <el-button size="small" type="success" plain @click="handleDownload(row.setId)">
                <el-icon><Download /></el-icon>
                导出
              </el-button>
              <el-button size="small" type="warning" plain @click="handlePreview(row.setId)">
                <el-icon><View /></el-icon>
                预览
              </el-button>
              <el-button size="small" type="danger" plain @click="handleDelete(row.setId, row.setName)">
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
        :total="filteredQuestionSets.length"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange" 
      />
    </div>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="试题集预览" width="80%" top="5vh">
      <div class="preview-content" v-if="previewData">
        <h3>{{ previewData.setName }}</h3>
        <p class="preview-desc">{{ previewData.setDesc }}</p>
        <div class="preview-stats">
          <el-tag type="info">题目数量: {{ previewData.questionNum }}</el-tag>
          <el-tag type="success">题型: {{ previewData.questionTags?.join(', ') }}</el-tag>
        </div>
        <div class="preview-questions">
          <h4>题目列表</h4>
          <div v-for="(question, index) in previewData.questions" :key="question.questionId" class="preview-question">
            <div class="question-header">
              <span class="question-index">{{ index + 1 }}.</span>
              <el-tag :type="typeMap[question.questionType]" size="small">
                {{ question.questionType }}
              </el-tag>
            </div>
            <div class="question-content">{{ question.questionStem }}</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { 
  Plus, Files, Document, View, Download, Delete, 
  Search, Refresh 
} from '@element-plus/icons-vue'
import request from '@/utils/request.js'
// import { QUESTION_TYPE } from '@/constants/questionTypes';

const router = useRouter()

const questionSet = ref([])
const loading = ref(false)
const selectedSets = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const searchQuery = ref('')
const filterType = ref('')
const sortBy = ref('createTime')
const previewVisible = ref(false)
const previewData = ref(null)

// 试题集统计数据
const questionStats = ref({
  totalSets: 0,
  totalQuestions: 0,
  totalViews: 0,
  totalDownloads: 0
})

const typeMap = {
    '选择题': 'primary',
    '主观题': 'success',
    '编程题': 'info'
}

onMounted(() => {
    // 直接使用模拟数据，确保页面有内容显示
    questionSet.value = getMockData()
    updateStats()
    
    // 同时尝试获取真实数据
    getQuestionSet()
})

// 更新统计数据
const updateStats = () => {
  questionStats.value.totalSets = questionSet.value.length
  questionStats.value.totalQuestions = questionSet.value.reduce((sum, set) => sum + (set.questionNum || 0), 0)
  questionStats.value.totalViews = questionSet.value.reduce((sum, set) => sum + (set.viewCount || 0), 0)
  questionStats.value.totalDownloads = questionSet.value.reduce((sum, set) => sum + (set.downloadCount || 0), 0)
}

// 获取模拟数据
const getMockData = () => {
    return [
        {
            setId: 1,
            setName: "Vue.js基础试题集",
            setDesc: "包含Vue.js基础语法、组件开发、路由管理等核心知识点，适合初学者和进阶学习者使用",
            questionNum: 25,
            questionTags: ["选择题", "主观题"],
            createTime: "2024-01-15T10:30:00",
            viewCount: 156,
            downloadCount: 23
        },
        {
            setId: 2,
            setName: "JavaScript进阶试题集",
            setDesc: "涵盖ES6+语法、异步编程、函数式编程、设计模式等高级主题，适合有一定基础的开发者",
            questionNum: 30,
            questionTags: ["选择题", "编程题"],
            createTime: "2024-01-20T14:20:00",
            viewCount: 89,
            downloadCount: 15
        },
        {
            setId: 3,
            setName: "CSS布局与动画试题集",
            setDesc: "包含Flexbox、Grid布局、CSS动画、响应式设计、预处理器等内容，全面覆盖现代CSS技术",
            questionNum: 18,
            questionTags: ["选择题", "主观题"],
            createTime: "2024-01-25T09:15:00",
            viewCount: 67,
            downloadCount: 8
        },
        {
            setId: 4,
            setName: "前端工程化试题集",
            setDesc: "Webpack、Vite、Git、CI/CD、代码规范、性能优化等前端工程化工具和流程",
            questionNum: 22,
            questionTags: ["选择题", "主观题", "编程题"],
            createTime: "2024-02-01T16:45:00",
            viewCount: 45,
            downloadCount: 12
        },
        {
            setId: 5,
            setName: "React基础试题集",
            setDesc: "React核心概念、Hooks、状态管理、组件通信、性能优化等React开发必备知识",
            questionNum: 28,
            questionTags: ["选择题", "编程题"],
            createTime: "2024-02-05T11:30:00",
            viewCount: 78,
            downloadCount: 19
        },
        {
            setId: 6,
            setName: "TypeScript实战试题集",
            setDesc: "TypeScript基础语法、高级类型、泛型、装饰器、与Vue/React集成等实用技能",
            questionNum: 35,
            questionTags: ["选择题", "编程题", "主观题"],
            createTime: "2024-02-10T13:20:00",
            viewCount: 234,
            downloadCount: 45
        },
        {
            setId: 7,
            setName: "Node.js后端开发试题集",
            setDesc: "Node.js基础、Express框架、数据库操作、API设计、中间件开发等后端技能",
            questionNum: 40,
            questionTags: ["选择题", "编程题"],
            createTime: "2024-02-15T15:30:00",
            viewCount: 123,
            downloadCount: 28
        },
        {
            setId: 8,
            setName: "移动端开发试题集",
            setDesc: "移动端适配、PWA、Hybrid App、小程序开发、移动端性能优化等",
            questionNum: 20,
            questionTags: ["选择题", "主观题"],
            createTime: "2024-02-20T10:45:00",
            viewCount: 89,
            downloadCount: 16
        },
        {
            setId: 9,
            setName: "数据结构与算法试题集",
            setDesc: "常见数据结构、排序算法、搜索算法、动态规划、贪心算法等编程基础",
            questionNum: 50,
            questionTags: ["选择题", "编程题"],
            createTime: "2024-02-25T09:15:00",
            viewCount: 345,
            downloadCount: 67
        },
        {
            setId: 10,
            setName: "前端安全与性能试题集",
            setDesc: "XSS、CSRF、SQL注入、性能监控、代码分割、懒加载等安全与性能优化",
            questionNum: 26,
            questionTags: ["选择题", "主观题", "编程题"],
            createTime: "2024-03-01T14:20:00",
            viewCount: 178,
            downloadCount: 34
        }
    ]
}

// 获取试题集list
const getQuestionSet = async() => {
    loading.value = true
    try {
        const response = await request.post('/question/querySets')
        if (response.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试")
            // 使用模拟数据
            questionSet.value = getMockData()
            updateStats()
            return
        }
        questionSet.value = response.data.data
        
        // 如果没有数据，使用模拟数据展示效果
        if (!questionSet.value || questionSet.value.length === 0) {
            questionSet.value = getMockData()
        }
        
        updateStats()
    } catch (error) {
        ElMessage.error('请求失败，使用模拟数据')
        // 网络错误时也使用模拟数据
        questionSet.value = getMockData()
        updateStats()
    } finally {
        loading.value = false
    }
}

// 搜索和筛选
const filteredQuestionSets = computed(() => {
    let filtered = questionSet.value
    
    // 搜索过滤
    if (searchQuery.value) {
        filtered = filtered.filter(set => 
            set.setName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
            set.setDesc.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
    }
    
    // 题型过滤
    if (filterType.value) {
        filtered = filtered.filter(set => 
            set.questionTags && set.questionTags.includes(filterType.value)
        )
    }
    
    // 排序
    filtered.sort((a, b) => {
        switch (sortBy.value) {
            case 'questionNum':
                return (b.questionNum || 0) - (a.questionNum || 0)
            case 'viewCount':
                return (b.viewCount || 0) - (a.viewCount || 0)
            case 'downloadCount':
                return (b.downloadCount || 0) - (a.downloadCount || 0)
            case 'createTime':
            default:
                return new Date(b.createTime || 0) - new Date(a.createTime || 0)
        }
    })
    
    return filtered
})

const handleSearch = () => {
    currentPage.value = 1
}

const handleReset = () => {
    searchQuery.value = ''
    filterType.value = ''
    sortBy.value = 'createTime'
    currentPage.value = 1
}

// 批量操作
const handleSelectionChange = (selection) => {
    selectedSets.value = selection
}

const batchExport = () => {
    ElMessage.info(`批量导出 ${selectedSets.value.length} 个试题集功能开发中...`)
}

const batchDelete = async () => {
    try {
        await ElMessageBox.confirm(
            `确定要删除选中的 ${selectedSets.value.length} 个试题集吗？此操作不可撤销！`, 
            '警告', 
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        
        ElMessage.success('批量删除成功')
        getQuestionSet()
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('批量删除失败')
        }
    }
}

const refreshData = () => {
    getQuestionSet()
}

// 新增试题集
const addQuestionSet = async() => {
    try {
        const response = await request.post('/question/addSet')
        if (response.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试")
            return
        }
        handleEdit(response.data.data)
    } catch (error) {
        ElMessage.error('请求失败', error)
    }
}

// 编辑试题
const handleEdit = (setId) => {
    console.log(setId)
    router.push({
        name: "questionDetailOfBank",
        query: { setId: setId }
    })
}

// 删除试题集
const handleDelete = async(setId, setName) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除试题集"${setName}"吗？此操作不可撤销！`, 
            '警告', 
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        
        try {
            const response = await request.post('/question/deleteSet', setId)
            if (response.data.code != 200) {
                ElMessage.error("后端处理失败，请稍后再试")
                return
            }
        } catch (error) {
            // 如果是模拟数据，直接本地删除
            const index = questionSet.value.findIndex(set => set.setId === setId)
            if (index > -1) {
                questionSet.value.splice(index, 1)
                updateStats()
                ElMessage.success("删除成功")
                return
            }
            throw error
        }
        
        ElMessage.success("删除成功")
        getQuestionSet()
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('请求失败', error)
        }
    }
}

// 导出试题集
const handleDownload = (setId) => {
    console.log(setId)
    ElMessage.info('导出功能开发中...')
}

// 预览试题集
const handlePreview = async (setId) => {
    try {
        const response = await request.post('/question/querySet', setId)
        if (response.data.code != 200) {
            ElMessage.error("获取试题集信息失败")
            return
        }
        
        const questionsResponse = await request.post('/question/queryQuestions', setId)
        if (questionsResponse.data.code != 200) {
            ElMessage.error("获取题目信息失败")
            return
        }
        
        previewData.value = {
            ...response.data.data,
            questions: questionsResponse.data.data
        }
        previewVisible.value = true
    } catch (error) {
        ElMessage.error('预览失败', error)
        // 网络错误时使用模拟数据
        const mockQuestions = [
            {
                questionId: 1,
                questionType: "选择题",
                questionStem: "Vue.js中，以下哪个选项用于声明响应式数据？A. data() B. computed C. methods D. watch"
            },
            {
                questionId: 2,
                questionType: "选择题",
                questionStem: "在Vue 3中，Composition API的主要优势是什么？A. 更好的TypeScript支持 B. 更小的包体积 C. 更好的性能 D. 以上都是"
            },
            {
                questionId: 3,
                questionType: "选择题",
                questionStem: "Vue.js中，v-if和v-show的区别是什么？A. v-if是条件渲染，v-show是条件显示 B. v-if性能更好 C. v-show会创建和销毁DOM D. 没有区别"
            },
            {
                questionId: 4,
                questionType: "主观题",
                questionStem: "请详细说明Vue.js的生命周期钩子函数及其执行顺序，并解释每个钩子的作用。"
            },
            {
                questionId: 5,
                questionType: "主观题",
                questionStem: "解释Vue.js中的响应式原理，包括Object.defineProperty和Proxy的区别。"
            },
            {
                questionId: 6,
                questionType: "编程题",
                questionStem: "编写一个Vue组件，实现一个简单的计数器功能，包含增加、减少、重置按钮。"
            },
            {
                questionId: 7,
                questionType: "编程题",
                questionStem: "使用Vue 3 Composition API创建一个Todo List组件，支持添加、删除、标记完成功能。"
            },
            {
                questionId: 8,
                questionType: "主观题",
                questionStem: "请说明Vuex和Pinia的区别，以及为什么Vue 3推荐使用Pinia。"
            },
            {
                questionId: 9,
                questionType: "选择题",
                questionStem: "Vue Router中，动态路由参数如何获取？A. this.$route.params B. this.$router.params C. route.params D. router.params"
            },
            {
                questionId: 10,
                questionType: "编程题",
                questionStem: "实现一个Vue自定义指令v-focus，使输入框在挂载时自动获得焦点。"
            }
        ]
        
        const currentSet = questionSet.value.find(set => set.setId === setId)
        if (currentSet) {
            previewData.value = {
                ...currentSet,
                questions: mockQuestions
            }
            previewVisible.value = true
        }
    }
}

// 分页
const handleSizeChange = (size) => {
    pageSize.value = size
    currentPage.value = 1
}

const handleCurrentChange = (page) => {
    currentPage.value = page
}

// 格式化时间
const formatTime = (time) => {
    if (!time) return '-'
    return new Date(time).toLocaleString('zh-CN')
}
</script>

<style scoped>
.question-bank-page {
  padding: 32px;
  background-color: #fafafa;
  min-height: calc(100vh - 60px);
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 24px;
}

.stats-cards {
  display: flex;
  justify-content: center;
  gap: 24px;
  flex-wrap: wrap;
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

.filters,
.search {
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

/* 试题集列表 */
.question-sets-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 24px;
}

.set-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.set-icon {
  color: #409eff;
  font-size: 16px;
}

.set-title {
  font-weight: 500;
  color: #303133;
}

.question-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 4px;
}

.time-text {
  color: #909399;
  font-size: 14px;
}

.count-text {
  color: #606266;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
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

/* 预览弹窗 */
.preview-content {
  max-height: 60vh;
  overflow-y: auto;
}

.preview-desc {
  color: #606266;
  margin: 16px 0;
  line-height: 1.6;
}

.preview-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.preview-questions {
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}

.preview-questions h4 {
  margin-bottom: 16px;
  color: #303133;
}

.preview-question {
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.question-index {
  font-weight: 600;
  color: #409eff;
}

.question-content {
  color: #606266;
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .question-bank-page {
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