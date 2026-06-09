<template>
  <div class="login-container">
    <el-card class="login-card" :header="false" :bordered="false">
      <div class="login-header">
        <h2>教学管理系统</h2>
        <p>为教师与学生提供高效、个性化的教学支持平台</p>
      </div>

      <!-- 表单区域 -->
      <form class="login-form">
        <el-tabs size="large" v-model="activeTab">
          <el-tab-pane label="学生登录" name="student"></el-tab-pane>
          <el-tab-pane label="教师登录" name="teacher"></el-tab-pane>
          <el-tab-pane label="管理员登录" name="admin"></el-tab-pane>
        </el-tabs>
        <el-input v-model="formData.userId" placeholder="请输入账号" size="large">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
        <!-- 密码输入框 -->
        <el-input v-model="formData.password" type="password" placeholder="请输入密码" size="large">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
        <el-button style="width:100%" type="primary" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus';
import { userLogin } from '@/api/user.js'
import { useAuthStore } from '@/store/auth'
import { useRouter } from 'vue-router';
import { User, Lock } from '@element-plus/icons-vue'
const router = useRouter()
// 当前激活的 Tab（身份）
const activeTab = ref('student')

// 表单数据
const formData = ref({
  userId: '',
  password: ''
})

// 登录状态 loading
const loading = ref(false)


// 登录逻辑
const handleLogin = async () => {
  const { userId, password } = formData.value
  if (!userId || !password) {
    ElMessage.warning('请输入账号或密码');
    return;
  }
  
  // 开始 loading
  loading.value = true
  const authStore = useAuthStore()
  
  try {
    // 添加身份信息到请求中
    const loginData = {
      ...formData.value,
      role: activeTab.value
    }
    
    const res = await userLogin(loginData)
    
    if (res.data.code !== "200") {
      ElMessage.error(res.data.message || "登录失败，请稍后再试");
      return;
    }
    
    // 登录成功
    authStore.loginSuccess(res.data.data)
    ElMessage.success("登录成功");
    
    // 根据身份跳转到不同页面
    if (activeTab.value === 'teacher') {
      router.push({ name: "teacherIndex" })
    } else if (activeTab.value === 'student') {
      router.push({ name: "studentIndex" })
    } else if (activeTab.value === 'admin') {
      router.push({ name: "adminIndex" })
    }
    
  } catch (error) {
    console.error('登录错误:', error)
    if (error.response) {
      ElMessage.error(error.response.data?.message || '登录失败，请检查网络连接');
    } else {
      ElMessage.error('网络连接失败，请稍后重试');
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  background: url('@/assets/background.jpg') no-repeat center center;
  background-size: cover;
  padding: 20px;
  box-sizing: border-box;
}

.login-card {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  max-width: 45rem;
  padding: 3rem;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.login-header {
  margin-bottom: 2rem;
  text-align: center;
}

.login-header h2 {
  font-size: 2.5rem;
  font-weight: 600;
  color: #1a73e8;
  margin-bottom: 0.5rem;
}

.login-header p {
  font-size: 1rem;
  color: #666;
  line-height: 1.5;
  margin-top: 1.5rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 23rem;
  gap: 1.5rem;
}

.login-form .t-tabs {
  margin-bottom: 0.5rem;
  width: 100%;
  margin-top: -1rem;
}

.login-form .t-tabs :deep(.t-tabs__nav) {
  justify-content: center;
  width: 100%;
}

.login-form .t-tabs :deep(.t-tabs__nav-item) {
  flex: 1;
  text-align: center;
}

.login-form .t-input,
.login-form .t-button {
  width: 100%;
  height: 3rem;
  font-size: 1rem;
}

.login-form .t-button {
  margin-top: 0.5rem;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    padding: 10px;
  }
  
  .login-card {
    padding: 2rem;
  }
  
  .login-header h2 {
    font-size: 2rem;
  }
  
  .login-form {
    max-width: 100%;
  }
}
</style>