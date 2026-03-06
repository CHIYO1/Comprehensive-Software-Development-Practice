<template>
    <el-dialog v-model="dialogVisible" title="视频上传" width="900px" center>
        <div class="upload-container">
            <div class="upload-section">
                <div class="section-title">
                    <el-icon><VideoPlay /></el-icon>
                    <span>视频文件</span>
                </div>
                <el-upload 
                    v-model:file-list="videoFile" 
                    class="video-upload" 
                    drag 
                    action="#" 
                    :on-change="onVideoChange"
                    :before-remove="beforeVideoRemove" 
                    :limit="1" 
                    accept="video/*" 
                    :auto-upload="false"
                >
                    <div v-if="videoUrl" class="preview-container">
                        <video ref="videoPlayer" :src="videoUrl" controls class="video-preview"
                            @loadeddata="captureFirstFrame"></video>
                    </div>
                    <div v-if="!videoUrl" class="upload-placeholder">
                        <el-icon class="upload-icon"><UploadFilled /></el-icon>
                        <div class="upload-text">
                            拖动或<em>点击上传视频</em>
                        </div>
                        <div class="upload-hint">支持 MP4、AVI、MOV 格式</div>
                    </div>
                </el-upload>
            </div>
            
            <div class="upload-section">
                <div class="section-title">
                    <el-icon><Picture /></el-icon>
                    <span>视频封面</span>
                </div>
                <el-upload 
                    v-model:file-list="coverFile" 
                    class="cover-upload" 
                    action="#"
                    :auto-upload="false" 
                    :limit="1" 
                    :on-exceed="handleExceed" 
                    :on-change="handleChange"
                    :before-remove="beforeCoverRemove" 
                    drag 
                    accept="image/*"
                >
                    <div v-if="!coverImageUrl" class="upload-placeholder">
                        <el-icon class="upload-icon"><UploadFilled /></el-icon>
                        <div class="upload-text">
                            拖动或<em>点击上传封面</em>
                        </div>
                        <div class="upload-hint">支持 JPG、PNG 格式</div>
                    </div>
                    <div v-if="coverImageUrl" class="cover-preview">
                        <img :src="coverImageUrl" alt="视频封面">
                    </div>
                </el-upload>
            </div>
        </div>
        
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="cleanupResources()">重置</el-button>
                <el-button type="primary" @click="uploadVideoAndCover()">保存并上传</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, onUnmounted, defineProps, defineEmits, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { UploadFilled, VideoPlay, Picture } from '@element-plus/icons-vue'
import request from '@/utils/request.js'


// 双向绑定当前组件关闭状态
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
});
const emit = defineEmits(['update:visible', 'handleAddVideo']);
const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
});

const videoUrl = ref(null);
const coverImageUrl = ref(null);
const videoPlayer = ref(null);
const videoFile = ref([]); // 视频文件对象
const coverFile = ref([]);    //封面文件对象
// 视频上传
const onVideoChange = (file) => {
    // 验证文件类型
    if (!file.raw.type.includes('video/')) {
        ElMessage.error('请上传视频文件');
        return false;
    }

    // 存储原始文件
    videoFile.value = [file];
    // 创建预览URL
    videoUrl.value = URL.createObjectURL(file.raw);
};
const beforeVideoRemove = () => {
    if (videoUrl.value) {
        URL.revokeObjectURL(videoUrl.value);
    }
    videoUrl.value = null;
};

// 处理文件超出限制
const handleExceed = () => {
    ElMessage.warning('只能上传一个文件');
};
// 截取视频前0.9秒作封面
const captureFirstFrame = () => {
    coverType.value = 0;
    const video = videoPlayer.value;
    if (!video || video.readyState < 2) return;
    const lastDotIndex = videoFile.value[0].name.lastIndexOf('.');
    const videoName = videoFile.value[0].name.substring(0, lastDotIndex);
    // 确保定位到起始帧
    video.currentTime = 0.9;

    setTimeout(() => {
        const canvas = document.createElement('canvas');
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        const ctx = canvas.getContext('2d');

        // 绘制视频帧
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

        // 生成封面URL
        coverImageUrl.value = canvas.toDataURL('image/jpeg');

        // 转换为文件对象
        canvas.toBlob(blob => {
            // 创建封面文件(File.raw)
            coverFile.value = [new File([blob], videoName + '_cover.jpg', {
                type: 'image/jpeg'
            })];
        }, 'image/jpeg', 0.95); // 设置图片质量为95%
    }, 500); // 增加延迟确保帧就绪
};

// 上传视频和封面
const uploadVideoAndCover = async () => {
    const formData = new FormData();
    formData.append('video', videoFile.value[0].raw);
    if (coverType.value === 0) {//自动生成的封面

        formData.append('cover', coverFile.value[0]);
    } else if (coverType.value === 1) {//手动上传的封面

        formData.append('cover', coverFile.value[0].raw);
    }
        const response = await request.post('/file/teacher/uploadVideo', formData)
        ElMessage.success('上传成功');
        emit('handleAddVideo', response.data.data);
        cleanupResources();
};
// 封面图片上传
// 处理文件选择变化
const coverType = ref(0)  //判断该封面文件时用户上传的还是自动生成的
const handleChange = (uploadFile) => {
    // 验证文件类型
    if (!uploadFile.raw.type.startsWith('image/')) {
        ElMessage.error('请上传图片格式文件（JPG/PNG）')
        return false
    }
    coverFile.value = [uploadFile]
    coverType.value = 1;
    // 生成预览URL
    coverImageUrl.value = URL.createObjectURL(uploadFile.raw)
}
const beforeCoverRemove = () => {
    coverImageUrl.value = null;
    if (coverImageUrl.value) {
        URL.revokeObjectURL(coverImageUrl.value);
    }
};


// 清理资源
const cleanupResources = () => {
    beforeVideoRemove();
    beforeCoverRemove();
    videoFile.value = [];
    coverFile.value = [];

};

onUnmounted(cleanupResources);
</script>

<style scoped>
.upload-container {
    display: flex;
    gap: 30px;
    padding: 20px 0;
    justify-content: center;
    align-items: flex-start;
}

.upload-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 10px;
}

.section-title .el-icon {
    font-size: 18px;
    color: #409eff;
}

.video-upload {
    width: 100%;
}

.cover-upload {
    width: 100%;
}

.video-preview {
    width: 100%;
    height: 300px;
    background: #000;
    display: block;
    border-radius: 8px;
    object-fit: contain;
}

.cover-preview {
    width: 100%;
    height: 250px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    overflow: hidden;
}

.cover-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 8px;
}

.upload-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 250px;
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

.preview-container {
    width: 100%;
    height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
}

:deep(.el-upload-dragger) {
    width: 100%;
    height: 100%;
    padding: 0;
    border-radius: 8px;
    border: 2px dashed #d9d9d9;
    transition: all 0.3s ease;
    background: transparent;
}

:deep(.el-upload-dragger:hover) {
    border-color: #409eff;
    background-color: #f5f7fa;
}

:deep(.el-dialog) {
    border-radius: 12px;
}

:deep(.el-dialog__header) {
    border-bottom: 1px solid #ebeef5;
    padding: 20px 20px 15px;
}

:deep(.el-dialog__body) {
    padding: 30px;
}

:deep(.el-dialog__footer) {
    border-top: 1px solid #ebeef5;
    padding: 20px;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}
</style>