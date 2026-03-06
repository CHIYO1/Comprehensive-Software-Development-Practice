<template>
  <div class="user-management-container">
    <div class="management-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <t-button theme="primary" @click="showAddUserDialog = true">
          添加用户
        </t-button>
        <t-button @click="goBack">返回首页</t-button>
      </div>
    </div>
    
    <div class="management-content">
      <t-card title="用户列表">
        <t-table :data="users" :columns="columns" :loading="loading">
          <template #status="{ row }">
            <t-tag :theme="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '正常' : '禁用' }}
            </t-tag>
          </template>
          
          <template #role="{ row }">
            <t-tag :theme="getRoleTheme(row.role)">
              {{ getRoleName(row.role) }}
            </t-tag>
          </template>
          
          <template #actions="{ row }">
            <t-button size="small" @click="editUser(row)">编辑</t-button>
            <t-button size="small" theme="danger" @click="deleteUser(row)">
              删除
            </t-button>
          </template>
        </t-table>
      </t-card>
    </div>
    
    <!-- 添加用户对话框 -->
    <t-dialog v-model:visible="showAddUserDialog" title="添加用户" width="500px">
      <t-form :data="newUser" :rules="rules" ref="formRef">
        <t-form-item label="用户名" name="username">
          <t-input v-model="newUser.username" placeholder="请输入用户名" />
        </t-form-item>
        <t-form-item label="姓名" name="name">
          <t-input v-model="newUser.name" placeholder="请输入姓名" />
        </t-form-item>
        <t-form-item label="角色" name="role">
          <t-select v-model="newUser.role" placeholder="请选择角色">
            <t-option value="teacher" label="教师" />
            <t-option value="student" label="学生" />
            <t-option value="admin" label="管理员" />
          </t-select>
        </t-form-item>
        <t-form-item label="邮箱" name="email">
          <t-input v-model="newUser.email" placeholder="请输入邮箱" />
        </t-form-item>
      </t-form>
      <template #footer>
        <t-button @click="showAddUserDialog = false">取消</t-button>
        <t-button theme="primary" @click="addUser">确定</t-button>
      </template>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'



const router = useRouter()
const loading = ref(false)
const showAddUserDialog = ref(false)

// 表格列配置
const columns = [
  { colKey: 'username', title: '用户名', width: 120 },
  { colKey: 'name', title: '姓名', width: 120 },
  { colKey: 'email', title: '邮箱', width: 200 },
  { colKey: 'role', title: '角色', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'actions', title: '操作', width: 150 }
]

// 模拟用户数据
const users = ref([
  {
    id: 1,
    username: 'teacher001',
    name: '张老师',
    email: 'teacher001@example.com',
    role: 'teacher',
    status: 'active',
    createTime: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    username: 'student001',
    name: '李同学',
    email: 'student001@example.com',
    role: 'student',
    status: 'active',
    createTime: '2024-01-16 14:20:00'
  }
])

// 新用户表单
const newUser = reactive({
  username: '',
  name: '',
  role: '',
  email: ''
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  name: [{ required: true, message: '请输入姓名' }],
  role: [{ required: true, message: '请选择角色' }],
  email: [
    { required: true, message: '请输入邮箱' },
    { pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '邮箱格式不正确' }
  ]
}

const goBack = () => {
  router.push({ name: 'adminIndex' })
}

const getRoleTheme = (role) => {
  const themes = {
    admin: 'danger',
    teacher: 'warning',
    student: 'primary'
  }
  return themes[role] || 'default'
}

const getRoleName = (role) => {
  const names = {
    admin: '管理员',
    teacher: '教师',
    student: '学生'
  }
  return names[role] || role
}

const editUser = (user) => {
  ElMessage.info(`编辑用户：${user.name}`)
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 ${user.name} 吗？`, '确认删除', {
      type: 'warning'
    })
    users.value = users.value.filter(u => u.id !== user.id)
    ElMessage.success('删除成功')
  } catch {
    // 用户取消删除
  }
}

const addUser = () => {
  // 这里应该调用API添加用户
  const user = {
    id: Date.now(),
    ...newUser,
    status: 'active',
    createTime: new Date().toLocaleString()
  }
  users.value.push(user)
  showAddUserDialog.value = false
  ElMessage.success('添加用户成功')
  
  // 重置表单
  Object.assign(newUser, {
    username: '',
    name: '',
    role: '',
    email: ''
  })
}
</script>

<style scoped>
.user-management-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  padding: 20px;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.management-header h2 {
  color: #333;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.management-content {
  max-width: 1400px;
  margin: 0 auto;
}

.management-content :deep(.t-card) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}
</style> 