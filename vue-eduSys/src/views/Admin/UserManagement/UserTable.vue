<template>
  <div class="user-table-wrapper">
    <div class="user-table-header">
      <el-form :inline="true" class="user-table-form">
        <el-form-item label="账号名：">
          <el-input v-model="searchName" placeholder="请输入" clearable style="width: 240px" />
        </el-form-item>
        <el-form-item label="状态：">
          <el-select v-model="searchStatus" placeholder="请选择" style="width: 200px">
            <el-option label="全部" value="" />
            <el-option label="启用" value="enabled" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary">查询</el-button>
          <el-button>重置</el-button>
          <el-button type="link">展开 <el-icon><el-icon-arrow-down /></el-icon></el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" class="create-btn">+ 新建</el-button>
    </div>
    <el-table :data="filteredUsers" border style="margin-top: 24px; width: 80%">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="username" label="账号名" width="150" align="center" />
      
      <!-- 管理员特有列 -->
      <template v-if="userType === 'admin'">
        <el-table-column prop="realName" label="真实姓名" width="120" align="center" />
        <el-table-column prop="email" label="邮箱" width="200" align="center" />
        <el-table-column prop="phone" label="电话" width="130" align="center" />
        <el-table-column prop="role" label="角色" width="120" align="center" />
        <el-table-column prop="department" label="部门" width="150" align="center" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" align="center" />
      </template>
      
      <!-- 教师特有列 -->
      <template v-if="userType === 'teacher'">
        <el-table-column prop="realName" label="真实姓名" width="120" align="center" />
        <el-table-column prop="email" label="邮箱" width="200" align="center" />
        <el-table-column prop="phone" label="电话" width="130" align="center" />
        <el-table-column prop="title" label="职称" width="100" align="center" />
        <el-table-column prop="department" label="学院" width="180" align="center" />
        <el-table-column prop="specialty" label="专业" width="120" align="center" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" align="center" />
      </template>
      
      <!-- 学生特有列 -->
      <template v-if="userType === 'student'">
        <el-table-column prop="realName" label="真实姓名" width="120" align="center" />
        <el-table-column prop="email" label="邮箱" width="200" align="center" />
        <el-table-column prop="phone" label="电话" width="130" align="center" />
        <el-table-column prop="grade" label="年级" width="100" align="center" />
        <el-table-column prop="major" label="专业" width="150" align="center" />
        <el-table-column prop="class" label="班级" width="100" align="center" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" align="center" />
      </template>
      
      <!-- 通用列 -->
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 'enabled' ? 'success' : 'danger'">
            {{ row.status === 'enabled' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160" align="center" />
      <el-table-column label="操作" align="center" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" plain @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" plain @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="user-table-pagination">
      <el-pagination
        background
        layout="prev, pager, next, sizes, jumper, total"
        :total="filteredUsers.length"
        :page-size="pageSize"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 16px;"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown as ElIconArrowDown } from '@element-plus/icons-vue'

const props = defineProps({
  users: {
    type: Array,
    required: true
  },
  userType: {
    type: String,
    required: true,
    validator: (value) => ['admin', 'teacher', 'student'].includes(value)
  }
})

const searchName = ref('')
const searchStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 过滤用户数据
const filteredUsers = computed(() => {
  let filtered = props.users
  
  if (searchName.value) {
    filtered = filtered.filter(user => 
      user.username.toLowerCase().includes(searchName.value.toLowerCase()) ||
      (user.realName && user.realName.toLowerCase().includes(searchName.value.toLowerCase()))
    )
  }
  
  if (searchStatus.value) {
    filtered = filtered.filter(user => user.status === searchStatus.value)
  }
  
  return filtered
})

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 操作处理
const handleEdit = (user) => {
  ElMessage.info(`编辑用户: ${user.username}`)
}

const handleDelete = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户"${user.realName || user.username}"吗？此操作不可撤销！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.user-table-wrapper {
  background: #fff;
  border-radius: 8px;
  padding: 32px 24px 24px 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.user-table-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  flex-wrap: wrap;
}
.user-table-form {
  flex: 1;
  min-width: 600px;
}
.user-table-pagination {
  display: flex;
  justify-content: flex-end;
}
.create-btn {
  margin-top: 8px;
}

/* 操作按钮样式 */
.el-table .el-button {
  transition: all 0.3s ease;
}

.el-table .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
</style> 