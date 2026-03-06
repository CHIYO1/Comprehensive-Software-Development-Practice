<!-- 基于codemirror的在线代码编辑运行器 -->
<template>
  <!-- 代码编辑器 -->
  <div style="height: 70%;" ref="editorContainer" class="code-editor"></div>
  <!-- 运行结果 -->
  <div style="height: 30%;background-color: black;">
    <el-row style="padding: 10px;">
      <el-col :span="6">
        <span style="color: white;">运行结果:</span>
      </el-col>
      <el-col :span="4" :offset="14">
        <el-button type="primary" @click="excuteCode()">执行</el-button>
      </el-col>
    </el-row>
    <div
      style="height: 60%;background-color: #2b2d30;border-top: 1px solid white;padding: 20px;color: #808183;white-space: pre-line;">
      {{ result }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, defineEmits, defineProps,defineExpose } from 'vue'
import { EditorView } from 'codemirror'
import { EditorState } from '@codemirror/state'
import { python } from '@codemirror/lang-python'
import { oneDark } from '@codemirror/theme-one-dark'
import { ElMessage } from 'element-plus';
import { runPythonCode } from '@/api'

const result = ref('这是运行结果')
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const editorContainer = ref(null)
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

//代码执行
const excuteCode = async () => {
  try {
    const code = editorView.state.doc.toString()
    if (!code.trim()) {
      ElMessage.warning('请输入代码')
      return
    }
    
    result.value = '正在执行代码...'
    // 使用兼容的Python代码执行方法
    const response = await runPythonCode(code)
    
    if (response.data.code === '200') {
      ElMessage.success('代码执行成功')
      result.value = response.data.data || '执行完成，无输出'
    } else {
      ElMessage.error('代码执行出错')
      result.value = response.data.message || '执行失败'
    }
  } catch (error) {
    console.error('代码执行失败:', error)
    ElMessage.error('代码执行失败')
    result.value = '执行失败: ' + (error.message || '未知错误')
  }
}

// 暴露方法给父组件
defineExpose({
  executeCode: excuteCode,
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
  clearResult: () => {
    result.value = '这是运行结果'
  }
})
</script>

<style scoped>
.code-editor {
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
  transition: all 0.3s ease;
}

.code-editor:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  transform: translateY(-1px);
}

/* 深度样式穿透 */
:deep(.cm-editor) {
  height: 100%;
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 15px;
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