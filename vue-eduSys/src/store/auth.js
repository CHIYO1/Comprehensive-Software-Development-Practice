import { defineStore } from 'pinia'
import Cookies from 'js-cookie'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    userId:null,
    userName:'' ,
    role: '',            
  }),
  actions: {
    loginSuccess(response) {
      Cookies.set('token', response.token, { secure: true, expires: 7 })
      this.userId = response.userId
      this.userName = response.userName
      this.role = response.role
    },
    //判断角色权限是否相符
    isRole(requiredRole) {
      // console.log("role:"+this.role+";reuqired："+requiredRole)
      return this.role === requiredRole;
    },
    // 是否登录且有认证token
    isAuthenticated() {
      // console.log("token:"+Cookies.get('token')+" ;userid:"+this.userId)
      return !!Cookies.get('token') && !!this.userId
    },
    // 退出登录
    logout() {
      Cookies.remove('token')
      this.$reset() 
    }
  },
  persist: true, 
})