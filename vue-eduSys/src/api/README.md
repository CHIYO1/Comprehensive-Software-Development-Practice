# API 接口文档

## 课程管理相关接口

### 课程基础操作

#### 1. 新增课程
```javascript
import { addCourse } from '@/api'

// 新增教师课程
const createCourse = async () => {
  try {
    const response = await addCourse()
    console.log('新增的课程ID:', response.data)
    return response.data
  } catch (error) {
    console.error('新增课程失败:', error)
  }
}
```

#### 2. 更新课程
```javascript
import { updateCourse } from '@/api'

// 更新课程信息
const updateCourseInfo = async (courseData, coverFile) => {
  try {
    const response = await updateCourse({
      courseId: 1,
      courseName: 'Vue.js进阶课程',
      courseDesc: '深入学习Vue.js的高级特性',
      coverPath: '/covers/vue-advanced.jpg',
      userId: 123
    }, coverFile)
    console.log('课程更新成功:', response)
  } catch (error) {
    console.error('更新课程失败:', error)
  }
}

// 使用示例
const handleCourseUpdate = (event) => {
  const coverFile = event.target.files[0]
  if (coverFile) {
    updateCourseInfo(courseData, coverFile)
  }
}
```

#### 3. 查询课程详情
```javascript
import { queryCourseDetail } from '@/api'

// 查询单个课程详情
const getCourseDetail = async (courseId) => {
  try {
    const response = await queryCourseDetail(courseId)
    console.log('课程详情:', response.data)
    return response.data
  } catch (error) {
    console.error('查询课程详情失败:', error)
  }
}
```

#### 4. 查询教师课程列表
```javascript
import { queryCourseByTeacherId } from '@/api'

// 查询当前登录教师的所有课程
const getTeacherCourses = async () => {
  try {
    const response = await queryCourseByTeacherId()
    console.log('教师课程列表:', response.data)
    return response.data
  } catch (error) {
    console.error('查询教师课程失败:', error)
  }
}
```

#### 5. 查询所有课程
```javascript
import { queryAllCourse } from '@/api'

// 查询所有课程基础信息
const getAllCourses = async () => {
  try {
    const response = await queryAllCourse()
    console.log('所有课程:', response.data)
    return response.data
  } catch (error) {
    console.error('查询所有课程失败:', error)
  }
}
```

#### 6. 学生加入课程
```javascript
import { joinCourse } from '@/api'

// 学生加入课程
const studentJoinCourse = async (courseId) => {
  try {
    const response = await joinCourse(courseId)
    console.log('加入课程成功:', response.data)
    return response.data
  } catch (error) {
    console.error('加入课程失败:', error)
  }
}
```

#### 7. 删除课程
```javascript
import { deleteCourse } from '@/api'

// 删除课程
const removeCourse = async (courseId) => {
  try {
    const response = await deleteCourse(courseId)
    console.log('课程删除成功:', response)
  } catch (error) {
    console.error('删除课程失败:', error)
  }
}
```

#### 8. 获取课程上下文信息
```javascript
import { queryCourseDetailStr } from '@/api'

// 通过章节ID获取课程上下文
const getCourseContext = async (sectionId) => {
  try {
    const response = await queryCourseDetailStr(sectionId)
    console.log('课程上下文:', response.data)
    return response.data
  } catch (error) {
    console.error('获取课程上下文失败:', error)
  }
}
```

## 学习助手相关接口

### 对话管理

#### 1. 获取所有对话
```javascript
import { getAllConv } from '@/api'

// 获取所有对话
const getConversations = async () => {
  try {
    const response = await getAllConv()
    console.log('对话列表:', response.data)
    return response.data
  } catch (error) {
    console.error('获取对话列表失败:', error)
  }
}
```

#### 2. 设置对话激活状态
```javascript
import { setConvsActivate } from '@/api'

// 设置对话激活
const activateConversation = async (convId) => {
  try {
    const response = await setConvsActivate(convId)
    console.log('设置激活状态成功:', response)
  } catch (error) {
    console.error('设置激活状态失败:', error)
  }
}
```

#### 3. 反转激活状态
```javascript
import { invertActivate } from '@/api'

// 反转激活状态
const toggleActivate = async (convId) => {
  try {
    const response = await invertActivate(convId)
    console.log('反转激活状态成功:', response)
  } catch (error) {
    console.error('反转激活状态失败:', error)
  }
}
```

#### 4. 删除对话
```javascript
import { deleteConvs } from '@/api'

// 删除对话
const removeConversation = async (convId) => {
  try {
    const response = await deleteConvs(convId)
    console.log('删除对话成功:', response)
  } catch (error) {
    console.error('删除对话失败:', error)
  }
}
```

#### 5. 聊天接口
```javascript
import { chat } from '@/api'

// 发送聊天消息
const sendChatMessage = async (message) => {
  try {
    const response = await chat(message)
    console.log('聊天响应:', response.data)
    return response.data
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}

// 使用示例
const handleChat = async () => {
  const message = '请解释Vue.js的响应式原理'
  const response = await sendChatMessage(message)
  // 处理响应
}
```

## 试题管理相关接口

### 试题集管理

#### 1. 查询试题集列表
```javascript
import { querySets } from '@/api'

// 查询当前登录教师名下的所有试题集
const getQuestionSets = async () => {
  try {
    const response = await querySets()
    console.log('试题集列表:', response.data)
    return response.data
  } catch (error) {
    console.error('查询试题集失败:', error)
  }
}
```

#### 2. 新增试题集
```javascript
import { addSet } from '@/api'

// 新增一个空试题集
const createQuestionSet = async () => {
  try {
    const response = await addSet()
    console.log('新增的试题集ID:', response.data)
    return response.data
  } catch (error) {
    console.error('新增试题集失败:', error)
  }
}
```

#### 3. 查询单个试题集
```javascript
import { querySet } from '@/api'

// 根据ID查询试题集详情
const getQuestionSet = async (setId) => {
  try {
    const response = await querySet(setId)
    console.log('试题集详情:', response.data)
    return response.data
  } catch (error) {
    console.error('查询试题集详情失败:', error)
  }
}
```

#### 4. 更新试题集
```javascript
import { updateSet } from '@/api'

// 更新试题集信息
const updateQuestionSet = async (questionSet) => {
  try {
    const response = await updateSet({
      setId: 1,
      setName: 'Vue.js基础练习',
      setDesc: '包含Vue.js基础知识的练习题',
      userId: 123
    })
    console.log('更新成功:', response)
  } catch (error) {
    console.error('更新试题集失败:', error)
  }
}
```

#### 5. 删除试题集
```javascript
import { deleteSet } from '@/api'

// 删除试题集
const removeQuestionSet = async (setId) => {
  try {
    const response = await deleteSet(setId)
    console.log('删除成功:', response)
  } catch (error) {
    console.error('删除试题集失败:', error)
  }
}
```

### 题目管理

#### 1. 查询题目列表
```javascript
import { queryQuestions } from '@/api'

// 查询试题集下的所有题目
const getQuestions = async (setId) => {
  try {
    const response = await queryQuestions(setId)
    console.log('题目列表:', response.data)
    return response.data
  } catch (error) {
    console.error('查询题目失败:', error)
  }
}
```

#### 2. 新增题目
```javascript
import { addQuestion } from '@/api'

// 新建空题目
const createQuestion = async (setId, questionType) => {
  try {
    const response = await addQuestion(setId, questionType)
    console.log('新建的题目:', response.data)
    return response.data
  } catch (error) {
    console.error('新建题目失败:', error)
  }
}
```

#### 3. 更新题目
```javascript
import { updateQuestion } from '@/api'

// 更新题目信息
const updateQuestionInfo = async (questionDTO) => {
  try {
    const response = await updateQuestion({
      questionId: 'q123',
      questionStem: '什么是Vue.js？',
      questionAnswer: 'Vue.js是一个渐进式JavaScript框架',
      questionParse: 'Vue.js用于构建用户界面',
      codeOutput: '',
      questionType: 'SINGLE_CHOICE',
      setId: 'set123',
      choiceList: ['选项A', '选项B', '选项C', '选项D'],
      knowledgeList: ['Vue.js基础'],
      editable: true
    })
    console.log('更新成功:', response)
  } catch (error) {
    console.error('更新题目失败:', error)
  }
}
```

#### 4. 删除题目
```javascript
import { deleteQuestion } from '@/api'

// 删除题目
const removeQuestion = async (questionId) => {
  try {
    const response = await deleteQuestion(questionId)
    console.log('删除成功:', response)
  } catch (error) {
    console.error('删除题目失败:', error)
  }
}
```

## 文件管理相关接口

### 视频管理

#### 1. 上传视频
```javascript
import { uploadVideo } from '@/api'

// 上传视频文件
const uploadVideoFile = async (videoFile, coverFile) => {
  try {
    const response = await uploadVideo(videoFile, coverFile)
    console.log('视频上传成功:', response.data)
    return response.data
  } catch (error) {
    console.error('视频上传失败:', error)
  }
}

// 使用示例
const handleVideoUpload = (event) => {
  const videoFile = event.target.files[0]
  const coverFile = document.getElementById('cover').files[0]
  if (videoFile && coverFile) {
    uploadVideoFile(videoFile, coverFile)
  }
}
```

#### 2. 查询视频列表
```javascript
import { queryTeacherVideos } from '@/api'

// 查询教师上传的视频列表
const getVideoList = async () => {
  try {
    const response = await queryTeacherVideos()
    console.log('视频列表:', response.data)
    return response.data
  } catch (error) {
    console.error('查询视频列表失败:', error)
  }
}
```

#### 3. 删除视频
```javascript
import { deleteVideo } from '@/api'

// 删除视频
const removeVideo = async (videoId) => {
  try {
    const response = await deleteVideo(videoId)
    console.log('视频删除成功:', response)
  } catch (error) {
    console.error('视频删除失败:', error)
  }
}
```

### 文档管理

#### 1. 上传文档
```javascript
import { uploadDocument } from '@/api'

// 上传文档文件
const uploadDocumentFile = async (documentFile, documentDesc) => {
  try {
    const response = await uploadDocument(documentFile, documentDesc)
    console.log('文档上传成功:', response.data)
    return response.data
  } catch (error) {
    console.error('文档上传失败:', error)
  }
}

// 使用示例
const handleDocumentUpload = (event) => {
  const documentFile = event.target.files[0]
  const documentDesc = 'Vue.js基础教程文档'
  if (documentFile) {
    uploadDocumentFile(documentFile, documentDesc)
  }
}
```

#### 2. 查询文档列表
```javascript
import { queryTeacherDocuments } from '@/api'

// 查询教师上传的文档列表
const getDocumentList = async () => {
  try {
    const response = await queryTeacherDocuments()
    console.log('文档列表:', response.data)
    return response.data
  } catch (error) {
    console.error('查询文档列表失败:', error)
  }
}
```

#### 3. 删除文档
```javascript
import { deleteDocument } from '@/api'

// 删除文档
const removeDocument = async (documentId) => {
  try {
    const response = await deleteDocument(documentId)
    console.log('文档删除成功:', response)
  } catch (error) {
    console.error('文档删除失败:', error)
  }
}
```

### 文件查询

```javascript
import { queryFile } from '@/api'

// 查询文件信息
const getFileInfo = async (fileId, fileType) => {
  try {
    const response = await queryFile({
      fileId: fileId,
      fileType: fileType,
      fileName: '',
      fileUrl: ''
    })
    console.log('文件信息:', response.data)
    return response.data
  } catch (error) {
    console.error('查询文件失败:', error)
  }
}
```

## 做题相关接口

### 代码执行

```javascript
import { runCode } from '@/api'

// 执行Python代码
const executeCode = async (code) => {
  try {
    const response = await runCode(code)
    console.log('代码执行结果:', response.data)
    return response.data
  } catch (error) {
    console.error('代码执行失败:', error)
  }
}

// 使用示例
const pythonCode = `
print("Hello, World!")
x = 10
y = 20
print(f"x + y = {x + y}")
`
executeCode(pythonCode)
```

### 做题记录管理

#### 1. 创建做题记录
```javascript
import { addRecordSet } from '@/api'

// 创建新的做题记录
const createRecordSet = async (recordSetData) => {
  try {
    const response = await addRecordSet({
      setRecordId: 0,
      setScore: 0,
      setId: 1,
      subsectionId: 1,
      userId: 123,
      state: 'in_progress'
    })
    console.log('做题记录创建成功:', response.data)
    return response.data
  } catch (error) {
    console.error('创建做题记录失败:', error)
  }
}
```

#### 2. 查询做题记录
```javascript
import { queryRecordSet } from '@/api'

// 查询特定做题记录
const getRecordSet = async (recordId) => {
  try {
    const response = await queryRecordSet(recordId)
    console.log('做题记录:', response.data)
    return response.data
  } catch (error) {
    console.error('查询做题记录失败:', error)
  }
}
```

#### 3. 查询小节做题记录
```javascript
import { queryRecordSetBySubsection } from '@/api'

// 查询当前用户特定小节的做题记录
const getRecordSetBySubsection = async (subsectionId) => {
  try {
    const response = await queryRecordSetBySubsection(subsectionId)
    console.log('小节做题记录:', response.data)
    return response.data
  } catch (error) {
    console.error('查询小节做题记录失败:', error)
  }
}
```

## GPT相关接口

### 1. 试题生成
```javascript
import { questionGenerate } from '@/api'

// 使用GPT生成试题
const generateQuestion = async () => {
  try {
    const response = await questionGenerate({
      questionDTO: {
        questionId: 'q123',
        questionStem: '请解释Vue.js的响应式原理',
        questionAnswer: '',
        questionParse: '',
        codeOutput: '',
        questionType: 'SINGLE_CHOICE',
        setId: 'set123',
        choiceList: [],
        knowledgeList: ['Vue.js响应式'],
        editable: true
      },
      demand: '生成一道关于Vue.js响应式原理的选择题'
    })
    console.log('生成的题目:', response.data)
    return response.data
  } catch (error) {
    console.error('生成题目失败:', error)
  }
}
```

### 2. 智能打分
```javascript
import { gradeAnswers } from '@/api'

// 智能打分
const gradeStudentAnswers = async (recordQuestions) => {
  try {
    const response = await gradeAnswers([
      {
        questionRecordId: 1,
        questionScore: 0,
        studentAnswer: 'Vue.js是一个前端框架',
        scoreParse: '',
        errorParse: '',
        questionId: 1,
        setRecordId: 1
      }
    ])
    console.log('打分完成:', response)
  } catch (error) {
    console.error('打分失败:', error)
  }
}
```

### 3. 文档生成
```javascript
import { documentGenerate } from '@/api'

// 生成文档
const generateDocument = async () => {
  try {
    const response = await documentGenerate({
      opMode: 'add',
      courseDetailStr: 'Vue.js课程详情',
      demand: '生成Vue.js基础教程文档',
      selectedText: '',
      beforeText: '',
      afterText: ''
    })
    console.log('生成的文档:', response.data)
    return response.data
  } catch (error) {
    console.error('生成文档失败:', error)
  }
}
```

### 4. 课程架构生成
```javascript
import { courseGenerate } from '@/api'

// 根据文件生成课程架构
const generateCourseStructure = async (demand, courseId, file) => {
  try {
    const response = await courseGenerate(demand, courseId, file)
    console.log('生成的课程架构:', response.data)
    return response.data
  } catch (error) {
    console.error('生成课程架构失败:', error)
  }
}

// 使用示例
const handleFileUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    generateCourseStructure(
      '生成Vue.js前端开发课程',
      'course123',
      file
    )
  }
}
```

## 学习助手相关接口

### 对话管理

#### 1. 获取所有对话
```javascript
import { getAllConv } from '@/api'

// 获取所有对话
const getConversations = async () => {
  try {
    const response = await getAllConv()
    console.log('对话列表:', response.data)
    return response.data
  } catch (error) {
    console.error('获取对话列表失败:', error)
  }
}
```

#### 2. 设置对话激活状态
```javascript
import { setConvsActivate } from '@/api'

// 设置对话激活
const activateConversation = async (convId) => {
  try {
    const response = await setConvsActivate(convId)
    console.log('设置激活状态成功:', response)
  } catch (error) {
    console.error('设置激活状态失败:', error)
  }
}
```

#### 3. 反转激活状态
```javascript
import { invertActivate } from '@/api'

// 反转激活状态
const toggleActivate = async (convId) => {
  try {
    const response = await invertActivate(convId)
    console.log('反转激活状态成功:', response)
  } catch (error) {
    console.error('反转激活状态失败:', error)
  }
}
```

#### 4. 删除对话
```javascript
import { deleteConvs } from '@/api'

// 删除对话
const removeConversation = async (convId) => {
  try {
    const response = await deleteConvs(convId)
    console.log('删除对话成功:', response)
  } catch (error) {
    console.error('删除对话失败:', error)
  }
}
```

#### 5. 聊天接口
```javascript
import { chat } from '@/api'

// 发送聊天消息
const sendChatMessage = async (message) => {
  try {
    const response = await chat(message)
    console.log('聊天响应:', response.data)
    return response.data
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}

// 使用示例
const handleChat = async () => {
  const message = '请解释Vue.js的响应式原理'
  const response = await sendChatMessage(message)
  // 处理响应
}
```

## 注意事项

1. 所有API都返回Promise，建议使用async/await或.then()处理
2. 错误处理：建议在每个API调用中添加try-catch错误处理
3. 文件上传：uploadVideo、uploadDocument、updateCourse、courseGenerate接口需要处理文件上传，使用FormData格式
4. 参数验证：调用API前请确保参数格式正确
5. 权限控制：某些接口可能需要登录权限，请确保用户已登录
6. 文件大小限制：上传文件时注意服务器对文件大小的限制
7. 支持的格式：确保上传的文件格式符合系统要求
8. 聊天接口：chat接口使用GET请求，注意URL编码
9. 课程管理：课程相关操作需要确保用户有相应权限 