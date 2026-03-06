import request from '@/utils/request.js'

/**
 * 设置对话激活状态
 * @param {string} convId - 对话ID
 * @returns {Promise} 设置结果
 */
export const setConvsActivate = (convId) => {
  return request.post('/LrnAst/setConvsAct', convId)
}

/**
 * 反转激活状态
 * @param {string} convId - 对话ID
 * @returns {Promise} 反转结果
 */
export const invertActivate = (convId) => {
  return request.post('/LrnAst/invertActivate', convId)
}

/**
 * 获取所有对话
 * @returns {Promise} 对话列表
 */
export const getAllConv = () => {
  return request.post('/LrnAst/getAllConv')
}

/**
 * 删除对话
 * @param {string} convId - 对话ID
 * @returns {Promise} 删除结果
 */
export const deleteConvs = (convId) => {
  return request.post('/LrnAst/deleteConvs', convId)
}

/**
 * 聊天接口
 * @param {string} query - 查询内容
 * @returns {Promise} 聊天响应
 */
export const chat = (query) => {
  return request.get(`/LrnAst/chat?query=${encodeURIComponent(query)}`)
} 