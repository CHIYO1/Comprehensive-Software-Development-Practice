import request from '@/utils/request.js'

// 查询所有课程
export const queryAllCourse = () => {
  return request({
    url: '/course/queryAllCourse',
    method: 'post'
  })
}


// 查询学生已选课程
export const queryMyCourses = (userId) => {
  return request({
    url: '/students/my-courses',
    method: 'get',
    params: {
      user_id: userId
    }
  })
}


// 退课
export const dropCourseApi = (data) => {
  return request({
    url: '/students/drop',
    method: 'delete',
    data
  })
}


// 选课
export const enrollCourse = (data) => {
  return request({
    url: '/students/enroll',
    method: 'post',
    data
  })
}


// 查询课程详情
export const queryCourseDetail = (courseId) => {
  return request({
    url: '/courses/detail',
    method: 'get',
    params: {
      course_id: courseId
    }
  })
}

// 查询教师课程
export const queryCourseByTeacherId = (teacherId) => {
  return request({
    url: '/course/queryCourseByTeacherId',
    method: 'post',
    params: {
      teacherId
    }
  })
}