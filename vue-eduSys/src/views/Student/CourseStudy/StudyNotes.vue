<template>
    <div class="study-notes-page">
        <el-card class="notes-card">
            <template #header>
                <div class="card-header">
                    <el-icon class="header-icon"><Memo /></el-icon>
                    <span>学习笔记</span>
                </div>
            </template>
            
            <div class="notes-content">
                <el-tabs v-model="activeNoteTab">
                    <el-tab-pane label="我的笔记" name="my-notes">
                        <div class="notes-list">
                            <div v-for="note in myNotes" :key="note.id" class="note-item">
                                <div class="note-header">
                                    <span class="note-title">{{ note.title }}</span>
                                    <span class="note-time">{{ formatTime(note.createTime) }}</span>
                                </div>
                                <div class="note-content">{{ note.content }}</div>
                                <div class="note-actions">
                                    <el-button size="small" type="primary" @click="editNote(note)">编辑</el-button>
                                    <el-button size="small" type="danger" @click="deleteNote(note.id)">删除</el-button>
                                </div>
                            </div>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="添加笔记" name="add-note">
                        <el-form :model="noteForm" label-width="80px">
                            <el-form-item label="标题">
                                <el-input v-model="noteForm.title" placeholder="请输入笔记标题" />
                            </el-form-item>
                            <el-form-item label="内容">
                                <el-input v-model="noteForm.content" type="textarea" :rows="6" placeholder="请输入笔记内容" />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="saveNote">保存笔记</el-button>
                            </el-form-item>
                        </el-form>
                    </el-tab-pane>
                </el-tabs>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Memo } from '@element-plus/icons-vue'

const activeNoteTab = ref('my-notes')

// 笔记相关
const myNotes = ref([
    {
        id: 1,
        title: 'Vue.js基础概念笔记',
        content: 'Vue.js是一个渐进式JavaScript框架，具有响应式数据绑定和组件化开发的特点...',
        createTime: '2024-01-15T10:30:00'
    },
    {
        id: 2,
        title: '组件通信方式总结',
        content: '父子组件通信：props和emit；兄弟组件通信：事件总线；跨级组件通信：provide/inject...',
        createTime: '2024-01-16T14:20:00'
    }
])

const noteForm = ref({
    title: '',
    content: ''
})

// 笔记相关功能
const saveNote = () => {
    if (!noteForm.value.title || !noteForm.value.content) {
        ElMessage.error('请填写笔记标题和内容')
        return
    }
    
    const newNote = {
        id: Date.now(),
        title: noteForm.value.title,
        content: noteForm.value.content,
        createTime: new Date().toISOString()
    }
    
    myNotes.value.unshift(newNote)
    noteForm.value = { title: '', content: '' }
    activeNoteTab.value = 'my-notes'
    ElMessage.success('笔记保存成功')
}

const editNote = (note) => {
    noteForm.value = { ...note }
    activeNoteTab.value = 'add-note'
}

const deleteNote = (noteId) => {
    myNotes.value = myNotes.value.filter(note => note.id !== noteId)
    ElMessage.success('笔记删除成功')
}

// 格式化时间
const formatTime = (time) => {
    if (!time) return '-'
    return new Date(time).toLocaleString('zh-CN')
}
</script>

<style scoped>
.study-notes-page {
    padding: 20px;
}

.notes-card {
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

.notes-content {
    max-height: 600px;
    overflow-y: auto;
}

.notes-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.note-item {
    padding: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background: #fafafa;
}

.note-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.note-title {
    font-weight: 500;
    color: #303133;
}

.note-time {
    font-size: 12px;
    color: #909399;
}

.note-content {
    color: #606266;
    line-height: 1.6;
    margin-bottom: 12px;
}

.note-actions {
    display: flex;
    gap: 8px;
}
</style> 