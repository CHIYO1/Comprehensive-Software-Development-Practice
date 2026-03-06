<template>
    <el-dialog v-model="dialogVisible" style="margin-top: 5px; " width="600">
        <el-form ref="formRef">
            <el-form-item label="文件简介：">
                <el-input v-model="documentDesc" placeholder="请输入文件简介" style="height: 100px;width: 400px;"
                    type="textarea" />
            </el-form-item>
            <el-form-item label="文件上传：" style="margin-top: 50px;">
                <el-upload v-model:file-list="document" style="width: 400px;height: 300px;" action="#"
                    :auto-upload="false" :limit="1" drag>
                    <div v-if="!coverImageUrl" class="el-upload__text">
                        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                        <div>
                            拖动或<em>点击选择文件</em>
                        </div>
                    </div>
                </el-upload>
            </el-form-item>
        </el-form>
        <template #footer>
            <div style="padding-top: 0px;">
                <el-button type="primary" @click="AddQuestionSet()">保存并上传</el-button>
                <el-button @click="reset()">重置</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue'
import request from '@/utils/request.js'

// 双向绑定当前组件关闭状态
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    }
});
const formRef = ref(null);
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
const coverImageUrl = ref(null);

// 上传视频和封面
const AddQuestionSet = async() => {
    if (!document.value[0]) {
        ElMessage.error("请选择要上传的文件");
        return;
    }
    
    const formData = new FormData();
    formData.append('document', document.value[0].raw);
    formData.append('documentDesc', documentDesc.value);
    try {
        const response = await request.post('/file/teacher/uploadDocument', formData)
        if (response.data.code != '200') {
            ElMessage.error("后端处理失败，请稍后再试");
            return;
        }
        ElMessage.success('上传成功');
        emit('handleAddDocument', response.data.data);
        reset();
    } catch (error) {
        ElMessage.error('请求失败')
    }
};

const reset = () => {
    document.value = [];
    documentDesc.value = null;
    coverImageUrl.value = null;
}
</script>

<style scoped>

</style> 