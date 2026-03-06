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
                        <el-tab-pane label="草稿" name="draft" />
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
                            <el-button size="small" @click="viewAnalytics(courseDTO.course.courseId)">
                                数据分析
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
import request from '@/utils/request.js'

const router = useRouter()

const activeName = ref('all')

// 课程统计数据
const courseStats = ref({
    totalCourses: 12,
    totalStudents: 156,
    activeCourses: 8,
    avgRating: 4.6
})

// 假数据 - 课程列表
const courseDTOList = ref([
    {
        course: {
            courseId: 1,
            courseName: 'Web前端开发基础',
            courseDesc: 'HTML、CSS、JavaScript基础入门，掌握网页开发核心技术',
            coverPath: 'https://image-cdn.tuchong.com/weili/image/l/2153901102248493063.jpeg',
            status: 'ongoing',
            viewCount: 1250,
            rating: 4.8
        },
        sectionNum: 15,
        studentNum: 156
    },
    {
        course: {
            courseId: 2,
            courseName: 'Vue.js框架开发',
            courseDesc: 'Vue.js组件化开发，Vue Router路由管理，Vuex状态管理',
            coverPath: 'https://image-cdn.tuchong.com/weili/image/l/2093654891623219200.jpeg',
            status: 'ongoing',
            viewCount: 980,
            rating: 4.6
        },
        sectionNum: 12,
        studentNum: 89
    },
    {
        course: {
            courseId: 3,
            courseName: 'React前端开发',
            courseDesc: 'React Hooks、组件生命周期、状态管理、性能优化',
            coverPath: 'https://image-cdn.tuchong.com/weili/l/2113862558194991116.jpeg',
            status: 'finished',
            viewCount: 756,
            rating: 4.5
        },
        sectionNum: 18,
        studentNum: 67
    },
    {
        course: {
            courseId: 4,
            courseName: 'Python数据分析',
            courseDesc: 'pandas数据处理、numpy数值计算、matplotlib数据可视化',
            coverPath: 'https://image-cdn.tuchong.com/weili/image/l/2314040203495473165.jpeg',
            status: 'draft',
            viewCount: 0,
            rating: 0
        },
        sectionNum: 8,
        studentNum: 0
    },
    {
        course: {
            courseId: 5,
            courseName: 'Java Web开发',
            courseDesc: 'Spring Boot框架、MyBatis持久层、MySQL数据库设计',
            coverPath: 'https://image-cdn.tuchong.com/weili/image/l/2146817258150166540.jpeg',
            status: 'ongoing',
            viewCount: 890,
            rating: 4.7
        },
        sectionNum: 20,
        studentNum: 98
    },
    {
        course: {
            courseId: 6,
            courseName: '数据结构与算法',
            courseDesc: '线性表、栈、队列、树、图、排序算法、查找算法',
            coverPath: 'https://image-cdn.tuchong.com/weili/l/2180383432623915013.jpeg',
            status: 'finished',
            viewCount: 1200,
            rating: 4.9
        },
        sectionNum: 16,
        studentNum: 203
    },
    {
        course: {
            courseId: 7,
            courseName: '移动端开发',
            courseDesc: '微信小程序开发、uni-app跨平台开发、移动端适配',
            coverPath: 'https://image-cdn.tuchong.com/weili/l/963123910559268959.jpeg',
            status: 'draft',
            viewCount: 0,
            rating: 0
        },
        sectionNum: 10,
        studentNum: 0
    },
    {
        course: {
            courseId: 8,
            courseName: 'DevOps运维开发',
            courseDesc: 'Docker容器化、Kubernetes编排、CI/CD自动化部署',
            coverPath: 'https://image-cdn.tuchong.com/weili/image/l/1942752512861011982.jpeg',
            status: 'draft',
            viewCount: 0,
            rating: 0
        },
        sectionNum: 6,
        studentNum: 0
    }
])

onMounted(() => {
    // getCourseList();
});

// 查询课程列表
// eslint-disable-next-line no-unused-vars
const getCourseList = async () => {
    const res = await request.post('/course/queryCourseByTeacherId')
    courseDTOList.value = res.data.data;
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
const addCourse = async() => {
    try {
        const response = await request.post('/course/addCourse')
        if (response.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试");
            return;
        }
        editCourse(response.data.data);
    } catch (error) {
        ElMessage.error('请求失败', error)
    }
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