<template>
  <el-menu
    class="admin-side-menu"
    :default-active="activeIndex"
    active-text-color="#1890ff"
    background-color="#fff"
    router
  >
    <el-sub-menu index="/admin/user">
      <template #title>
        <el-icon style="margin-right: 8px"><User /></el-icon>
        <span>用户管理</span>
      </template>
      <el-menu-item index="/admin/user/admin">管理员</el-menu-item>
      <el-menu-item index="/admin/user/teacher">教师</el-menu-item>
      <el-menu-item index="/admin/user/student">学生</el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="/admin/resource">
      <template #title>
        <el-icon style="margin-right: 8px"><Files /></el-icon>
        <span>资源管理</span>
      </template>
      <el-menu-item index="/admin/resource/courseware">课件</el-menu-item>
      <el-menu-item index="/admin/resource/exercise">习题</el-menu-item>
    </el-sub-menu>
    <el-menu-item index="/admin/visual">
      <el-icon style="margin-right: 8px"><DataAnalysis /></el-icon>
      <span>数据可视化</span>
    </el-menu-item>
    <el-menu-item index="/admin/settings">
      <el-icon style="margin-right: 8px"><Setting /></el-icon>
      <span>系统设置</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { User, Files, DataAnalysis, Setting } from '@element-plus/icons-vue'

const route = useRoute()

const activeIndex = computed(() => {
  // 精确高亮子菜单
  const path = route.path
  if (path.startsWith('/admin/user/admin')) return '/admin/user/admin'
  if (path.startsWith('/admin/user/teacher')) return '/admin/user/teacher'
  if (path.startsWith('/admin/user/student')) return '/admin/user/student'
  if (path.startsWith('/admin/resource/courseware')) return '/admin/resource/courseware'
  if (path.startsWith('/admin/resource/exercise')) return '/admin/resource/exercise'
  if (path.startsWith('/admin/visual')) return '/admin/visual'
  if (path.startsWith('/admin/settings')) return '/admin/settings'
  return '/admin/user/admin'
})
</script>

<style scoped>
.admin-side-menu {
  height: calc(100vh - 60px);
  padding: 10px;
  border-top: 2px solid #e5e5e5;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  border-right: none;
  min-width: 240px;
}
</style> 