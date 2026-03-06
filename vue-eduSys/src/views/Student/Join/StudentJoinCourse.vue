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
            <el-tag :type="getStatusType(course.status)" size="small">
              {{ course.status }}
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
              <el-button type="primary" size="small" @click="joinCourse(course.id)">
                加入课程
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
import { ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Document, Clock, Calendar, User, UserFilled } from '@element-plus/icons-vue';

const activeTab = ref('all')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(8)

// 课程统计数据
const courseStats = ref({
  totalCourses: 15,
  ongoingCourses: 8,
  upcomingCourses: 4,
  totalStudents: 1250
})

// 假数据，实际开发请用接口获取
const allCourses = ref([
  {
    id: 1,
    cover: 'https://image-cdn.tuchong.com/weili/image/l/2153901102248493063.jpeg',
    title: 'Web前端开发基础',
    desc: 'HTML、CSS、JavaScript基础入门，掌握网页开发核心技术，包含DOM操作、事件处理、AJAX异步请求',
    teacher: '王明华',
    students: 156,
    status: '进行中',
    startDate: '2024-01-15',
    duration: '8周',
    difficulty: 2,
    category: '前端开发',
    rating: 4.8
  },
  {
    id: 2,
    cover: 'https://image-cdn.tuchong.com/weili/image/l/2093654891623219200.jpeg',
    title: 'Vue.js框架开发',
    desc: 'Vue.js组件化开发，Vue Router路由管理，Vuex状态管理，Vue CLI脚手架工具',
    teacher: '李志强',
    students: 89,
    status: '进行中',
    startDate: '2024-01-20',
    duration: '10周',
    difficulty: 3,
    category: '前端开发',
    rating: 4.6
  },
  {
    id: 3,
    cover: 'https://image-cdn.tuchong.com/weili/l/2113862558194991116.jpeg',
    title: 'React前端开发',
    desc: 'React Hooks、组件生命周期、状态管理、性能优化、Redux状态管理',
    teacher: '张伟',
    students: 67,
    status: '即将开始',
    startDate: '2024-02-01',
    duration: '12周',
    difficulty: 3,
    category: '前端开发',
    rating: 4.5
  },
  {
    id: 4,
    cover: 'https://image-cdn.tuchong.com/weili/image/l/2314040203495473165.jpeg',
    title: 'Python数据分析',
    desc: 'pandas数据处理、numpy数值计算、matplotlib数据可视化、scikit-learn机器学习',
    teacher: '陈思思',
    students: 134,
    status: '即将开始',
    startDate: '2024-02-15',
    duration: '6周',
    difficulty: 2,
    category: '数据分析',
    rating: 4.7
  },
  {
    id: 5,
    cover: 'https://image-cdn.tuchong.com/weili/image/l/2146817258150166540.jpeg',
    title: 'Java Web开发',
    desc: 'Spring Boot框架、MyBatis持久层、MySQL数据库设计、RESTful API设计',
    teacher: '刘建国',
    students: 98,
    status: '进行中',
    startDate: '2024-01-10',
    duration: '16周',
    difficulty: 3,
    category: '后端开发',
    rating: 4.7
  },
  {
    id: 6,
    cover: 'https://image-cdn.tuchong.com/weili/l/2180383432623915013.jpeg',
    title: '数据结构与算法',
    desc: '线性表、栈、队列、树、图、排序算法、查找算法、算法复杂度分析',
    teacher: '赵敏',
    students: 203,
    status: '已结束',
    startDate: '2023-10-15',
    duration: '10周',
    difficulty: 3,
    category: '计算机基础',
    rating: 4.9
  },
  {
    id: 7,
    cover: 'https://image-cdn.tuchong.com/weili/l/963123910559268959.jpeg',
    title: '移动端开发',
    desc: '微信小程序开发、uni-app跨平台开发、移动端适配、H5移动端开发',
    teacher: '孙丽丽',
    students: 76,
    status: '即将开始',
    startDate: '2024-03-15',
    duration: '6周',
    difficulty: 2,
    category: '移动开发',
    rating: 4.4
  },
  {
    id: 8,
    cover: 'https://image-cdn.tuchong.com/weili/image/l/1942752512861011982.jpeg',
    title: 'DevOps运维开发',
    desc: 'Docker容器化、Kubernetes编排、CI/CD自动化部署、Linux服务器管理',
    teacher: '周建军',
    students: 45,
    status: '即将开始',
    startDate: '2024-04-01',
    duration: '4周',
    difficulty: 3,
    category: '运维部署',
    rating: 4.6
  }
])

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '进行中': 'success',
    '即将开始': 'warning',
    '已结束': 'info'
  }
  return statusMap[status] || 'info'
}

// 加入课程
// eslint-disable-next-line no-unused-vars
const joinCourse = async (courseId) => {
  try {
    await ElMessageBox.confirm('确定要加入这个课程吗？', '确认加入', {
      confirmButtonText: '确定加入',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    ElMessage.success('成功加入课程！')
  } catch (error) {
    // 用户取消操作
  }
}

// 查看课程详情
// eslint-disable-next-line no-unused-vars
const viewDetails = (courseId) => {
  ElMessage.info('课程详情功能开发中...')
}

// 课程预览
// eslint-disable-next-line no-unused-vars
const previewCourse = (courseId) => {
  ElMessage.info('课程预览功能开发中...')
}

const handleSearch = () => {
  currentPage.value = 1
}

const filterList = computed(() => {
  let list = allCourses.value
  if (activeTab.value === 'ongoing') {
    list = list.filter(c => c.status === '进行中')
  } else if (activeTab.value === 'upcoming') {
    list = list.filter(c => c.status === '即将开始')
  } else if (activeTab.value === 'finished') {
    list = list.filter(c => c.status === '已结束')
  }
  if (searchQuery.value) {
    list = list.filter(c => c.title.includes(searchQuery.value))
  }
  return list
})

const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filterList.value.slice(start, end)
})

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
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