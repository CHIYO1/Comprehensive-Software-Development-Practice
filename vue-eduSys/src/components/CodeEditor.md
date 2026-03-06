# CodeEditor 组件使用说明

## 概述

CodeEditor 是一个基于 CodeMirror 的在线代码编辑和运行组件，支持多种编程语言，提供代码执行、语法高亮、自动补全等功能。

## 功能特性

- 🎨 **多语言支持**：Python、JavaScript、Java、C++
- ⚡ **实时执行**：在线代码运行，即时查看结果
- 🎯 **语法高亮**：智能语法高亮和代码提示
- 🛠️ **工具栏**：语言切换、代码格式化、清空等功能
- 📋 **结果管理**：复制结果、清空结果等操作
- 🔧 **高度可配置**：支持自定义高度、显示选项等

## 基础用法

### 1. 引入组件

```vue
<template>
  <CodeEditor 
    v-model="code" 
    language="python"
    height="400px"
    @code-executed="handleCodeExecuted"
  />
</template>

<script setup>
import CodeEditor from '@/components/CodeEditor.vue'

const code = ref('print("Hello, World!")')

const handleCodeExecuted = (data) => {
  console.log('执行结果:', data)
}
</script>
```

### 2. 在编程题中使用

```vue
<template>
  <div class="programming-question">
    <div class="question-content">
      <h4>题目：计算斐波那契数列</h4>
      <p>请编写一个Python函数，计算斐波那契数列的第n项。</p>
    </div>
    <CodeEditor 
      v-model="studentCode" 
      language="python"
      height="350px"
      :show-toolbar="false"
      @code-executed="handleProgrammingExecuted"
    />
  </div>
</template>
```

## Props 配置

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `modelValue` | String | `''` | 代码内容（支持 v-model） |
| `language` | String | `'python'` | 编程语言 |
| `height` | String | `'400px'` | 编辑器高度 |
| `showToolbar` | Boolean | `true` | 是否显示工具栏 |
| `showResult` | Boolean | `true` | 是否显示结果面板 |
| `resultHeight` | String | `'200px'` | 结果面板高度 |
| `placeholder` | String | `'请输入代码...'` | 占位符文本 |

## Events 事件

| 事件名 | 参数 | 说明 |
|--------|------|------|
| `update:modelValue` | `(value: string)` | 代码内容变化时触发 |
| `code-executed` | `(data: object)` | 代码执行完成时触发 |
| `language-changed` | `(language: string)` | 语言切换时触发 |

### code-executed 事件参数

```javascript
{
  code: string,        // 执行的代码
  result: string,      // 执行结果
  success: boolean,    // 是否执行成功
  language: string     // 编程语言
}
```

## 方法

通过 ref 可以调用以下方法：

| 方法名 | 参数 | 说明 |
|--------|------|------|
| `executeCode()` | - | 执行代码 |
| `getCode()` | - | 获取当前代码内容 |
| `setCode(code)` | `code: string` | 设置代码内容 |
| `clearCode()` | - | 清空代码 |
| `clearResult()` | - | 清空执行结果 |
| `getResult()` | - | 获取执行结果 |
| `setResult(text)` | `text: string` | 设置执行结果 |
| `changeLanguage(language)` | `language: string` | 切换编程语言 |
| `getCurrentLanguage()` | - | 获取当前语言 |

## 使用示例

### 1. 基础用法

```vue
<template>
  <CodeEditor 
    v-model="code" 
    language="python"
    height="300px"
  />
</template>

<script setup>
import { ref } from 'vue'
import CodeEditor from '@/components/CodeEditor.vue'

const code = ref('print("Hello, World!")')
</script>
```

### 2. 编程题模式

```vue
<template>
  <div class="programming-exercise">
    <div class="question">
      <h3>编程题：计算阶乘</h3>
      <p>编写一个函数计算 n 的阶乘</p>
    </div>
    
    <CodeEditor 
      ref="codeEditor"
      v-model="studentAnswer" 
      language="python"
      height="400px"
      :show-toolbar="false"
      @code-executed="checkAnswer"
    />
    
    <div class="actions">
      <el-button @click="runCode">运行代码</el-button>
      <el-button @click="submitAnswer">提交答案</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CodeEditor from '@/components/CodeEditor.vue'

const codeEditor = ref(null)
const studentAnswer = ref('')

const runCode = () => {
  codeEditor.value.executeCode()
}

const checkAnswer = (data) => {
  if (data.success) {
    // 检查答案逻辑
    console.log('代码执行结果:', data.result)
  }
}

const submitAnswer = () => {
  const code = codeEditor.value.getCode()
  console.log('提交的代码:', code)
}
</script>
```

### 3. 代码模板功能

```vue
<template>
  <div class="code-templates">
    <el-button @click="loadTemplate('python')">Python模板</el-button>
    <el-button @click="loadTemplate('javascript')">JavaScript模板</el-button>
    
    <CodeEditor 
      ref="editor"
      v-model="code" 
      language="python"
      height="400px"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CodeEditor from '@/components/CodeEditor.vue'

const editor = ref(null)
const code = ref('')

const templates = {
  python: `def main():
    print("Hello from Python!")
    return "Success"

if __name__ == "__main__":
    result = main()
    print(result)`,
  
  javascript: `function main() {
    console.log("Hello from JavaScript!");
    return "Success";
}

const result = main();
console.log(result);`
}

const loadTemplate = (language) => {
  code.value = templates[language]
  editor.value.changeLanguage(language)
}
</script>
```

### 4. 自定义配置

```vue
<template>
  <CodeEditor 
    v-model="code" 
    language="javascript"
    height="500px"
    :show-toolbar="true"
    :show-result="true"
    result-height="250px"
    @code-executed="handleResult"
    @language-changed="handleLanguageChange"
  />
</template>

<script setup>
import { ref } from 'vue'
import CodeEditor from '@/components/CodeEditor.vue'

const code = ref('console.log("Hello, World!");')

const handleResult = (data) => {
  console.log('执行结果:', data)
  if (data.success) {
    // 处理成功结果
  } else {
    // 处理错误
  }
}

const handleLanguageChange = (language) => {
  console.log('语言已切换为:', language)
}
</script>
```

## 集成到现有功能

### 1. 在试题管理中使用

```vue
<template>
  <div class="question-editor">
    <el-form :model="questionForm">
      <el-form-item label="题目标干">
        <el-input v-model="questionForm.questionStem" type="textarea" />
      </el-form-item>
      
      <el-form-item label="期望输出">
        <el-input v-model="questionForm.codeOutput" type="textarea" />
      </el-form-item>
      
      <el-form-item label="代码编辑器">
        <CodeEditor 
          v-model="questionForm.sampleCode" 
          language="python"
          height="300px"
        />
      </el-form-item>
    </el-form>
  </div>
</template>
```

### 2. 在学习助手中使用

```vue
<template>
  <div class="learning-assistant">
    <div class="chat-area">
      <!-- 聊天内容 -->
    </div>
    
    <div class="code-playground">
      <h4>代码练习区</h4>
      <CodeEditor 
        v-model="practiceCode" 
        language="python"
        height="300px"
        @code-executed="handlePracticeResult"
      />
    </div>
  </div>
</template>
```

## 注意事项

1. **依赖安装**：确保已安装 CodeMirror 相关依赖
2. **API 接口**：需要后端提供代码执行接口
3. **安全性**：代码执行可能存在安全风险，建议在沙箱环境中运行
4. **性能**：大量代码执行可能影响性能，建议添加执行限制
5. **错误处理**：妥善处理代码执行错误和网络异常

## 依赖要求

```json
{
  "dependencies": {
    "codemirror": "^6.0.0",
    "@codemirror/basic-setup": "^6.0.0",
    "@codemirror/lang-python": "^6.0.0",
    "@codemirror/lang-javascript": "^6.0.0",
    "@codemirror/lang-java": "^6.0.0",
    "@codemirror/lang-cpp": "^6.0.0",
    "@codemirror/theme-one-dark": "^6.0.0"
  }
}
```

## 更新日志

- **v1.0.0**: 初始版本，支持基础代码编辑和执行
- **v1.1.0**: 添加多语言支持和工具栏
- **v1.2.0**: 优化错误处理和用户体验
- **v1.3.0**: 添加代码模板和编程题模式 