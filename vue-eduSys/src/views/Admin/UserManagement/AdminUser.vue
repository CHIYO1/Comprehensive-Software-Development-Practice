<template>
  <UserTable :users="adminUsers" user-type="admin" />
</template>

<script setup>
import UserTable from './UserTable.vue'

const adminUsers = [
  { 
    id: 1, 
    username: 'admin_zhang', 
    realName: '张伟',
    email: 'admin_zhang@edusys.com',
    phone: '13800138001',
    status: 'enabled',
    role: '超级管理员',
    department: '系统管理部',
    lastLoginTime: '2024-01-15 14:30:00',
    createdAt: '2023-01-01 10:00:00' 
  },
  { 
    id: 2, 
    username: 'superadmin_li', 
    realName: '李建国',
    email: 'superadmin_li@edusys.com',
    phone: '13800138002',
    status: 'enabled',
    role: '系统管理员',
    department: '技术部',
    lastLoginTime: '2024-01-14 16:45:00',
    createdAt: '2023-01-02 11:00:00' 
  },
  { 
    id: 3, 
    username: 'root_wang', 
    realName: '王明',
    email: 'root_wang@edusys.com',
    phone: '13800138003',
    status: 'enabled',
    role: '运维管理员',
    department: '运维部',
    lastLoginTime: '2024-01-13 09:20:00',
    createdAt: '2023-01-03 12:00:00' 
  },
  { 
    id: 4, 
    username: 'admin_chen', 
    realName: '陈志强',
    email: 'admin_chen@edusys.com',
    phone: '13800138004',
    status: 'enabled',
    role: '安全管理员',
    department: '安全部',
    lastLoginTime: '2024-01-12 11:15:00',
    createdAt: '2023-02-15 14:20:00' 
  },
  { 
    id: 5, 
    username: 'sysadmin_liu', 
    realName: '刘建华',
    email: 'sysadmin_liu@edusys.com',
    phone: '13800138005',
    status: 'enabled',
    role: '数据库管理员',
    department: '数据部',
    lastLoginTime: '2024-01-11 13:40:00',
    createdAt: '2023-03-10 16:30:00' 
  },
  { 
    id: 6, 
    username: 'admin_zhao', 
    realName: '赵敏',
    email: 'admin_zhao@edusys.com',
    phone: '13800138006',
    status: 'disabled',
    role: '内容管理员',
    department: '内容部',
    lastLoginTime: '2024-01-10 10:25:00',
    createdAt: '2023-04-05 09:45:00' 
  },
  { 
    id: 7, 
    username: 'webadmin_sun', 
    realName: '孙立新',
    email: 'webadmin_sun@edusys.com',
    phone: '13800138007',
    status: 'enabled',
    role: '网站管理员',
    department: '网络部',
    lastLoginTime: '2024-01-09 15:50:00',
    createdAt: '2023-05-20 11:10:00' 
  },
  { 
    id: 8, 
    username: 'admin_zhou', 
    realName: '周慧',
    email: 'admin_zhou@edusys.com',
    phone: '13800138008',
    status: 'enabled',
    role: '用户管理员',
    department: '用户部',
    lastLoginTime: '2024-01-08 08:35:00',
    createdAt: '2023-06-12 14:25:00' 
  }
]
</script>

<style scoped>
.admin-page-card {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
</style> 