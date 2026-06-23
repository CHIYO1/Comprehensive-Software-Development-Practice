// 硅基流动API配置
const SILICON_FLOW_API = 'https://api.siliconflow.cn/v1/chat/completions'
const API_KEY = 'sk-yjxjlqaahmzoqxgaojczxaoiskvknptvkxhncmuboiibidaz' // 替换成你的实际API密钥

// 生成课程计划
export const generateCoursePlan = async (params) => {
    const { courseName, description, subject, difficulty, totalHours, objectives, demand, outline } = params
    
    // 构建详细的提示词
    const systemPrompt = `你是一位资深的教育专家和课程设计师，拥有20年的教学经验。请根据用户提供的课程信息，生成一份详细、专业、实用的课程计划。`

    const userPrompt = `
请根据以下课程信息，生成一份详细的课程计划：

课程名称：${courseName || '未命名课程'}
课程描述：${description || '无'}
学科类型：${subject || '未指定'}
难度等级：${difficulty || 3}/5
总课时：${totalHours || 32} 课时
教学目标：${(objectives && objectives.length > 0) ? objectives.join('、') : '未指定'}
教学需求：${demand || '无特殊需求'}
${outline ? `现有课程大纲：${outline}` : ''}

请生成一份完整的课程计划，包含以下内容：

1. 课程总览（课程定位、教学目标、适用对象、先修要求）
2. 详细的教学大纲（按章节/模块划分，每章节包含：标题、课时、教学目标、核心知识点、教学方法建议）
3. 每周/每节课的详细安排（具体到每节课的教学内容、活动安排、作业布置）
4. 教学资源推荐（推荐教材、参考书、在线资源、工具软件）
5. 评估方案（过程性评估、终结性评估的详细方案和评分标准）
6. 教学策略建议（针对不同教学内容的建议教学方法）
7. 注意事项（常见问题、应急方案）

请确保课程计划：
- 结构清晰，层次分明
- 内容专业，符合学科特点
- 实用性强，可直接用于教学
- 考虑学生的认知规律和学习特点
- 包含具体的教学活动和互动环节

请以Markdown格式输出，使用标题、列表、表格等格式让内容更加清晰易读。`

    try {
        console.log(userPrompt)
        const response = await fetch(SILICON_FLOW_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${API_KEY}`
            },
            body: JSON.stringify({
                model: 'zai-org/GLM-5.2',
                //zai-org/GLM-5.2
                //Qwen/Qwen2.5-72B-Instruct
                messages: [
                    { role: 'system', content: systemPrompt },
                    { role: 'user', content: userPrompt }
                ],
                temperature: 0.7,
                max_tokens: 8192,
                top_p: 0.9,
                frequency_penalty: 0.1,
                presence_penalty: 0.1,
                stream: false
            })
        })

        if (!response.ok) {
            const errorData = await response.json()
            throw new Error(errorData.error?.message || 'API请求失败')
        }

        const data = await response.json()
        return {
            success: true,
            content: data.choices[0].message.content
        }
    } catch (error) {
        console.error('生成课程计划失败:', error)
        return {
            success: false,
            error: error.message
        }
    }
}


// 生成练习题
export const generateExercises = async (params) => {
    const { questionTypes, difficulty, count, knowledgePoints, requirements, courseName, chapterName } = params
    
    const systemPrompt = `你是 eduSys 教学实训平台的出题助手。
请根据用户需求生成练习题，以 Markdown 格式输出。
题目应适合高校软件实践/计算机课程教学，避免偏题、重复和含糊表述。

输出格式要求：
1. 使用 ## 作为每道题的标题（如 "## 第1题"）
2. 选择题选项使用列表格式（- A. xxx / - B. xxx）
3. 答案使用 **答案：** 格式标注
4. 解析使用 **解析：** 格式标注
5. 知识点使用 **知识点：** 格式标注
6. 不同题型之间用 --- 分隔

请确保输出格式清晰、规范、易读。`

    // 处理题型映射
    const typeMap = {
        'choice': '选择题',
        'fill': '填空题',
        'judge': '判断题',
        'short': '简答题',
        'programming': '编程题'
    }
    
    const knowledgeText = (knowledgePoints && knowledgePoints.length > 0) 
        ? knowledgePoints.join('、') 
        : '由模型根据章节主题合理提取'
    
    const extraText = requirements || '无特殊要求'
    const typeNames = questionTypes.map(t => typeMap[t] || t).join('、')
    const difficultyMap = {
        'easy': '简单',
        'medium': '中等',
        'hard': '困难',
        'mixed': '混合难度'
    }

    const userPrompt = `
请生成 ${count} 道练习题，要求如下：

- 课程名称：${courseName || '未命名课程'}
- 章节名称：${chapterName || '未指定章节'}
- 题目类型：${typeNames}
- 难度等级：${difficultyMap[difficulty] || '中等'}
- 知识点：${knowledgeText}
- 额外要求：${extraText}

请以 Markdown 格式输出，每道题包含：题目标号、题干、选项（如有）、答案、解析、知识点。

输出示例：
## 第1题
题干内容

A. 选项A
B. 选项B
C. 选项C
D. 选项D

**答案：** B
**解析：** 这是解析内容
**知识点：** 知识点名称

---
## 第2题
题干内容

**答案：** xxx
**解析：** 这是解析内容
**知识点：** 知识点名称
`

    try {
        const response = await fetch(SILICON_FLOW_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${API_KEY}`
            },
            body: JSON.stringify({
                model: 'zai-org/GLM-5.2',
                messages: [
                    { role: 'system', content: systemPrompt },
                    { role: 'user', content: userPrompt }
                ],
                temperature: 0.7,
                max_tokens: 8192,
                top_p: 0.9,
                frequency_penalty: 0.1,
                presence_penalty: 0.1,
                stream: false
            })
        })

        if (!response.ok) {
            const errorData = await response.json()
            throw new Error(errorData.error?.message || 'API请求失败')
        }

        const data = await response.json()
        const content = data.choices[0].message.content
        
        return {
            success: true,
            content: content
        }
    } catch (error) {
        console.error('生成题目失败:', error)
        return {
            success: false,
            error: error.message
        }
    }
}