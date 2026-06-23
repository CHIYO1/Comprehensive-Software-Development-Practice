<template>
  <div class="course-page">
    <!-- 页面标题和统计 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">我的课程</h2>
        <div class="stats-cards">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Document /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ courseStats.totalCourses }}</div>
                <div class="stat-label">已选课程</div>
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
              <el-icon class="stat-icon"><Trophy /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ courseStats.completedCourses }}</div>
                <div class="stat-label">已完成</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Star /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ courseStats.avgScore }}</div>
                <div class="stat-label">平均成绩</div>
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
            <el-tab-pane label="已结束" name="finished" />
            <el-tab-pane label="待开始" name="pending" />
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
          <div class="course-progress" v-if="course.progress !== undefined">
            <div class="progress-text">{{ course.progress }}%</div>
            <el-progress :percentage="course.progress" :stroke-width="4" />
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
          
          <div class="course-stats">
            <div class="stat-item">
              <span class="stat-label">学习进度</span>
              <span class="stat-value">{{ course.progress || 0 }}%</span>
            </div>
            <div class="stat-item">
              <!-- <span class="stat-label">作业完成</span>
              <span class="stat-value">{{ course.homeworkCompleted || 0 }}/{{ course.totalHomework || 0 }}</span> -->
              <span class="stat-label">课程章节</span>
              <span class="stat-value">
                {{ course.sectionNum || 0 }}
              </span>
            </div>
            <div class="stat-item">
              <span class="stat-label">考试成绩</span>
              <span class="stat-value">{{ course.score || '--' }}</span>
            </div>
          </div>
          
          <div class="course-footer">
            <div class="course-actions">
              <el-button type="primary" size="small" @click="enterCourse(course.id)">
                进入课程
              </el-button>
              <el-button size="small" @click="viewProgress(course.id)">
                学习进度
              </el-button>
              <el-button type="danger" size="small" @click="dropCourse(course.id)">
                退课
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Clock,
  Trophy,
  Star,
  User,
  UserFilled,
  Calendar
} from '@element-plus/icons-vue'

import { queryMyCourses, dropCourseApi  } from '@/api/course'
import { useAuthStore } from '@/store/auth'

const router = useRouter()

const activeTab = ref('all')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(8)

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

// 课程统计数据
const courseStats = ref({
  totalCourses: 0,
  ongoingCourses: 0,
  completedCourses: 0,
  avgScore: 0
})

// 课程列表
const allCourses = ref([])

// 获取课程状态
// const getCourseStatus = (startDate) => {
//   if (!startDate) return '进行中'

//   const today = new Date()
//   const start = new Date(startDate)

//   if (start > today) {
//     return '待开始'
//   }

//   return '进行中'
// }

// 获取课程列表
const loadCourseList = async () => {
  try {

    const authStore = useAuthStore()

    const userId = authStore.userId

    const res = await queryMyCourses(userId)

    if (res.data.code !== '200') {
      ElMessage.error(res.data.message || '获取课程失败')
      return
    }

    const courses = res.data.data?.courses || []

    allCourses.value = courses.map(item => {

      let progress = 0

      if (
        item.progress &&
        item.progress.total_lessons > 0
      ) {
        progress = Math.round(
          item.progress.completed_lessons /
            item.progress.total_lessons *
            100
        )
      }

      // 判断课程状态
      let status = '进行中'

      const today = new Date()
      const startDate = new Date(item.start_date)
      const endDate = new Date(item.end_date)

      if (today < startDate) {
        status = '待开始'
      } else if (today > endDate) {
        status = '已结束'
      }

      return {
        id: item.course_id,

        cover:
          coverList[
            (item.course_id - 1) %
              coverList.length
          ],

        title: item.course_name,

        desc: item.description,

        teacher: item.teacher_name,

        students: item.student_count,

        startDate: item.start_date,

        status,

        progress,

        homeworkCompleted:
          item.homework?.completed || 0,

        totalHomework:
          item.homework?.total || 0,

        score: item.score || '--',

        sectionNum:
          item.progress?.total_lessons || 0
      }
    })

    updateCourseStats()

  } catch (error) {
    console.error(error)
    ElMessage.error('获取课程列表失败')
  }
}

// 更新统计信息
const updateCourseStats = () => {
  const list = allCourses.value

  courseStats.value.totalCourses = list.length

  courseStats.value.ongoingCourses =
    list.filter(item => item.status === '进行中').length

  courseStats.value.completedCourses =
    list.filter(item => item.status === '已结束').length

  const scores = list
    .map(item => Number(item.score))
    .filter(item => !isNaN(item))

  courseStats.value.avgScore =
    scores.length > 0
      ? (
          scores.reduce((sum, item) => sum + item, 0) / scores.length
        ).toFixed(1)
      : 0
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    进行中: 'success',
    已结束: 'info',
    待开始: 'warning'
  }
  return statusMap[status] || 'info'
}

// 进入课程
const enterCourse = (courseId) => {
  const authStore = useAuthStore()

  console.log('======================')
  console.log('进入课程点击')
  console.log('courseId:', courseId)
  console.log('当前token:', authStore.token)
  console.log('userInfo:', authStore.userInfo)
  console.log('======================')

  router.push({
    name: 'courseStudy',
    params: { courseId }
  })
}

// 查看学习进度
const viewProgress = (courseId) => {
  console.log(courseId)
  ElMessage.info('学习进度功能开发中...')
}

// 退课
const dropCourse = async (courseId) => {
  try {
    await ElMessageBox.confirm(
      '确定要退课吗？此操作不可撤销！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const authStore = useAuthStore()

    const res = await dropCourseApi({
      student_id: authStore.userId,
      course_id: courseId
    })

    if (res.data.code !== '200') {
      ElMessage.error(res.data.message || '退课失败')
      return
    }

    // 前端同步删除
    const index = allCourses.value.findIndex(
      course => course.id === courseId
    )

    if (index > -1) {
      allCourses.value.splice(index, 1)
      updateCourseStats()
    }

    ElMessage.success('退课成功')

  } catch (error) {
    // 用户取消 or 请求失败
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('退课失败')
    }
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
}

// 筛选
const filterList = computed(() => {
  let list = allCourses.value

  if (activeTab.value === 'ongoing') {
    list = list.filter(c => c.status === '进行中')
  } else if (activeTab.value === 'finished') {
    list = list.filter(c => c.status === '已结束')
  } else if (activeTab.value === 'pending') {
    list = list.filter(c => c.status === '待开始')
  }

  if (searchQuery.value) {
    list = list.filter(c =>
      c.title.includes(searchQuery.value)
    )
  }

  return list
})

// 当前页数据
const currentPageData = computed(() => {
  const start =
    (currentPage.value - 1) * pageSize.value

  const end = start + pageSize.value

  return filterList.value.slice(start, end)
})

// 分页
const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
}

// 页面加载
onMounted(() => {
  loadCourseList()
})
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

.stats-cards .stat-label {
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

.course-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 8px 12px;
}

.progress-text {
  font-size: 12px;
  margin-bottom: 4px;
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

.course-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
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