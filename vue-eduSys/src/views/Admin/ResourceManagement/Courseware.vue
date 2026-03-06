<template>
  <div class="courseware-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">课件资源管理</h2>
        <div class="stats-cards">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Document /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ resourceStats.totalResources }}</div>
                <div class="stat-label">总资源数</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Folder /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ resourceStats.totalSubjects }}</div>
                <div class="stat-label">学科分类</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Download /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ resourceStats.totalDownloads }}</div>
                <div class="stat-label">总下载量</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><User /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ resourceStats.activeUsers }}</div>
                <div class="stat-label">活跃用户</div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 学科分类和筛选 -->
    <div class="filter-section">
      <div class="filter-content">
        <div class="subject-filter">
          <el-select v-model="selectedSubject" placeholder="选择学科" style="width: 200px;" @change="handleSubjectChange">
            <el-option label="全部学科" value="" />
            <el-option v-for="subject in subjects" :key="subject.id" :label="subject.name" :value="subject.id" />
          </el-select>
          <el-select v-model="selectedType" placeholder="资源类型" style="width: 150px;" @change="handleTypeChange">
            <el-option label="全部类型" value="" />
            <el-option label="课件" value="courseware" />
            <el-option label="练习题" value="exercise" />
            <el-option label="参考答案" value="answer" />
            <el-option label="拓展资料" value="extension" />
          </el-select>
          <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px;">
            <el-option label="上传时间" value="createTime" />
            <el-option label="下载量" value="downloadCount" />
            <el-option label="文件大小" value="fileSize" />
            <el-option label="评分" value="rating" />
          </el-select>
        </div>
        <div class="search">
          <el-input 
            v-model="searchQuery" 
            placeholder="搜索资源名称、标签或关键词" 
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
          <el-button type="primary" @click="showUploadDialog = true">
            <el-icon><Upload /></el-icon>
            上传资源
          </el-button>
          <el-button type="success" @click="batchExport" :disabled="selectedResources.length === 0">
            <el-icon><Download /></el-icon>
            批量导出
          </el-button>
          <el-button type="warning" @click="batchTag" :disabled="selectedResources.length === 0">
            <el-icon><Edit /></el-icon>
            批量标签
          </el-button>
          <el-button type="danger" @click="batchDelete" :disabled="selectedResources.length === 0">
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

    <!-- 资源列表 -->
    <div class="resource-section">
      <el-table 
        :data="filteredResources" 
        @selection-change="handleSelectionChange"
        style="width: 100%"
        v-loading="loading"
        stripe
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="资源名称" min-width="200">
          <template #default="{ row }">
            <div class="resource-name">
              <el-icon class="resource-icon" :class="getResourceIconClass(row.type)">
                <component :is="getResourceIcon(row.type)" />
              </el-icon>
              <span class="resource-title">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="subject" label="学科分类" width="120" align="center" />
        <el-table-column prop="type" label="资源类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tags" label="标签" width="200" align="center">
          <template #default="{ row }">
            <div class="resource-tags">
              <el-tag 
                v-for="tag in row.tags" 
                :key="tag" 
                size="small"
                style="margin: 2px;"
              >
                {{ tag }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="文件大小" width="100" align="center" />
        <el-table-column prop="downloadCount" label="下载量" width="100" align="center" sortable>
          <template #default="{ row }">
            <span class="count-text">{{ row.downloadCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="120" align="center" sortable>
          <template #default="{ row }">
            <el-rate :model-value="row.rating" disabled show-score text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="creator" label="上传者" width="120" align="center" />
        <el-table-column prop="createTime" label="上传时间" width="180" align="center" sortable>
          <template #default="{ row }">
            <span class="time-text">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="previewResource(row)">
                <el-icon><View /></el-icon>
                预览
              </el-button>
              <el-button size="small" type="success" plain @click="downloadResource(row)">
                <el-icon><Download /></el-icon>
                下载
              </el-button>
              <el-button size="small" type="warning" plain @click="editResource(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button size="small" type="danger" plain @click="deleteResource(row)">
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
        :total="filteredResources.length"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange" 
      />
    </div>

    <!-- 上传资源弹窗 -->
    <el-dialog v-model="showUploadDialog" title="上传资源" width="600px" center>
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="资源名称" required>
          <el-input v-model="uploadForm.name" placeholder="请输入资源名称" />
        </el-form-item>
        <el-form-item label="学科分类" required>
          <el-select v-model="uploadForm.subject" placeholder="选择学科" style="width: 100%;">
            <el-option v-for="subject in subjects" :key="subject.id" :label="subject.name" :value="subject.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源类型" required>
          <el-select v-model="uploadForm.type" placeholder="选择资源类型" style="width: 100%;">
            <el-option label="课件" value="courseware" />
            <el-option label="练习题" value="exercise" />
            <el-option label="参考答案" value="answer" />
            <el-option label="拓展资料" value="extension" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源描述">
          <el-input v-model="uploadForm.description" type="textarea" :rows="3" placeholder="请输入资源描述" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input-tag v-model="uploadForm.tags" placeholder="请输入标签，回车确认" />
        </el-form-item>
        <el-form-item label="上传文件" required>
          <el-upload 
            v-model:file-list="uploadForm.files" 
            class="resource-upload" 
            action="#"
            :auto-upload="false" 
            :limit="5" 
            drag
            accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.zip,.rar"
          >
            <div class="upload-placeholder">
              <el-icon class="upload-icon"><Upload /></el-icon>
              <div class="upload-text">
                拖动或<em>点击选择文件</em>
              </div>
              <div class="upload-hint">支持 PDF、DOC、PPT、XLS、ZIP 格式，单个文件不超过100MB</div>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showUploadDialog = false">取消</el-button>
          <el-button type="primary" @click="uploadResource">上传</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, Folder, Download, User, Search, Upload, 
  Edit, Delete, Refresh, View, Memo, Files, VideoPlay 
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const selectedResources = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const searchQuery = ref('')
const selectedSubject = ref('')
const selectedType = ref('')
const sortBy = ref('createTime')
const showUploadDialog = ref(false)

// 资源统计数据
const resourceStats = ref({
  totalResources: 0,
  totalSubjects: 0,
  totalDownloads: 0,
  activeUsers: 0
})

// 学科分类
const subjects = ref([
  { id: 'linux', name: 'Linux系统管理' },
  { id: 'math', name: '高等数学' },
  { id: 'cpp', name: 'C++程序设计' },
  { id: 'java', name: 'Java程序设计' },
  { id: 'python', name: 'Python编程' },
  { id: 'web', name: 'Web前端开发' },
  { id: 'database', name: '数据库原理' },
  { id: 'network', name: '计算机网络' },
  { id: 'os', name: '操作系统' },
  { id: 'algorithm', name: '数据结构与算法' }
])

// 资源列表
const resourceList = ref([])

// 上传表单
const uploadForm = ref({
  name: '',
  subject: '',
  type: '',
  description: '',
  tags: [],
  files: []
})

onMounted(() => {
  getResourceList()
  updateStats()
})

// 获取资源列表
const getResourceList = () => {
  // 模拟数据
  resourceList.value = [
    {
      id: 1,
      name: 'Linux基础命令详解',
      subject: 'Linux系统管理',
      type: 'courseware',
      tags: ['Linux', '命令', '基础'],
      fileSize: '2.5MB',
      downloadCount: 156,
      rating: 4.8,
      creator: '张老师',
      createTime: '2024-01-15T10:30:00',
      description: 'Linux系统基础命令的详细讲解，包含常用命令的使用方法和实例。'
    },
    {
      id: 2,
      name: '高等数学第一章练习题',
      subject: '高等数学',
      type: 'exercise',
      tags: ['高数', '练习题', '第一章'],
      fileSize: '1.2MB',
      downloadCount: 89,
      rating: 4.5,
      creator: '李老师',
      createTime: '2024-01-20T14:20:00',
      description: '高等数学第一章函数与极限的练习题集，包含详细解答。'
    },
    {
      id: 3,
      name: 'C++面向对象编程课件',
      subject: 'C++程序设计',
      type: 'courseware',
      tags: ['C++', '面向对象', '编程'],
      fileSize: '5.8MB',
      downloadCount: 234,
      rating: 4.9,
      creator: '王老师',
      createTime: '2024-01-25T09:15:00',
      description: 'C++面向对象编程的完整课件，包含类、继承、多态等内容。'
    },
    {
      id: 4,
      name: 'Java集合框架参考答案',
      subject: 'Java程序设计',
      type: 'answer',
      tags: ['Java', '集合', '参考答案'],
      fileSize: '800KB',
      downloadCount: 67,
      rating: 4.6,
      creator: '陈老师',
      createTime: '2024-02-01T16:45:00',
      description: 'Java集合框架相关练习题的参考答案和详细解析。'
    },
    {
      id: 5,
      name: 'Python数据分析拓展资料',
      subject: 'Python编程',
      type: 'extension',
      tags: ['Python', '数据分析', '拓展'],
      fileSize: '3.2MB',
      downloadCount: 123,
      rating: 4.7,
      creator: '刘老师',
      createTime: '2024-02-05T11:30:00',
      description: 'Python数据分析的拓展学习资料，包含实际项目案例。'
    },
    {
      id: 6,
      name: 'Web前端开发实战课件',
      subject: 'Web前端开发',
      type: 'courseware',
      tags: ['Web', '前端', '实战'],
      fileSize: '8.5MB',
      downloadCount: 345,
      rating: 4.9,
      creator: '赵老师',
      createTime: '2024-02-10T13:20:00',
      description: 'Web前端开发实战课程课件，包含HTML、CSS、JavaScript完整教程。'
    },
    {
      id: 7,
      name: '数据库设计练习题',
      subject: '数据库原理',
      type: 'exercise',
      tags: ['数据库', '设计', '练习题'],
      fileSize: '1.8MB',
      downloadCount: 78,
      rating: 4.4,
      creator: '孙老师',
      createTime: '2024-02-15T15:30:00',
      description: '数据库设计相关的练习题集，包含ER图设计和SQL语句编写。'
    },
    {
      id: 8,
      name: '计算机网络实验指导',
      subject: '计算机网络',
      type: 'courseware',
      tags: ['网络', '实验', '指导'],
      fileSize: '4.1MB',
      downloadCount: 112,
      rating: 4.6,
      creator: '周老师',
      createTime: '2024-02-20T10:45:00',
      description: '计算机网络课程的实验指导书，包含各种网络协议的实验。'
    }
  ]
}

// 更新统计数据
const updateStats = () => {
  resourceStats.value.totalResources = resourceList.value.length
  resourceStats.value.totalSubjects = subjects.value.length
  resourceStats.value.totalDownloads = resourceList.value.reduce((sum, resource) => sum + resource.downloadCount, 0)
  resourceStats.value.activeUsers = 156
}

// 过滤和搜索
const filteredResources = computed(() => {
  let filtered = resourceList.value
  
  // 搜索过滤
  if (searchQuery.value) {
    filtered = filtered.filter(resource => 
      resource.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      resource.description.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      resource.tags.some(tag => tag.toLowerCase().includes(searchQuery.value.toLowerCase()))
    )
  }
  
  // 学科过滤
  if (selectedSubject.value) {
    const subjectName = subjects.value.find(s => s.id === selectedSubject.value)?.name
    filtered = filtered.filter(resource => resource.subject === subjectName)
  }
  
  // 类型过滤
  if (selectedType.value) {
    filtered = filtered.filter(resource => resource.type === selectedType.value)
  }
  
  // 排序
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'downloadCount':
        return b.downloadCount - a.downloadCount
      case 'fileSize':
        return parseFloat(b.fileSize) - parseFloat(a.fileSize)
      case 'rating':
        return b.rating - a.rating
      case 'createTime':
      default:
        return new Date(b.createTime) - new Date(a.createTime)
    }
  })
  
  return filtered
})

// 事件处理
const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  searchQuery.value = ''
  selectedSubject.value = ''
  selectedType.value = ''
  sortBy.value = 'createTime'
  currentPage.value = 1
}

const handleSubjectChange = () => {
  currentPage.value = 1
}

const handleTypeChange = () => {
  currentPage.value = 1
}

const handleSelectionChange = (selection) => {
  selectedResources.value = selection
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const refreshData = () => {
  getResourceList()
  updateStats()
}

// 批量操作
const batchExport = () => {
  ElMessage.info(`批量导出 ${selectedResources.value.length} 个资源功能开发中...`)
}

const batchTag = () => {
  ElMessage.info(`批量标签 ${selectedResources.value.length} 个资源功能开发中...`)
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedResources.value.length} 个资源吗？此操作不可撤销！`, 
      '警告', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('批量删除成功')
    getResourceList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 单个资源操作
const previewResource = (resource) => {
  ElMessage.info(`预览资源: ${resource.name}`)
}

const downloadResource = (resource) => {
  ElMessage.success(`开始下载: ${resource.name}`)
}

const editResource = (resource) => {
  ElMessage.info(`编辑资源: ${resource.name}`)
}

const deleteResource = async (resource) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除资源"${resource.name}"吗？此操作不可撤销！`, 
      '警告', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('删除成功')
    getResourceList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const uploadResource = () => {
  if (!uploadForm.value.name || !uploadForm.value.subject || !uploadForm.value.type) {
    ElMessage.error('请填写必填项')
    return
  }
  
  ElMessage.success('资源上传成功')
  showUploadDialog.value = false
  uploadForm.value = {
    name: '',
    subject: '',
    type: '',
    description: '',
    tags: [],
    files: []
  }
  getResourceList()
}

// 工具函数
const getResourceIcon = (type) => {
  const iconMap = {
    courseware: Document,
    exercise: Memo,
    answer: Files,
    extension: VideoPlay
  }
  return iconMap[type] || Document
}

const getResourceIconClass = (type) => {
  const classMap = {
    courseware: 'icon-courseware',
    exercise: 'icon-exercise',
    answer: 'icon-answer',
    extension: 'icon-extension'
  }
  return classMap[type] || 'icon-default'
}

const getTypeTagType = (type) => {
  const typeMap = {
    courseware: 'primary',
    exercise: 'success',
    answer: 'info',
    extension: 'warning'
  }
  return typeMap[type] || 'default'
}

const getTypeText = (type) => {
  const textMap = {
    courseware: '课件',
    exercise: '练习题',
    answer: '参考答案',
    extension: '拓展资料'
  }
  return textMap[type] || '未知'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}
</script>

<style scoped>
.courseware-page {
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

.subject-filter,
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

/* 资源列表 */
.resource-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 24px;
}

.resource-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.resource-icon {
  font-size: 16px;
  color: #409eff;
}

.resource-title {
  font-weight: 500;
  color: #303133;
}

.resource-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
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

/* 上传弹窗 */
.resource-upload {
  width: 100%;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 150px;
  padding: 20px;
}

.upload-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.upload-text {
  font-size: 16px;
  color: #606266;
  margin-bottom: 8px;
  text-align: center;
}

.upload-text em {
  color: #409eff;
  font-style: normal;
  font-weight: 500;
}

.upload-hint {
  font-size: 14px;
  color: #909399;
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .courseware-page {
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