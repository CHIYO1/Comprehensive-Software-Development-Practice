<template>
  <div class="admin-layout">
    <MainSideBar />
    <div class="admin-main">
      <AdminSideBar />
      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import AdminSideBar from '@/components/AdminSideBar.vue'
import MainSideBar from '@/components/MainSideBar.vue'
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  background: #f5f6fa;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  color: #0052d9;
}
.user-info {
  color: #666;
}
.admin-main {
  display: flex;
  height: calc(100% - 60px);
}
.admin-content {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
}
.admin-header-right {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-right: 32px;
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