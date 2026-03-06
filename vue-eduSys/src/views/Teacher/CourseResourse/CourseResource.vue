<template>
  <div class="resource-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">课程资源管理</h2>
        <div class="stats-cards">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><VideoPlay /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ resourceStats.totalVideos }}</div>
                <div class="stat-label">视频资源</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Document /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ resourceStats.totalDocuments }}</div>
                <div class="stat-label">文档资源</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Folder /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ resourceStats.totalSize }}</div>
                <div class="stat-label">总存储空间</div>
              </div>
            </div>
          </el-card>
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon"><Download /></el-icon>
              <div class="stat-info">
                <div class="stat-number">{{ resourceStats.totalDownloads }}</div>
                <div class="stat-label">总下载次数</div>
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
          <el-tabs v-model="activeName" @tab-change="handleChange" style="width: 400px;" stretch>
            <el-tab-pane label="视频资源" name="video">
              <template #label>
                <el-icon><VideoPlay /></el-icon>
                <span>视频资源</span>
              </template>
            </el-tab-pane>
            <el-tab-pane label="文档资源" name="document">
              <template #label>
                <el-icon><Document /></el-icon>
                <span>文档资源</span>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>
        <div class="search">
          <el-input 
            v-model="searchQuery" 
            placeholder="搜索资源名称" 
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

    <!-- 视频资源页面 -->
    <div v-show="activeName === RESOURCE_TYPES.VIDEO" class="video-section">
      <div class="section-header">
        <h3>视频资源</h3>
        <el-button type="primary" @click="VideoUploadVisable = true">
          <el-icon><Plus /></el-icon>
          上传视频
        </el-button>
      </div>
      
      <div class="video-grid">
        <!-- 视频上传卡片 -->
        <el-card v-if="currentPage === 1" class="upload-card" shadow="hover" @click="VideoUploadVisable = true">
          <div class="upload-content">
            <el-icon class="upload-icon"><Plus /></el-icon>
            <div class="upload-text">上传新视频</div>
            <div class="upload-desc">支持 MP4、AVI、MOV 格式</div>
          </div>
        </el-card>
        
        <!-- 视频列表 -->
        <el-card 
          v-for="video in currentPageData" 
          :key="video.videoId" 
          class="video-card" 
          shadow="hover"
        >
          <div class="video-header">
            <el-image 
              class="video-cover" 
              :src="video.coverPath" 
              fit="cover"
              @click="playVideo(video.videoPath)"
            />
            <div class="video-overlay">
              <el-button type="primary" circle @click="playVideo(video.videoPath)">
                <el-icon><VideoPlay /></el-icon>
              </el-button>
            </div>
            <div class="video-duration">12:34</div>
          </div>
          
          <div class="video-content">
            <h4 class="video-title" :title="video.videoName">
              {{ truncatedContent(video.videoName, 20) }}
            </h4>
            <div class="video-info">
              <div class="info-item">
                <el-icon><Calendar /></el-icon>
                <span>{{ video.createdDate }}</span>
              </div>
              <div class="info-item">
                <el-icon><View /></el-icon>
                <span>{{ video.viewCount || 0 }} 次播放</span>
              </div>
            </div>
            
            <div class="video-actions">
              <el-button type="primary" size="small" @click="downloadFileWithFetch(video.videoPath, video.videoName)">
                <el-icon><Download /></el-icon>
                下载
              </el-button>
              <el-button size="small" @click="editVideo(video)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="deleteVideo(video.videoId)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 文档资源页面 -->
    <div v-show="activeName === RESOURCE_TYPES.DOCUMENT" class="document-section">
      <div class="section-header">
        <h3>文档资源</h3>
        <el-button type="primary" @click="DocumentUploadVisable = true">
          <el-icon><Plus /></el-icon>
          上传文档
        </el-button>
      </div>
      
      <div class="document-list">
        <!-- 文档上传卡片 -->
        <el-card v-if="currentPage === 1" class="upload-card document-upload" shadow="hover" @click="DocumentUploadVisable = true">
          <div class="upload-content">
            <el-icon class="upload-icon"><Plus /></el-icon>
            <div class="upload-text">上传新文档</div>
            <div class="upload-desc">支持 PDF、DOC、PPT、XLS 格式</div>
          </div>
        </el-card>
        
        <!-- 文档列表 -->
        <el-card 
          v-for="document in currentPageData" 
          :key="document.documentId" 
          class="document-card" 
          shadow="hover"
        >
          <div class="document-content">
            <div class="document-icon">
              <el-icon class="file-icon"><Document /></el-icon>
            </div>
            
            <div class="document-info">
              <h4 class="document-title" :title="document.documentName">
                {{ document.documentName }}
              </h4>
              <p class="document-desc" :title="document.documentDesc">
                {{ truncatedContent(document.documentDesc, 80) }}
              </p>
              <div class="document-meta">
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ document.createdDate }}
                </span>
                <span class="meta-item">
                  <el-icon><Files /></el-icon>
                  {{ document.documentSize }}
                </span>
                <span class="meta-item">
                  <el-icon><Download /></el-icon>
                  {{ document.downloadCount || 0 }} 次下载
                </span>
              </div>
            </div>
            
            <div class="document-actions">
              <el-button type="primary" size="small" @click="downloadFileWithFetch(document.path, document.documentName)">
                <el-icon><Download /></el-icon>
                下载
              </el-button>
              <el-button size="small" @click="editDocument(document)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="deleteDocument(document.documentId)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
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

    <!-- 弹窗组件 -->
    <VideoUploadDialog v-model:visible="VideoUploadVisable" @handleAddVideo="handleAddVideo" />
    <DocumentUploadDialog v-model:visible="DocumentUploadVisable" @handleAddDocument="handleAddDocument" />
    <VideoPlayDialog v-model:visible="VideoPlayVisable" :videoUrl="VideoPlayingUrl" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
// import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
import { 
  Plus, VideoPlay, Document, Folder, Download, Search, 
  Calendar, View, Edit, Delete, Files 
} from '@element-plus/icons-vue'
import { RESOURCE_TYPES } from '@/constants/resourceTypes';
import VideoUploadDialog from '@/components/file/VideoUpload.vue';
import VideoPlayDialog from '@/components/file/VideoPlay.vue';
import DocumentUploadDialog from '@/components/file/DocumentUpload.vue';
import request from '@/utils/request.js'

// const route = useRoute();

// 响应式数据
const VideoUploadVisable = ref(false);
const DocumentUploadVisable = ref(false);
const VideoPlayVisable = ref(false);
const VideoPlayingUrl = ref(null);

const activeName = ref(RESOURCE_TYPES.VIDEO)
// const currentRouteName = "courseResource"
// const isRouteActive = computed(() => {
//     return route.name === currentRouteName;
// })
const videoList = ref([])
const documentList = ref([])

// 资源统计数据
const resourceStats = ref({
  totalVideos: 0,
  totalDocuments: 0,
  totalSize: '2.5GB',
  totalDownloads: 1250
})

onMounted(() => {
    getVideos();
    getDocuments();
    updateStats();
});

// 更新统计数据
const updateStats = () => {
  resourceStats.value.totalVideos = videoList.value.length;
  resourceStats.value.totalDocuments = documentList.value.length;
}

const handleReset = () => {
    searchQuery.value = '';
    conditionOfName.value = '';
    currentPage.value = 1;
}

// 编辑视频
const editVideo = () => {
    ElMessage.info('视频编辑功能开发中...');
}

// 编辑文档
const editDocument = () => {
    ElMessage.info('文档编辑功能开发中...');
}

const handleAddVideo = (video) => {
    videoList.value.unshift(video);
    updateStats();
}

const handleAddDocument = (document) => {
    documentList.value.unshift(document);
    updateStats();
}

const handleSearch = () => {
    conditionOfName.value = searchQuery.value;
    currentPage.value = 1;
}

const handleChange = () => {
    currentPage.value = 1;
    conditionOfName.value = '';
    searchQuery.value = '';
}

const playVideo = (videoUrl) => {
    VideoPlayVisable.value = true;
    VideoPlayingUrl.value = videoUrl;
}

// 文件导出
const downloadFileWithFetch = async (path, fileName) => {
    try {
        ElNotification({
            title: '成功',
            message: '已请求下载资源，请稍后',
            type: 'success',
        })
        const response = await fetch(path);
        if (!response.ok) throw new Error(`下载失败: ${response.status}`);
        const blob = await response.blob();

        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = fileName
        document.body.appendChild(link);

        link.click();

        // 清理资源
        URL.revokeObjectURL(link.href);
        document.body.removeChild(link);
        
        ElMessage.success('下载成功');
    } catch (error) {
        ElMessage.error(error.message || '文件下载失败');
    }
};

// 文件删除
const deleteVideo = async (videoId) => {
    try {
        await ElMessageBox.confirm('确定要删除这个视频吗？此操作不可撤销！', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });
        
        const response = await request.post('/file/deleteVideo', videoId)
        if (response.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试");
            return;
        }
        ElMessage.success("删除成功");
        getVideos();
        updateStats();
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('请求失败', error)
        }
    }
}

const deleteDocument = async (documentId) => {
    try {
        await ElMessageBox.confirm('确定要删除这个文档吗？此操作不可撤销！', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });
        
        const response = await request.post('/file/deleteDocument', documentId)
        if (response.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试");
            return;
        }
        ElMessage.success("删除成功");
        getDocuments();
        updateStats();
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('请求失败', error)
        }
    }
}

// 获取视频list
const getVideos = async () => {
    try {
        const response = await request.post('/file/teacher/queryVideos')
        if (response.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试");
            return;
        }
        videoList.value = response.data.data;
        updateStats();
    } catch (error) {
        ElMessage.error('请求失败', error)
    }
}

//获取文档List
const getDocuments = async () => {
    try {
        const response = await request.post('/file/teacher/queryDocuments')
        if (response.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试");
            return;
        }
        documentList.value = response.data.data;
        updateStats();
    } catch (error) {
        ElMessage.error('请求失败', error)
    }
}

// 分页
const currentPage = ref(1)
const pageSize = computed(() => {
    return activeName.value == RESOURCE_TYPES.VIDEO ? 8 : 7;
})

const handleCurrentChange = (newPage) => {
    currentPage.value = newPage;
};

// 处理搜索操作
const searchQuery = ref("")
const conditionOfName = ref("")

//条件过滤后的数据
const filterList = computed(() => {
    if (activeName.value === RESOURCE_TYPES.VIDEO) {
        return videoList.value.filter(video => {
            return (conditionOfName.value == '' || video.videoName.includes(conditionOfName.value))
        })
    } else if (activeName.value === RESOURCE_TYPES.DOCUMENT) {
        return documentList.value.filter(document => {
            return (conditionOfName.value == '' || document.documentName.includes(conditionOfName.value))
        })
    } else {
        return [];
    }
})

// 计算当前页显示的数据
const currentPageData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value - 1;
    const end = start + pageSize.value;
    return filterList.value.slice(start < 0 ? 0 : start, end);
});

// 截断文本内容
const truncatedContent = (text, maxLength) => {
    if (!text) return '';
    if (text.length > maxLength) {
        return text.slice(0, maxLength) + "...";
    }
    return text;
};
</script>

<style scoped>
.resource-page {
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

/* Tab样式优化 */
:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  padding: 0 20px;
}

:deep(.el-tabs__item .el-icon) {
  font-size: 18px;
  margin-right: 6px;
}

:deep(.el-tabs__item span) {
  font-size: 16px;
}

/* 视频区域 */
.video-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.upload-card {
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  padding: 0;
}

/* 确保所有上传卡片的el-card内部没有额外的padding */
.upload-card :deep(.el-card__body) {
  padding: 0;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-card:hover {
  border-color: #409eff;
  transform: translateY(-2px);
}

.upload-content {
  text-align: center;
}

.upload-icon {
  font-size: 48px;
  color: #409eff;
}

/* 视频区域 */
.video-header {
  position: relative;
}

.video-cover {
  width: 100%;
  height: 160px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.video-card:hover .video-cover {
  transform: scale(1.05);
}

.video-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.video-card:hover .video-overlay {
  opacity: 1;
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.video-content {
  padding: 16px;
}

.video-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-info {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.video-actions {
  display: flex;
  gap: 8px;
}

/* 文档区域 */
.document-section {
  margin-bottom: 32px;
}

.document-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 确保文档上传卡片与视频上传卡片完全一致的尺寸 */
.document-list .upload-card {
  width: 280px;
  height: 200px;
  align-self: flex-start;
  margin: 0;
  padding: 0;
}

/* 确保el-card内部没有额外的padding */
.document-list .upload-card :deep(.el-card__body) {
  padding: 0;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.document-card {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.document-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.document-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.document-icon {
  flex-shrink: 0;
}

.file-icon {
  font-size: 48px;
  color: #409eff;
}

.document-info {
  flex: 1;
  min-width: 0;
}

.document-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.document-desc {
  font-size: 14px;
  color: #606266;
  margin: 0 0 12px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.document-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.document-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resource-page {
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
  
  .video-grid {
    grid-template-columns: 1fr;
  }
  
  .document-content {
    flex-direction: column;
    text-align: center;
  }
  
  .document-actions {
    justify-content: center;
  }
}
</style>