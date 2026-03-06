<template>
    <el-container v-if="isRouteActive" style="height:666px;">
        <el-header style="height: 30%;">
            <el-card id="top-card"
                style="width: 80%;align-content: center;height: 70%;margin-bottom: 20px;justify-self: center;">
                <el-form ref="formRef" :model="subsection" :inline="true" label-width="auto" :rules="rules">
                    <el-form-item label="小节名:" style="width: 40%;" prop="subsectionName" :rules="[
                        { required: true, message: '请填写小节名' }]">
                        <el-input v-model="subsection.subsectionName" style="width: 100%;" />
                    </el-form-item>
                    <el-form-item style="width: 50%;">
                        <el-button style="margin-left: 70%;" type="success" @click="save()" plain>保存并返回</el-button>
                    </el-form-item>
                    <el-form-item label="小节描述:" style="width: 50%;">
                        <el-input v-model="subsection.subsectionDesc" type="textarea" style="width: 100%;" />
                    </el-form-item>
                    <el-form-item label="小节内容:" style="margin-left: 20px;width: 40%;" prop="resourceId">
                        <el-icon style="font-size: 20px;margin-top: 2px;color: #409EFF;">
                            <component :is="getIconComponent(subsection.subsectionType)" />
                        </el-icon>
                        <span v-if="selectedResourceName == ''"
                            style="align-content: center;font-size:x-small; color: #909399;">
                            请选择小节内容
                        </span>
                        <span v-if="!selectedResourceName == ''"
                            style="align-content: center;margin-left: 5px;font-size:large; color: #409EFF;">
                            {{ selectedResourceName }}
                        </span>
                    </el-form-item>
                </el-form>
            </el-card>
            <el-row>
                <el-col :span="2" style="align-self: center;">
                    <span style=" font-weight: bold;">小节类型：</span>
                </el-col>
                <el-col :span="8" :offset="0">
                    <el-tabs v-model="activeName" @tab-change="handleChange" style="width: 300px;" stretch>
                        <el-tab-pane label="视频" :name="RESOURCE_TYPES.VIDEO" />
                        <el-tab-pane label="文档" :name="RESOURCE_TYPES.DOCUMENT" />
                        <el-tab-pane label="试题集" :name="RESOURCE_TYPES.QUESTIONS" />
                    </el-tabs>
                </el-col>
                <el-col :span="4" :offset="8">
                    <el-input v-model="searchQuery" placeholder="搜索资源" />
                </el-col>
                <el-col :span="2">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                </el-col>
            </el-row>
        </el-header>
        <el-main style="height: 50%;margin-top: 20px;background-color: white;">
            <!-- 资源显示区 -->
            <div style="height: 90%;">
                <!-- 视频 -->
                <div v-show="activeName == RESOURCE_TYPES.VIDEO" class="video-container">
                    <!-- 视频上传card -->
                    <el-card v-if="currentPage == 1" class="hover-zoom"
                        style="width: 300px;height: 250px;cursor: pointer;"
                        @click="VideoUploadVisable = !VideoUploadVisable">
                        <el-icon class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-card>
                    <!-- 视频列表 -->
                    <el-card v-for="video in currentPageData" :key="video.videoId" style="width: 300px;height: 250px;"
                        :class="['hover-bg', { 'active-card': subsection.subsectionType == RESOURCE_TYPES.VIDEO && subsection.resourceId == video.videoId }]"
                        @click="selecteResource(video.videoId, video.videoName)">
                        <el-row>
                            <el-col :span="24">
                                <el-image class="hover-zoom" style="width: 100%; height: 150px;cursor: pointer;"
                                    :src="video.coverPath" />
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="24" style="height: 45px;font-size: small;font-weight: bold;">
                                <el-popover placement="top-start" :width="300" trigger="hover"
                                    :content="video.videoName">
                                    <template #reference>
                                        <el-text>{{ truncatedContent(video.videoName, 20) }}</el-text>
                                    </template>
                                </el-popover>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="16" style="font-size: smaller; color: #909399;">
                                <span>创建日期：{{ video.createdDate }}</span>
                            </el-col>
                            <!-- 操作 -->
                            <el-col :span="8">
                                <el-button type="primary" @click.stop="selecteResource(video.videoId, video.videoName)"
                                    link>选择</el-button>
                                <el-button type="primary" @click.stop="playVideo(video.videoPath)" link>预览</el-button>
                            </el-col>
                        </el-row>
                    </el-card>
                </div>
                <!-- 文档 -->
                <div v-show="activeName == RESOURCE_TYPES.DOCUMENT" class="document-container">
                    <el-card v-if="currentPage == 1"
                        style="width: 80%;height: 70px;justify-self: center; margin-bottom: 10px;">
                        <el-row>
                            <el-col :span="3">
                                <el-button type="primary" plain
                                    @click="DocumentUploadVisable = !DocumentUploadVisable">上传文件</el-button>
                            </el-col>
                            <el-col :span="3">
                                <el-button type="primary" @click="documentGenerate()">智能生成</el-button>
                            </el-col>
                        </el-row>
                    </el-card>
                    <el-row v-for="document in currentPageData" :key="document.documentId"
                        style="height: 75px;padding: 10px 20px;width: 80%;justify-self: center;"
                        :class="['hover-bg', { 'active-card': subsection.subsectionType == RESOURCE_TYPES.DOCUMENT && subsection.resourceId == document.documentId }]"
                        @click="selecteResource(document.documentId, document.documentName)">
                        <el-col :span="13">
                            <el-row style="font-weight: bold;">{{ document.documentName }}</el-row>
                            <el-row style="font-size: smaller; color: #909399;">
                                <el-popover placement="bottom-start" :width="500" trigger="hover"
                                    :content="document.documentDesc">
                                    <template #reference>
                                        <el-text>{{ truncatedContent(document.documentDesc, 100) }}</el-text>
                                    </template>
                                </el-popover>
                            </el-row>
                        </el-col>
                        <el-col :span="4"></el-col>
                        <el-col :span="2" style="font-size: smaller; color: #909399;padding-top: 20px;">
                            <el-row>文件大小:</el-row>
                            <el-row>{{ document.documentSize }}</el-row>
                        </el-col>
                        <el-col :span="2" style="font-size: smaller; color: #909399;padding-top: 20px;">
                            <el-row>创建时间:</el-row>
                            <el-row>{{ document.createdDate }}</el-row>
                        </el-col>
                        <el-col :span="2" style="text-align: center;padding-top: 20px;">
                            <el-button type="primary"
                                @click.stop="selecteResource(document.documentId, document.documentName)"
                                link>选择</el-button>
                        </el-col>
                        <el-col :span="2"></el-col>
                        <el-col :span="24">
                            <el-divider border-style="dashed" style="margin: 0px;" />
                        </el-col>
                    </el-row>
                </div>
                <!-- 试题集 -->
                <div v-show="activeName == RESOURCE_TYPES.QUESTIONS">
                    <el-card style="width: 80%;height: 70px;justify-self: center; margin-bottom: 10px;">
                        <el-row>
                            <el-col :span="8">
                                <el-button type="primary" plain @click="addQuestionSet()">新增试题集</el-button>
                            </el-col>

                        </el-row>
                    </el-card>
                    <el-table :data="questionSetList" style="width: 80%;height: 100%;padding: 15px;justify-self: center;" show-overflow-tooltip
                        :row-class-name="rowClassName">
                        <el-table-column prop="setName" label="试题集名" width="200" />
                        <el-table-column prop="setDesc" label="描述" width="360" />
                        <el-table-column prop="questionNum" label="总题数" width="120" align="center" header-align="center"
                            sortable />
                        <el-table-column prop="questionTags" label="题型" width="240" align="center"
                            header-align="center">
                            <template #default="{ row }">
                                <el-tag v-for="(tag, index) in row.questionTags" :key="index" class="tag-item"
                                    :type="typeMap[tag]">{{ tag }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="280" header-align="center" align="center">
                            <template #default="{ row }">
                                <el-button size="small" type="primary"
                                    @click.stop="selecteResource(row.setId, row.setName)" plain>
                                    选择
                                </el-button>
                                <el-button size="small" type="primary" @click.stop="handleQuestionsEdit(row.setId)"
                                    plain>
                                    查看
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </div>

        </el-main>
        <el-footer style="margin-top: 10px;background-color: white;align-content: center;">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" background
                layout="total,prev, pager, next, jumper" :total="filterList.length + 1"
                @current-change="handleCurrentChange" />
        </el-footer>
        <VideoUploadDialog v-model:visible="VideoUploadVisable" @handleAddVideo="handleAddVideo" />
        <DocumentUploadDialog v-model:visible="DocumentUploadVisable" @handleAddDocument="handleAddDocument" />
        <VideoPlayDialog v-model:visible="VideoPlayVisable" :videoUrl="VideoPlayingUrl" />
    </el-container>

    <router-view></router-view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useRouter,onBeforeRouteUpdate } from 'vue-router';
import { ElMessage } from 'element-plus'
import { Plus, Document, VideoPlay, EditPen } from '@element-plus/icons-vue'
import VideoUploadDialog from '@/components/file/VideoUpload.vue';
import VideoPlayDialog from '@/components/file/VideoPlay.vue';
import DocumentUploadDialog from '@/components/file/DocumentUpload.vue';
import { addSubsection, updateSubsection, querySubsection } from '@/api/section.js'
import { querySet, querySets, addSet } from '@/api/question.js'
import { queryFile, queryTeacherVideos, queryTeacherDocuments } from '@/api/file.js'
import { RESOURCE_TYPES } from '@/constants/resourceTypes';
import { QUESTION_TYPE } from '@/constants/questionTypes';

const route = useRoute()
const router = useRouter()

// 组件调用传参
const VideoUploadVisable = ref(false);
const DocumentUploadVisable = ref(false);
const VideoPlayVisable = ref(false);
const VideoPlayingUrl = ref(null);

const subsection = ref({
    subsectionId: null,
    subsectionName: '',
    subsectionDesc: '',
    subsectionType: '',
    resourceId: null,
    sectionId: null,
})
const typeMap = {
    [QUESTION_TYPE.CHOICE]: 'primary',
    [QUESTION_TYPE.SUBJECTIVE]: 'success',
    [QUESTION_TYPE.PROGRAM]: 'info'
};
const activeName = ref(RESOURCE_TYPES.VIDEO);        //当前激活的资源类型面板
const selectedResourceName = ref('')
// 资源list
const videoList = ref([])
const documentList = ref([])
const questionSetList = ref([])

const currentRouteName='OpSubsection'
const isRouteActive = computed(() => {
    return route.name === currentRouteName;
})


onMounted(async () => {
    const subsectionId = route.query.subsectionId;
    const sectionId = route.query.sectionId;
    if (subsectionId != null) {//编辑操作
        const response = await querySubsection(subsectionId);
        subsection.value = response.data.data;
        //查询资源名
        if (subsection.value.subsectionType == RESOURCE_TYPES.QUESTIONS) {
            const response = await querySet(subsection.value.resourceId)
            selectedResourceName.value = response.data.data.setName;
        } else {
            const response = await queryFile({
                fileId: subsection.value.resourceId,
                fileType: subsection.value.subsectionType
            })
            selectedResourceName.value = response.data.data.fileName;
        }
    } else if (subsectionId == null && sectionId != null) {//新增操作
        subsection.value.sectionId = sectionId;
    } else {
        ElMessage.error("错误的路由参数");
        router.go(-1);
    }
    // 加载资源
    //加载视频
    {
        const response = await queryTeacherVideos()
        videoList.value = response.data.data;
    }
    // 加载文档
    {
        const response = await queryTeacherDocuments()
        documentList.value = response.data.data;
    }
    //加载试题集
    getQuestionSet();
})
onBeforeRouteUpdate((to,from) => {
    // 从子路由跳转回来时更新数据
    if (to.name === currentRouteName) {
        getQuestionSet();   
        console.log(from.name)
    }
})
const save = async () => {
    try {
        // 将 validate 方法转换为 Promise 形式
        await new Promise((resolve, reject) => {
            formRef.value.validate(valid => {
                valid ? resolve() : reject(new Error('表单验证失败'))
            })
        })
        // 根据新增/编辑操作确定 API 调用
        let response
        if (subsection.value.subsectionId == null && subsection.value.sectionId != null) {
            response = await addSubsection(subsection.value)
        } else {
            response = await updateSubsection(subsection.value)
        }
        if (response.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试")
            return
        }
        // 路由跳转
        router.go(-1)
    } catch (error) {
        // 统一错误处理
        if (error.message === '表单验证失败') {
            ElMessage.error('请完整填写表单')
        } else {
            ElMessage.error(`请求失败: ${error.message}`)
        }
    }
}
// 点选资源
const selecteResource = (resourceId, resourceName) => {
    subsection.value.resourceId = resourceId;
    selectedResourceName.value = resourceName;
    subsection.value.subsectionType = activeName.value;
}
const handleSearch = () => {
    conditionOfName.value = searchQuery.value;
}

const handleChange = () => {
    currentPage.value = 1;
    conditionOfName.value = '';
    searchQuery.value = '';
}
const playVideo = (videoUrl) => {
    // console.log("父组件", VideoPlayVisable.value);
    VideoPlayVisable.value = true;
    VideoPlayingUrl.value = videoUrl;
}
const handleAddVideo = (video) => {
    videoList.value.unshift(video);
}
const handleAddDocument = (document) => {
    documentList.value.unshift(document);
}
// 文档生成
const documentGenerate = () => {
    router.push({
        name: "documentGenerateOfSection",
        query: {
            'sectionId': subsection.value.sectionId,
        }
    })
}
// 试题集
// 获取试题集list
const getQuestionSet = async () => {
    const response = await querySets()
    questionSetList.value = response.data.data;

}
// 新增试题集(需跳转路由并数据同步)
const addQuestionSet = async () => {
    const response = await addSet()
    handleQuestionsEdit(response.data.data, null);

}
// 编辑试题
const handleQuestionsEdit = (setId) => {
    router.push({
        name: "questionDetail",
        query: {
            'setId': setId,
            'sectionId':subsection.value.sectionId,
        }
    })
}
const rowClassName = ({ row }) => {
    // console.log("id", subsection.value.resourceId == row.setId);
    if (subsection.value.subsectionType === RESOURCE_TYPES.QUESTIONS && subsection.value.resourceId === row.setId) {
        return 'active-card';
    }
    return '';
};

// 自定义表单验证规则
const formRef = ref(null);
const validateResource = () => {

    return new Promise((resolve, reject) => {
        if (!subsection.value.resourceId) {
            reject(new Error('点选下方资源以选择小节内容'));
        } else {
            resolve(); // 验证通过
        }
    });
};
const rules = {
    resourceId: [
        { required: true, validator: validateResource, trigger: 'blur' }
    ]
};
// 分页逻辑
const currentPage = ref(1)
const pageSize = computed(() => {
    return activeName.value == RESOURCE_TYPES.VIDEO ? 8 : 7;
})
const handleCurrentChange = (newPage) => {
    currentPage.value = newPage;
};
// 小节图标映射
const iconMap = {
    [RESOURCE_TYPES.DOCUMENT]: Document,
    [RESOURCE_TYPES.VIDEO]: VideoPlay,
    [RESOURCE_TYPES.QUESTIONS]: EditPen
}

// 获取图标的函数方法
const getIconComponent = (type) => {
    return iconMap[type] || null // 添加默认值防止undefined
}

// 处理搜索操作
const searchQuery = ref("")
const conditionOfName = ref("")

//条件过滤后的数据
const filterList = computed(() => {
    if (activeName.value === RESOURCE_TYPES.VIDEO) {
        // console.log("video值：", videoList.value);
        return videoList.value.filter(video => {
            return (conditionOfName.value == '' || video.videoName.includes(conditionOfName.value))
        })
    } else if (activeName.value === RESOURCE_TYPES.DOCUMENT) {
        // console.log("documnet值：", documentList.value);
        return documentList.value.filter(document => {
            return (conditionOfName.value == '' || document.documentName.includes(conditionOfName.value))
        })
    } else {
        return questionSetList.value.filter(questionSet => {
            return (conditionOfName.value == '' || questionSet.setName.includes(conditionOfName.value))
        })
    }

})
// 计算当前页显示的数据

const currentPageData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value - 1;
    const end = start + pageSize.value;
    // console.log(filterList.value);
    return filterList.value.slice(start < 0 ? 0 : start, end);
});

// 截断文本内容
const truncatedContent = (text, maxLength) => {
    if (!text) return;
    if (text.length > maxLength) {
        return text.slice(0, maxLength) + "...";
    }
    return text;
};

</script>

<style scoped>
/* 鼠标悬浮、点击动画效果 */
.hover-zoom {
    transition: transform 0.3s ease;
}

.hover-zoom:hover {
    transform: scale(1.05);
}

.hover-bg {
    border-radius: 4px;
    transition: all 0.3s ease;
    cursor: pointer;
}

:deep(.active-card) {
    border-radius: 4px;
    background-color: rgb(159.5, 206.5, 255);
}

.hover-bg:hover {
    background-color: #F0F8FF;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.hover-bg:active {
    transform: scale(0.98);
    background: rgb(159.5, 206.5, 255);
}

/* 图标 */
.avatar-uploader-icon {
    font-size: 32px;
    color: #8c939d;
    width: 260px;
    height: 210px;
    text-align: center;
}

.icon2 {
    font-size: 24px;
    color: #8c939d;
    width: 100%;
    height: 100%;
    text-align: center;
}

.video-container {
    margin-left: 70px;
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
}

.document-container {
    margin-left: 70px;
}
</style>
