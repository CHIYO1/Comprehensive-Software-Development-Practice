import request from '@/utils/request.js'

/**
 * 试题生成
 * 参考输入的试题非空信息，补充完整其它空字段
 * @param {Object} requestData - 请求数据
 * @param {Object} requestData.questionDTO - 题目信息
 * @param {string} requestData.questionDTO.questionId - 题目ID
 * @param {string} requestData.questionDTO.questionStem - 题目标干
 * @param {string} requestData.questionDTO.questionAnswer - 题目答案
 * @param {string} requestData.questionDTO.questionParse - 题目解析
 * @param {string} requestData.questionDTO.codeOutput - 代码输出
 * @param {string} requestData.questionDTO.questionType - 题目类型
 * @param {string} requestData.questionDTO.setId - 试题集ID
 * @param {Array} requestData.questionDTO.choiceList - 选项列表
 * @param {Array} requestData.questionDTO.knowledgeList - 知识点列表
 * @param {boolean} requestData.questionDTO.editable - 是否可编辑
 * @param {string} requestData.demand - 需求描述
 * @returns {Promise} 生成的题目信息
 */
export const questionGenerate = (requestData) => {
  return request.post('/gpt/questionGenerate', requestData)
}

/**
 * 给做题记录对应的试题集下的多道题目答案智能打分
 * @param {Array} recordQuestions - 做题记录列表
 * @param {Object} recordQuestions[].questionRecordId - 题目记录ID
 * @param {number} recordQuestions[].questionScore - 题目分数
 * @param {string} recordQuestions[].studentAnswer - 学生答案
 * @param {string} recordQuestions[].scoreParse - 评分解析
 * @param {string} recordQuestions[].errorParse - 错误解析
 * @param {number} recordQuestions[].questionId - 题目ID
 * @param {number} recordQuestions[].setRecordId - 试题集记录ID
 * @returns {Promise} 打分结果
 */
export const gradeAnswers = (recordQuestions) => {
  return request.post('/gpt/gradeAnswers', recordQuestions)
}

/**
 * 文档生成
 * 流式文档生成
 * @param {Object} requestData - 请求数据
 * @param {string} requestData.opMode - 操作模式：add或edit
 * @param {string} requestData.courseDetailStr - 课程详情字符串
 * @param {string} requestData.demand - 需求描述
 * @param {string} requestData.selectedText - 选中的文本
 * @param {string} requestData.beforeText - 前文
 * @param {string} requestData.afterText - 后文
 * @returns {Promise} 生成的文档内容
 */
export const documentGenerate = (requestData) => {
  return request.post('/gpt/documentGenerate', requestData)
}

/**
 * 根据课程提纲文件（doc、pdf）及用户要求生成课程架构
 * 包括课程名、描述，章节名、描述和知识点。不包括小节
 * @param {string} demand - 需求描述
 * @param {string} courseId - 课程ID
 * @param {File} file - 课程提纲文件
 * @returns {Promise} 生成的课程架构
 */
export const courseGenerate = (demand, courseId, file) => {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post(`/gpt/courseGenerate?demand=${demand}&courseId=${courseId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 