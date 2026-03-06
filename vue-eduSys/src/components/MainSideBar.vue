<template>
  <el-row class="layout-header">
    <el-col :span="4" class="left-section">
      <el-icon style="margin-right: 8px;" :size="24">
        <Menu></Menu>
      </el-icon>
      <span>智慧AI教学平台</span>
    </el-col>

    <el-col :span="17">
      <el-menu v-if="menuTabs.length" :default-active="activeIndex" class="el-menu-demo" mode="horizontal" text-color="#666;"
        active-text-color="#0052d9" @select="handleSelect">
        <el-menu-item v-for="tab in menuTabs" :key="tab.index" :index="tab.index">
          {{ tab.label }}
        </el-menu-item>
      </el-menu>
    </el-col>
    <el-col :span="2">
      <div class="header-right">
        <el-icon class="header-icon"><Bell /></el-icon>
        <img class="header-avatar" src="https://img2.baidu.com/it/u=3049483413,931302730&fm=253&fmt=auto&app=120&f=JPEG?w=506&h=500" alt="avatar" />
        <span class="header-name">派大星</span>
      </div>
    </el-col>
  </el-row>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Menu, Bell } from '@element-plus/icons-vue';

const router = useRouter()
const route = useRoute()

const isTeacher = computed(() => route.path.startsWith('/teacher'))
const isStudent = computed(() => route.path.startsWith('/student'))
const isAdmin = computed(() => route.path.startsWith('/admin'))

const teacherTabs = [
  { index: 'MyCourse', label: '我的课程' },
  { index: 'courseResource', label: '教师资源' },
  { index: 'questionBank', label: '试题集' }
]
const studentTabs = [
  { index: 'studentCourses', label: '我的课程' },
  { index: 'studentJoinCourse', label: '加入课程' }
]

const menuTabs = computed(() => {
  if (isAdmin.value) return []
  return isTeacher.value ? teacherTabs : studentTabs
})

// 根据当前路由自动高亮tab
const getActiveIndex = () => {
  if (isTeacher.value) {
    if (route.name && teacherTabs.some(tab => tab.index === route.name)) {
      return route.name
    }
    return 'MyCourse'
  } else if (isStudent.value) {
    if (route.name && studentTabs.some(tab => tab.index === route.name)) {
      return route.name
    }
    return 'studentCourses'
  }
  return ''
}

const activeIndex = ref(getActiveIndex())

watch(
  () => route.fullPath,
  () => {
    activeIndex.value = getActiveIndex()
  }
)

const handleSelect = (index) => {
  // 跳转到对应路由
  router.push({ name: index })
  activeIndex.value = index
}
</script>

<style scoped>
.layout-header {
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 16px;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.left-section {
  width: 20%;
  margin-right: 1rem;
  font-size: 18px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  color: #0052d9;
}
.header-right {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 18px;
  margin-right: 16px;
}
.header-icon {
  font-size: 22px;
  color: #888;
  cursor: pointer;
}
.header-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #eee;
}
.header-name {
  color: #333;
  font-weight: 500;
  font-size: 15px;
}
</style>