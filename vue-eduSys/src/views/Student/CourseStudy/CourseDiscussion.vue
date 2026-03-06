<template>
    <div class="course-discussion-page">
        <el-card class="discussion-card">
            <template #header>
                <div class="card-header">
                    <el-icon class="header-icon"><ChatDotRound /></el-icon>
                    <span>课程讨论</span>
                </div>
            </template>
            
            <div class="discussion-content">
                <el-row :gutter="24">
                    <el-col :span="16">
                        <div class="discussion-list">
                            <div v-for="discussion in discussions" :key="discussion.id" class="discussion-item">
                                <div class="discussion-header">
                                    <el-avatar :src="discussion.avatar" />
                                    <div class="discussion-info">
                                        <div class="discussion-author">{{ discussion.author }}</div>
                                        <div class="discussion-time">{{ formatTime(discussion.createTime) }}</div>
                                    </div>
                                </div>
                                <div class="discussion-content">{{ discussion.content }}</div>
                                <div class="discussion-actions">
                                    <el-button size="small" type="text" @click="likeDiscussion(discussion.id)">
                                        <el-icon><Star /></el-icon>
                                        {{ discussion.likes }}
                                    </el-button>
                                    <el-button size="small" type="text" @click="replyDiscussion(discussion.id)">
                                        <el-icon><ChatDotRound /></el-icon>
                                        回复
                                    </el-button>
                                </div>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="discussion-form">
                            <h4>发表讨论</h4>
                            <el-form :model="discussionForm">
                                <el-form-item>
                                    <el-input v-model="discussionForm.content" type="textarea" :rows="4" placeholder="请输入讨论内容" />
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="submitDiscussion">发表</el-button>
                                </el-form-item>
                            </el-form>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Star } from '@element-plus/icons-vue'

// 讨论相关
const discussions = ref([
    {
        id: 1,
        author: '张三',
        avatar: '',
        content: '这个Vue.js的响应式原理讲得很清楚，特别是Object.defineProperty的使用方法。',
        createTime: '2024-01-15T10:30:00',
        likes: 5
    },
    {
        id: 2,
        author: '李四',
        avatar: '',
        content: '请问有人能详细解释一下computed和watch的区别吗？',
        createTime: '2024-01-16T14:20:00',
        likes: 3
    }
])

const discussionForm = ref({
    content: ''
})

// 讨论相关功能
const likeDiscussion = (discussionId) => {
    const discussion = discussions.value.find(d => d.id === discussionId)
    if (discussion) {
        discussion.likes++
    }
}

const replyDiscussion = (discussionId) => {
    console.log('回复讨论:', discussionId)
    ElMessage.info('回复功能开发中...')
}

const submitDiscussion = () => {
    if (!discussionForm.value.content) {
        ElMessage.error('请输入讨论内容')
        return
    }
    
    const newDiscussion = {
        id: Date.now(),
        author: '当前用户',
        avatar: '',
        content: discussionForm.value.content,
        createTime: new Date().toISOString(),
        likes: 0
    }
    
    discussions.value.unshift(newDiscussion)
    discussionForm.value.content = ''
    ElMessage.success('讨论发表成功')
}

// 格式化时间
const formatTime = (time) => {
    if (!time) return '-'
    return new Date(time).toLocaleString('zh-CN')
}
</script>

<style scoped>
.course-discussion-page {
    padding: 20px;
}

.discussion-card {
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

.discussion-content {
    max-height: 600px;
    overflow-y: auto;
}

.discussion-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.discussion-item {
    padding: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background: #fafafa;
}

.discussion-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
}

.discussion-info {
    flex: 1;
}

.discussion-author {
    font-weight: 500;
    color: #303133;
}

.discussion-time {
    font-size: 12px;
    color: #909399;
}

.discussion-content {
    color: #606266;
    line-height: 1.6;
    margin-bottom: 12px;
}

.discussion-actions {
    display: flex;
    gap: 16px;
}

.discussion-form {
    padding: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background: #fafafa;
}

.discussion-form h4 {
    margin: 0 0 16px 0;
    color: #303133;
}
</style> 