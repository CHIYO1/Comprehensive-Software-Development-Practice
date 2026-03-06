// 缓存课程表单数据
import { defineStore } from 'pinia'

export const useCourseStore = defineStore('course', {
    state: () => ({
        course: {
            'courseName': '',
            'courseDesc': '',
            'coverPath': null,
        },
        coverFile: [],
        subSection: {
            'subsectionId': null,
            'subsectionName': '',
            'subsectionDesc': '',
            'subsectionType': '',
            'resourceId': null,
            'sectionId': null,
        }
    }),
    actions: {
        // 缓存
        cacheFormData(course, coverFile) {
            this.course = course
            this.coverFile = coverFile
        },
        // 获取缓存
        getCourse() {
            return this.course
        },
        getCoverFile() {
            return this.coverFile
        },
        // 清除缓存
        clearCourse() {
            this.course = {
                'courseName': '',
                'courseDesc': '',
                'coverPath': null,
            };
            this.coverFile = [];
        }
    },
    persist: true
})