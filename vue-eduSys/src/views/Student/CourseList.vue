<!-- 学生查看课程列表页面 -->
<template>
    <el-container>
        <el-header>
            <!-- 顶部筛选和搜索栏 -->
            <div class="filter-search-bar">
                <div class="filters">
                    <el-tabs v-model="activeName" style="width: 300px;" stretch>
                        <el-tab-pane label="全部" :name="JOIN_TYPES.ALL" />
                        <el-tab-pane label="已加入" :name="JOIN_TYPES.JOIN" />
                        <el-tab-pane label="未加入" :name="JOIN_TYPES.UNJOIN" />
                    </el-tabs>
                </div>
                <div class="search">
                    <el-input v-model="searchQuery" placeholder="搜索课程" />
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                </div>
            </div>
        </el-header>
        <!-- main -->
        <el-main class="course-container">
            <!-- 课程列表 -->
            <TransitionGroup name="fade">
                <el-card v-for="(courseDTO, index) in currentPageData" :key="courseDTO.course.courseId"
                    style="width: 300px;height: 250px;">
                    <!-- 课程封面 -->
                    <el-row>
                        <el-col :span="24">
                            <el-image class="coverImg"
                                style="width: 100%; height: 150px;transition: transform 0.3s ease;"
                                :src="courseDTO.course.coverPath" fit="cover" />
                        </el-col>
                    </el-row>
                    <!-- 课程基础信息 -->
                    <el-row>
                        <el-col :span="18"
                            style="height: 30px;font-size: medium;font-weight: bold;margin-top: 5px;cursor: pointer;">
                            <el-popover placement="left" title="课程描述" :width="300" trigger="hover"
                                :content="courseDTO.course.courseDesc">
                                <template #reference>
                                    <span>{{ courseDTO.course.courseName }}</span>
                                </template>
                            </el-popover>
                        </el-col>

                    </el-row>
                    <!-- 底部统计信息栏 -->
                    <el-row style="align-items: center;">
                        <el-tooltip class="box-item" effect="dark" content="章节总数" placement="bottom">
                            <el-col :span="4" style="font-size: smaller; color: #909399;align-items: center;">
                                <el-icon style="font-size: 15px;vertical-align: middle;">
                                    <Memo />
                                </el-icon>
                                <span style="vertical-align: middle;">{{ "：" + courseDTO.sectionNum }}</span>
                            </el-col>
                        </el-tooltip>
                        <el-tooltip class="box-item" effect="dark" content="学生总数" placement="bottom">
                            <el-col :span="4" style="font-size: smaller; color: #909399;">
                                <el-icon style="font-size: 15px;vertical-align: middle;">
                                    <User />
                                </el-icon>
                                <span style="vertical-align: middle;">{{ "：" + courseDTO.studentNum }}</span>
                            </el-col>
                        </el-tooltip>
                        <el-tooltip class="box-item" effect="dark" content="课程教师" placement="bottom">
                            <el-col :span="8" style="font-size: smaller; color: #909399;">
                                <el-icon style="font-size: 15px;vertical-align: middle;">
                                    <Stamp />
                                </el-icon>
                                <span style="vertical-align: middle;">{{ "：" + courseDTO.teacherName }}</span>
                            </el-col>
                        </el-tooltip>
                        <el-col :span="4" :offset="2" style="margin-bottom: 5px;">
                            <el-button v-if="courseDTO.ifJoin > 0" type="primary"
                                @click="studyCourse(courseDTO.course.courseId)">开始学习</el-button>
                            <el-button v-if="courseDTO.ifJoin == 0" type="primary"
                                @click="joinCourse(courseDTO.course.courseId, index)" plain>加入课程</el-button>
                        </el-col>
                    </el-row>
                </el-card>
            </TransitionGroup>

        </el-main>
        <!-- 分页 -->
        <el-footer style="height: 32px;">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" background
                layout="total,prev, pager, next, jumper" :total="filterList.length"
                @current-change="handleCurrentChange" />
        </el-footer>
    </el-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
// import { ElMessage } from 'element-plus';
import { User, Memo, Stamp } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router';
import request from '@/utils/request.js'
import { ElMessage } from 'element-plus';

const router = useRouter()

const JOIN_TYPES = {
    ALL: 'all',
    JOIN: 'join',
    UNJOIN: 'unJoin'
}
const activeName = ref(JOIN_TYPES.JOIN)

const courseDTOList = ref([])

onMounted(() => {
    getCourseList();
});

// 查询课程列表
const getCourseList = async () => {
    const res = await request.post('/course/queryAllCourse')
    courseDTOList.value = res.data.data;
}
const handleSearch = () => {
    conditionOfName.value = searchQuery.value;
}

// 跳转course详情学习页面
const studyCourse = (courseId) => {
    router.push({
        name: "courseStudy",
        query: {
            'courseId': courseId,
        }
    })
}
// 加入课程
const joinCourse = async (courseId, index) => {
    const res = await request.post('/course/joinCourse', courseId);
    if (res.data.code == "200") {
        courseDTOList.value[index].ifJoin = 1;
        courseDTOList.value[index].studentNum++;
    }
    ElMessage.success("成功加入课程");
}

// footer
// 分页
const currentPage = ref(1)
const pageSize = ref(8)
const handleCurrentChange = (newPage) => {
    currentPage.value = newPage;
};
// 处理搜索操作
const searchQuery = ref("")
const conditionOfName = ref("")

//条件过滤后的数据
const filterList = computed(() => {
    return courseDTOList.value.filter(courseDTO => {
        return ((conditionOfName.value == '' || courseDTO.course.courseName.includes(conditionOfName.value))
            && judgeJoinType(courseDTO.ifJoin));
    })
})
const judgeJoinType = (ifJoin) => {
    if (activeName.value == JOIN_TYPES.ALL
        || activeName.value == JOIN_TYPES.JOIN && ifJoin > 0
        || activeName.value == JOIN_TYPES.UNJOIN && ifJoin == 0) return true;
    return false;
}
// 计算当前页显示的数据

const currentPageData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    return filterList.value.slice(start, end);
});

</script>

<style scoped>
/* 鼠标悬浮、点击动画效果 */
.hover-zoom {
    cursor: pointer;
    transition: transform 0.3s ease;
}

.hover-zoom:hover {
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.hover-zoom:hover .coverImg {
    transform: scale(1.05);
}

.hover-zoom:active {
    transform: scale(0.98);
    background: rgb(159.5, 206.5, 255);
}
/* 淡入淡出动画效果 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.fade-move {
    transition: transform 0.3s ease;
}
/* head */
.filters,
.search {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.filter-search-bar {
    display: flex;
    justify-content: space-between;
    margin-top: 0.8rem;
    align-items: center;
    flex-wrap: wrap;
    gap: 0.5rem;
}

/* Main */
.course-container {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
    height: 570px;
    margin-left: 70px;
}

.courseCard {
    padding: 5px;
}
</style>