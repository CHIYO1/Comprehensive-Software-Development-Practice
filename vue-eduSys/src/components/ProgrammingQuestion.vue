<!-- 编程题组件 -->
<template>
  <div class="programming-question">
    <!-- 题目信息 -->
    <div class="question-header">
      <div class="question-title">
        <el-tag type="info" size="small">{{ questionType }}</el-tag>
        <span class="title-text">{{ title }}</span>
      </div>
      <div class="question-actions">
        <el-button size="small" @click="runCode" :loading="executing">
          {{ executing ? '执行中...' : '运行代码' }}
        </el-button>
        <el-button size="small" @click="submitAnswer" type="primary">
          提交答案
        </el-button>
      </div>
    </div>

    <!-- 题目内容 -->
    <div class="question-content">
      <div class="question-stem">
        <h4>题目描述</h4>
        <div class="stem-text">{{ questionStem }}</div>
      </div>
      
      <div v-if="codeOutput" class="expected-output">
        <h4>期望输出</h4>
        <pre class="output-text">{{ codeOutput }}</pre>
      </div>
      
      <div v-if="knowledgeList && knowledgeList.length > 0" class="knowledge-points">
        <h4>涉及知识点</h4>
        <div class="knowledge-tags">
          <el-tag 
            v-for="knowledge in knowledgeList" 
            :key="knowledge" 
            size="small" 
            type="primary" 
            effect="plain"
          >
            {{ knowledge }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 代码编辑器 -->
    <div class="code-section">
      <div class="code-header">
        <span class="code-title">代码编辑器</span>
        <div class="code-actions">
          <el-button size="small" @click="loadTemplate">加载模板</el-button>
          <el-button size="small" @click="clearCode">清空代码</el-button>
        </div>
      </div>
      <CodeEditor 
        ref="codeEditor"
        v-model="studentCode" 
        language="python"
        height="400px"
        :show-toolbar="false"
        :show-result="true"
        result-height="200px"
        @code-executed="handleCodeExecuted"
      />
    </div>

    <!-- 执行结果 -->
    <div v-if="executionResult" class="execution-result">
      <div class="result-header">
        <h4>执行结果</h4>
        <div class="result-actions">
          <el-button size="small" @click="clearResult">清空结果</el-button>
          <el-button size="small" @click="copyResult">复制结果</el-button>
        </div>
      </div>
      <div class="result-content">
        <pre class="result-text">{{ executionResult }}</pre>
      </div>
    </div>

    <!-- 答案检查 -->
    <div v-if="answerCheck" class="answer-check">
      <el-alert
        :title="answerCheck.title"
        :type="answerCheck.type"
        :description="answerCheck.description"
        show-icon
        :closable="false"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, defineProps, defineEmits, defineExpose } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CodeEditor from './CodeEditor.vue'

const props = defineProps({
  // 题目信息
  questionId: {
    type: String,
    required: true
  },
  questionStem: {
    type: String,
    required: true
  },
  codeOutput: {
    type: String,
    default: ''
  },
  knowledgeList: {
    type: Array,
    default: () => []
  },
  // 学生答案
  modelValue: {
    type: String,
    default: ''
  },
  // 是否只读模式
  readonly: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'answer-submitted', 'code-executed'])

// 响应式数据
const studentCode = ref(props.modelValue)
const executing = ref(false)
const executionResult = ref('')
const answerCheck = ref(null)

// 组件引用
const codeEditor = ref(null)

// 计算属性
const title = computed(() => `编程题 ${props.questionId}`)
const questionType = computed(() => '编程题')

// 监听答案变化
watch(studentCode, (newValue) => {
  emit('update:modelValue', newValue)
})

// 运行代码
const runCode = async () => {
  if (!codeEditor.value) return
  
  try {
    executing.value = true
    await codeEditor.value.executeCode()
  } catch (error) {
    console.error('运行代码失败:', error)
    ElMessage.error('运行代码失败')
  } finally {
    executing.value = false
  }
}

// 处理代码执行结果
const handleCodeExecuted = (data) => {
  console.log('代码执行结果:', data)
  executionResult.value = data.result
  
  // 触发代码执行事件
  emit('code-executed', {
    questionId: props.questionId,
    code: data.code,
    result: data.result,
    success: data.success,
    language: data.language
  })
  
  // 自动检查答案
  if (data.success && props.codeOutput) {
    checkAnswer(data.result)
  }
}

// 检查答案
const checkAnswer = (result) => {
  if (!props.codeOutput) return
  
  const expectedOutput = props.codeOutput.trim()
  const actualOutput = result.trim()
  
  if (actualOutput.includes(expectedOutput)) {
    answerCheck.value = {
      title: '答案正确',
      type: 'success',
      description: '恭喜！你的代码输出符合期望结果。'
    }
  } else {
    answerCheck.value = {
      title: '答案可能不正确',
      type: 'warning',
      description: '请检查代码逻辑，确保输出符合期望结果。'
    }
  }
}

// 提交答案
const submitAnswer = async () => {
  if (!studentCode.value.trim()) {
    ElMessage.warning('请先编写代码')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      '确定要提交答案吗？提交后将无法修改。',
      '确认提交',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 触发答案提交事件
    emit('answer-submitted', {
      questionId: props.questionId,
      code: studentCode.value,
      result: executionResult.value
    })
    
    ElMessage.success('答案提交成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败')
    }
  }
}

// 加载代码模板
const loadTemplate = () => {
  const template = `# 编程题模板
# 请在这里编写你的代码

def main():
    # 在这里实现你的逻辑
    print("Hello, World!")
    
    # 返回结果
    return "Success"

if __name__ == "__main__":
    result = main()
    print(result)`
  
  studentCode.value = template
  ElMessage.success('已加载代码模板')
}

// 清空代码
const clearCode = () => {
  studentCode.value = ''
  executionResult.value = ''
  answerCheck.value = null
  ElMessage.info('代码已清空')
}

// 清空结果
const clearResult = () => {
  executionResult.value = ''
  answerCheck.value = null
}

// 复制结果
const copyResult = async () => {
  if (executionResult.value) {
    try {
      await navigator.clipboard.writeText(executionResult.value)
      ElMessage.success('结果已复制到剪贴板')
    } catch (error) {
      ElMessage.error('复制失败')
    }
  }
}

// 暴露方法给父组件
defineExpose({
  runCode,
  submitAnswer,
  getCode: () => studentCode.value,
  setCode: (code) => {
    studentCode.value = code
  },
  getResult: () => executionResult.value,
  clearCode,
  clearResult,
  loadTemplate
})
</script>

<style scoped>
.programming-question {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}

.question-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-text {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.question-actions {
  display: flex;
  gap: 8px;
}

.question-content {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.question-stem h4,
.expected-output h4,
.knowledge-points h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.stem-text {
  line-height: 1.6;
  color: #666;
  margin-bottom: 20px;
}

.expected-output {
  margin-bottom: 20px;
}

.output-text {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  color: #495057;
  margin: 0;
  white-space: pre-wrap;
}

.knowledge-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.code-section {
  border-bottom: 1px solid #e4e7ed;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.code-title {
  font-weight: 600;
  color: #333;
}

.code-actions {
  display: flex;
  gap: 8px;
}

.execution-result {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.result-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.result-actions {
  display: flex;
  gap: 8px;
}

.result-content {
  background: #1e1e1e;
  border-radius: 4px;
  padding: 12px;
}

.result-text {
  margin: 0;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  color: #d4d4d4;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.answer-check {
  padding: 16px 20px;
}

.answer-check .el-alert {
  margin: 0;
}
</style> 