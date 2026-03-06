<template>
    <vue-office-pptx v-if="fileExt === 'pptx'" :src="fileUrl" style="height: 100vh" @rendered="renderedHandler"
        @error="errorHandler" />
    <vue-office-pdf v-if="fileExt==='pdf'" :src="fileUrl" style="height: 100vh" @rendered="renderedHandler" @error="errorHandler" />
    <vue-office-docx v-if="fileExt==='docx'" :src="fileUrl" style="height: 100vh;" @rendered="renderedHandler" @error="errorHandler" />
</template>

<script setup>
import VueOfficePptx from '@vue-office/pptx'
import VueOfficePdf from '@vue-office/pdf'
import VueOfficeDocx from '@vue-office/docx'
import '@vue-office/docx/lib/index.css'

import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import request from '@/utils/request.js'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { RESOURCE_TYPES } from '@/constants/resourceTypes'
const route = useRoute()
const router = useRouter()

const fileUrl = ref('')
const fileExt = ref('') //文件扩展名
const EXTALLOW = (['pptx','docx','pdf']) //允许的后缀名

onMounted(async () => {
    const fileId = route.query.fileId;
    if (fileId == null || fileId == '') {
        ElMessage.error("fileId为空");
        router.go(-1);
    }
    try {
        const response = await request.post('/file/queryFile', {
            "fileId": fileId,
            "fileType": RESOURCE_TYPES.DOCUMENT
        })
        if (response.data.code != 200) {
            ElMessage.error("后端处理失败，请稍后再试");
            return;
        }
        fileUrl.value = response.data.data.fileUrl;
        fileExt.value = response.data.data.fileName.split('.').pop();
        console.log(fileExt.value)
        if (!EXTALLOW.some(item =>
            item.toLowerCase() == fileExt.value.toLowerCase()
        )) {
            ElMessage.error("文件类型不属于可预览:" + EXTALLOW);
        }
    } catch (error) {
        ElMessage.error('请求失败')
    }
});
const renderedHandler = () => {
}
const errorHandler = () => {
    ElMessage.error("加载错误")
} 
</script>

<style scoped>
.file-preview-container {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f5f5;
}

.preview-placeholder {
    text-align: center;
    padding: 40px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.file-icon {
    font-size: 64px;
    color: #409eff;
    margin-bottom: 20px;
}

.preview-placeholder h3 {
    margin: 0 0 16px 0;
    color: #303133;
}

.preview-placeholder p {
    margin: 8px 0;
    color: #606266;
}
</style>