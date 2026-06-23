import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),

  getters: {
    // 用户ID
    userId: (state) => state.userInfo?.userId || null,

    // 用户名
    userName: (state) =>
      state.userInfo?.userName ||
      state.userInfo?.username ||
      '',

    // ⭐ 关键：统一角色格式（全部转大写）
    role: (state) =>
      (state.userInfo?.role || '').toLowerCase()
  },

  actions: {
    // 登录成功
    loginSuccess(data) {
      this.token = data.token

      // ⭐ 关键：统一角色格式
      this.userInfo = {
        ...data,
        role: (data.role || '').toUpperCase()
      }

      localStorage.setItem('token', data.token)
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    },

    // ⭐ 权限判断（支持多角色）
    isRole(requiredRole) {
      const userRole = (this.role || '').toUpperCase()

      if (Array.isArray(requiredRole)) {
        return requiredRole
          .map(r => r.toUpperCase())
          .includes(userRole)
      }

      return userRole === (requiredRole || '').toUpperCase()
    },

    // 是否登录
    isAuthenticated() {
      return !!this.token && !!this.userId
    },

    // 退出登录
    logout() {
      this.token = ''
      this.userInfo = {}

      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})