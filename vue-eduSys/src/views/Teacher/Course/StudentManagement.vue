<template>
    <div class="student-management-content">
        <!-- 页面头部 -->
        <div class="content-header">
            <div class="header-content">
                <div class="header-left">
                    <h2 class="page-title">学生管理</h2>
                </div>
                <div class="header-right">
                    <el-button type="primary" @click="exportStudentData">
                        <el-icon><Download /></el-icon>
                        导出数据
                    </el-button>
                    <el-button type="success" @click="inviteStudents">
                        <el-icon><Plus /></el-icon>
                        邀请学生
                    </el-button>
                </div>
            </div>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-section">
            <el-row :gutter="24">
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><User /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ studentStats.totalStudents }}</div>
                                <div class="stat-label">总学生数</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><Check /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ studentStats.activeStudents }}</div>
                                <div class="stat-label">活跃学生</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><TrendCharts /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ studentStats.avgProgress }}%</div>
                                <div class="stat-label">平均进度</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card class="stat-card" shadow="hover">
                        <div class="stat-content">
                            <el-icon class="stat-icon"><Star /></el-icon>
                            <div class="stat-info">
                                <div class="stat-number">{{ studentStats.avgRating }}</div>
                                <div class="stat-label">平均评分</div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <!-- 主要内容区域 -->
        <el-card class="main-card">
            <template #header>
                <div class="card-header">
                    <span>学生列表</span>
                    <div class="header-actions">
                        <el-input
                            v-model="searchKeyword"
                            placeholder="搜索学生姓名或学号"
                            style="width: 300px; margin-right: 16px;"
                            clearable
                        >
                            <template #prefix>
                                <el-icon><Search /></el-icon>
                            </template>
                        </el-input>
                        <el-select v-model="filterStatus" placeholder="状态筛选" style="width: 120px; margin-right: 16px;">
                            <el-option label="全部" value="" />
                            <el-option label="活跃" value="active" />
                            <el-option label="离线" value="offline" />
                        </el-select>
                        <el-button type="primary" @click="refreshData">
                            <el-icon><Refresh /></el-icon>
                            刷新
                        </el-button>
                    </div>
                </div>
            </template>

            <el-table :data="filteredStudentList" style="width: 80%" v-loading="loading">
                <el-table-column prop="name" label="学生姓名" width="120" />
                <el-table-column prop="studentId" label="学号" width="120" />
                <el-table-column prop="email" label="邮箱" width="200" />
                <el-table-column prop="joinDate" label="加入时间" width="180" />
                <el-table-column prop="progress" label="学习进度" width="150">
                    <template #default="{ row }">
                        <el-progress :percentage="row.progress" :color="getProgressColor(row.progress)" />
                    </template>
                </el-table-column>
                <el-table-column prop="lastLogin" label="最后登录" width="180" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 'active' ? 'success' : 'warning'">
                            {{ row.status === 'active' ? '活跃' : '离线' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="320" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" type="primary" @click="viewStudentProgress(row)">
                            <el-icon><View /></el-icon>
                            查看进度
                        </el-button>
                        <el-button size="small" type="info" @click="sendMessage(row)">
                            <el-icon><Message /></el-icon>
                            发送消息
                        </el-button>
                        <el-button size="small" type="danger" @click="removeStudent(row)">
                            <el-icon><Delete /></el-icon>
                            移除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="totalStudents"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </el-card>

        <!-- 邀请学生弹窗 -->
        <el-dialog v-model="showInviteDialog" title="邀请学生" width="500px">
            <el-form :model="inviteForm" label-width="100px">
                <el-form-item label="邀请方式">
                    <el-radio-group v-model="inviteForm.method">
                        <el-radio label="email">邮箱邀请</el-radio>
                        <el-radio label="code">邀请码</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item v-if="inviteForm.method === 'email'" label="邮箱地址">
                    <el-input v-model="inviteForm.emails" type="textarea" :rows="4" 
                        placeholder="请输入邮箱地址，多个邮箱用逗号分隔" />
                </el-form-item>
                <el-form-item v-if="inviteForm.method === 'code'" label="邀请码">
                    <el-input v-model="inviteForm.inviteCode" readonly>
                        <template #append>
                            <el-button @click="generateInviteCode">生成新码</el-button>
                        </template>
                    </el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showInviteDialog = false">取消</el-button>
                    <el-button type="primary" @click="sendInvite">发送邀请</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

import { ElMessage, ElMessageBox } from 'element-plus'
import { 
    User, Check, TrendCharts, Star, Download, Plus, Search, 
    Refresh, View, Message, Delete 
} from '@element-plus/icons-vue'





const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const totalStudents = ref(0)

// 学生统计数据
const studentStats = ref({
    totalStudents: 0,
    activeStudents: 0,
    avgProgress: 0,
    avgRating: 0
})

// 学生列表
const studentList = ref([])

// 邀请相关
const showInviteDialog = ref(false)
const inviteForm = ref({
    method: 'email',
    emails: '',
    inviteCode: ''
})

// 计算属性
const filteredStudentList = computed(() => {
    let filtered = studentList.value

    if (searchKeyword.value) {
        filtered = filtered.filter(student => 
            student.name.includes(searchKeyword.value) || 
            student.studentId.includes(searchKeyword.value)
        )
    }

    if (filterStatus.value) {
        filtered = filtered.filter(student => student.status === filterStatus.value)
    }

    return filtered
})



const getProgressColor = (progress) => {
    if (progress >= 80) return '#67C23A'
    if (progress >= 60) return '#E6A23C'
    return '#F56C6C'
}

const loadStudentData = async () => {
    loading.value = true
    try {
        // 这里应该调用实际的API
        // const response = await request.get(`/api/courses/${courseId.value}/students`)
        
        // 模拟数据
        studentList.value = [
            { id: 1, name: '张三', studentId: '2021001', email: 'zhangsan@example.com', progress: 85, lastLogin: '2024-01-15 10:30', status: 'active', joinDate: '2024-01-01' },
            { id: 2, name: '李四', studentId: '2021002', email: 'lisi@example.com', progress: 72, lastLogin: '2024-01-14 15:20', status: 'active', joinDate: '2024-01-02' },
            { id: 3, name: '王五', studentId: '2021003', email: 'wangwu@example.com', progress: 95, lastLogin: '2024-01-15 09:15', status: 'offline', joinDate: '2024-01-03' },
            { id: 4, name: '赵六', studentId: '2021004', email: 'zhaoliu@example.com', progress: 60, lastLogin: '2024-01-13 16:45', status: 'active', joinDate: '2024-01-04' },
            { id: 5, name: '钱七', studentId: '2021005', email: 'qianqi@example.com', progress: 88, lastLogin: '2024-01-15 11:20', status: 'active', joinDate: '2024-01-05' }
        ]

        // 计算统计数据
        studentStats.value = {
            totalStudents: studentList.value.length,
            activeStudents: studentList.value.filter(s => s.status === 'active').length,
            avgProgress: Math.round(studentList.value.reduce((sum, s) => sum + s.progress, 0) / studentList.value.length),
            avgRating: 4.8
        }

        totalStudents.value = studentList.value.length
    } catch (error) {
        ElMessage.error('加载学生数据失败')
    } finally {
        loading.value = false
    }
}

const refreshData = () => {
    loadStudentData()
}

const handleSizeChange = (val) => {
    pageSize.value = val
    loadStudentData()
}

const handleCurrentChange = (val) => {
    currentPage.value = val
    loadStudentData()
}

const viewStudentProgress = (student) => {
    ElMessage.info(`查看学生 ${student.name} 的学习进度`)
    // 这里可以跳转到学生进度详情页面
}

const sendMessage = (student) => {
    ElMessage.info(`发送消息给 ${student.name}`)
    // 这里可以打开消息发送弹窗
}

const removeStudent = async (student) => {
    try {
        await ElMessageBox.confirm(
            `确定要移除学生 ${student.name} 吗？`,
            '确认移除',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        )
        
        // 这里应该调用实际的API
        // await request.delete(`/api/courses/${courseId.value}/students/${student.id}`)
        
        ElMessage.success('学生移除成功')
        loadStudentData()
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('移除学生失败')
        }
    }
}

const exportStudentData = () => {
    ElMessage.success('学生数据导出成功')
    // 这里可以实现数据导出功能
}

const inviteStudents = () => {
    showInviteDialog.value = true
    if (inviteForm.value.method === 'code') {
        generateInviteCode()
    }
}

const generateInviteCode = () => {
    inviteForm.value.inviteCode = Math.random().toString(36).substring(2, 8).toUpperCase()
}

const sendInvite = async () => {
    try {
        // 这里应该调用实际的API
        // await request.post(`/api/courses/${courseId.value}/invite`, inviteForm.value)
        
        ElMessage.success('邀请发送成功')
        showInviteDialog.value = false
        inviteForm.value = {
            method: 'email',
            emails: '',
            inviteCode: ''
        }
    } catch (error) {
        ElMessage.error('发送邀请失败')
    }
}

onMounted(() => {
    loadStudentData()
})
</script>

<style scoped>
.student-management-content {
    padding: 0;
    background: transparent;
    min-height: auto;
}

/* 滚动条美化 */
.student-management-content::-webkit-scrollbar {
    width: 6px;
}

.student-management-content::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.student-management-content::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
}

.student-management-content::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}

.content-header {
    background: white;
    padding: 20px 24px;
    border-radius: 8px;
    margin-bottom: 24px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}

.back-btn {
    font-size: 14px;
    color: #606266;
}

.page-title {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
}

.header-right {
    display: flex;
    gap: 12px;
}

.stats-section {
    margin-bottom: 24px;
}

.stat-card {
    border-radius: 8px;
    transition: all 0.3s ease;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
    display: flex;
    align-items: center;
    gap: 16px;
}

.stat-icon {
    font-size: 32px;
    color: #409eff;
}

.stat-info {
    flex: 1;
}

.stat-number {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    line-height: 1;
}

.stat-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
}

.main-card {
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-actions {
    display: flex;
    align-items: center;
}

.pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #ebeef5;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .student-management-page {
        padding: 16px;
    }
    
    .header-content {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }
    
    .header-actions {
        flex-direction: column;
        gap: 12px;
        width: 100%;
    }
    
    .stats-section .el-col {
        margin-bottom: 16px;
    }
}
</style> 