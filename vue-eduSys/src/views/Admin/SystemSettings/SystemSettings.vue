<template>
  <div class="system-settings">
    <el-row :gutter="24">
      <!-- 系统参数配置 -->
      <el-col :span="12">
        <el-card class="settings-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>系统参数配置</span>
            </div>
          </template>
          <el-form :model="systemForm" label-width="120px" size="large">
            <el-form-item label="系统名称">
              <el-input v-model="systemForm.systemName" placeholder="请输入系统名称" />
            </el-form-item>
            <el-form-item label="系统版本">
              <el-input v-model="systemForm.version" placeholder="当前版本" disabled />
            </el-form-item>
            <el-form-item label="最大文件大小">
              <el-input-number 
                v-model="systemForm.maxFileSize" 
                :min="1" 
                :max="1000" 
                controls-position="right"
              />
              <span style="margin-left: 8px; color: #666;">MB</span>
            </el-form-item>
            <el-form-item label="会话超时时间">
              <el-input-number 
                v-model="systemForm.sessionTimeout" 
                :min="5" 
                :max="480" 
                controls-position="right"
              />
              <span style="margin-left: 8px; color: #666;">分钟</span>
            </el-form-item>
            <el-form-item label="系统维护模式">
              <el-switch v-model="systemForm.maintenanceMode" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 安全设置 -->
      <el-col :span="12">
        <el-card class="settings-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </div>
          </template>
          <el-form :model="securityForm" label-width="120px" size="large">
            <el-form-item label="密码最小长度">
              <el-input-number 
                v-model="securityForm.minPasswordLength" 
                :min="6" 
                :max="20" 
                controls-position="right"
              />
              <span style="margin-left: 8px; color: #666;">位</span>
            </el-form-item>
            <el-form-item label="密码复杂度">
              <el-select v-model="securityForm.passwordComplexity" placeholder="请选择密码复杂度">
                <el-option label="低" value="low" />
                <el-option label="中" value="medium" />
                <el-option label="高" value="high" />
              </el-select>
            </el-form-item>
            <el-form-item label="登录失败锁定">
              <el-input-number 
                v-model="securityForm.loginFailLock" 
                :min="3" 
                :max="10" 
                controls-position="right"
              />
              <span style="margin-left: 8px; color: #666;">次</span>
            </el-form-item>
            <el-form-item label="锁定时间">
              <el-input-number 
                v-model="securityForm.lockTime" 
                :min="5" 
                :max="60" 
                controls-position="right"
              />
              <span style="margin-left: 8px; color: #666;">分钟</span>
            </el-form-item>
            <el-form-item label="启用验证码">
              <el-switch v-model="securityForm.enableCaptcha" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top: 24px;">
      <!-- 通知设置 -->
      <el-col :span="12">
        <el-card class="settings-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Bell /></el-icon>
              <span>通知设置</span>
            </div>
          </template>
          <el-form :model="notificationForm" label-width="120px" size="large">
            <el-form-item label="邮件通知">
              <el-switch v-model="notificationForm.emailNotification" />
            </el-form-item>
            <el-form-item label="SMTP服务器" v-if="notificationForm.emailNotification">
              <el-input v-model="notificationForm.smtpServer" placeholder="请输入SMTP服务器地址" />
            </el-form-item>
            <el-form-item label="SMTP端口" v-if="notificationForm.emailNotification">
              <el-input-number 
                v-model="notificationForm.smtpPort" 
                :min="1" 
                :max="65535" 
                controls-position="right"
              />
            </el-form-item>
            <el-form-item label="系统消息">
              <el-switch v-model="notificationForm.systemMessage" />
            </el-form-item>
            <el-form-item label="课程提醒">
              <el-switch v-model="notificationForm.courseReminder" />
            </el-form-item>
            <el-form-item label="作业提醒">
              <el-switch v-model="notificationForm.homeworkReminder" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 备份设置 -->
      <el-col :span="12">
        <el-card class="settings-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Download /></el-icon>
              <span>备份设置</span>
            </div>
          </template>
          <el-form :model="backupForm" label-width="120px" size="large">
            <el-form-item label="自动备份">
              <el-switch v-model="backupForm.autoBackup" />
            </el-form-item>
            <el-form-item label="备份频率" v-if="backupForm.autoBackup">
              <el-select v-model="backupForm.backupFrequency" placeholder="请选择备份频率">
                <el-option label="每天" value="daily" />
                <el-option label="每周" value="weekly" />
                <el-option label="每月" value="monthly" />
              </el-select>
            </el-form-item>
            <el-form-item label="保留备份数" v-if="backupForm.autoBackup">
              <el-input-number 
                v-model="backupForm.keepBackups" 
                :min="1" 
                :max="30" 
                controls-position="right"
              />
              <span style="margin-left: 8px; color: #666;">个</span>
            </el-form-item>
            <el-form-item label="备份数据库">
              <el-switch v-model="backupForm.backupDatabase" />
            </el-form-item>
            <el-form-item label="备份文件">
              <el-switch v-model="backupForm.backupFiles" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleManualBackup" :loading="backupLoading">
                立即备份
              </el-button>
              <el-button @click="handleRestore" style="margin-left: 12px;">
                恢复备份
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <el-row style="margin-top: 32px;">
      <el-col :span="24" style="text-align: center;">
        <el-button type="primary" size="large" @click="handleSave" :loading="saveLoading">
          保存设置
        </el-button>
        <el-button size="large" @click="handleReset" style="margin-left: 16px;">
          重置设置
        </el-button>
        <el-button size="large" @click="handleTestConnection" style="margin-left: 16px;">
          测试连接
        </el-button>
      </el-col>
    </el-row>

    <!-- 备份历史记录 -->
    <el-row style="margin-top: 32px;">
      <el-col :span="24">
        <el-card class="settings-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>备份历史记录</span>
            </div>
          </template>
          <el-table :data="backupHistory" style="width: 80%" size="large">
            <el-table-column prop="backupTime" label="备份时间" width="180" />
            <el-table-column prop="backupType" label="备份类型" width="120" />
            <el-table-column prop="fileSize" label="文件大小" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '成功' ? 'success' : 'danger'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="handleDownload(row)">
                  下载
                </el-button>
                <el-button size="small" type="danger" @click="handleDelete(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Lock, Bell, Download, Document } from '@element-plus/icons-vue'

// 系统参数配置
const systemForm = reactive({
  systemName: '智慧AI教学平台',
  version: 'v1.0.0',
  maxFileSize: 100,
  sessionTimeout: 30,
  maintenanceMode: false
})

// 安全设置
const securityForm = reactive({
  minPasswordLength: 8,
  passwordComplexity: 'medium',
  loginFailLock: 5,
  lockTime: 30,
  enableCaptcha: true
})

// 通知设置
const notificationForm = reactive({
  emailNotification: false,
  smtpServer: '',
  smtpPort: 587,
  systemMessage: true,
  courseReminder: true,
  homeworkReminder: true
})

// 备份设置
const backupForm = reactive({
  autoBackup: true,
  backupFrequency: 'daily',
  keepBackups: 7,
  backupDatabase: true,
  backupFiles: true
})

// 备份历史记录
const backupHistory = ref([
  {
    backupTime: '2024-01-15 14:30:00',
    backupType: '自动备份',
    fileSize: '256MB',
    status: '成功',
    description: '系统自动备份'
  },
  {
    backupTime: '2024-01-14 14:30:00',
    backupType: '自动备份',
    fileSize: '248MB',
    status: '成功',
    description: '系统自动备份'
  },
  {
    backupTime: '2024-01-13 10:15:00',
    backupType: '手动备份',
    fileSize: '245MB',
    status: '成功',
    description: '管理员手动备份'
  }
])

// 加载状态
const saveLoading = ref(false)
const backupLoading = ref(false)

// 保存设置
const handleSave = async () => {
  saveLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败，请重试')
  } finally {
    saveLoading.value = false
  }
}

// 重置设置
const handleReset = async () => {
  try {
    await ElMessageBox.confirm('确定要重置所有设置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    Object.assign(systemForm, {
      systemName: '智慧AI教学平台',
      version: 'v1.0.0',
      maxFileSize: 100,
      sessionTimeout: 30,
      maintenanceMode: false
    })
    
    Object.assign(securityForm, {
      minPasswordLength: 8,
      passwordComplexity: 'medium',
      loginFailLock: 5,
      lockTime: 30,
      enableCaptcha: true
    })
    
    Object.assign(notificationForm, {
      emailNotification: false,
      smtpServer: '',
      smtpPort: 587,
      systemMessage: true,
      courseReminder: true,
      homeworkReminder: true
    })
    
    Object.assign(backupForm, {
      autoBackup: true,
      backupFrequency: 'daily',
      keepBackups: 7,
      backupDatabase: true,
      backupFiles: true
    })
    
    ElMessage.success('设置已重置')
  } catch (error) {
    // 用户取消操作
  }
}

// 测试连接
const handleTestConnection = async () => {
  try {
    await ElMessageBox.confirm('确定要测试邮件服务器连接吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('连接测试成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('连接测试失败')
    }
  }
}

// 立即备份
const handleManualBackup = async () => {
  backupLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 3000))
    
    backupHistory.value.unshift({
      backupTime: new Date().toLocaleString(),
      backupType: '手动备份',
      fileSize: '260MB',
      status: '成功',
      description: '管理员手动备份'
    })
    
    ElMessage.success('备份完成')
  } catch (error) {
    ElMessage.error('备份失败，请重试')
  } finally {
    backupLoading.value = false
  }
}

// 恢复备份
const handleRestore = async () => {
  try {
    await ElMessageBox.confirm('确定要恢复备份吗？此操作将覆盖当前数据！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('备份恢复成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复失败，请重试')
    }
  }
}

// 下载备份
const handleDownload = (row) => {
  ElMessage.success(`开始下载备份文件：${row.backupTime}`)
}

// 删除备份
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除备份记录：${row.backupTime} 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = backupHistory.value.findIndex(item => item.backupTime === row.backupTime)
    if (index > -1) {
      backupHistory.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch (error) {
    // 用户取消操作
  }
}
</script>

<style scoped>
.system-settings {
  padding: 32px 16px;
  background-color: #fafafa;
  min-height: calc(100vh - 60px);
}

.settings-card {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
  color: #409eff;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input-number) {
  width: 120px;
}

:deep(.el-select) {
  width: 120px;
}

@media (max-width: 1200px) {
  .system-settings {
    padding: 16px 8px;
  }
  
  .settings-card {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .system-settings {
    padding: 8px 4px;
  }
  
  .card-header {
    font-size: 14px;
  }
  
  :deep(.el-form-item__label) {
    font-size: 14px;
  }
}
</style> 