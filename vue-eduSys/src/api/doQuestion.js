import request from '@/utils/request.js'

/**
 * 执行代码,返回执行结果
 * @param {string} code - 代码内容
 * @param {string} language - 编程语言 (python/javascript/java/cpp)
 * @returns {Promise} 执行结果
 */
export const runCode = (code, language = 'python') => {
  return request.post('/doQuestion/runCode', {
    code: code,
    language: language
  })
}

/**
 * 执行Python代码文件,返回执行结果 (兼容旧版本)
 * @param {string} code - Python代码
 * @returns {Promise} 执行结果
 */
export const runPythonCode = (code) => {
  return request.post('/doQuestion/runCode', code, { 
    headers: { 'Content-Type': 'text/plain' } 
  })
}

/**
 * 查询做题记录
 * @param {string} recordId - 记录ID
 * @returns {Promise} 做题记录
 */
export const queryRecordSet = (recordId) => {
  return request.post('/doQuestion/queryRecordSet', recordId)
}

/**
 * 查询当前登录用户特定试题小节的做题记录
 * @param {string} subsectionId - 小节ID
 * @returns {Promise} 做题记录
 */
export const queryRecordSetBySubsection = (subsectionId) => {
  return request.post('/doQuestion/queryRecordSetBySubsection', subsectionId)
}

/**
 * 创建做题记录,返回该记录的Id。所属用户为当前登录角色
 * @param {Object} recordSet - 做题记录信息
 * @param {number} recordSet.setRecordId - 试题集记录ID
 * @param {number} recordSet.setScore - 试题集分数
 * @param {number} recordSet.setId - 试题集ID
 * @param {number} recordSet.subsectionId - 小节ID
 * @param {number} recordSet.userId - 用户ID
 * @param {string} recordSet.state - 状态
 * @returns {Promise} 创建的记录ID
 */
export const addRecordSet = (recordSet) => {
  return request.post('/doQuestion/addRecordSet', recordSet)
} 