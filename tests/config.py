"""
测试配置文件
定义了 Java 后端服务和 Python AI 服务的地址，以及测试所需要的基础数据。
"""

# Java 后端基础路径 (Spring Boot 运行在 10020 端口)
JAVA_BACKEND_URL = "http://localhost:10020"

# Python AI API 基础路径 (默认运行在 8000 端口)
PYTHON_AI_URL = "http://localhost:8000"

# Vue 前端系统运行地址 (默认运行在 8080 端口，若被占用可能运行在 8081 等)
FRONTEND_URL = "http://localhost:8081"


# 默认测试账户信息（用于自动注册和登录）
TEST_USER = {
    "account": "test_student_999",     # 测试账号
    "password": "Password123!",         # 测试密码
    "username": "测试学生甲",            # 测试姓名
    "role": "Student"                  # 角色：Student 或 Teacher
}

# 默认测试课程ID（用于加入课程、退课、新建笔记等操作的测试，请根据您的数据库中已有的课程ID进行修改）
TEST_COURSE_ID = 1

# 默认测试章节ID
TEST_CHAPTER_ID = 1
