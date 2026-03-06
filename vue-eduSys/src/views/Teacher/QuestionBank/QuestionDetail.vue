<template>
    <el-container style="height:666px;justify-self: center; width: 90%;">
        <!-- 侧边锚点跳转 -->
        <el-aside style="width:20%;height: 100%;margin-top: 0px;display: flex;justify-content: end;">
            <el-card style="height: 99%;width: 220px;">
                <h1>试题集编辑</h1>
                <el-anchor :container="mainRef" direction="vertical" type="default" :offset="30"
                    @click="handleAnchorClick">
                    <el-anchor-link style="font-weight: bold;" href="#top-card" title="试题集信息" />
                    <el-popover v-for="(question, index) in questionsData" :key="question.questionId"
                        placement="left-start" :width="200" trigger="hover" :content="question.questionStem">
                        <template #reference>
                            <el-anchor-link style="margin-left: 10px;" :href="'#question' + question.questionId"
                                :title="`[${question.questionType.slice(0, 2)}] ${index + 1}、${truncatedContent(question.questionStem, 10)}`" />
                        </template>
                    </el-popover>
                    <el-anchor-link style="font-weight: bold;" href="#addNewQuestion" title="新增题目" />
                </el-anchor>
            </el-card>
        </el-aside>
        <el-main style=" margin-left: 1px;height: 100%; width: 80%;">
            <!-- 试题集信息 -->
            <div ref="mainRef" style="height: 100%;overflow: auto;">
                <el-card id="top-card" style="width: 80%;align-content: center;height: 150px;margin-bottom: 20px;">
                    <el-form ref="setFormRef" :model="formSet" :inline="true" label-width="auto">
                        <el-form-item label="试题集名:" prop="setName" :rules="[
                            { required: true, message: '请填写试题集名' }]">
                            <el-input v-model="formSet.setName" style="width: 200px;" placeholder="请填写试题集名" />
                        </el-form-item>
                        <el-form-item>
                            <el-button style="margin-left: 170px;" type="success" @click="updateSet()"
                                plain>保存并返回</el-button>
                        </el-form-item>
                        <el-form-item label="试题集描述:" placeholder="请填写试题集描述">
                            <el-input v-model="formSet.setDesc" type="textarea" style="width: 500px;" />
                        </el-form-item>
                    </el-form>
                </el-card>
                <!-- 题目列表 -->
                <TransitionGroup name="fade">
                    <div :id="'question' + question.questionId" class="questionDiv"
                        v-for="(question, index) in questionsData" :key="question.questionId"
                        v-loading="loadingStates[question.questionId]">
                        <!-- 头部信息 -->
                        <el-row>
                            <el-col :span="4">
                                {{ index + 1 }}、 <el-tag :type="typeMap[question.questionType]">{{
                                    question.questionType
                                    }}</el-tag>
                            </el-col>
                            <el-col :span="8">
                            </el-col>
                            <el-col :span="3" :offset="3" v-if="question.editable">
                                <el-popover placement="top" title="将根据已填写的信息自动填补其它内容" :width="400" trigger="hover">
                                    <template #reference>
                                        <el-button type="primary"
                                            @click="generateQuestion(question, index)">智能生成</el-button>
                                    </template>
                                    <el-input v-model="otherRequirements[index]" style="height: 100%;" type="textarea"
                                        placeholder="若有其它需求可此处填写" />
                                </el-popover>
                            </el-col>
                            <el-col :span="2" :offset="1" v-if="question.editable">
                                <el-button type="success" plain @click="updateQuestion(question, index)">保存</el-button>
                            </el-col>
                            <el-col :span="2" :offset="6" v-if="!question.editable">
                                <el-button type="primary" plain @click="question.editable = true">编辑</el-button>
                            </el-col>
                            <el-col :span="2" :offset="1">
                                <el-button type="danger" plain
                                    @click="deleteQuestion(question.questionId, index)">删除</el-button>
                            </el-col>
                        </el-row>
                        <!-- 关联知识点 -->
                        <el-row style="margin-top: 40px;margin-bottom: 10px;">
                            <el-col :span="4" :offset="2">
                                <span style="font-weight: bold;">关联知识点:</span>
                            </el-col>
                            <el-col :span="16">
                                <el-input-tag v-model="question.knowledgeList" placeholder="请输入关联知识点" size="large"
                                    tag-type="primary" tag-effect="plain" :disabled="!question.editable">
                                    <template #suffix>
                                        <el-dropdown ref="dropdownRef" trigger="click" :hide-on-click="false"
                                            @command="(command) => handleCommand(command, question)"
                                            :disabled="!question.editable">
                                            <el-icon :size="16" style="cursor: pointer; margin-right: 8px;">
                                                <arrow-down />
                                            </el-icon>
                                            <template #dropdown>
                                                <el-dropdown-menu style="max-height: 250px;">
                                                    <el-dropdown-item v-for="item in knowledgeSuggestions" :key="item"
                                                        :command="item"
                                                        :disabled="question.knowledgeList.includes(item)">
                                                        {{ item }}
                                                    </el-dropdown-item>
                                                </el-dropdown-menu>
                                            </template>
                                        </el-dropdown>
                                    </template>
                                </el-input-tag>
                            </el-col>
                        </el-row>
                        <!-- 具体题目详细-选择题 -->
                        <el-divider>题目详情</el-divider>
                        <el-form class="questionForm" v-if="question.questionType == QUESTION_TYPE.CHOICE"
                            :model="question" style="padding:0 50px;">
                            <el-form-item label="题干:" prop="questionStem" :rules="[
                                { required: true, message: '请填写题干' }]">
                                <el-input class="form-input" v-model="question.questionStem" type="textarea"
                                    placeholder="请输入题干" :disabled="!question.editable"
                                    :autosize="{ minRows: 3, maxRows: 12 }" />
                            </el-form-item>
                            <el-form-item class="custom-form-item" label="答案选项（蓝色背景标记的为正确选项）:" prop="questionAnswer"
                                :rules="[
                                    { required: true, message: '请填写选项' }]">
                                <template #label>
                                    答案选项
                                    <span style="font-size: 10px; color: grey;font-weight: normal;">(点选标记正确选项)</span>:
                                </template>
                                <el-radio-group v-model="question.questionAnswer" :min="1" :max="1"
                                    style="width: 100%;">
                                    <el-row class="choiceRow" v-for="(_, choiceIndex) in question.choiceList"
                                        :key="choiceIndex">
                                        <el-col :span="2" :offset="1">
                                            <el-radio :value="indexToLetter(choiceIndex)">{{
                                                indexToLetter(choiceIndex)
                                                }}.</el-radio>
                                        </el-col>
                                        <el-col :span="19">
                                            <el-input v-model="question.choiceList[choiceIndex]" placeholder="请输入选项内容"
                                                :disabled="!question.editable" />
                                        </el-col>
                                        <Transition name="fade">
                                            <el-col v-if="question.editable" :span="2">
                                                <el-popover placement="right" title="" :width="20" trigger="hover"
                                                    content="删除">
                                                    <template #reference>
                                                        <el-button style="margin-left: 10px;height: 20px;width: 20px;"
                                                            type="danger" :icon="Delete" circle
                                                            @click="deleteChoice(choiceIndex, index)" />
                                                    </template>
                                                </el-popover>
                                            </el-col>
                                        </Transition>
                                    </el-row>
                                </el-radio-group>
                                <!-- 新增选择题选项 -->
                                <el-row style="width: 80%;height: 40px;">
                                    <el-col :span="24" v-if="question.editable" :class="['choiceRow', 'hover-zoom']"
                                        style="border: 1px dashed;" @click="addChoice(index)">
                                        <div class="el-upload__text" style="width: 80%;">
                                            <el-icon class="icon2" style="margin-left: 50px;">
                                                <Plus /><em style="font-size: 16px;">新增选项</em>
                                            </el-icon>
                                        </div>
                                    </el-col>
                                </el-row>
                            </el-form-item>
                            <el-form-item label="答案解析:">
                                <el-input class="form-input" v-model="question.questionParse" type="textarea"
                                    placeholder="请输入答案解析" :disabled="!question.editable"
                                    :autosize="{ minRows: 4, maxRows: 12 }" />
                            </el-form-item>
                        </el-form>
                        <!-- 具体题目详细-主观题 -->
                        <el-form class="questionForm" v-else-if="question.questionType == QUESTION_TYPE.SUBJECTIVE"
                            :model="question" style="padding:0 50px;">
                            <el-form-item label="题干:" prop="questionStem" :rules="[
                                { required: true, message: '请填写题干' }]">
                                <el-input :autosize="{ minRows: 3, maxRows: 12 }" class="form-input"
                                    v-model="question.questionStem" type="textarea" placeholder="请输入题干"
                                    :disabled="!question.editable" />
                            </el-form-item>
                            <el-form-item label="参考答案:" prop="questionAnswer">
                                <el-input :autosize="{ minRows: 4, maxRows: 12 }" class="form-input"
                                    v-model="question.questionAnswer" type="textarea" placeholder="请输入参考答案"
                                    :disabled="!question.editable" />
                            </el-form-item>
                            <el-form-item label="答案解析(评分细则):">
                                <el-input :autosize="{ minRows: 4, maxRows: 12 }" class="form-input"
                                    v-model="question.questionParse" type="textarea" placeholder="请输入答案解析"
                                    :disabled="!question.editable" style="max-height: 300px;" />
                            </el-form-item>
                        </el-form>
                        <!-- 具体题目详细-编程题 -->
                        <el-form class="questionForm" v-else-if="question.questionType == QUESTION_TYPE.PROGRAM"
                            :model="question" style="padding:0 50px;">
                            <el-form-item label="题干:" prop="questionStem" :rules="[
                                { required: true, message: '请填写题干' }]">
                                <el-input class="form-input" v-model="question.questionStem" type="textarea"
                                    placeholder="请输入题干" :disabled="!question.editable"
                                    :autosize="{ minRows: 3, maxRows: 12 }" />
                            </el-form-item>
                            <el-form-item label="程序示例代码:" prop="questionAnswer">
                                <el-input class="form-input" v-model="question.questionAnswer" type="textarea"
                                    placeholder="请输入实例代码" :disabled="!question.editable"
                                    :autosize="{ minRows: 3, maxRows: 12 }" />
                            </el-form-item>
                            <el-form-item label="程序标准输出:" prop="codeOutput">
                                <el-input class="form-input" v-model="question.codeOutput" type="textarea"
                                    placeholder="" :disabled="!question.editable"
                                    :autosize="{ minRows: 3, maxRows: 12 }" />
                            </el-form-item>
                            <el-form-item label="评分标准:">
                                <el-input class="form-input" v-model="question.questionParse" type="textarea"
                                    placeholder="请输入答案解析" :disabled="!question.editable"
                                    :autosize="{ minRows: 4, maxRows: 12 }" />
                            </el-form-item>
                        </el-form>
                    </div>
                </TransitionGroup>
                <!-- 新增试题按钮 -->
                <el-row id="addNewQuestion" style="width: 80%;height: 450px;  justify-self: center;">
                    <el-col v-for="(item, index) in Object.values(QUESTION_TYPE)" :key="index" :span="8">
                        <el-card class="hover-zoom" style="width: 160px;height: 70px;margin-top: 5px;"
                            @click="addQuestion(item)">
                            <div class="el-upload__text">
                                <el-icon class="icon2">
                                    <Plus /><em style="font-size: 16px;">新增{{ item }}</em>
                                </el-icon>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
            </div>
        </el-main>
    </el-container>

</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Delete, ArrowDown } from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { QUESTION_TYPE } from '@/constants/questionTypes'
import { ElNotification } from 'element-plus'

const router = useRouter()
const route = useRoute()
const setId = ref(null)
const mainRef = ref(null)
const setFormRef = ref(null)
const formSet = ref({
    setName: '',
    setDesc: '',
})
const typeMap = {
    [QUESTION_TYPE.CHOICE]: 'primary',
    [QUESTION_TYPE.SUBJECTIVE]: 'success',
    [QUESTION_TYPE.PROGRAM]: 'info'
};

const questionsData = ref([])       //questionDTO

onMounted(async () => {
    // 从 URL query 获取参数
    setId.value = route.query.setId;
    // const sectionId = route.query.sectionId;
    if (!setId.value || setId.value == '') {
        ElMessage.error("setid不存在")
        return;
        // router.go(-1);
    } else {
        //获取章节知识点（仅在小节编辑时生效）
        try {
            const sharedData = inject('sharedData', null);
            if (sharedData && sharedData.value && sharedData.value.sectionId) {
                const response = await request.post('/section/querySectionKnowledges', sharedData.value.sectionId)
                if (response.data.code != 200) {
                    ElMessage.error("后端处理失败，请稍后再试");
                    return;
                }
                knowledgeSuggestions.value = response.data.data;
            }
        } catch (error) {
            console.log('获取知识点失败:', error);
        }
        //获取试题集信息
        try {
            const response = await request.post('/question/querySet', setId.value)
            formSet.value = response.data.data;
        } catch (error) {
            // 使用模拟数据
            formSet.value = {
                setId: setId.value,
                setName: "Vue.js基础试题集",
                setDesc: "包含Vue.js基础语法、组件开发、路由管理等核心知识点，适合初学者和进阶学习者使用"
            };
        }
        
        //获取该实体集下具体的各题目信息
        try {
            const response = await request.post('/question/queryQuestions', setId.value)
            questionsData.value = response.data.data;
        } catch (error) {
            // 使用模拟数据
            questionsData.value = [
                {
                    questionId: 1,
                    questionType: "选择题",
                    questionStem: "Vue.js中，以下哪个选项用于声明响应式数据？A. data() B. computed C. methods D. watch",
                    questionAnswer: "A",
                    choiceList: ["data()", "computed", "methods", "watch"],
                    questionParse: "在Vue.js中，data()函数用于声明组件的响应式数据。当data中的数据发生变化时，Vue会自动更新相关的DOM。",
                    knowledgeList: ["Vue基础", "响应式数据"],
                    editable: false
                },
                {
                    questionId: 2,
                    questionType: "选择题",
                    questionStem: "在Vue 3中，Composition API的主要优势是什么？A. 更好的TypeScript支持 B. 更小的包体积 C. 更好的性能 D. 以上都是",
                    questionAnswer: "D",
                    choiceList: ["更好的TypeScript支持", "更小的包体积", "更好的性能", "以上都是"],
                    questionParse: "Composition API提供了更好的TypeScript支持、更小的包体积和更好的性能，因此答案是以上都是。",
                    knowledgeList: ["Vue 3", "Composition API"],
                    editable: false
                },
                {
                    questionId: 3,
                    questionType: "主观题",
                    questionStem: "请详细说明Vue.js的生命周期钩子函数及其执行顺序，并解释每个钩子的作用。",
                    questionAnswer: "Vue.js的生命周期钩子函数执行顺序如下：\n1. beforeCreate: 实例初始化之后，数据观测和事件配置之前\n2. created: 实例创建完成，数据观测、属性和方法配置完成\n3. beforeMount: 挂载开始之前，render函数首次被调用\n4. mounted: 实例挂载完成，DOM已创建\n5. beforeUpdate: 数据更新时调用，发生在虚拟DOM重新渲染之前\n6. updated: 数据更新完成，DOM已重新渲染\n7. beforeDestroy: 实例销毁之前调用\n8. destroyed: 实例销毁完成",
                    questionParse: "评分标准：\n- 正确列出8个生命周期钩子：4分\n- 正确说明执行顺序：3分\n- 准确解释每个钩子的作用：3分",
                    knowledgeList: ["Vue生命周期", "组件开发"],
                    editable: false
                },
                {
                    questionId: 4,
                    questionType: "编程题",
                    questionStem: "编写一个Vue组件，实现一个简单的计数器功能，包含增加、减少、重置按钮。",
                    questionAnswer: "编写一个Vue组件，实现计数器功能，包含增加、减少、重置按钮。使用data()声明count变量，methods中实现increment、decrement、reset方法。",
                    questionParse: "评分标准：\n- 正确使用data()声明响应式数据：2分\n- 实现increment方法：2分\n- 实现decrement方法：2分\n- 实现reset方法：2分\n- 正确绑定事件和显示数据：2分",
                    knowledgeList: ["Vue组件", "事件处理", "响应式数据"],
                    editable: false
                }
            ];
        }
    }
})
// 侧边锚点跳转
const handleAnchorClick = (e) => {
    e.preventDefault();
    // console.log(e);
}

// 保存更新
const updateSet = async () => {
    try {
        // 使用 await 等待表单验证结果
        const valid = await setFormRef.value.validate();
        if (!valid) {
            ElMessage.error('请完整填写表单');
            return;
        }

        const data = {
            setId: setId.value,
            setName: formSet.value.setName,
            setDesc: formSet.value.setDesc
        };

        const response = await request.post('/question/updateSet', data);

        if (response.data.code !== "200") {
            ElMessage.error("后端处理失败，请稍后再试");
            return;
        }

        ElMessage.success("更新成功");
        router.go(-1);

    } catch (error) {
        // 统一捕获所有异常（包括网络错误和验证异常）
        console.error('操作失败:', error);
        ElMessage.error(`请求失败: ${error.message || '未知错误'}`);
    }
};
// 新增题目
const addQuestion = async (type) => {
    const formData = new FormData();
    formData.append('setId', setId.value);
    formData.append('questionType', type)
    const response = await request.post('/question/addQuestion', formData)
    response.data.data.editable = true;
    questionsData.value.push(response.data.data);

}
// 删除题目
const deleteQuestion = async (questionId, index) => {
    const formData = new FormData();
    formData.append('questionId', questionId);
    await request.post('/question/deleteQuestion', questionId)
    // 本地同步删除
    questionsData.value.splice(index, 1);
    ElMessage.success("删除成功");
}
// 更新题目
const updateQuestion = async (question, index) => {
    if (!question.questionStem || question.questionStem == '') {
        ElMessage.error("请完整填写内容");
        return;
    }
    await request.post('/question/updateQuestion', question)
    // 本地同步更新编辑状态
    questionsData.value[index].editable = false;
    ElMessage.success("成功保存");
}
// 选择题
// 数字下标转大写字母
const indexToLetter = computed(() => (index) => {
    return String.fromCharCode(65 + index);
});
// 添加选择题选项
const addChoice = (index) => {
    console.log(questionsData.value[index]);
    questionsData.value[index].choiceList.push("");
}
// 删除选项
const deleteChoice = (choiceIndex, index) => {
    questionsData.value[index].choiceList.splice(choiceIndex, 1);
}


// 智能生成（不自动保存）
const otherRequirements = ref([]);  //其它需求
const generateQuestion = async (question, index) => {
    loadingStates.value = {
        ...loadingStates.value,
        [question.questionId]: true
    };
    const demand = otherRequirements.value[index];
    ElNotification({
        title: '提示',
        message: '已请求智能生成试题' + (index + 1),
        type: 'primary',
    })
    try {//不用携带认证头

        const response = await request.post('/gpt/questionGenerate', {
            "questionDTO": question,
            "demand": demand
        })
        if (response.data.code != 200) {
            ElNotification({
                title: '失败',
                message: '试题' + (index + 1) + '生成失败',
                type: 'erro',
            })
            return;
        }
        // 本地同步更新状态
        questionsData.value[index] = response.data.data;
        ElNotification({
            title: '成功',
            message: '试题' + (index + 1) + '已生成',
            type: 'success',
        })
    } catch (error) {
        ElMessage.error('请求失败')
    } finally {
        loadingStates.value = {
            ...loadingStates.value,
            [question.questionId]: false
        };
    }
}
// 加载动画
const loadingStates = ref({});
// 知识点-下拉列表
const dropdownRef = ref(null);

// 固定的建议列表
const knowledgeSuggestions = ref([
    "Vue基础",
    "Vue 3",
    "Composition API",
    "响应式数据",
    "生命周期",
    "组件开发",
    "事件处理",
    "路由管理",
    "状态管理",
    "Vuex",
    "Pinia",
    "TypeScript",
    "JavaScript",
    "ES6+",
    "异步编程",
    "Promise",
    "async/await",
    "函数式编程",
    "设计模式",
    "CSS布局",
    "Flexbox",
    "Grid",
    "响应式设计",
    "前端工程化",
    "Webpack",
    "Vite",
    "Git",
    "性能优化"
]);
//下拉列表被点击
const handleCommand = (command, question) => {
    if (!question.knowledgeList.includes(command)) {
        question.knowledgeList.push(command);
    }

};

// 截断文本内容
const truncatedContent = (text, maxLength) => {
    if (!text || typeof text !== 'string') return '';
    if (text.length > maxLength) {
        return text.slice(0, maxLength) + "...";
    }

    return text;
};
</script>

<style scoped>
/* 鼠标悬浮动画效果 */
.hover-zoom {
    display: flex;
    transition: transform 0.3s ease;
    cursor: pointer;
    color: #8c939d;

}

.hover-zoom:hover {
    transform: scale(1.05);
    color: rgb(150, 197, 255);
}

/* 淡入淡出动画效果 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.fade-move {
    transition: transform 0.5s ease;
}

.icon2 {
    font-size: 24px;

    width: 100%;
    height: 100%;
    text-align: center;
}

/* 下拉列表 */
:deep(.el-dropdown-menu) {
    max-height: 150px;
}

:deep(.el-dropdown-menu__item.is-disabled) {
    color: var(--el-text-color-disabled);
    cursor: not-allowed;
}

/* question表单 */
.questionForm :deep(.el-form-item) {
    display: block;
    /* 单独一行 */
}

.questionForm :deep(.el-form-item__content) {
    justify-content: center;
}

.questionForm :deep(.el-form-item__label) {
    display: block;
    margin-bottom: 8px;
    width: 100%;
    font-weight: bold;
}

.form-input {
    margin-left: 40px;
}

/* 选择题 */
.choiceRow {
    width: 100%;
    display: flex;
    align-content: center;
    justify-self: center;

}

.questionDiv {
    width: 70%;
    padding: 50px;
    padding-bottom: 5px;
    padding-top: 15px;
    margin-bottom: 10px;
    background-color: white;
}
</style>