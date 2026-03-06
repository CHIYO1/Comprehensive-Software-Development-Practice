<template>
    <el-dialog v-model="dialogVisible" title="文档上传" width="900px" center>
        <div class="upload-container">
            <div class="form-section">
                <div class="section-title">
                    <el-icon><Document /></el-icon>
                    <span>文档信息</span>
                </div>
                <el-form>
                    <el-form-item label="文件简介：">
                        <el-input 
                            v-model="documentDesc" 
                            placeholder="请输入文档简介，描述文档的主要内容和用途" 
                            type="textarea" 
                            :rows="4"
                            maxlength="200"
                            show-word-limit
                        />
                    </el-form-item>
                </el-form>
            </div>
            
            <div class="upload-section">
                <div class="section-title">
                    <el-icon><UploadFilled /></el-icon>
                    <span>文件上传</span>
                </div>
                <el-upload 
                    v-model:file-list="document" 
                    class="document-upload" 
                    action="#"
                    :auto-upload="false" 
                    :limit="1" 
                    drag
                    accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx"
                >
                    <div v-if="!coverImageUrl" class="upload-placeholder">
                        <el-icon class="upload-icon"><UploadFilled /></el-icon>
                        <div class="upload-text">
                            拖动或<em>点击选择文件</em>
                        </div>
                        <div class="upload-hint">支持 PDF、DOC、PPT、XLS 格式</div>
                    </div>
                </el-upload>
            </div>
        </div>
        
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="reset()">重置</el-button>
                <el-button type="primary" @click="uploadFile()">保存并上传</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { UploadFilled, Document } from '@element-plus/icons-vue'
import request from '@/utils/request.js'

// 双向绑定当前组件关闭状态
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    }
});
const emit = defineEmits([
    'update:visible',
    'handleAddDocument',
]);
const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
});

const document = ref([]);    //封面文件对象
const documentDesc = ref(null);
const coverImageUrl = ref(null); // 添加这个变量定义
// 上传视频和封面
const uploadFile = async () => {
    const formData = new FormData();
    formData.append('document', document.value[0].raw);
    formData.append('documentDesc', documentDesc.value);
    const response = await request.post('/file/teacher/uploadDocument', formData)
    ElMessage.success('上传成功');
    emit('handleAddDocument', response.data.data);
    reset();
};

const reset = () => {
    document.value = [];
    documentDesc.value = null;
}

</script>

<style scoped>
/* 容器布局 */
.upload-container {
    display: flex;
    flex-direction: column;
    gap: 30px;
    padding: 20px 10px;
}

/* 表单和上传区域 */
.form-section,
.upload-section {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

/* 区域标题 */
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

/* 上传组件 */
.document-upload {
    width: 100%;
}

/* 上传占位符 */
.upload-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 150px;
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

/* 弹窗样式 */
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

/* 上传拖拽区域 */
:deep(.el-upload-dragger) {
    width: 100%;
    height: 150px;
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

/* 表单样式 */
:deep(.el-form-item) {
    margin-bottom: 20px;
}

:deep(.el-form-item__label) {
    font-weight: 500;
    color: #303133;
}

:deep(.el-textarea__inner) {
    border-radius: 6px;
    resize: none;
}

/* 底部按钮 */
.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}
</style>