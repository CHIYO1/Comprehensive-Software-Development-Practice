<template>
    <el-dialog v-model="dialogVisible" title="视频播放" width="900px" center>
       <video v-if="dialogVisible" ref="videoPlayer" :src="props.videoUrl" controls class="video-preview"></video>
    </el-dialog>
</template>

<script setup>
import { ref,defineProps, defineEmits, computed } from 'vue';

const videoPlayer = ref(null);
// 双向绑定当前组件关闭状态
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    videoUrl: {  
        type: String,
        default: ''
    }
});
const emit = defineEmits(['update:visible']);
const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
});


</script>

<style scoped>
.video-preview {
    width: 100%;
    max-height: 500px;
    background: #000;
    border-radius: 8px;
    object-fit: contain;
}

:deep(.el-dialog) {
    border-radius: 12px;
}

:deep(.el-dialog__header) {
    border-bottom: 1px solid #ebeef5;
    padding: 20px 20px 15px;
}

:deep(.el-dialog__body) {
    padding: 20px;
    text-align: center;
}
</style>