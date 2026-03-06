<template>
  <UserTable :users="studentUsers" user-type="student" />
</template>

<script setup>
import UserTable from './UserTable.vue'

const studentUsers = [
  { 
    id: 1, 
    username: 'student_2021001', 
    realName: '张小明',
    email: 'zhang.xiaoming@student.edu.cn',
    phone: '13700137001',
    status: 'enabled',
    grade: '2021级',
    major: '计算机科学与技术',
    class: '计科2101班',
    lastLoginTime: '2024-01-15 20:30:00',
    createdAt: '2021-09-01 08:00:00' 
  },
  { 
    id: 2, 
    username: 'cool_girl_2022', 
    realName: '李雨欣',
    email: 'li.yuxin@student.edu.cn',
    phone: '13700137002',
    status: 'enabled',
    grade: '2022级',
    major: '软件工程',
    class: '软工2202班',
    lastLoginTime: '2024-01-14 19:15:00',
    createdAt: '2022-09-01 09:00:00' 
  },
  { 
    id: 3, 
    username: 'tech_boy_2021', 
    realName: '王浩然',
    email: 'wang.haoran@student.edu.cn',
    phone: '13700137003',
    status: 'enabled',
    grade: '2021级',
    major: '人工智能',
    class: '智能2101班',
    lastLoginTime: '2024-01-13 22:20:00',
    createdAt: '2021-09-01 10:00:00' 
  },
  { 
    id: 4, 
    username: 'study_master_2023', 
    realName: '陈思思',
    email: 'chen.sisi@student.edu.cn',
    phone: '13700137004',
    status: 'enabled',
    grade: '2023级',
    major: '数据科学与大数据技术',
    class: '数据2301班',
    lastLoginTime: '2024-01-12 18:45:00',
    createdAt: '2023-09-01 11:00:00' 
  },
  { 
    id: 5, 
    username: 'gaming_pro_2022', 
    realName: '刘志强',
    email: 'liu.zhiqiang@student.edu.cn',
    phone: '13700137005',
    status: 'enabled',
    grade: '2022级',
    major: '网络工程',
    class: '网络2201班',
    lastLoginTime: '2024-01-11 23:10:00',
    createdAt: '2022-09-01 12:00:00' 
  },
  { 
    id: 6, 
    username: 'art_lover_2023', 
    realName: '赵雅琪',
    email: 'zhao.yaqi@student.edu.cn',
    phone: '13700137006',
    status: 'enabled',
    grade: '2023级',
    major: '数字媒体技术',
    class: '数媒2301班',
    lastLoginTime: '2024-01-10 16:25:00',
    createdAt: '2023-09-01 13:00:00' 
  },
  { 
    id: 7, 
    username: 'math_genius_2021', 
    realName: '孙博文',
    email: 'sun.bowen@student.edu.cn',
    phone: '13700137007',
    status: 'enabled',
    grade: '2021级',
    major: '数学与应用数学',
    class: '数学2101班',
    lastLoginTime: '2024-01-09 14:50:00',
    createdAt: '2021-09-01 14:00:00' 
  },
  { 
    id: 8, 
    username: 'english_queen_2022', 
    realName: '周梦瑶',
    email: 'zhou.mengyao@student.edu.cn',
    phone: '13700137008',
    status: 'enabled',
    grade: '2022级',
    major: '英语',
    class: '英语2201班',
    lastLoginTime: '2024-01-08 20:35:00',
    createdAt: '2022-09-01 15:00:00' 
  },
  { 
    id: 9, 
    username: 'sports_star_2023', 
    realName: '吴天宇',
    email: 'wu.tianyu@student.edu.cn',
    phone: '13700137009',
    status: 'disabled',
    grade: '2023级',
    major: '体育教育',
    class: '体育2301班',
    lastLoginTime: '2024-01-07 07:40:00',
    createdAt: '2023-09-01 16:00:00' 
  },
  { 
    id: 10, 
    username: 'music_angel_2021', 
    realName: '徐雅琴',
    email: 'xu.yaqin@student.edu.cn',
    phone: '13700137010',
    status: 'enabled',
    grade: '2021级',
    major: '音乐学',
    class: '音乐2101班',
    lastLoginTime: '2024-01-06 21:20:00',
    createdAt: '2021-09-01 17:00:00' 
  },
  { 
    id: 11, 
    username: 'design_master_2022', 
    realName: '黄子轩',
    email: 'huang.zixuan@student.edu.cn',
    phone: '13700137011',
    status: 'enabled',
    grade: '2022级',
    major: '视觉传达设计',
    class: '设计2201班',
    lastLoginTime: '2024-01-05 15:15:00',
    createdAt: '2022-09-01 18:00:00' 
  },
  { 
    id: 12, 
    username: 'business_elite_2023', 
    realName: '马思远',
    email: 'ma.siyuan@student.edu.cn',
    phone: '13700137012',
    status: 'enabled',
    grade: '2023级',
    major: '工商管理',
    class: '工商2301班',
    lastLoginTime: '2024-01-04 12:50:00',
    createdAt: '2023-09-01 19:00:00' 
  },
  { 
    id: 13, 
    username: 'law_student_2021', 
    realName: '郑雨涵',
    email: 'zheng.yuhan@student.edu.cn',
    phone: '13700137013',
    status: 'enabled',
    grade: '2021级',
    major: '法学',
    class: '法学2101班',
    lastLoginTime: '2024-01-03 17:35:00',
    createdAt: '2021-09-01 20:00:00' 
  },
  { 
    id: 14, 
    username: 'medical_future_2022', 
    realName: '林志豪',
    email: 'lin.zhihao@student.edu.cn',
    phone: '13700137014',
    status: 'enabled',
    grade: '2022级',
    major: '临床医学',
    class: '临床2201班',
    lastLoginTime: '2024-01-02 10:20:00',
    createdAt: '2022-09-01 21:00:00' 
  },
  { 
    id: 15, 
    username: 'chemistry_lab_2023', 
    realName: '何美琪',
    email: 'he.meiqi@student.edu.cn',
    phone: '13700137015',
    status: 'enabled',
    grade: '2023级',
    major: '应用化学',
    class: '化学2301班',
    lastLoginTime: '2024-01-01 13:40:00',
    createdAt: '2023-09-01 22:00:00' 
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