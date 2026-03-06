import request from '@/utils/request.js'

/**
 * 查询当前登录教师名下的所有试题集（不包括题目本身）
 * @returns {Promise} 试题集列表
 */
export const querySets = () => {
  return request.post('/question/querySets')
}

/**
 * 新增一个从属当前登录教师的空试题集,返回该试题集的id
 * @returns {Promise} 新增的试题集ID
 */
export const addSet = () => {
  return request.post('/question/addSet')
}

/**
 * 根据setId查询单个试题集（不包括题目本身）
 * @param {string} setId - 试题集ID
 * @returns {Promise} 试题集详情
 */
export const querySet = (setId) => {
  return request.post('/question/querySet', setId)
}

/**
 * 更新试题集
 * @param {Object} questionSet - 试题集信息
 * @param {number} questionSet.setId - 试题集ID
 * @param {string} questionSet.setName - 试题集名称
 * @param {string} questionSet.setDesc - 试题集描述
 * @param {number} questionSet.userId - 用户ID
 * @returns {Promise} 更新结果
 */
export const updateSet = (questionSet) => {
  return request.post('/question/updateSet', questionSet)
}

/**
 * 删除试题集
 * @param {string} setId - 试题集ID
 * @returns {Promise} 删除结果
 */
export const deleteSet = (setId) => {
  return request.post('/question/deleteSet', setId)
}

/**
 * 根据setId查询试题集下的所有题目（不包括试题集本身）
 * @param {string} setId - 试题集ID
 * @returns {Promise} 题目列表
 */
export const queryQuestions = (setId) => {
  return request.post('/question/queryQuestions', setId)
}

/**
 * 新建从属setId试题集的空题目，并返回该空实体
 * @param {string} setId - 试题集ID
 * @param {string} questionType - 题目类型
 * @returns {Promise} 新建的题目信息
 */
export const addQuestion = (setId, questionType) => {
  return request.post(`/question/addQuestion?setId=${setId}&questionType=${questionType}`)
}

/**
 * 更新单个试题
 * @param {Object} questionDTO - 题目信息
 * @param {string} questionDTO.questionId - 题目ID
 * @param {string} questionDTO.questionStem - 题目标干
 * @param {string} questionDTO.questionAnswer - 题目答案
 * @param {string} questionDTO.questionParse - 题目解析
 * @param {string} questionDTO.codeOutput - 代码输出
 * @param {string} questionDTO.questionType - 题目类型
 * @param {string} questionDTO.setId - 试题集ID
 * @param {Array} questionDTO.choiceList - 选项列表
 * @param {Array} questionDTO.knowledgeList - 知识点列表
 * @param {boolean} questionDTO.editable - 是否可编辑
 * @returns {Promise} 更新结果
 */
export const updateQuestion = (questionDTO) => {
  return request.post('/question/updateQuestion', questionDTO)
}

/**
 * 删除题目
 * @param {string} questionId - 题目ID
 * @returns {Promise} 删除结果
 */
export const deleteQuestion = (questionId) => {
  return request.post('/question/deleteQuestion', questionId)
} 