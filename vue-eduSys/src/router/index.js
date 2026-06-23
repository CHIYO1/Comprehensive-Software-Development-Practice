import { createRouter, createWebHistory } from 'vue-router'
import { ROLE_TYPE } from '@/constants/roleTypes'

// 角色映射
const roleType = {
  TEACHER: 'Teacher',
  STUDENT: 'Student',
  ADMIN: 'Admin',
}

const routes = [
  {
    path: '/',
    redirect: '/login'
  },

  // 登录页
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login/UserLogin.vue'),
    meta: {
      title: '登录'
    }
  },

  // 教师端
  {
    path: '/teacher',
    name: 'teacherIndex',   
    component: () => import('@/views/Teacher/TeacherIndex.vue'), 
    redirect: '/teacher/MyCourse',
    children: [
      // 课程资源管理
      {
        path: 'resource',
        name: 'courseResource',
        component: () => import('@/views/Teacher/CourseResourse/CourseResource.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [ROLE_TYPE.TEACHER]
        },
        children: [
          // 文档生成
          {
            path: 'documentGenerate',
            name: 'documentGenerateOfResource',
            component: () => import('@/components/file/DocumentGenerate.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [ROLE_TYPE.TEACHER]
            },
          },
          // 文档预览
          {
            path: 'previewFile',
            name: 'filePreviewOfResource',
            component: () => import('@/components/file/filePreview.vue')
          },
        ]
      },
      // 试题集及试题编辑
      {
        path: 'questions',
        name: 'questionBank',
        component: () => import('@/views/Teacher/QuestionBank/QuestionBank.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [ROLE_TYPE.TEACHER]
        },
        children: [
          {
            path: 'questionDetail',
            name: 'questionDetailOfBank',
            component: () => import('@/views/Teacher/QuestionBank/QuestionDetail.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [ROLE_TYPE.TEACHER]
            },
          },
        ]
      },
      // 我的课程（教师）
      {
        path: 'MyCourse',
        name: 'MyCourse',
        component: () => import('@/views/Teacher/Course/MyCourse.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [ROLE_TYPE.TEACHER]
        },
      },
      // 创建课程（基础信息填写）
      {
        path: 'createCourse',
        name: 'createCourse',
        component: () => import('@/views/Teacher/Course/CreateCourse.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [ROLE_TYPE.TEACHER]
        },
      },
      // 课程编辑
      {
        path: 'courseDetail/:courseId',
        name: 'courseDetail',
        component: () => import('@/views/Teacher/Course/CourseDetail.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [ROLE_TYPE.TEACHER]
        },
        redirect: (to) => `/teacher/courseDetail/${to.params.courseId}/courseEdit`,
        children: [
          // 课程基本信息编辑
          {
            path: 'courseEdit',
            name: 'courseEdit',
            component: () => import('@/views/Teacher/Course/CourseEdit.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [ROLE_TYPE.TEACHER]
            },
          },
          // 学生管理
          {
            path: 'studentManagement',
            name: 'studentManagement',
            component: () => import('@/views/Teacher/Course/StudentManagement.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [ROLE_TYPE.TEACHER]
            },
          },
          // 智能备课
          {
            path: 'autoDesign',
            name: 'autoDesign',
            component: () => import('@/views/Teacher/Course/AutoDesign.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [ROLE_TYPE.TEACHER]
            },
          },
          // 生成题目
          {
            path: 'questionGenerate',
            name: 'questionGenerate',
            component: () => import('@/views/Teacher/Course/QuestionGenerate.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [ROLE_TYPE.TEACHER]
            },
          }
        ]
      },
      {
        path: 'homeView',
        name: 'HomeView',
        component: () => import('@/views/HomeView.vue')
      }
    ]
  },





  // =========================
  // 学生端（关键修复点）
  // =========================
  {
    path: '/student',
    name: 'studentIndex',
    component: () => import('@/views/Student/StudentIndex.vue'),
    meta: {
      requiresAuth: true,
      requiredRole: [roleType.STUDENT],
      title: '学生端'
    },
    redirect: '/student/courses',
    children: [
      {
        path: 'courses',
        name: 'studentCourses',
        component: () => import('@/views/Student/Courses/StudentCourseList.vue'),
      },
      {
        path: 'join',
        name: 'studentJoinCourse',
        component: () => import('@/views/Student/Join/StudentJoinCourse.vue'),
      }
    ]
  },

  // =========================
  // 课程学习体系（不动）
  // =========================
  {
    path: '/courseStudy/:courseId',
    name: 'courseStudy',
    component: () => import('@/views/Student/CourseStudy.vue'),
    meta: {
      title: '课程学习'
    },
    redirect: (to) => `/courseStudy/${to.params.courseId}/courseInfo`,
    children: [
      {
        path: 'courseInfo',
        name: 'courseInfo',
        component: () => import('@/views/Student/CourseStudy/CourseInfo.vue'),
      },
      {
        path: 'studyNotes',
        name: 'studyNotes',
        component: () => import('@/views/Student/CourseStudy/StudyNotes.vue'),
      },
      {
        path: 'courseDiscussion',
        name: 'courseDiscussion',
        component: () => import('@/views/Student/CourseStudy/CourseDiscussion.vue'),
      },
      {
        path: 'learningAssistant',
        name: 'learningAssistant',
        component: () => import('@/views/Student/CourseStudy/LearningAssistant.vue'),
      },
      {
        path: 'practice',
        name: 'practice',
        component: () => import('@/views/Student/CourseStudy/Practice.vue'),
      }
    ]
  },

  // =========================
  // 工具页（不动）
  // =========================
  {
    path: '/doQuestions',
    name: 'doQuestions',
    component: () => import('@/views/Student/DoQuestions.vue')
  },
  {
    path: '/recordQuestion',
    name: 'recordQuestion',
    component: () => import('@/views/Student/RecordQuestion.vue')
  },
  {
    path: '/previewFile',
    name: 'filePreview',
    component: () => import('@/components/file/filePreview.vue')
  },








  // =========================
  // 管理员端（不动）
  // =========================
  // 管理员端路由
  {
    path: '/admin',
    name: 'adminIndex',
    component: () => import('@/views/Admin/AdminIndex.vue'),
    meta: {
      title: '管理员端'
    },
    redirect: '/admin/user',
    children: [
      {
        path: 'user',
        name: 'userManagement',
        component: () => import('@/views/Admin/UserManagement/UserManagement.vue'),
        meta: {
          title: '用户管理'
        },
        redirect: '/admin/user/admin',
        children: [
          {
            path: 'admin',
            name: 'adminUser',
            component: () => import('@/views/Admin/UserManagement/AdminUser.vue'),
            meta: {
              title: '管理员管理'
            }
          },
          {
            path: 'teacher',
            name: 'teacherUser',
            component: () => import('@/views/Admin/UserManagement/TeacherUser.vue'),
            meta: {
              title: '教师管理'
            }
          },
          {
            path: 'student',
            name: 'studentUser',
            component: () => import('@/views/Admin/UserManagement/StudentUser.vue'),
            meta: {
              title: '学生管理'
            }
          }
        ]
      },
      {
        path: 'resource',
        name: 'resourceManagement',
        component: () => import('@/views/Admin/ResourceManagement/ResourceManagement.vue'),
        meta: {
          title: '资源管理'
        },
        redirect: '/admin/resource/courseware',
        children: [
          {
            path: 'courseware',
            name: 'resourceCourseware',
            component: () => import('@/views/Admin/ResourceManagement/Courseware.vue'),
            meta: {
              title: '课件管理'
            }
          },
          {
            path: 'exercise',
            name: 'resourceExercise',
            component: () => import('@/views/Admin/ResourceManagement/Exercise.vue'),
            meta: {
              title: '习题管理'
            }
          }
        ]
      },
      {
        path: 'visual',
        name: 'dataVisual',
        component: () => import('@/views/Admin/DataVisual/DataVisual.vue'),
        meta: {
          title: '数据可视化'
        }
      },
      {
        path: 'settings',
        name: 'systemSettings',
        component: () => import('@/views/Admin/SystemSettings/SystemSettings.vue'),
        meta: {
          title: '系统设置'
        }
      }
    ]
  },

  // =========================
  // 404
  // =========================
  {
    path: '/:pathMatch(.*)*',
    name: 'notFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})



export default router
