<template>
  <el-row>
    <el-col :span="8">
      <el-card style="height: 660px;">
        <template #header>
          <div class="card-header">
            <span>文档智能生成/修改</span>
          </div>
        </template>
        <!-- 对话框 -->
        <el-scrollbar class="message-area" ref="scrollbar">
          <div>
            <!-- 对话气泡 -->
            <div v-for="conv in convs" :key="conv.convId">
              <div v-if="conv.convUser != ''" :class="['message-bubble', 'user']">
                <div class="message-content">{{ conv.convUser }}</div>
              </div>
              <div v-if="conv.convGpt != ''" :class="['message-bubble', 'gpt']">
                <div class="message-content">{{ conv.convGpt }}</div>
              </div>
            </div>

            <el-row v-if="ifLoading">
              <el-col :class="['message-bubble', 'gpt']">
                <span class="message-content">{{generateMode=='add'?'文档生成中':'文档修改中'}}</span>
                <el-icon class="loading-icon" :size="20">
                  <Loading />
                </el-icon>
              </el-col>
            </el-row>
          </div>
        </el-scrollbar>

        <template #footer>
          <!-- 输入框 -->
          <div class="input-wrapper">
            <el-input v-model="userInput" :placeholder="generateMode == 'add' ? '请输入要生成的文档类型和要求' : '请在输入修改要求后再框选待修改的文本段'"
              @keydown.enter="sendMessage" type="textarea" :rows="3" resize="none" class="custom-input">
            </el-input>
            <el-radio-group v-model="generateMode" size="small" text-color="#626aef" fill="rgb(239, 240, 253)">
              <el-tooltip class="box-item" effect="dark" content="重新生成一段文本" placement="top">
                <el-radio-button label="生成" value="add" />
              </el-tooltip>
              <el-tooltip class="box-item" effect="dark" content="局部修改被选择的文本内容" placement="top">
                <el-radio-button label="修改" value="edit" />
              </el-tooltip>
            </el-radio-group>
            <el-tooltip class="box-item" effect="dark" content="发送" placement="top">
              <el-button class="send-btn" :disabled="!userInput.trim()" @click="sendMessage" :icon="Position" circle />
            </el-tooltip>
          </div>
        </template>
      </el-card>
    </el-col>
    <el-col :span="16">
      <!-- 基于Vditor的Markdown编辑器 -->
      <div ref="editorRef"></div>
    </el-col>
  </el-row>
</template>

<script setup>
import { onMounted, ref, onBeforeUnmount, } from 'vue'
import { useRoute } from 'vue-router'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { Loading, Position } from '@element-plus/icons-vue'
import { fetchStreamResponse } from '@/utils/fetchStream'
import { ElMessage } from 'element-plus'
import request from '@/utils/request.js'
const route = useRoute()
const generateMode = ref('add')


const ifLoading = ref(false)  //是否正在加载
const convs = ref([   //对话记录
  // {
  //   "convGpt": '',
  //   "convUser": ''
  // }
])
const userInput = ref('')
const courseDetailStr = ref('');  //描述课程信息及章节信息的上下文字符串
const editorRef = ref(null)
let vditorInstance = null
onMounted(async () => {
  const sectionId = route.query.sectionId;
  if (sectionId) {//仅在课程编辑页面才可获取课程信息上下文
    const response = await request.post('/course/queryCourseDetailStr', sectionId)
    courseDetailStr.value = response.data.data
  }
  //vditor实例配置
  vditorInstance = new Vditor(editorRef.value, {
    preview: {
      markdown: {
        mark: true,      // 启用mark标记
        mermaid: true,   // 启用Mermaid图表
      }
    },
    autoMatch: false,// 禁用Markdown语法补全
    export: {
      pdf: true,
    },
    height: 666,
    placeholder: "生成的文档内容将在此处显示...",
    value: '',
    cache: {
      enable: false
    },
    // input(value) {
    //   //  console.log('input 事件触发');
    // },
    after() {
      // 编辑器初始化完成后的回调
    }
  })
});
onBeforeUnmount(() => {
  if (vditorInstance) {
    vditorInstance.destroy()
    vditorInstance = null
  }
})

// 消息发送
const sendMessage = async () => {
  ifLoading.value = true;
  // vditorInstance.disabled();
  if (generateMode.value == 'edit') {//编辑模式
    convs.value.push({
    "convUser": userInput.value+"：\n"+vditorInstance.getSelection(),
    "convGpt": "",
  })
    await editDoc();
  } else if (generateMode.value == 'add') { //生成模式
    convs.value.push({
    "convUser": userInput.value,
    "convGpt": "",
  })
    await addDoc();
    convs.value[convs.value.length - 1].convGpt = "文档生成完成。"
  }
  ifLoading.value = false;
  // vditorInstance.enable();
}
// 从头生成文档
const addDoc = async () => {
  let formData = {
    "opMode": 'add',
    "courseDetailStr": courseDetailStr.value,
    "demand": userInput.value,
    // "selectedText": "",
    // "beforeText": "",
    // "afterText": ""
  }
  userInput.value = '';
  try {
    let tempContent = '';
    await fetchStreamResponse("/gpt/documentGenerate",
      formData,
      (data) => {
        // 处理接收到的数据片段
        tempContent += data;
        vditorInstance.setValue(tempContent);
      },
      () => {
        // 流式响应完成
        ElMessage.success("生成完成")
      },
      (error) => {
        // 错误处理
        ElMessage.error("数据接收出错：" + error);
      }
    )
  } catch (err) {
    ElMessage.error("未知错误:");
    console.log("错误：" + err);
  }
}
// 局部修改文档内容
const editDoc = async () => {
  const selectionText = vditorInstance.getSelection() //获取鼠标框选内容
  console.log("selected:", selectionText);
  if (!selectionText || selectionText == '') {
    ElMessage.warning("请框选要修改的文本");
    return
  }
  //文本处理
  const originText = vditorInstance.getValue();
  vditorInstance.deleteValue() // 删除选中内容(input回调方法延迟触发)
  const deletedText = vditorInstance.getValue();
  // console.log("删除后:",docContent.value);

  // 获取选中文本的上下文
  const minlength = deletedText.length;
  let i = 0;
  while (i < minlength && originText[i] === deletedText[i]) {
    i++;
  }
  const beforeText = deletedText.substring(0, i)
  const afterText = deletedText.substring(i)

  let formData = {
    "opMode": 'edit',
    "courseDetailStr": courseDetailStr.value,
    "demand": userInput.value,
    "selectedText": selectionText,
    "beforeText": beforeText,
    "afterText": afterText
  }
  userInput.value = '';
  try {
    // let tempContent = '';
    // vditorInstance.setValue(beforeText + '<br><div name="选中" style="background:#eef4ff;"><br>'+selectionText+'<br><div style="background:white"><br>' + afterText);
    await fetchStreamResponse("/gpt/documentGenerate",
      formData,
      (data) => {
        // 处理接收到的数据片段
        // tempContent += data;
        convs.value[convs.value.length - 1].convGpt+=data;
      },
      () => {
        // 流式响应完成
        vditorInstance.setValue(beforeText + convs.value[convs.value.length - 1].convGpt + afterText);
        ElMessage.success("修改完成")
      },
      (error) => {
        // 错误处理
        ElMessage.error("数据接收出错：" + error);
      }
    )
  } catch (err) {
    ElMessage.error("未知错误:");
    console.log("错误：" + err);
  }

}
</script>

<style scoped>
/* 加载图标 */
.loading-icon {
  animation: rotating 1.5s linear infinite;
  margin-left: 5px
}

/* 对话气泡 */
.message-area {
  flex: 1;
  height: 400px;
  padding: 3px;
  overflow-y: auto;
}

.message-bubble {
  margin: 16px 0;
  padding: 12px;
  border-radius: 12px;
  position: relative;

}

.message-bubble.gpt {
  max-width: 90%;
  background: #f7f6f6;
  border: 1px solid #ebeef5;
  margin-right: auto;
}

.message-bubble.user {
  max-width: 60%;
  background: #409EFF;
  color: white;
  margin-left: auto;
}

.message-content {
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

/* 输入框容器 */
.input-wrapper {
  position: relative;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.custom-input {
  width: 100%;
}

.custom-input :deep(.el-textarea__inner) {
  border: none;
  background-color: transparent;
  box-shadow: none;
  padding: 0;
  font-size: 14px;
  line-height: 1.5;
}


/* 发送按钮样式 */
.send-btn {
  position: absolute;
  right: 8px;
  bottom: 8px;
  padding: 8px;
  background: #409EFF;
  color: white;
  border: none;
}

.send-btn:hover {
  background: #66b1ff;
}

.send-btn:active {
  background: #3a8ee6;
}

.send-btn.is-disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}
</style>