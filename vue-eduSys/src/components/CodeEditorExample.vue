<!-- CodeEditor组件使用示例 -->
<template>
  <div class="code-editor-example">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>代码编辑器使用示例</span>
          <el-button type="primary" @click="showExample = !showExample">
            {{ showExample ? '隐藏示例' : '显示示例' }}
          </el-button>
        </div>
      </template>
      
      <div v-if="showExample">
        <!-- 基础用法 -->
        <el-divider content-position="left">基础用法</el-divider>
        <CodeEditor 
          v-model="basicCode" 
          language="python"
          height="300px"
          @code-executed="handleCodeExecuted"
        />
        
        <!-- 自定义配置 -->
        <el-divider content-position="left">自定义配置</el-divider>
        <CodeEditor 
          v-model="customCode" 
          language="javascript"
          height="400px"
          :show-toolbar="true"
          :show-result="true"
          result-height="150px"
          @code-executed="handleCodeExecuted"
          @language-changed="handleLanguageChanged"
        />
        
        <!-- 编程题模式 -->
        <el-divider content-position="left">编程题模式</el-divider>
        <div class="programming-question">
          <div class="question-content">
            <h4>题目：计算斐波那契数列</h4>
            <p>请编写一个Python函数，计算斐波那契数列的第n项。</p>
            <div class="expected-output">
              <strong>期望输出：</strong>
              <pre>请输入n: 10
第10项斐波那契数是: 55</pre>
            </div>
          </div>
          <CodeEditor 
            ref="programmingEditor"
            v-model="programmingCode" 
            language="python"
            height="350px"
            :show-toolbar="false"
            @code-executed="handleProgrammingExecuted"
          />
        </div>
        
        <!-- 代码模板 -->
        <el-divider content-position="left">代码模板</el-divider>
        <div class="code-templates">
          <el-button @click="loadPythonTemplate">Python模板</el-button>
          <el-button @click="loadJavaScriptTemplate">JavaScript模板</el-button>
          <el-button @click="loadJavaTemplate">Java模板</el-button>
        </div>
        <CodeEditor 
          ref="templateEditor"
          v-model="templateCode" 
          language="python"
          height="300px"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import CodeEditor from './CodeEditor.vue'

const showExample = ref(false)
const basicCode = ref('print("Hello, World!")')
const customCode = ref('console.log("Hello from JavaScript!");')
const programmingCode = ref(`def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n-1) + fibonacci(n-2)

n = int(input("请输入n: "))
result = fibonacci(n)
print(f"第{n}项斐波那契数是: {result}")`)
const templateCode = ref('')

const programmingEditor = ref(null)
const templateEditor = ref(null)

// 处理代码执行结果
const handleCodeExecuted = (data) => {
  console.log('代码执行结果:', data)
  if (data.success) {
    ElMessage.success('代码执行成功！')
  } else {
    ElMessage.error('代码执行失败！')
  }
}

// 处理语言变化
const handleLanguageChanged = (language) => {
  console.log('语言已切换为:', language)
  ElMessage.info(`已切换到 ${language} 语言`)
}

// 处理编程题执行
const handleProgrammingExecuted = (data) => {
  console.log('编程题执行结果:', data)
  // 这里可以添加编程题的评分逻辑
  if (data.success) {
    // 检查输出是否包含期望的内容
    if (data.result.includes('55')) {
      ElMessage.success('恭喜！答案正确！')
    } else {
      ElMessage.warning('答案可能不正确，请检查代码逻辑')
    }
  }
}

// 加载代码模板
const loadPythonTemplate = () => {
  templateCode.value = `# Python代码模板
def main():
    print("这是一个Python程序模板")
    
    # 在这里添加你的代码
    for i in range(5):
        print(f"第{i+1}次循环")
    
    return "程序执行完成"

if __name__ == "__main__":
    result = main()
    print(result)`
  ElMessage.success('已加载Python模板')
}

const loadJavaScriptTemplate = () => {
  templateCode.value = `// JavaScript代码模板
function main() {
    console.log("这是一个JavaScript程序模板");
    
    // 在这里添加你的代码
    for (let i = 0; i < 5; i++) {
        console.log(\`第\${i+1}次循环\`);
    }
    
    return "程序执行完成";
}

// 执行主函数
const result = main();
console.log(result);`
  ElMessage.success('已加载JavaScript模板')
}

const loadJavaTemplate = () => {
  templateCode.value = `// Java代码模板
public class Main {
    public static void main(String[] args) {
        System.out.println("这是一个Java程序模板");
        
        // 在这里添加你的代码
        for (int i = 0; i < 5; i++) {
            System.out.println("第" + (i+1) + "次循环");
        }
        
        System.out.println("程序执行完成");
    }
}`
  ElMessage.success('已加载Java模板')
}
</script>

<style scoped>
.code-editor-example {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.programming-question {
  margin: 20px 0;
}

.question-content {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 15px;
}

.question-content h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.question-content p {
  margin: 0 0 15px 0;
  color: #666;
}

.expected-output {
  background: #e9ecef;
  padding: 10px;
  border-radius: 4px;
}

.expected-output pre {
  margin: 5px 0 0 0;
  color: #495057;
}

.code-templates {
  margin: 15px 0;
  display: flex;
  gap: 10px;
}

.code-templates .el-button {
  margin-right: 10px;
}
</style> 