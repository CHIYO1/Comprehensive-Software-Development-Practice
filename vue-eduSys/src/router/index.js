import { createRouter, createWebHistory } from 'vue-router'
import { ROLE_TYPE } from '@/constants/roleTypes'

// 角色映射
const roleType = {
  TEACHER: 'teacher',
  STUDENT: 'student',
  ADMIN: 'admin',
}

// 路由配置
const routes = [
  {
    path: '/',
    name: 'index',
    component: () => import('@/views/IndexPage.vue'),
    children: [
      // 教师端
      {
        path: '/teacher',
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
              requiresAuth: true,   //是否需要登录验证
              requiredRole: [ROLE_TYPE.TEACHER] //限定哪些角色可以访问
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
          // 课程编辑
          {
            path: 'courseDetail/:courseId',
            name: 'courseDetail',
            component: () => import('@/views/Teacher/Course/CourseDetail.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [ROLE_TYPE.TEACHER]
            },
            children: [
              // 文档预览
              {
                path: 'previewFile',
                name: 'filePreviewOfCourse',
                component: () => import('@/components/file/filePreview.vue')
              },
              //编辑小节
              {
                path: 'OpSubsection',
                name: 'OpSubsection',
                component: () => import('@/views/Teacher/Course/OpSubsection.vue'),
                meta: {
                  requiresAuth: true,
                  requiredRole: [ROLE_TYPE.TEACHER]
                },
                children: [
                  {
                    path: 'questionDetail',
                    name: 'questionDetail',
                    component: () => import('@/views/Teacher/QuestionBank/QuestionDetail.vue'),
                    meta: {
                      requiresAuth: true,
                      requiredRole: [ROLE_TYPE.TEACHER]
                    },
                  },
                  // 文档生成
                  {
                    path: 'documentGenerate',
                    name: 'documentGenerateOfSection',
                    component: () => import('@/components/file/DocumentGenerate.vue'),
                    meta: {
                      requiresAuth: true,
                      requiredRole: [ROLE_TYPE.TEACHER]
                    },
                  },
                ]
              },
            ]
          },
          {
            path: 'homeView',
            name: 'HomeView',
            component: () => import('@/views/HomeView.vue')
          }
        ]
      },
      //学生端
      //所有课程列表
      {
        path: 'courseList',
        name: 'courseList',
        component: () => import('@/views/Student/CourseList.vue'),
        meta: {
          requiresAuth: true
        },
      },
      // 课程学习
      {
        path: 'courseStudy/:courseId',
        name: 'courseStudy',
        component: () => import('@/views/Student/CourseStudy.vue'),
        meta: {
          requiresAuth: true
        },
        redirect: '/courseStudy/:courseId/courseInfo',
        children: [
          // 课程详情页面
          {
            path: 'courseInfo',
            name: 'courseInfo',
            component: () => import('@/views/Student/CourseStudy/CourseInfo.vue'),
            meta: {
              requiresAuth: true,
              title: '课程详情'
            }
          },
          // 学习笔记页面
          {
            path: 'studyNotes',
            name: 'studyNotes',
            component: () => import('@/views/Student/CourseStudy/StudyNotes.vue'),
            meta: {
              requiresAuth: true,
              title: '学习笔记'
            }
          },
          // 课程讨论页面
          {
            path: 'courseDiscussion',
            name: 'courseDiscussion',
            component: () => import('@/views/Student/CourseStudy/CourseDiscussion.vue'),
            meta: {
              requiresAuth: true,
              title: '课程讨论'
            }
          },
          // 学习助手页面
          {
            path: 'learningAssistant',
            name: 'learningAssistant',
            component: () => import('@/views/Student/CourseStudy/LearningAssistant.vue'),
            meta: {
              requiresAuth: true,
              title: '学习助手'
            }
          },
          // 实时练习页面
          {
            path: 'practice',
            name: 'practice',
            component: () => import('@/views/Student/CourseStudy/Practice.vue'),
            meta: {
              requiresAuth: true,
              title: '实时练习'
            }
          }
        ]
      },

      // 文档预览
      {
        path: 'previewFile',
        name: 'filePreview',
        component: () => import('@/components/file/filePreview.vue')
      },
      //做题页面
      {
        path: 'doQuestions',
        name: 'doQuestions',
        component: () => import('@/views/Student/DoQuestions.vue'),
        meta: {
          requiresAuth: true
        },
      },
      //试题评分页面
      {
        path: 'recordQuestion',
        name: 'recordQuestion',
        component: () => import('@/views/Student/RecordQuestion.vue'),
        meta: {
          requiresAuth: true
        },
      },
    ]
  },
  // 登录
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login/UserLogin.vue'),
    meta: {
      title: '登录'
    }
  },
  
  // 教师端路由
  {
    path: '/teacher',
    name: 'teacherIndex',
    component: () => import('@/views/Teacher/TeacherIndex.vue'),
    meta: {
      requiresAuth: true,
      requiredRole: [roleType.TEACHER],
      title: '教师端'
    },
    redirect: '/teacher/MyCourse',
    children: [
      {
        path: 'resource',
        name: 'courseResource',
        component: () => import('@/views/Teacher/CourseResourse/CourseResource.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [roleType.TEACHER],
          title: '课程资源'
        }
      },
      {
        path: 'questions',
        name: 'questionBank',
        component: () => import('@/views/Teacher/QuestionBank/QuestionBank.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [roleType.TEACHER],
          title: '题库管理'
        },
        children: [
          {
            path: 'questionDetail',
            name: 'questionDetailOfBank',
            component: () => import('@/views/Teacher/QuestionBank/QuestionDetail.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.TEACHER],
              title: '题目详情'
            },
          },
        ]
      },
      {
        path: 'MyCourse',
        name: 'MyCourse',
        component: () => import('@/views/Teacher/Course/MyCourse.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [roleType.TEACHER],
          title: '我的课程'
        },
      },
      {
        path: 'courseDetail/:courseId',
        name: 'courseDetail',
        component: () => import('@/views/Teacher/Course/CourseDetail.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [roleType.TEACHER],
          title: '课程详情'
        },
        redirect: '/teacher/courseDetail/:courseId/courseEdit',
        children: [
          // 编辑课程页面
          {
            path: 'courseEdit',
            name: 'courseEdit',
            component: () => import('@/views/Teacher/Course/CourseEdit.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.TEACHER],
              title: '编辑课程'
            }
          },
          {
            path: 'editSubsection',
            name: 'editSubsection',
            component: () => import('@/views/Teacher/Course/OpSubsection.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.TEACHER],
              title: '编辑章节'
            },
            children: [
              {
                path: 'questionDetail',
                name: 'questionDetail',
                component: () => import('@/views/Teacher/QuestionBank/QuestionDetail.vue'),
                meta: {
                  requiresAuth: true,
                  requiredRole: [roleType.TEACHER],
                  title: '题目详情'
                },
              }
            ]
          },
          {
            path: 'addSubsection',
            name: 'addSubsection',
            component: () => import('@/views/Teacher/Course/OpSubsection.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.TEACHER],
              title: '添加章节'
            },
            children: [
              {
                path: 'questionDetail',
                name: 'questionDetail',
                component: () => import('@/views/Teacher/QuestionBank/QuestionDetail.vue'),
                meta: {
                  requiresAuth: true,
                  requiredRole: [roleType.TEACHER],
                  title: '题目详情'
                },
              }
            ]
          },
          // 学生管理页面
          {
            path: 'studentManagement',
            name: 'studentManagement',
            component: () => import('@/views/Teacher/Course/StudentManagement.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.TEACHER],
              title: '学生管理'
            }
          },
          // 学习分析页面
          {
            path: 'learningAnalytics',
            name: 'learningAnalytics',
            component: () => import('@/views/Teacher/Course/LearningAnalytics.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.TEACHER],
              title: '学习分析'
            }
          },
          // 智能备课页面
          {
            path: 'autoDesign',
            name: 'autoDesign',
            component: () => import('@/views/Teacher/Course/AutoDesign.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.TEACHER],
              title: '智能备课'
            }
          },
          // 题目生成页面
          {
            path: 'questionGenerate',
            name: 'questionGenerate',
            component: () => import('@/views/Teacher/Course/QuestionGenerate.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.TEACHER],
              title: '题目生成'
            }
          },
        ]
      },
    ]
  },

  // 学生端路由
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
        meta: {
          requiresAuth: true,
          requiredRole: [roleType.STUDENT],
          title: '我的课程'
        }
      },
      {
        path: 'join',
        name: 'studentJoinCourse',
        component: () => import('@/views/Student/Join/StudentJoinCourse.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [roleType.STUDENT],
          title: '加入课程'
        }
      }
    ]
  },

  // 管理员端路由
  {
    path: '/admin',
    name: 'adminIndex',
    component: () => import('@/views/Admin/AdminIndex.vue'),
    meta: {
      requiresAuth: true,
      requiredRole: [roleType.ADMIN],
      title: '管理员端'
    },
    redirect: '/admin/user',
    children: [
      {
        path: 'user',
        name: 'userManagement',
        component: () => import('@/views/Admin/UserManagement/UserManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [roleType.ADMIN],
          title: '用户管理'
        },
        redirect: '/admin/user/admin',
        children: [
          {
            path: 'admin',
            name: 'adminUser',
            component: () => import('@/views/Admin/UserManagement/AdminUser.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.ADMIN],
              title: '管理员管理'
            }
          },
          {
            path: 'teacher',
            name: 'teacherUser',
            component: () => import('@/views/Admin/UserManagement/TeacherUser.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.ADMIN],
              title: '教师管理'
            }
          },
          {
            path: 'student',
            name: 'studentUser',
            component: () => import('@/views/Admin/UserManagement/StudentUser.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.ADMIN],
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
          requiresAuth: true,
          requiredRole: [roleType.ADMIN],
          title: '资源管理'
        },
        redirect: '/admin/resource/courseware',
        children: [
          {
            path: 'courseware',
            name: 'resourceCourseware',
            component: () => import('@/views/Admin/ResourceManagement/Courseware.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.ADMIN],
              title: '课件管理'
            }
          },
          {
            path: 'exercise',
            name: 'resourceExercise',
            component: () => import('@/views/Admin/ResourceManagement/Exercise.vue'),
            meta: {
              requiresAuth: true,
              requiredRole: [roleType.ADMIN],
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
          requiresAuth: true,
          requiredRole: [roleType.ADMIN],
          title: '数据可视化'
        }
      },
      {
        path: 'settings',
        name: 'systemSettings',
        component: () => import('@/views/Admin/SystemSettings/SystemSettings.vue'),
        meta: {
          requiresAuth: true,
          requiredRole: [roleType.ADMIN],
          title: '系统设置'
        }
      }
    ]
  },

  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'notFound',
    component: () => import('@/views/NotFound.vue'),
    meta: {
      title: '页面不存在'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
// router.beforeEach((to) => {
//   console.log("进入路由守卫");
//   const authStore = useAuthStore()
  
//   // 设置页面标题
//   if (to.meta.title) {
//     document.title = `${to.meta.title} - 教学管理系统`
//   }
  
//   // 如果已登录用户访问登录页，重定向到对应首页
//   if (to.path === '/login' && authStore.isAuthenticated()) {
//     // 根据角色重定向到不同首页
//     switch (authStore.role) {
//       case roleType.TEACHER:
//         return '/teacher'
//       case roleType.STUDENT:
//         return '/student'
//       case roleType.ADMIN:
//         return '/admin'
//       default:
//         return '/teacher'
//     }
//   }
  
//   // 登录检查
//   if (to.meta.requiresAuth && !authStore.isAuthenticated()) {
//     ElMessage.warning("请先登录");
//     return '/login'
//   }
  
//   // 角色权限检查
//   if (to.meta.requiredRole) {
//     if (!to.meta.requiredRole.some(role => authStore.isRole(role))) {
//       ElMessage.error("角色权限不够!");
//       return '/login'
//     }
//   }
// })

export default router
