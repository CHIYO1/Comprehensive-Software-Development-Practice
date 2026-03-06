import request from '@/utils/request.js'

/**
 * 更新课程(仅更新单个课程实体，不包括其下的章节)
 * @param {Object} courseData - 课程数据
 * @param {number} courseData.courseId - 课程ID
 * @param {string} courseData.courseName - 课程名称
 * @param {string} courseData.courseDesc - 课程描述
 * @param {string} courseData.coverPath - 封面路径
 * @param {number} courseData.userId - 用户ID
 * @param {File} coverFile - 课程封面文件
 * @returns {Promise} 更新结果
 */
export const updateCourse = (courseData, coverFile = null) => {
  const formData = new FormData()
  
  // 添加查询参数
  const params = new URLSearchParams()
  if (courseData.courseId) params.append('courseId', courseData.courseId)
  if (courseData.courseName) params.append('courseName', courseData.courseName)
  if (courseData.courseDesc) params.append('courseDesc', courseData.courseDesc)
  if (courseData.coverPath) params.append('coverPath', courseData.coverPath)
  if (courseData.userId) params.append('userId', courseData.userId)
  
  // 添加封面文件
  if (coverFile) {
    formData.append('coverFile', coverFile)
  }
  
  return request.post(`/course/updateCourse?${params.toString()}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 查询单个课程详情
 * @param {string} courseId - 课程ID
 * @returns {Promise} 课程详情
 */
export const queryCourseDetail = (courseId) => {
  return request.post('/course/queryCourseDetail', courseId)
}

/**
 * 通过章节id获取该章节及其所属课程的上下文信息
 * @param {string} sectionId - 章节ID
 * @returns {Promise} 课程上下文信息
 */
export const queryCourseDetailStr = (sectionId) => {
  return request.post('/course/queryCourseDetailStr', sectionId)
}

/**
 * 查询当前登录教师名下的所有课程信息
 * @returns {Promise} 教师课程列表
 */
export const queryCourseByTeacherId = () => {
  return request.post('/course/queryCourseByTeacherId')
}

/**
 * 查询所有课程基础信息（不包括其下的章节列表）
 * @returns {Promise} 所有课程列表
 */
export const queryAllCourse = () => {
  return request.post('/course/queryAllCourse')
}

/**
 * 登录学生加入课程
 * @param {string} courseId - 课程ID
 * @returns {Promise} 加入结果
 */
export const joinCourse = (courseId) => {
  return request.post('/course/joinCourse', courseId)
}

/**
 * 删除课程
 * @param {string} courseId - 课程ID
 * @returns {Promise} 删除结果
 */
export const deleteCourse = (courseId) => {
  return request.post('/course/deleteCourse', courseId)
}

/**
 * 新增教师课程(归属当前教师Id)。返回课程新增后的自增Id
 * @returns {Promise} 新增的课程ID
 */
export const addCourse = () => {
  return request.post('/course/addCourse')
} 