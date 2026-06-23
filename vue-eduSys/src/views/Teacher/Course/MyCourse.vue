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
                                <div class="stat-label">总课程数</div>
                            </div>
                        </div>
                    </el-card>
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><User /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ courseStats.totalStudents }}</div>
                                <div class="stat-label">总学生数</div>
                            </div>
                        </div>
                    </el-card>
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><Clock /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ courseStats.activeCourses }}</div>
                                <div class="stat-label">进行中</div>
                            </div>
                        </div>
                    </el-card>
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><Star /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ courseStats.avgRating }}</div>
                                <div class="stat-label">平均评分</div>
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
                    <el-tabs v-model="activeName" style="width: 400px;" stretch>
                        <el-tab-pane label="全部课程" name="all" />
                        <el-tab-pane label="进行中" name="ongoing" />
                        <el-tab-pane label="已结束" name="finished" />
                        <!-- <el-tab-pane label="草稿" name="draft" /> -->
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
            <!-- 新增课程card -->
            <el-card v-if="currentPage == 1" class="course-card add-course-card" shadow="hover" @click="addCourse">
                <div class="add-course-content">
                    <el-icon class="add-icon"><Plus /></el-icon>
                    <div class="add-text">创建新课程</div>
                    <div class="add-desc">开始您的教学之旅</div>
                </div>
            </el-card>
            
            <!-- 课程列表 -->
            <el-card class="course-card" shadow="hover" v-for="courseDTO in currentPageData"
                :key="courseDTO.course.courseId">
                <div class="course-header">
                    <el-image class="course-cover" :src="courseDTO.course.coverPath" fit="cover" />
                    <div class="course-status">
                        <el-tag :type="getStatusType(courseDTO.course.status)" size="small">
                            {{ getStatusText(courseDTO.course.status) }}
                        </el-tag>
                    </div>
                </div>
                
                <div class="course-content">
                    <h3 class="course-title">{{ courseDTO.course.courseName }}</h3>
                    <p class="course-desc">{{ courseDTO.course.courseDesc || '暂无课程描述' }}</p>
                    
                    <div class="course-stats">
                        <div class="stat-item">
                            <el-icon><Memo /></el-icon>
                            <span>{{ courseDTO.sectionNum }} 章节</span>
                        </div>
                        <div class="stat-item">
                            <el-icon><User /></el-icon>
                            <span>{{ courseDTO.studentNum }} 学生</span>
                        </div>
                        <div class="stat-item">
                            <el-icon><View /></el-icon>
                            <span>{{ courseDTO.course.viewCount || 0 }} 浏览</span>
                        </div>
                    </div>
                    
                    <div class="course-footer">
                        <div class="course-rating">
                            <el-rate :model-value="courseDTO.course.rating" disabled show-score text-color="#ff9900" />
                        </div>
                        <div class="course-actions">
                            <el-button type="primary" size="small" @click="editCourse(courseDTO.course.courseId)">
                                编辑课程
                            </el-button>
                            <!-- <el-button size="small" @click="viewAnalytics(courseDTO.course.courseId)">
                                数据分析
                            </el-button> -->
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
                :total="filterList.length + 1"
                @current-change="handleCurrentChange" 
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus, User, Memo, Document, Clock, Star, View } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router';
import { queryCourseByTeacherId } from '@/api/course'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()

const router = useRouter()

const activeName = ref('all')

// 课程统计数据
const courseStats = ref({
    totalCourses: 12,
    totalStudents: 156,
    activeCourses: 8,
    avgRating: 4.6
})

const courseDTOList = ref([])

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

// 查询课程列表
// eslint-disable-next-line no-unused-vars
const getCourseList = async () => {
    try {
        const teacherId = authStore.userId

        const res = await queryCourseByTeacherId(teacherId)

        const list = res?.data?.data || []

        courseDTOList.value = list.map((item, index) => {
            const course = item.course || {}

            return {
                course: {
                    courseId: course.courseId,
                    courseName: course.courseName,
                    courseDesc: course.description,
                    coverPath: coverList[index % coverList.length],
                    status: 'ongoing',
                    viewCount: 0,
                    rating: course.score || 0
                },
                sectionNum: item.sectionNum || 0,
                studentNum: item.studentNum || 0
            }
        })

    } catch (error) {
        ElMessage.error('课程加载失败')
        console.error(error)
    }
}

const handleSearch = () => {
    conditionOfName.value = searchQuery.value;
}

// 跳转编辑course页面
const editCourse = (courseId) => {
    router.push({
        name: "courseDetail",
        params: {
            courseId: courseId,
        }
    })
}

// 查看数据分析
// eslint-disable-next-line no-unused-vars
const viewAnalytics = (courseId) => {
    ElMessage.info('数据分析功能开发中...')
}

// 新增课程
// 跳转到创建课程页面
const addCourse = () => {
  router.push({ name: 'createCourse' })
}

// 获取状态类型
const getStatusType = (status) => {
    const statusMap = {
        'ongoing': 'success',
        'finished': 'info',
        'draft': 'warning'
    }
    return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
    const statusMap = {
        'ongoing': '进行中',
        'finished': '已结束',
        'draft': '草稿'
    }
    return statusMap[status] || '未知'
}

// 处理搜索操作
const searchQuery = ref("")
const conditionOfName = ref("")

// 分页
const currentPage = ref(1)
const pageSize = ref(8)
const handleCurrentChange = (newPage) => {
    currentPage.value = newPage;
};

//条件过滤后的数据
const filterList = computed(() => {
    let filtered = courseDTOList.value.filter(courseDTO => {
        return (conditionOfName.value == '' || courseDTO.course.courseName.includes(conditionOfName.value));
    })
    
    // 根据tab筛选
    if (activeName.value === 'ongoing') {
        filtered = filtered.filter(courseDTO => courseDTO.course.status === 'ongoing')
    } else if (activeName.value === 'finished') {
        filtered = filtered.filter(courseDTO => courseDTO.course.status === 'finished')
    } else if (activeName.value === 'draft') {
        filtered = filtered.filter(courseDTO => courseDTO.course.status === 'draft')
    }
    
    return filtered
})

// 计算当前页显示的数据
const currentPageData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value - 1;
    const end = start + pageSize.value;
    return filterList.value.slice(start < 0 ? 0 : start, end);
});

onMounted(() => {
    getCourseList()
});


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

.add-course-card {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 280px;
    border: 2px dashed #d9d9d9;
    background: #fafafa;
}

.add-course-card:hover {
    border-color: #409eff;
    background: #f0f9ff;
}

.add-course-content {
    text-align: center;
}

.add-icon {
    font-size: 48px;
    color: #409eff;
    margin-bottom: 16px;
}

.add-text {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
}

.add-desc {
    font-size: 14px;
    color: #909399;
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

.course-stats {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #909399;
}

.stat-item .el-icon {
    font-size: 14px;
}

.course-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.course-rating {
    flex: 1;
}

.course-actions {
    display: flex;
    margin-left: 6px;
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