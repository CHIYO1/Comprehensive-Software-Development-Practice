// import request from '@/utils/request.js'

// /**
//  * 用户登录
//  * @param {Object} loginData - 登录数据
//  * @param {string} loginData.userId - 用户ID
//  * @param {string} loginData.password - 密码
//  * @returns {Promise} 登录结果
//  */
// export const userLogin = (loginData) => {
//   return request.post('/user/login', loginData)
// } 


import request from '@/utils/request'

// 登录
export const userLogin = (data) => {
  return request({
    url: '/users/login',
    method: 'post',
    data
  })
}

// 注册
export const userRegister = (data) => {
  return request({
    url: '/users/register',
    method: 'post',
    data
  })
}