//全局后端请求实例
import axios from 'axios'
import Cookies from 'js-cookie';
import { ElMessage } from 'element-plus';

// 设置API基础URL，如果没有环境变量则使用默认值
const apiBaseUrl = process.env.VUE_APP_BASE_URL || 'http://localhost:8080/api';

// 创建axios实例
const service = axios.create({
  baseURL: apiBaseUrl,
  timeout: 60000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    if (config.url !== '/user/login') {
      const token = Cookies.get('token')
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)
// 响应拦截器
service.interceptors.response.use(
  (response) => {
    // if (response.data.code != 200) {
    //   ElMessage.error("后端处理失败，请稍后再试");
    //   return Promise.reject(response);
    // }
    return response
  },
  (error) => {
    let errorMessage = '请求错误'

    if (error.code === 'ECONNABORTED') {
      errorMessage = '请求超时，请稍后再试'
    } else if (error.response) {
      const status = error.response.status
      const data = error.response.data

      switch (status) {
        case 400:
          errorMessage = data.message || '请求参数错误'
          break
        case 401:
          errorMessage = ''
          Cookies.remove('token')
          if (window.location.pathname !== '/login') {
            window.location.href = '/login'
          }
          break
        case 403:
          errorMessage = '登录已过期/认证失败，请重新登录'
          // if (window.location.pathname !== '/login') {
          //   window.location.href = '/login'
          // }
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        default:
          errorMessage = data.message || `服务器错误: ${status}`
      }
    } else if (error.request) {
      errorMessage = '网络错误，请检查网络连接'
    } else {
      errorMessage = error.message || '未知错误'
    }

    
    console.log("请求错误：" + error);
    ElMessage.error(errorMessage)
    // 返回一个统一的错误对象
    return Promise.reject({
      message: errorMessage,
      code: error.response?.status || 0,
      data: error.response?.data
    })
  }
)
export default service