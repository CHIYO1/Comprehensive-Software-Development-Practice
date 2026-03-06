<template>
    <el-dialog style="margin-top: 20px;pointer-events: auto" width="600px" v-model="dialogVisible" draggable :modal="false"
        @close="handleClose" :close-on-click-modal="false" :overflow="true" :lock-scroll="false" >
        <!-- 头部 -->
        <template #header>
            <div class="chat-header">
                <span style="margin: 0">在线学习助手</span>
                <span>
                    <el-tooltip effect="dark" content="保留所有对话">
                        <el-icon class="icon" @click="setAllConvsActivate(1)">
                            <View></View>
                        </el-icon>
                    </el-tooltip>
                    <el-tooltip effect="dark" content="隐藏所有对话">
                        <el-icon class="icon" @click="setAllConvsActivate(0)">
                            <Hide></Hide>
                        </el-icon>
                    </el-tooltip>
                    <el-tooltip effect="dark" content="删除所有对话">
                        <el-icon class="icon" @click="deleteConvs()">
                            <Delete></Delete>
                        </el-icon>
                    </el-tooltip>
                </span>

            </div>
        </template>
        <!-- 消息区域 -->
        <el-scrollbar class="message-area" ref="scrollbar">

            <div v-for="conv in convs" :key="conv.convId" class="collapse-container">
                <!-- 折叠触发按钮 -->
                <el-button @click="toggleCollapse(conv)" class="collapse-btn">
                    <el-icon class="mr-1" style="margin-right: 4px;">
                        <ArrowDownBold v-if="conv.activateState == 1" />
                        <ArrowRightBold v-else />
                    </el-icon>
                    <template v-if="conv.activateState == 1"> </template>
                    <el-tooltip v-else :content="conv.convUser" placement="bottom">
                        <span style="font-size: 12px;color: lightslategray;">{{ conv.convUser.slice(0, 15) +
                            (conv.convUser.length > 10 ? '...' : '') }}</span>
                    </el-tooltip>
                </el-button>
                <!-- 对话气泡 -->
                <el-collapse-transition>
                    <div v-show="conv.activateState" class="collapse-content">
                        <div :class="['message-bubble', 'user']">
                            <MessageItem :message="conv.convUser" class="message-content" />
                        </div>
                        <div :class="['message-bubble', 'gpt']">
                            <MessageItem :message="conv.convGpt" class="message-content" />
                        </div>
                    </div>
                </el-collapse-transition>
            </div>
        </el-scrollbar>

        <!-- 输入区域 -->
        <template #footer>
            <div class="input-wrapper">
                <el-input v-model="convInput" placeholder="向助手询问" @keydown.enter="sendMessage" type="textarea" :rows="2"
                    resize="none" class="custom-input">
                </el-input>
                <el-button class="send-btn" :disabled="!convInput.trim()" @click="sendMessage" :icon="Position"
                    circle />
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import { defineProps, defineEmits, ref, nextTick, computed, onMounted } from 'vue'
import { ElScrollbar } from 'element-plus'
import { Position } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import MessageItem from './MessageItem.vue'
import { View, Hide, Delete, ArrowRightBold, ArrowDownBold } from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { useAuthStore } from '@/store/auth'
const authStore = useAuthStore()
const apiBaseUrl = process.env.VUE_APP_BASE_URL || 'http://localhost:8080/api'


// 双向绑定当前组件关闭状态
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    }
});
const emit = defineEmits(['update:visible']);
const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
});
const handleClose = () => {
    dialogVisible.value = false;
};

// 删除对话记录
const deleteConvs = async () => {
    await request.post('/LrnAst/deleteConvs')
    ElMessage.success("已清空");
    convs.value = []
};
//一键更改所有对话的隐藏状态
const setAllConvsActivate = async (actState) => {
    await request.post('/LrnAst/setConvsAct', actState);
    convs.value.forEach(item => {
        item.activateState = actState // 直接修改响应式对象的属性
    })
}
// 对话数据（发生+接收）
// convId:
// convUser:
// convGpt:
// activateState:
const convs = ref([
])

const convInput = ref('')
const scrollbar = ref(null)

// 发送消息
let timer = null    //定时滚动置底
const eventSource = ref(null)
const sendMessage = () => {

    const contentUser = convInput.value.trim()
    if (!contentUser) return

    convs.value.push({
        "convId": -1,
        "convUser": contentUser,
        "convGpt": "",
        "activateState": 1,
    })

    // 清空旧连接
    if (eventSource.value) {
        eventSource.value.close()
    }

    const params = new URLSearchParams()
    params.append('query', convInput.value)
    params.append('userId', authStore.userId)
    if (!timer) {
        timer = setInterval(() => {
            scrollToBottom()
        }, 500) //ms
    }
    // 返回markdown格式的数据流
    eventSource.value = new EventSource(apiBaseUrl + `/LrnAst/chat?${params.toString()}`)
    eventSource.value.onmessage = (e) => {
        if (e.data === '[DONE]') {
            ElMessage.success('对话完成')
            // 插入convId
            console.log("event", e)
            const lastConv = convs.value[convs.value.length - 1]
            lastConv.convId = e.lastEventId
            // console.log(lastConv.contentGpt)
            // md.render(lastConv.contentGpt)
            eventSource.value.close()
            if (timer) {
                clearInterval(timer)
                timer = null
            }
            scrollToBottom()
            return
        }

        const lastConv = convs.value[convs.value.length - 1]
        lastConv.convGpt += e.data

    }

    eventSource.value.onerror = (e) => {
        console.log("Error", e)
        eventSource.value.close()
    }
    convInput.value = ''
}

// 滚动到底部
const scrollToBottom = () => {
    nextTick(() => {
        const scrollContainer = scrollbar.value?.wrapRef
        if (scrollContainer) {
            scrollContainer.scrollTop = scrollContainer.scrollHeight
        }
    })
}

//折叠
const toggleCollapse = async (conv) => {
    await request.post('/LrnAst/invertActivate', conv.convId)
    conv.activateState = conv.activateState === 1 ? 0 : 1
}

// 生命周期
onMounted(async () => {
    // 获取用户的对话记录
    const response = await request.post('/LrnAst/getAllConv')
    convs.value = response.data.results
})
</script>

<style scoped>
:deep(.el-dialog__footer) {
  padding-top: 0px;
}

:deep(.el-dialog__header) {
  padding-bottom: 0px;
}

.chat-header {
    display: flex;
    height: 50px;
    justify-content: space-between;
    align-items: center;
    padding: 0px;
    border-bottom: 1px solid;
}

.icon {
    margin-left: 10px;
    cursor: pointer;
    display: inline-block;
    transition:
        color 0.3s ease,
        transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    transform-origin: center;
    /* 从中心缩放 */
}

.icon:hover {
    color: #409EFF;
    transform: scale(1.2);
    /* 放大20% */
}

/* 消息显示区域 */
.message-area {
    flex: 1;
    height: 450px;
    padding: 3px;
    overflow-y: auto;
}

.message-bubble {
    margin: 16px 0;
    padding: 4px 12px;
    border-radius: 12px;
    position: relative;

}

.message-bubble.gpt {
    max-width: 90%;
    background: #f7f6f6;
    border: 1px solid #ebeef5;
    margin-right: auto;
}

.message-bubble.user {
    max-width: 60%;
    background: #409EFF;
    color: white;
    margin-left: auto;
}

.message-content {
    font-size: 14px;
    line-height: 1.5;
    word-break: break-word;
    /* white-space: pre-wrap;
    white-space-collapse: preserve;  */
}

:deep(.el-dialog__footer) {
    padding-top: 0px;
}

:deep(.el-dialog__header) {
    padding-bottom: 0px;
}

.collapse-btn {
    width: 100%;
    border-radius: 0px;
    border-left: 0px;
    border-right: 0px;
    border-bottom: 0px;

    display: inline-flex;
    justify-content: center;
    align-items: center;
}

/* 输入区域 */


/* 输入框容器 */
.input-wrapper {
    border-top: 1px solid;
    position: relative;
    padding: 0px;
    padding-top: 20px;
}

/* 输入框样式 */
:deep(.custom-input .el-textarea__inner) {
    border-radius: 16px;
    border: 0.25px, solid;
    padding: 12px 48px 24px 24px;
    box-shadow: none;
    transition: all 0.3s ease;
    min-height: 100%;
    background-color: #f8f8f9fc;
}

:deep(.custom-input:hover .el-textarea__inner) {
    border-color: #409EFF;

}

:deep(.custom-input.is-focus .el-textarea__inner) {
    border-color: #409EFF;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

/* 发送按钮样式 */
.send-btn {
    position: absolute;
    right: 8px;
    bottom: 8px;
    padding: 8px;
    background: #409EFF;
    color: white;
    border: none;
}

.send-btn:hover {
    background: #66b1ff;
}

.send-btn:active {
    background: #3a8ee6;
}

.send-btn.is-disabled {
    background: #c0c4cc;
    cursor: not-allowed;
}

</style>