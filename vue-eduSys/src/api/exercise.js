// /src/api/exercise.js
import request from '@/utils/request'

// 根据课程和题型查询题目
export const queryExerciseQuestions = (courseId, questionType) => {
    return request({
        url: '/exercise/queryByCourse',
        method: 'post',
        params: {
            courseId,
            questionType: questionType || ''
        }
    })
}

// 新增单个题目
export const addExerciseQuestion = (data) => {
    return request({
        url: '/exercise/add',
        method: 'post',
        data
    })
}

// 批量新增题目
export const addExerciseQuestions = (data) => {
    return request({
        url: '/exercise/addBatch',
        method: 'post',
        data
    })
}