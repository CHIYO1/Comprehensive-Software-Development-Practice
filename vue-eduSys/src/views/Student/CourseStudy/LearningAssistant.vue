<template>
    <div class="learning-assistant-page">
        <el-card class="assistant-card">
            <template #header>
                <div class="card-header">
                    <el-icon class="header-icon"><Service /></el-icon>
                    <span>AI学习助手</span>
                </div>
            </template>
            
            <div class="assistant-content">
                <div class="chat-container">
                    <div class="messages-container">
                        <div v-for="message in assistantMessages" :key="message.id" 
                             :class="['message', message.type === 'user' ? 'user-message' : 'assistant-message']">
                            <div class="message-content">{{ message.content }}</div>
                            <div class="message-time">{{ message.time }}</div>
                        </div>
                    </div>
                    <div class="input-container">
                        <el-input v-model="learningAssistantForm.question" 
                                 placeholder="输入你的问题..." 
                                 @keyup.enter="askAssistant"
                                 :rows="3"
                                 type="textarea" />
                        <el-button type="primary" @click="askAssistant" :disabled="!learningAssistantForm.question">
                            发送
                        </el-button>
                    </div>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Service } from '@element-plus/icons-vue'

// 学习助手相关
const learningAssistantForm = ref({
    question: '',
    context: ''
})

const assistantMessages = ref([
    {
        id: 1,
        type: 'assistant',
        content: '你好！我是你的学习助手，有什么问题可以随时问我。',
        time: new Date().toLocaleString()
    }
])

// 预设的问答对
const qaDatabase = {
    'vue': {
        keywords: ['vue', 'vue.js', 'vuejs'],
        responses: [
            'Vue.js是一个渐进式JavaScript框架，由尤雨溪在2014年创建。\n\n核心特性包括：\n1. 响应式数据绑定：数据变化自动更新视图，无需手动操作DOM\n2. 组件化开发：将页面拆分为可复用的组件，提高开发效率\n3. 虚拟DOM：通过虚拟DOM diff算法，高效更新真实DOM\n4. 指令系统：提供v-if、v-for、v-model等内置指令\n5. 单文件组件：.vue文件包含模板、脚本、样式，便于维护\n\nVue.js的设计理念是"渐进式框架"，你可以：\n- 只用Vue的核心视图层功能\n- 逐步添加路由、状态管理等\n- 构建完整的单页面应用\n\n学习路线建议：基础语法 → 组件化 → 路由管理 → 状态管理 → 高级特性（自定义指令、插件等）'
        ]
    },
    '响应式': {
        keywords: ['响应式', '响应式数据', '数据绑定', 'reactive', '响应式原理'],
        responses: [
            'Vue.js的响应式原理是框架的核心机制，让我详细解释：\n\nVue 2.x 响应式原理：\n1. 数据劫持：使用Object.defineProperty劫持对象的属性\n2. 依赖收集：在getter中收集依赖（Watcher）\n3. 派发更新：在setter中通知依赖更新\n\nVue 3.x 响应式原理：\n1. Proxy代理：使用ES6的Proxy替代Object.defineProperty\n2. 更好的性能：Proxy可以监听数组变化和对象新增属性\n3. 更简洁的API：reactive()、ref()、computed()等\n\n响应式数据的特点：\n- 数据变化自动触发视图更新\n- 支持深层监听（Vue 3）\n- 支持数组变化监听\n- 支持Map、Set等数据结构\n\n使用示例：\n// Vue 3\nimport { reactive, ref } from \'vue\'\n\nconst state = reactive({ count: 0 })\nconst count = ref(0)\n\n// 数据变化会自动更新视图\nstate.count++\ncount.value++'
        ]
    },
    '生命周期': {
        keywords: ['生命周期', 'lifecycle', '钩子', 'hook', 'mounted', 'created'],
        responses: [
            'Vue组件的生命周期是理解Vue.js的重要概念，让我详细说明：\n\n完整的生命周期流程：\n\n1. 创建阶段：\n   - beforeCreate：实例刚创建，data和methods不可用\n   - created：实例创建完成，data和methods可用，但DOM未挂载\n\n2. 挂载阶段：\n   - beforeMount：模板编译完成，但DOM未挂载\n   - mounted：DOM挂载完成，可以访问DOM元素\n\n3. 更新阶段：\n   - beforeUpdate：数据更新时，DOM更新前调用\n   - updated：DOM更新完成后调用\n\n4. 销毁阶段：\n   - beforeDestroy：实例销毁前调用\n   - destroyed：实例销毁后调用\n\nVue 3 新增钩子：\n- beforeUnmount：替代beforeDestroy\n- unmounted：替代destroyed\n\n使用场景：\n- created：初始化数据、发起API请求\n- mounted：DOM操作、第三方库初始化\n- beforeDestroy：清理定时器、事件监听器\n\n组合式API中的生命周期：\nimport { onMounted, onUnmounted } from \'vue\'\n\nexport default {\n  setup() {\n    onMounted(() => {\n      console.log(\'组件已挂载\')\n    })\n    \n    onUnmounted(() => {\n      console.log(\'组件已销毁\')\n    })\n  }\n}'
        ]
    },
    '路由': {
        keywords: ['路由', 'router', 'vue-router', '导航', '路由守卫'],
        responses: [
            'Vue Router是Vue.js官方的路由管理器，用于构建单页面应用：\n\n基本配置：\nimport { createRouter, createWebHistory } from \'vue-router\'\n\nconst routes = [\n  {\n    path: \'/\',\n    name: \'Home\',\n    component: () => import(\'@/views/Home.vue\')\n  },\n  {\n    path: \'/about\',\n    name: \'About\',\n    component: () => import(\'@/views/About.vue\')\n  }\n]\n\nconst router = createRouter({\n  history: createWebHistory(),\n  routes\n})\n\n路由导航方式：\n\n1. 声明式导航：\n   <router-link to="/about">关于</router-link>\n\n2. 编程式导航：\n   this.$router.push(\'/about\')\n   this.$router.push({ name: \'About\' })\n   this.$router.push({ path: \'/about\', query: { id: 1 } })\n\n路由守卫：\n- beforeEach：全局前置守卫\n- beforeResolve：全局解析守卫\n- afterEach：全局后置钩子\n- beforeEnter：路由独享守卫\n- beforeRouteEnter：组件内守卫\n\n动态路由：\n{\n  path: \'/user/:id\',\n  component: User,\n  props: true\n}\n\n嵌套路由：\n{\n  path: \'/user\',\n  component: User,\n  children: [\n    { path: \'profile\', component: UserProfile },\n    { path: \'posts\', component: UserPosts }\n  ]\n}'
        ]
    },
    '状态管理': {
        keywords: ['状态管理', 'vuex', 'pinia', 'store', '状态'],
        responses: [
            'Vue的状态管理用于管理应用中的共享状态，主要有Vuex和Pinia：\n\nVuex（Vue 2推荐）：\n\nimport { createStore } from \'vuex\'\n\nconst store = createStore({\n  state: {\n    count: 0\n  },\n  getters: {\n    doubleCount: state => state.count * 2\n  },\n  mutations: {\n    increment(state) {\n      state.count++\n    }\n  },\n  actions: {\n    asyncIncrement({ commit }) {\n      setTimeout(() => {\n        commit(\'increment\')\n      }, 1000)\n    }\n  }\n})\n\nPinia（Vue 3推荐）：\n\nimport { defineStore } from \'pinia\'\n\nexport const useCounterStore = defineStore(\'counter\', {\n  state: () => ({\n    count: 0\n  }),\n  getters: {\n    doubleCount: (state) => state.count * 2\n  },\n  actions: {\n    increment() {\n      this.count++\n    },\n    async incrementAsync() {\n      await new Promise(resolve => setTimeout(resolve, 1000))\n      this.count++\n    }\n  }\n})\n\n核心概念对比：\n\n概念 | Vuex | Pinia\n状态 | state | state\n计算属性 | getters | getters\n修改状态 | mutations | actions\n异步操作 | actions | actions\n\nPinia的优势：\n- 更轻量，没有mutations\n- 更好的TypeScript支持\n- 更简洁的API\n- 支持组合式API\n- 更好的开发工具支持\n\n使用场景：\n- 用户登录状态\n- 购物车数据\n- 主题设置\n- 全局配置'
        ]
    },
    '指令': {
        keywords: ['指令', 'directive', 'v-if', 'v-for', 'v-model', 'v-show'],
        responses: [
            'Vue指令是带有v-前缀的特殊属性，用于在模板中实现特定功能：\n\n常用内置指令：\n\n1. v-if / v-show（条件渲染）：\n   <div v-if="isVisible">条件渲染</div>\n   <div v-show="isVisible">显示隐藏</div>\n   - v-if：真正的条件渲染，会销毁重建DOM\n   - v-show：只是切换display属性，DOM始终存在\n\n2. v-for（列表渲染）：\n   <ul>\n     <li v-for="(item, index) in items" :key="item.id">\n       {{ item.name }}\n     </li>\n   </ul>\n\n3. v-model（双向绑定）：\n   <input v-model="message" />\n   <textarea v-model="description"></textarea>\n   <select v-model="selected">\n     <option value="A">选项A</option>\n     <option value="B">选项B</option>\n   </select>\n\n4. v-on（事件绑定）：\n   <button v-on:click="handleClick">点击</button>\n   <button @click="handleClick">简写</button>\n\n5. v-bind（属性绑定）：\n   <img v-bind:src="imageSrc" />\n   <img :src="imageSrc" /> <!-- 简写 -->\n\n自定义指令：\n// 全局注册\napp.directive(\'focus\', {\n  mounted(el) {\n    el.focus()\n  }\n})\n\n// 局部注册\nexport default {\n  directives: {\n    focus: {\n      mounted(el) {\n        el.focus()\n      }\n    }\n  }\n}\n\n指令钩子函数：\n- beforeMount：指令绑定到元素时\n- mounted：元素插入父DOM时\n- beforeUpdate：元素更新前\n- updated：元素更新后\n- beforeUnmount：元素卸载前\n- unmounted：元素卸载后'
        ]
    }
}

// 学习助手功能
const askAssistant = async () => {
    if (!learningAssistantForm.value.question) {
        ElMessage.error('请输入问题')
        return
    }
    
    // 添加用户问题
    const userMessage = {
        id: Date.now(),
        type: 'user',
        content: learningAssistantForm.value.question,
        time: new Date().toLocaleString()
    }
    assistantMessages.value.push(userMessage)
    
    // 查找匹配的回答
    const question = learningAssistantForm.value.question.toLowerCase()
    let response = '抱歉，我没有找到相关的问题。请尝试询问关于Vue.js、响应式、组件、生命周期、路由、状态管理或指令等方面的问题。'
    
    for (const category in qaDatabase) {
        const qa = qaDatabase[category]
        if (qa.keywords.some(keyword => question.includes(keyword))) {
            const randomIndex = Math.floor(Math.random() * qa.responses.length)
            response = qa.responses[randomIndex]
            break
        }
    }
    
    // 模拟AI回答
    setTimeout(() => {
        const assistantMessage = {
            id: Date.now() + 1,
            type: 'assistant',
            content: response,
            time: new Date().toLocaleString()
        }
        assistantMessages.value.push(assistantMessage)
    }, 1000)
    
    learningAssistantForm.value.question = ''
}
</script>

<style scoped>
.learning-assistant-page {
    padding: 20px;
}

.assistant-card {
    border-radius: 12px;
}

.card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    color: #303133;
}

.header-icon {
    color: #409eff;
    font-size: 18px;
}

.assistant-content {
    height: 600px;
    display: flex;
    flex-direction: column;
}

.chat-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 10px;
    overflow-y: auto;
}

.messages-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.message {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    padding: 10px 15px;
    border-radius: 10px;
    max-width: 80%;
}

.user-message {
    align-self: flex-end;
    background-color: #409eff;
    color: white;
    border-bottom-right-radius: 4px;
}

.assistant-message {
    align-self: flex-start;
    background-color: #e1f3d8;
    color: #303133;
    border-bottom-left-radius: 4px;
}

.message-content {
    word-break: break-word;
}

.message-time {
    font-size: 10px;
    color: #909399;
    margin-top: 5px;
}

.input-container {
    display: flex;
    gap: 10px;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
}

.input-container .el-textarea {
    flex: 1;
    margin-right: 10px;
}

.input-container .el-button {
    flex-shrink: 0;
}
</style> 