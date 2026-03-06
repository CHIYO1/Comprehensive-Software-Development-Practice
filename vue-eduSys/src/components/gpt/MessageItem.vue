<!-- Markdown文本渲染 -->
<template>
  <div v-html="htmlContent"  class="markdown-body"></div>
</template>
<script setup>
import { defineProps, onBeforeUnmount, onBeforeMount, onMounted, computed,ref,watch } from 'vue'
import { debounce } from 'lodash-es'
// import md from '@/utils/markdownParser'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/foundation.css'

let isUpdating = ref(false) // 用于展示更新状态的响应式变量
// 防抖函数
const debouncedUpdateEnd = debounce(() => {
  console.log('更新已停止，执行最终操作')
  hljs.highlightAll();
  isUpdating.value = false
}, 1000)

const render = new marked.Renderer()
marked.setOptions({
  renderer: render, 
  gfm: true,	// 启动类似于Github样式的Markdown语法
  pedantic: false, // 只解析符合Markdwon定义的，不修正Markdown的错误
  sanitize: false, // 原始输出，忽略HTML标签
	// 高亮(未生效)
  highlight: (code, lang) => hljs.highlight(code, { language: lang }).value,
})
const props = defineProps({
  message: String
})
const htmlContent = computed(() => {
  return marked(props.message)
})

watch(htmlContent, () => {
  if (!isUpdating.value) {
    isUpdating.value = true
  }
  debouncedUpdateEnd() // 重置防抖计时器
  // 调用marked解析
})

onMounted(()=>{
  hljs.highlightAll();
})
onBeforeMount(() => {
})
// 组件卸载时清理
onBeforeUnmount(() => {
  debouncedUpdateEnd.cancel()
})
</script>
<style scoped></style>
