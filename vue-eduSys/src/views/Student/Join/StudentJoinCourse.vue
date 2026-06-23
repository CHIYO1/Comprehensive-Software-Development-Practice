<template>
  <div class="course-page">
    <!-- 页面标题和统计 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">发现课程</h2>
        <div class="stats-cards">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Document /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ courseStats.totalCourses }}</div>
                <div class="stat-label">可选课程</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Clock /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ courseStats.ongoingCourses }}</div>
                <div class="stat-label">进行中</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Calendar /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ courseStats.upcomingCourses }}</div>
                <div class="stat-label">即将开始</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><User /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ courseStats.totalStudents }}</div>
                <div class="stat-label">总学员数</div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索栏 -->
    <div class="filter-section">
      <div class="filter-content">
        <div class="filters">
          <el-tabs v-model="activeTab" style="width: 400px;" stretch>
            <el-tab-pane label="全部课程" name="all" />
            <el-tab-pane label="进行中" name="ongoing" />
            <el-tab-pane label="即将开始" name="upcoming" />
            <el-tab-pane label="已结束" name="finished" />
          </el-tabs>
        </div>
        <div class="search">
          <el-input v-model="searchQuery" placeholder="搜索课程名称" style="width: 300px;" />
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </div>
    </div>

    <!-- 课程列表 -->
    <div class="course-container">
      <el-card class="course-card" shadow="hover" v-for="course in currentPageData" :key="course.id">
        <div class="course-header">
          <el-image class="course-cover" :src="course.cover" fit="cover" />
          <div class="course-status">
            <el-tag :type="getStatusType(getStatus(course))">
              {{ getStatus(course) }}
            </el-tag>
          </div>
          <div class="course-rating" v-if="course.rating">
            <el-rate :model-value="course.rating" disabled show-score text-color="#ff9900" />
          </div>
        </div>
        
        <div class="course-content">
          <h3 class="course-title">{{ course.title }}</h3>
          <p class="course-desc">{{ course.desc }}</p>
          
          <div class="course-info">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>{{ course.teacher }}</span>
            </div>
            <div class="info-item">
              <el-icon><UserFilled /></el-icon>
              <span>{{ course.students }} 人</span>
            </div>
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ course.startDate }}</span>
            </div>
          </div>
          
          <div class="course-details">
            <div class="detail-item">
              <span class="detail-label">课程时长</span>
              <span class="detail-value">{{ course.duration }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">课程难度</span>
              <span class="detail-value">
                <el-rate :model-value="course.difficulty" disabled :max="3" />
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">课程类型</span>
              <span class="detail-value">{{ course.category }}</span>
            </div>
          </div>
          
          <div class="course-footer">
            <div class="course-actions">
              <!-- <el-button type="primary" size="small" @click="joinCourse(course.id)">
                加入课程
              </el-button> -->
              <el-button
                type="primary"
                size="small"
                :disabled="course.joined"
                @click="joinCourse(course.id)"
              >
                {{ course.joined ? '已加入' : '加入课程' }}
              </el-button>
              <el-button size="small" @click="viewDetails(course.id)">
                课程详情
              </el-button>
              <el-button size="small" @click="previewCourse(course.id)">
                课程预览
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination 
        v-model:current-page="currentPage" 
        v-model:page-size="pageSize" 
        background
        layout="total, prev, pager, next, jumper" 
        :total="filterList.length"
        @current-change="handleCurrentChange" 
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Clock, Calendar, User, UserFilled } from '@element-plus/icons-vue'

import { queryAllCourse, enrollCourse  } from '@/api/course'
import { useAuthStore } from '@/store/auth'

const activeTab = ref('all')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(8)

const allCourses = ref([])

const authStore = useAuthStore()

const userId = authStore.userId

const courseStats = ref({
  totalCourses: 0,
  ongoingCourses: 0,
  upcomingCourses: 0,
  totalStudents: 0
})

const coverList = [
  'https://image-cdn.tuchong.com/weili/image/l/2153901102248493063.jpeg',
  'https://image-cdn.tuchong.com/weili/image/l/2093654891623219200.jpeg',
  'https://image-cdn.tuchong.com/weili/l/2113862558194991116.jpeg',
  'https://image-cdn.tuchong.com/weili/image/l/2314040203495473165.jpeg',
  'https://image-cdn.tuchong.com/weili/image/l/2146817258150166540.jpeg',
  'https://image-cdn.tuchong.com/weili/l/2180383432623915013.jpeg',
  'https://image-cdn.tuchong.com/weili/l/963123910559268959.jpeg',
  'https://image-cdn.tuchong.com/weili/image/l/1942752512861011982.jpeg'
]

/**
 * 加载课程列表（已统一兼容后端返回结构）
 */
const loadCourses = async () => {
  try {
    const res = await queryAllCourse()

    // ✅ 兼容三种可能结构（关键修复点）
    const list =
      res?.data?.data ||
      res?.data ||
      []

    if (!Array.isArray(list)) {
      console.error('课程接口返回结构异常：', res)
      ElMessage.error('课程数据格式异常')
      return
    }

    allCourses.value = list.map(item => ({
      id: item.course?.courseId,

      cover:
        coverList[
          ((item.course?.courseId || 1) - 1) % coverList.length
        ],

      title: item.course?.courseName || '未命名课程',
      desc: item.course?.description || '暂无描述',
      teacher: item.teacherName || '未知教师',
      students: item.studentNum || 0,

      startDate: item.course?.startDate || '--',
      duration: `${item.course?.weeks || 0}周`,
      difficulty: item.course?.difficulty || 1,
      category: item.course?.courseType || '未分类',
      rating: Number(item.course?.score || 0),

      joined: item.ifJoin === 1,
      raw: item
    }))

    // ✅ 统计
    courseStats.value.totalCourses = allCourses.value.length

    courseStats.value.totalStudents = allCourses.value.reduce(
      (sum, item) => sum + (item.students || 0),
      0
    )

    courseStats.value.ongoingCourses = allCourses.value.length
    courseStats.value.upcomingCourses = 0

  } catch (e) {
    console.error('加载课程失败：', e)
    ElMessage.error('获取课程列表失败')
  }
}

onMounted(() => {
  loadCourses()
})

/**
 * 状态显示
 */
const getStatus = (course) => {
  return course.joined ? '已加入' : '可选'
}

const getStatusType = (status) => {
  const map = {
    已加入: 'success',
    可选: 'warning'
  }
  return map[status] || 'info'
}

/**
 * 加入课程
 */
const joinCourse = async (courseId) => {
  try {
    const studentId = userId

    if (!studentId) {
      ElMessage.error('未获取到用户信息，请重新登录')
      return
    }

    await ElMessageBox.confirm(
      '确定要加入这个课程吗？',
      '确认加入',
      {
        confirmButtonText: '确定加入',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    const res = await enrollCourse({
      student_id: studentId,
      course_id: courseId
    })

    if (res.data.code !== '200') {
      ElMessage.error(res.data.message || '加入失败')
      return
    }

    ElMessage.success('加入课程成功')

    // 关键：直接更新状态，不用整页刷新
    const target = allCourses.value.find(c => c.id === courseId)
    if (target) {
      target.joined = true
    }

  } catch (error) {
    console.log(error)
  }
}

/**
 * 查看详情
 */
const viewDetails = (courseId) => {
  console.log(courseId)
  ElMessage.info('课程详情功能开发中...')
}

/**
 * 预览
 */
const previewCourse = (courseId) => {
  console.log(courseId)
  ElMessage.info('课程预览功能开发中...')
}

/**
 * 搜索
 */
const handleSearch = () => {
  currentPage.value = 1
}

/**
 * 过滤
 */
const filterList = computed(() => {
  let list = allCourses.value

  if (searchQuery.value) {
    list = list.filter(item =>
      item.title.includes(searchQuery.value)
    )
  }

  return list
})

/**
 * 分页数据
 */
const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filterList.value.slice(start, end)
})

/**
 * 分页切换
 */
const handleCurrentChange = (page) => {
  currentPage.value = page
}
</script>

<style scoped>
.course-page {
  padding: 32px;
  background-color: #fafafa;
  min-height: calc(100vh - 60px);
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
}

.header-content {
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
  font-weight: 500;
}

/* 筛选区域 */
.filter-section {
  margin-bottom: 32px;
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

/* 课程列表 */
.course-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.course-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.course-header {
  position: relative;
}

.course-cover {
  width: 100%;
  height: 180px;
  transition: transform 0.3s ease;
}

.course-card:hover .course-cover {
  transform: scale(1.05);
}

.course-status {
  position: absolute;
  top: 12px;
  right: 12px;
}

.course-rating {
  position: absolute;
  bottom: 12px;
  left: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
}

.course-content {
  padding: 20px;
}

.course-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.4;
}

.course-desc {
  font-size: 14px;
  color: #606266;
  margin-bottom: 16px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-info {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.info-item .el-icon {
  font-size: 14px;
}

.course-details {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.detail-item {
  text-align: center;
}

.detail-label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.detail-value {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.course-footer {
  display: flex;
  justify-content: center;
}

.course-actions {
  display: flex;
  gap: 8px;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .course-page {
    padding: 24px 16px;
  }
  
  .stats-cards {
    gap: 16px;
  }
  
  .stat-card {
    width: 180px;
  }
  
  .course-container {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .course-page {
    padding: 16px 12px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .stats-cards {
    gap: 12px;
  }
  
  .stat-card {
    width: 150px;
  }
  
  .filter-content {
    flex-direction: column;
    gap: 16px;
  }
  
  .course-container {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style> 