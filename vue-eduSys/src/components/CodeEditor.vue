<!-- 通用代码编辑器组件 -->
<template>
  <div class="code-editor-container" :style="{ height: height }">
    <!-- 工具栏 -->
    <div class="toolbar" v-if="showToolbar">
      <div class="toolbar-left">
        <el-button size="small" @click="formatCode">格式化</el-button>
        <el-button size="small" @click="clearCode">清空</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" size="small" @click="executeCode" :loading="executing">
          {{ executing ? '执行中...' : '执行代码' }}
        </el-button>
      </div>
    </div>
    
    <!-- 代码编辑器 -->
    <div ref="editorContainer" class="code-editor" :style="{ height: showToolbar ? 'calc(100% - 50px)' : '100%' }"></div>
    
    <!-- 运行结果 -->
    <div v-if="showResult" class="result-panel" :style="{ height: resultHeight }">
      <div class="result-header">
        <span class="result-title">运行结果</span>
        <div class="result-actions">
          <el-button size="small" @click="clearResult">清空结果</el-button>
          <el-button size="small" @click="copyResult">复制结果</el-button>
        </div>
      </div>
      <div class="result-content" ref="resultContent">
        <pre v-if="result" class="result-text">{{ result }}</pre>
        <div v-else class="result-placeholder">代码执行结果将显示在这里...</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, defineEmits, defineProps, defineExpose } from 'vue'
import { EditorView } from 'codemirror'
import { EditorState } from '@codemirror/state'
import { python } from '@codemirror/lang-python'
import { oneDark } from '@codemirror/theme-one-dark'
import { ElMessage } from 'element-plus'
import { runPythonCode } from '@/api'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  language: {
    type: String,
    default: 'python'
  },
  height: {
    type: String,
    default: '400px'
  },
  showToolbar: {
    type: Boolean,
    default: true
  },
  showResult: {
    type: Boolean,
    default: true
  },
  resultHeight: {
    type: String,
    default: '200px'
  },
  placeholder: {
    type: String,
    default: '请输入代码...'
  }
})

const emit = defineEmits(['update:modelValue', 'code-executed'])

const editorContainer = ref(null)
const resultContent = ref(null)
const result = ref('')
const executing = ref(false)

let editorView = null

// 创建编辑器状态
const createEditorState = (content) => {
  return EditorState.create({
    doc: content,
    extensions: [
      python(),
      oneDark,
      EditorView.updateListener.of(update => {
        if (update.docChanged) {
          const newValue = update.state.doc.toString()
          emit('update:modelValue', newValue)
        }
      })
    ]
  })
}

// 初始化编辑器
onMounted(() => {
  const startState = createEditorState(props.modelValue)
  editorView = new EditorView({
    state: startState,
    parent: editorContainer.value
  })
})

// 清理资源
onBeforeUnmount(() => {
  if (editorView) {
    editorView.destroy()
  }
})

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  if (editorView) {
    const currentValue = editorView.state.doc.toString()
    if (newValue !== currentValue) {
      editorView.dispatch({
        changes: {
          from: 0,
          to: editorView.state.doc.length,
          insert: newValue
        }
      })
    }
  }
})

// 执行代码
const executeCode = async () => {
  try {
    const code = editorView.state.doc.toString()
    if (!code.trim()) {
      ElMessage.warning('请输入代码')
      return
    }
    
    executing.value = true
    result.value = '正在执行代码...'
    
    // 使用Python代码执行
    const response = await runPythonCode(code)
    
    if (response.data.code === '200') {
      ElMessage.success('代码执行成功')
      result.value = response.data.data || '执行完成，无输出'
    } else {
      ElMessage.error('代码执行出错')
      result.value = response.data.message || '执行失败'
    }
    
    // 触发代码执行事件
    emit('code-executed', {
      code: code,
      result: result.value,
      success: response.data.code === '200',
      language: 'python',
      response: response.data
    })
  } catch (error) {
    console.error('代码执行失败:', error)
    ElMessage.error('代码执行失败')
    result.value = '执行失败: ' + (error.message || '未知错误')
  } finally {
    executing.value = false
  }
}

// 格式化代码
const formatCode = () => {
  // 这里可以添加代码格式化逻辑
  ElMessage.info('代码格式化功能开发中...')
}

// 清空代码
const clearCode = () => {
  if (editorView) {
    editorView.dispatch({
      changes: {
        from: 0,
        to: editorView.state.doc.length,
        insert: ''
      }
    })
  }
}

// 清空结果
const clearResult = () => {
  result.value = ''
}

// 复制结果
const copyResult = async () => {
  if (result.value) {
    try {
      await navigator.clipboard.writeText(result.value)
      ElMessage.success('结果已复制到剪贴板')
    } catch (error) {
      ElMessage.error('复制失败')
    }
  }
}

// 暴露方法给父组件
defineExpose({
  executeCode,
  getCode: () => editorView ? editorView.state.doc.toString() : '',
  setCode: (code) => {
    if (editorView) {
      editorView.dispatch({
        changes: {
          from: 0,
          to: editorView.state.doc.length,
          insert: code
        }
      })
    }
  },
  clearCode,
  clearResult,
  getResult: () => result.value,
  setResult: (text) => {
    result.value = text
  },
  getCurrentLanguage: () => 'python'
})
</script>

<style scoped>
.code-editor-container {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  height: 42px;
}

.toolbar-left {
  display: flex;
  gap: 8px;
  align-items: center;
}

.toolbar-right {
  display: flex;
  gap: 8px;
  align-items: center;
}

.code-editor {
  border-radius: 0;
  box-shadow: none;
  transition: none;
}

.code-editor:hover {
  box-shadow: none;
  transform: none;
}

.result-panel {
  border-top: 1px solid #e4e7ed;
  background: #1e1e1e;
  color: #d4d4d4;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #2d2d30;
  border-bottom: 1px solid #3e3e42;
}

.result-title {
  font-weight: 600;
  color: #fff;
}

.result-actions {
  display: flex;
  gap: 8px;
}

.result-content {
  padding: 12px;
  height: calc(100% - 40px);
  overflow-y: auto;
}

.result-text {
  margin: 0;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.result-placeholder {
  color: #808080;
  font-style: italic;
  text-align: center;
  margin-top: 20px;
}

/* 深度样式穿透 */
:deep(.cm-editor) {
  height: 100%;
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.6;
}

:deep(.cm-content) {
  caret-color: #64b5f6;
}

:deep(.cm-gutters) {
  background-color: #1e1e1e;
  border-right: 1px solid #333;
  color: #858585;
}

:deep(.cm-activeLine) {
  background-color: rgba(50, 50, 70, 0.4) !important;
}

:deep(.cm-activeLineGutter) {
  background-color: rgba(60, 60, 80, 0.6) !important;
  color: #d4d4d4;
}

:deep(.cm-selectionBackground) {
  background-color: rgba(66, 133, 244, 0.3);
}
</style> 