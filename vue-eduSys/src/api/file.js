import request from '@/utils/request.js'

/**
 * 资源（文档、图片）查询。需提供文件类型和Id
 * @param {Object} fileData - 文件查询数据
 * @param {number} fileData.fileId - 文件ID
 * @param {string} fileData.fileType - 文件类型
 * @param {string} fileData.fileName - 文件名
 * @param {string} fileData.fileUrl - 文件URL
 * @returns {Promise} 文件信息
 */
export const queryFile = (fileData) => {
  return request.post('/file/queryFile', fileData)
}

/**
 * 教师端视频资源上传
 * @param {File} video - 视频文件
 * @param {File} cover - 封面图片
 * @returns {Promise} 上传结果
 */
export const uploadVideo = (video, cover) => {
  const formData = new FormData()
  formData.append('video', video)
  formData.append('cover', cover)
  
  return request.post('/file/teacher/uploadVideo', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 教师端文档资源上传
 * @param {File} document - 文档文件
 * @param {string} documentDesc - 文档描述
 * @returns {Promise} 上传结果
 */
export const uploadDocument = (document, documentDesc) => {
  const formData = new FormData()
  formData.append('document', document)
  
  return request.post(`/file/teacher/uploadDocument?documentDesc=${documentDesc}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 查询某位教师上传的所有视频资源
 * @returns {Promise} 视频列表
 */
export const queryTeacherVideos = () => {
  return request.post('/file/teacher/queryVideos')
}

/**
 * 查询某位教师上传的所有文档资源
 * @returns {Promise} 文档列表
 */
export const queryTeacherDocuments = () => {
  return request.post('/file/teacher/queryDocuments')
}

/**
 * 视频删除
 * @param {string} videoId - 视频ID
 * @returns {Promise} 删除结果
 */
export const deleteVideo = (videoId) => {
  return request.post('/file/deleteVideo', videoId)
}

/**
 * 文档删除
 * @param {string} documentId - 文档ID
 * @returns {Promise} 删除结果
 */
export const deleteDocument = (documentId) => {
  return request.post('/file/deleteDocument', documentId)
} 