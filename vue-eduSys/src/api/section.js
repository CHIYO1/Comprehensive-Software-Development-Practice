import request from '@/utils/request.js'

/**
 * 新增章节
 * @param {Object} sectionData - 章节数据
 * @param {number} sectionData.sectionId - 章节ID
 * @param {string} sectionData.sectionName - 章节名称
 * @param {string} sectionData.sectionDesc - 章节描述
 * @param {number} sectionData.courseId - 课程ID
 * @returns {Promise} 新增结果
 */
export const addSection = (sectionData) => {
  return request.post('/section/addSection', sectionData)
}

/**
 * 更新章节（不包括小节）
 * @param {Object} sectionDTO - 章节DTO
 * @param {Object} sectionDTO.section - 章节信息
 * @param {Array} sectionDTO.knowledgeList - 知识点列表
 * @param {Array} sectionDTO.subsectionList - 小节列表
 * @param {boolean} sectionDTO.editable - 是否可编辑
 * @returns {Promise} 更新结果
 */
export const updateSection = (sectionDTO) => {
  return request.post('/section/updateSection', sectionDTO)
}

/**
 * 删除章节
 * @param {string} sectionId - 章节ID
 * @returns {Promise} 删除结果
 */
export const deleteSection = (sectionId) => {
  return request.post('/section/deleteSection', sectionId)
}

/**
 * 查询章节关联知识点
 * @param {string} sectionId - 章节ID
 * @returns {Promise} 知识点列表
 */
export const querySectionKnowledges = (sectionId) => {
  return request.post('/section/querySectionKnowledges', sectionId)
}

/**
 * 新增小节
 * @param {Object} subsectionData - 小节数据
 * @param {number} subsectionData.subsectionId - 小节ID
 * @param {string} subsectionData.subsectionName - 小节名称
 * @param {string} subsectionData.subsectionDesc - 小节描述
 * @param {string} subsectionData.subsectionType - 小节类型 (questions/video/document)
 * @param {number} subsectionData.resourceId - 资源ID
 * @param {number} subsectionData.sectionId - 章节ID
 * @returns {Promise} 新增结果
 */
export const addSubsection = (subsectionData) => {
  return request.post('/section/addSubsection', subsectionData)
}

/**
 * 更新小节
 * @param {Object} subsectionData - 小节数据
 * @param {number} subsectionData.subsectionId - 小节ID
 * @param {string} subsectionData.subsectionName - 小节名称
 * @param {string} subsectionData.subsectionDesc - 小节描述
 * @param {string} subsectionData.subsectionType - 小节类型 (questions/video/document)
 * @param {number} subsectionData.resourceId - 资源ID
 * @param {number} subsectionData.sectionId - 章节ID
 * @returns {Promise} 更新结果
 */
export const updateSubsection = (subsectionData) => {
  return request.post('/section/updateSubsection', subsectionData)
}

/**
 * 查询小节
 * @param {string} subsectionId - 小节ID
 * @returns {Promise} 小节信息
 */
export const querySubsection = (subsectionId) => {
  return request.post('/section/querySubSection', subsectionId)
}

/**
 * 删除小节
 * @param {string} subsectionId - 小节ID
 * @returns {Promise} 删除结果
 */
export const deleteSubsection = (subsectionId) => {
  return request.post('/section/deleteSubsection', subsectionId)
} 