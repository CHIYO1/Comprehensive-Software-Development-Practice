import request from '@/utils/request.js'

/**
 * 用户登录
 * @param {Object} loginData - 登录数据
 * @param {string} loginData.userId - 用户ID
 * @param {string} loginData.password - 密码
 * @returns {Promise} 登录结果
 */
export const userLogin = (loginData) => {
  return request.post('/user/login', loginData)
} 