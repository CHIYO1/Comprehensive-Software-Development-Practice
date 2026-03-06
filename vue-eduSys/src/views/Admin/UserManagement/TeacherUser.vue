<template>
  <UserTable :users="teacherUsers" user-type="teacher" />
</template>

<script setup>
import UserTable from './UserTable.vue'

const teacherUsers = [
  { 
    id: 1, 
    username: 'teacher_zhang', 
    realName: '张建国',
    email: 'zhang.jianguo@university.edu.cn',
    phone: '13900139001',
    status: 'enabled',
    title: '教授',
    department: '计算机科学与技术学院',
    specialty: '软件工程',
    lastLoginTime: '2024-01-15 08:30:00',
    createdAt: '2020-09-01 09:00:00' 
  },
  { 
    id: 2, 
    username: 'teacher_li', 
    realName: '李秀英',
    email: 'li.xiuying@university.edu.cn',
    phone: '13900139002',
    status: 'enabled',
    title: '副教授',
    department: '信息工程学院',
    specialty: '人工智能',
    lastLoginTime: '2024-01-14 10:15:00',
    createdAt: '2021-03-15 10:00:00' 
  },
  { 
    id: 3, 
    username: 'teacher_wang', 
    realName: '王德明',
    email: 'wang.deming@university.edu.cn',
    phone: '13900139003',
    status: 'enabled',
    title: '讲师',
    department: '数学学院',
    specialty: '应用数学',
    lastLoginTime: '2024-01-13 14:20:00',
    createdAt: '2022-02-20 11:00:00' 
  },
  { 
    id: 4, 
    username: 'teacher_chen', 
    realName: '陈志华',
    email: 'chen.zhihua@university.edu.cn',
    phone: '13900139004',
    status: 'enabled',
    title: '教授',
    department: '物理学院',
    specialty: '理论物理',
    lastLoginTime: '2024-01-12 16:45:00',
    createdAt: '2019-08-10 14:20:00' 
  },
  { 
    id: 5, 
    username: 'teacher_liu', 
    realName: '刘美玲',
    email: 'liu.meiling@university.edu.cn',
    phone: '13900139005',
    status: 'enabled',
    title: '副教授',
    department: '化学学院',
    specialty: '有机化学',
    lastLoginTime: '2024-01-11 09:10:00',
    createdAt: '2020-12-05 16:30:00' 
  },
  { 
    id: 6, 
    username: 'teacher_zhao', 
    realName: '赵国强',
    email: 'zhao.guoqiang@university.edu.cn',
    phone: '13900139006',
    status: 'enabled',
    title: '讲师',
    department: '机械工程学院',
    specialty: '机械设计',
    lastLoginTime: '2024-01-10 11:25:00',
    createdAt: '2021-07-18 09:45:00' 
  },
  { 
    id: 7, 
    username: 'teacher_sun', 
    realName: '孙丽华',
    email: 'sun.lihua@university.edu.cn',
    phone: '13900139007',
    status: 'enabled',
    title: '教授',
    department: '经济管理学院',
    specialty: '工商管理',
    lastLoginTime: '2024-01-09 13:50:00',
    createdAt: '2018-11-25 11:10:00' 
  },
  { 
    id: 8, 
    username: 'teacher_zhou', 
    realName: '周建军',
    email: 'zhou.jianjun@university.edu.cn',
    phone: '13900139008',
    status: 'enabled',
    title: '副教授',
    department: '外国语学院',
    specialty: '英语语言文学',
    lastLoginTime: '2024-01-08 15:35:00',
    createdAt: '2020-04-12 14:25:00' 
  },
  { 
    id: 9, 
    username: 'teacher_wu', 
    realName: '吴秀芳',
    email: 'wu.xiufang@university.edu.cn',
    phone: '13900139009',
    status: 'disabled',
    title: '讲师',
    department: '艺术学院',
    specialty: '视觉传达设计',
    lastLoginTime: '2024-01-07 08:40:00',
    createdAt: '2022-09-03 10:15:00' 
  },
  { 
    id: 10, 
    username: 'teacher_xu', 
    realName: '徐志强',
    email: 'xu.zhiqiang@university.edu.cn',
    phone: '13900139010',
    status: 'enabled',
    title: '教授',
    department: '医学院',
    specialty: '临床医学',
    lastLoginTime: '2024-01-06 12:20:00',
    createdAt: '2017-06-20 16:45:00' 
  },
  { 
    id: 11, 
    username: 'teacher_huang', 
    realName: '黄美华',
    email: 'huang.meihua@university.edu.cn',
    phone: '13900139011',
    status: 'enabled',
    title: '副教授',
    department: '法学院',
    specialty: '民商法学',
    lastLoginTime: '2024-01-05 14:15:00',
    createdAt: '2019-10-08 13:30:00' 
  },
  { 
    id: 12, 
    username: 'teacher_ma', 
    realName: '马建国',
    email: 'ma.jianguo@university.edu.cn',
    phone: '13900139012',
    status: 'enabled',
    title: '讲师',
    department: '体育学院',
    specialty: '体育教育',
    lastLoginTime: '2024-01-04 10:50:00',
    createdAt: '2021-01-15 09:20:00' 
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