<template>
    <div class="learning-assistant-page">
        <el-card class="assistant-card">
            <template #header>
                <div class="card-header">
                    <el-icon class="header-icon"><Service /></el-icon>
                    <span>AI学习助手</span>
                </div>
            </template>
            
            <div class="assistant-content">
                <div class="chat-container">
                    <div class="messages-container">
                        <div v-for="message in assistantMessages" :key="message.id" 
                             :class="['message', message.type === 'user' ? 'user-message' : 'assistant-message']">
                            <div class="message-content">{{ message.content }}</div>
                            <div class="message-time">{{ message.time }}</div>
                        </div>
                    </div>
                    <div class="input-container">
                        <el-input v-model="learningAssistantForm.question" 
                                 placeholder="输入你的问题..." 
                                 @keyup.enter="askAssistant"
                                 :rows="3"
                                 type="textarea" />
                        <el-button type="primary" @click="askAssistant" :disabled="!learningAssistantForm.question">
                            发送
                        </el-button>
                    </div>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import axios from 'axios'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Service } from '@element-plus/icons-vue'

// 学习助手相关
const learningAssistantForm = ref({
    question: '',
    context: ''
})

const assistantMessages = ref([
    {
        id: 1,
        type: 'assistant',
        content: '你好！我是你的学习助手，有什么问题可以随时问我。',
        time: new Date().toLocaleString()
    }
])

const loading = ref(false)

// 学习助手功能
const askAssistant = async () => {
    if (!learningAssistantForm.value.question) {
        ElMessage.error('请输入问题')
        return
    }

    const question = learningAssistantForm.value.question

    // ⭐ 关键：立刻清空输入框（避免“残留不消失”问题）
    learningAssistantForm.value.question = ''

    // 1. 添加用户消息
    assistantMessages.value.push({
        id: Date.now(),
        type: 'user',
        content: question,
        time: new Date().toLocaleString()
    })

    loading.value = true

    try {
        // 2. 调用硅基流动 API（修复模型名）
        const res = await axios.post(
            'https://api.siliconflow.cn/v1/chat/completions',
            {
                model: 'Qwen/Qwen3.5-397B-A17B', 
                messages: [
                    {
                        role: 'system',
                        content: '你是一个专业的学习助手，回答要清晰、结构化、文本化、适合学生学习。用一段话回答，不要有特殊符号或者空格。'
                    },
                    {
                        role: 'user',
                        content: question
                    }
                ],
                temperature: 0.7,
                max_tokens: 8192
            },
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer sk-cmotuavxnxrusopzoqstlheoofuopjyjgfvsokgalfycsnud'
                }
            }
        )

        console.log('API返回：', res.data)

        const answer = res.data?.choices?.[0]?.message?.content

        if (!answer) {
            throw new Error('未获取到返回内容')
        }

        // 3. 添加 AI 回复
        assistantMessages.value.push({
            id: Date.now() + 1,
            type: 'assistant',
            content: answer,
            time: new Date().toLocaleString()
        })

    } catch (error) {
        console.error('AI调用失败：', error)
        ElMessage.error('AI调用失败（检查模型 / Key / 网络 / CORS）')

        // fallback 提示
        assistantMessages.value.push({
            id: Date.now() + 1,
            type: 'assistant',
            content: '抱歉，AI服务暂时不可用，请稍后再试。',
            time: new Date().toLocaleString()
        })

    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.learning-assistant-page {
    padding: 20px;
}

.assistant-card {
    border-radius: 12px;
}

.card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    color: #303133;
}

.header-icon {
    color: #409eff;
    font-size: 18px;
}

.assistant-content {
    height: 600px;
    display: flex;
    flex-direction: column;
}

.chat-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 10px;
    overflow-y: auto;
}

.messages-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.message {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    padding: 10px 15px;
    border-radius: 10px;
    max-width: 80%;
}

.user-message {
    align-self: flex-end;
    background-color: #409eff;
    color: white;
    border-bottom-right-radius: 4px;
}

.assistant-message {
    align-self: flex-start;
    background-color: #e1f3d8;
    color: #303133;
    border-bottom-left-radius: 4px;
}

.message-content {
    word-break: break-word;
}

.message-time {
    font-size: 10px;
    color: #909399;
    margin-top: 5px;
}

.input-container {
    display: flex;
    gap: 10px;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
}

.input-container .el-textarea {
    flex: 1;
    margin-right: 10px;
}

.input-container .el-button {
    flex-shrink: 0;
}
</style> 