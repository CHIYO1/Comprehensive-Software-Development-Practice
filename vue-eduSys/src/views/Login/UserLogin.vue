<template>
  <div class="login-container">
    <el-card class="login-card" :header="false" :bordered="false">
      <div class="login-header">
        <h2>教学管理系统</h2>
        <p>为教师与学生提供高效、个性化的教学支持平台</p>
      </div>

      <!-- 登录表单 -->
      <form class="login-form">
        <el-tabs size="large" v-model="activeTab">
          <el-tab-pane label="学生登录" name="student"></el-tab-pane>
          <el-tab-pane label="教师登录" name="teacher"></el-tab-pane>
          <el-tab-pane label="管理员登录" name="admin"></el-tab-pane>
        </el-tabs>

        <el-input
          v-model="formData.account"
          placeholder="请输入账号"
          size="large"
        >
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>

        <el-input
          v-model="formData.password"
          type="password"
          show-password
          placeholder="请输入密码"
          size="large"
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>

        <div class="btn-group">
          <el-button
            style="width: 100%"
            type="primary"
            :loading="loading"
            @click.prevent="handleLogin"
          >
            登录
          </el-button>

          <el-button
            style="width: 100%"
            plain
            @click.prevent="registerVisible = true"
          >
            注册
          </el-button>
        </div>
      </form>
    </el-card>

    <!-- 注册弹窗 -->
    <el-dialog
      v-model="registerVisible"
      title="用户注册"
      width="500px"
      destroy-on-close
    >
      <el-form label-width="80px">
        <el-form-item label="账号">
          <el-input
            v-model="registerForm.account"
            placeholder="请输入登录账号"
          />
        </el-form-item>

        <el-form-item label="姓名">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入姓名"
          />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="registerForm.password"
            type="password"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>

        <el-form-item label="角色">
          <el-select
            v-model="registerForm.role"
            style="width: 100%"
          >
            <el-option
              label="学生"
              value="Student"
            />
            <el-option
              label="教师"
              value="Teacher"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="registerVisible = false">
          取消
        </el-button>

        <el-button
          type="primary"
          :loading="registerLoading"
          @click="handleRegister"
        >
          注册
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/store/auth'
import { userLogin, userRegister } from '@/api/user.js'

const router = useRouter()
const authStore = useAuthStore()

// 当前Tab
const activeTab = ref('student')

// 登录表单
const formData = ref({
  account: '',
  password: ''
})

// 登录loading
const loading = ref(false)

// 注册弹窗
const registerVisible = ref(false)

// 注册loading
const registerLoading = ref(false)

// 注册表单
const registerForm = ref({
  account: '',
  password: '',
  username: '',
  role: 'Student'
})

/**
 * 登录
 */
const handleLogin = async () => {
  const { account, password } = formData.value

  if (!account || !password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true

  try {
    const res = await userLogin({
      account,
      password
    })

    if (res.data.code !== '200') {
      ElMessage.error(res.data.message || '登录失败')
      return
    }

    // 保存token和用户信息
    authStore.loginSuccess(res.data.data)

    ElMessage.success('登录成功')

    const role = res.data.data.role

    // 根据后端返回角色跳转
    if (role === 'Teacher') {
      router.push({ name: 'teacherIndex' })
    } else if (role === 'Student') {
      router.push({ name: 'studentIndex' })
    } else if (role === 'Admin') {
      router.push({ name: 'adminIndex' })
    }

  } catch (error) {
    console.error(error)

    if (error.response) {
      ElMessage.error(
        error.response.data?.message || '登录失败'
      )
    } else {
      ElMessage.error('网络连接失败')
    }
  } finally {
    loading.value = false
  }
}

/**
 * 注册
 */
const handleRegister = async () => {
  const {
    account,
    password,
    username
  } = registerForm.value

  if (!account || !password || !username) {
    ElMessage.warning('请填写完整信息')
    return
  }

  registerLoading.value = true

  try {
    const res = await userRegister(registerForm.value)

    if (res.data.code !== '200') {
      ElMessage.error(res.data.message || '注册失败')
      return
    }

    ElMessage.success('注册成功，请登录')

    registerVisible.value = false

    // 自动填入登录框
    formData.value.account = account
    formData.value.password = password

    // 重置注册表单
    registerForm.value = {
      account: '',
      password: '',
      username: '',
      role: 'Student'
    }

  } catch (error) {
    console.error(error)

    if (error.response) {
      ElMessage.error(
        error.response.data?.message || '注册失败'
      )
    } else {
      ElMessage.error('网络连接失败')
    }
  } finally {
    registerLoading.value = false
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

.btn-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-group .el-button {
  margin-left: 0 !important;
}

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