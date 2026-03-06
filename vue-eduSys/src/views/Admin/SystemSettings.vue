<template>
  <div class="system-settings-container">
    <div class="settings-header">
      <h2>系统设置</h2>
      <t-button @click="goBack">返回首页</t-button>
    </div>
    
    <div class="settings-content">
      <t-card title="基本设置" class="settings-card">
        <t-form :data="basicSettings" :rules="basicRules" ref="basicFormRef">
          <t-form-item label="系统名称" name="systemName">
            <t-input v-model="basicSettings.systemName" placeholder="请输入系统名称" />
          </t-form-item>
          <t-form-item label="系统描述" name="systemDescription">
            <t-textarea v-model="basicSettings.systemDescription" placeholder="请输入系统描述" />
          </t-form-item>
          <t-form-item label="管理员邮箱" name="adminEmail">
            <t-input v-model="basicSettings.adminEmail" placeholder="请输入管理员邮箱" />
          </t-form-item>
          <t-form-item label="系统状态" name="systemStatus">
            <t-radio-group v-model="basicSettings.systemStatus">
              <t-radio value="active">正常运行</t-radio>
              <t-radio value="maintenance">维护模式</t-radio>
            </t-radio-group>
          </t-form-item>
          <t-form-item>
            <t-button theme="primary" @click="saveBasicSettings">保存基本设置</t-button>
          </t-form-item>
        </t-form>
      </t-card>
      
      <t-card title="安全设置" class="settings-card">
        <t-form :data="securitySettings" :rules="securityRules" ref="securityFormRef">
          <t-form-item label="密码最小长度" name="minPasswordLength">
            <t-input-number v-model="securitySettings.minPasswordLength" :min="6" :max="20" />
          </t-form-item>
          <t-form-item label="登录失败锁定" name="loginLockEnabled">
            <t-switch v-model="securitySettings.loginLockEnabled" />
          </t-form-item>
          <t-form-item label="锁定阈值" name="lockThreshold" v-if="securitySettings.loginLockEnabled">
            <t-input-number v-model="securitySettings.lockThreshold" :min="3" :max="10" />
            <span class="form-help">次失败后锁定账号</span>
          </t-form-item>
          <t-form-item label="会话超时时间" name="sessionTimeout">
            <t-input-number v-model="securitySettings.sessionTimeout" :min="30" :max="480" />
            <span class="form-help">分钟</span>
          </t-form-item>
          <t-form-item>
            <t-button theme="primary" @click="saveSecuritySettings">保存安全设置</t-button>
          </t-form-item>
        </t-form>
      </t-card>
      
      <t-card title="系统信息" class="settings-card">
        <div class="system-info">
          <div class="info-item">
            <span class="info-label">系统版本：</span>
            <span class="info-value">{{ systemInfo.version }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">运行时间：</span>
            <span class="info-value">{{ systemInfo.uptime }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">数据库状态：</span>
            <t-tag :theme="systemInfo.dbStatus === 'normal' ? 'success' : 'danger'">
              {{ systemInfo.dbStatus === 'normal' ? '正常' : '异常' }}
            </t-tag>
          </div>
          <div class="info-item">
            <span class="info-label">磁盘使用率：</span>
            <t-progress :percentage="systemInfo.diskUsage" :color="getDiskColor(systemInfo.diskUsage)" />
          </div>
          <div class="info-item">
            <span class="info-label">内存使用率：</span>
            <t-progress :percentage="systemInfo.memoryUsage" :color="getMemoryColor(systemInfo.memoryUsage)" />
          </div>
        </div>
      </t-card>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'



const router = useRouter()

// 基本设置
const basicSettings = reactive({
  systemName: '教学管理系统',
  systemDescription: '为教师与学生提供高效、个性化的教学支持平台',
  adminEmail: 'admin@example.com',
  systemStatus: 'active'
})

// 安全设置
const securitySettings = reactive({
  minPasswordLength: 8,
  loginLockEnabled: true,
  lockThreshold: 5,
  sessionTimeout: 120
})

// 系统信息
const systemInfo = reactive({
  version: 'v1.0.0',
  uptime: '15天 8小时 32分钟',
  dbStatus: 'normal',
  diskUsage: 65,
  memoryUsage: 45
})

// 表单验证规则
const basicRules = {
  systemName: [{ required: true, message: '请输入系统名称' }],
  adminEmail: [
    { required: true, message: '请输入管理员邮箱' },
    { pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '邮箱格式不正确' }
  ]
}

const securityRules = {
  minPasswordLength: [{ required: true, message: '请设置密码最小长度' }],
  lockThreshold: [{ required: true, message: '请设置锁定阈值' }],
  sessionTimeout: [{ required: true, message: '请设置会话超时时间' }]
}

const goBack = () => {
  router.push({ name: 'adminIndex' })
}

const saveBasicSettings = () => {
  ElMessage.success('基本设置保存成功')
  // 这里应该调用API保存设置
}

const saveSecuritySettings = () => {
  ElMessage.success('安全设置保存成功')
  // 这里应该调用API保存设置
}

const getDiskColor = (usage) => {
  if (usage >= 90) return '#ff4d4f'
  if (usage >= 70) return '#faad14'
  return '#52c41a'
}

const getMemoryColor = (usage) => {
  if (usage >= 80) return '#ff4d4f'
  if (usage >= 60) return '#faad14'
  return '#52c41a'
}
</script>

<style scoped>
.system-settings-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  padding: 20px;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.settings-header h2 {
  color: #333;
  margin: 0;
}

.settings-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.settings-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.form-help {
  margin-left: 10px;
  color: #666;
  font-size: 0.9rem;
}

.system-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-label {
  min-width: 120px;
  color: #333;
  font-weight: 500;
}

.info-value {
  color: #666;
}

.info-item .t-progress {
  flex: 1;
  max-width: 200px;
}
</style> 