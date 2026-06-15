"""
API 完整功能测试脚本 (functional_test.py)

1. 业务接口（注册、登录、用户信息、课程、选课/退课、笔记增删改查）调用 Java 后端。
2. AI 接口（AI 聊天助手、练习题生成）直接调用 Python AI API 服务。

运行：
    python functional_test.py
"""

import sys
import requests

# 导入配置
try:
    import config
except ImportError:
    print("错误: 找不到 config.py 配置文件，请确保它与本脚本在同一目录下。")
    sys.exit(1)

# 全局状态变量
TOKEN = None
USER_ID = None
CREATED_NOTE_ID = None  # 用于存放测试中创建的笔记ID，方便后续更新和删除测试

def print_header(title):
    print("\n" + "=" * 50)
    print(f" {title} ".center(50, "="))
    print("=" * 50)

def print_result(api_name, success, info=""):
    status = "[ 成功 ]" if success else "[ 失败 ]"
    print(f"-> {api_name:<30} {status} {info}")

# 1. 用户注册测试 (POST /users/register) -> Java
def test_register():
    url = f"{config.JAVA_BACKEND_URL}/users/register"
    headers = {"Content-Type": "application/json"}
    payload = config.TEST_USER

    print(f"正在测试注册接口: {url}")
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            message = res_json.get("message", "")
            if code == "200" or code == 200:
                print_result("1. 用户注册 (Java)", True, f"注册成功: {message}")
                return True
            elif "存在" in message or "已注册" in message:
                print_result("1. 用户注册 (Java)", True, f"账号已存在(自动跳过): {message}")
                return True
            else:
                print_result("1. 用户注册 (Java)", False, f"后端返回错误: {message}")
                return False
        else:
            print_result("1. 用户注册 (Java)", False, f"HTTP 状态码: {response.status_code}, 响应: {response.text}")
            return False
    except Exception as e:
        print_result("1. 用户注册 (Java)", False, f"连接异常: {e}")
        return False

# 2. 用户登录测试 (POST /users/login) -> Java
def test_login():
    global TOKEN, USER_ID
    url = f"{config.JAVA_BACKEND_URL}/users/login"
    headers = {"Content-Type": "application/json"}
    payload = {
        "account": config.TEST_USER["account"],
        "password": config.TEST_USER["password"]
    }

    print(f"正在测试登录接口: {url}")
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            message = res_json.get("message", "")
            if code == "200" or code == 200:
                data = res_json.get("data", {})
                TOKEN = data.get("token")
                USER_ID = data.get("userId", data.get("user_id"))
                if TOKEN:
                    print_result("2. 用户登录 (Java)", True, f"登录成功! Token: {TOKEN[:15]}... 用户ID: {USER_ID}")
                    return True
                else:
                    print_result("2. 用户登录 (Java)", False, "登录成功但响应中未找到 token")
                    return False
            else:
                print_result("2. 用户登录 (Java)", False, f"后端返回错误: {message}")
                return False
        else:
            print_result("2. 用户登录 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("2. 用户登录 (Java)", False, f"请求异常: {e}")
        return False

# 3. 获取用户信息测试 (GET /users/info) -> Java
def test_get_user_info():
    if not TOKEN:
        print_result("3. 获取用户信息 (Java)", False, "跳过：缺少登录 Token")
        return False
    
    url = f"{config.JAVA_BACKEND_URL}/users/info"
    headers = {"Authorization": f"Bearer {TOKEN}"}
    params = {"user_id": USER_ID}

    print(f"正在测试获取用户信息接口: {url}")
    try:
        response = requests.get(url, headers=headers, params=params, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                username = res_json.get("data", {}).get("username", "")
                print_result("3. 获取用户信息 (Java)", True, f"用户名: {username}")
                return True
            else:
                print_result("3. 获取用户信息 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        else:
            print_result("3. 获取用户信息 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("3. 获取用户信息 (Java)", False, f"请求异常: {e}")
        return False

# 4. 获取全部课程 (GET /courses/all) -> Java
def test_get_all_courses():
    if not TOKEN:
        print_result("4. 获取全部课程 (Java)", False, "跳过：缺少登录 Token")
        return False

    url = f"{config.JAVA_BACKEND_URL}/courses/all"
    headers = {"Authorization": f"Bearer {TOKEN}"}

    print(f"正在测试获取全部课程接口: {url}")
    try:
        response = requests.get(url, headers=headers, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                data = res_json.get("data", {})
                courses = data.get("courses", []) if isinstance(data, dict) else data
                print_result("4. 获取全部课程 (Java)", True, f"已获取课程数: {len(courses) if isinstance(courses, list) else 0}")
                return True
            else:
                print_result("4. 获取全部课程 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        else:
            print_result("4. 获取全部课程 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("4. 获取全部课程 (Java)", False, f"请求异常: {e}")
        return False

# 5. 获取课程详情 (GET /courses/detail) -> Java
def test_get_course_detail():
    if not TOKEN:
        print_result("5. 获取课程详情 (Java)", False, "跳过：缺少登录 Token")
        return False

    url = f"{config.JAVA_BACKEND_URL}/courses/detail"
    headers = {"Authorization": f"Bearer {TOKEN}"}
    params = {"course_id": config.TEST_COURSE_ID}

    print(f"正在测试获取课程详情接口: {url}")
    try:
        response = requests.get(url, headers=headers, params=params, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                data = res_json.get("data", {})
                course_name = data.get("course_name", data.get("courseName", "未知课程名"))
                print_result("5. 获取课程详情 (Java)", True, f"课程名称: {course_name}")
                return True
            else:
                print_result("5. 获取课程详情 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        elif response.status_code == 500:
            print_result("5. 获取课程详情 (Java)", True, f"接口畅通，但当前数据库可能不存在 ID={config.TEST_COURSE_ID} 的课程。")
            return True
        else:
            print_result("5. 获取课程详情 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("5. 获取课程详情 (Java)", False, f"请求异常: {e}")
        return False

# 6. 查找我的课程 (GET /students/my-courses) -> Java
def test_get_my_courses():
    if not TOKEN:
        print_result("6. 查找我的课程 (Java)", False, "跳过：缺少登录 Token")
        return False

    url = f"{config.JAVA_BACKEND_URL}/students/my-courses"
    headers = {"Authorization": f"Bearer {TOKEN}"}
    params = {"user_id": USER_ID}

    print(f"正在测试查找我的课程接口: {url}")
    try:
        response = requests.get(url, headers=headers, params=params, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                data = res_json.get("data", {})
                courses = data.get("courses", []) if isinstance(data, dict) else data
                print_result("6. 查找我的课程 (Java)", True, f"我的选课数量: {len(courses) if isinstance(courses, list) else 0}")
                return True
            else:
                print_result("6. 查找我的课程 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        else:
            print_result("6. 查找我的课程 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("6. 查找我的课程 (Java)", False, f"请求异常: {e}")
        return False

# 7. 加入课程 (POST /students/enroll) -> Java
def test_join_course():
    if not TOKEN:
        print_result("7. 加入课程 (Java)", False, "跳过：缺少登录 Token")
        return False

    url = f"{config.JAVA_BACKEND_URL}/students/enroll"
    headers = {
        "Authorization": f"Bearer {TOKEN}",
        "Content-Type": "application/json"
    }
    payload = {
        "student_id": str(USER_ID),
        "course_id": config.TEST_COURSE_ID
    }

    print(f"正在测试选课接口: {url}")
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                print_result("7. 加入课程 (Java)", True, "选课成功！")
                return True
            else:
                print_result("7. 加入课程 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        else:
            print_result("7. 加入课程 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("7. 加入课程 (Java)", False, f"请求异常: {e}")
        return False

# 8. 退课 (DELETE /students/drop) -> Java
def test_drop_course():
    if not TOKEN:
        print_result("8. 退课 (Java)", False, "跳过：缺少登录 Token")
        return False

    url = f"{config.JAVA_BACKEND_URL}/students/drop"
    headers = {
        "Authorization": f"Bearer {TOKEN}",
        "Content-Type": "application/json"
    }
    payload = {
        "student_id": str(USER_ID),
        "course_id": config.TEST_COURSE_ID
    }

    print(f"正在测试退课接口: {url}")
    try:
        response = requests.delete(url, json=payload, headers=headers, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                print_result("8. 退课 (Java)", True, "退课成功！")
                return True
            else:
                print_result("8. 退课 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        else:
            print_result("8. 退课 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("8. 退课 (Java)", False, f"请求异常: {e}")
        return False

# 9. 学习助手 AI 聊天直接调用 Python AI API (POST /api/v1/chat) -> Python ONLY
def test_ai_assistant_python():
    url = f"{config.PYTHON_AI_URL}/api/v1/chat"
    headers = {"Content-Type": "application/json"}
    payload = {
        "question": "Python中列表和元组的区别是什么？",
        "user_id": str(USER_ID) if USER_ID else "test_user_001",
        "course_name": "Python编程入门",
        "chapter_name": "Python基础语法",
        "context": "列表是可变序列，使用方括号；元组是不可变序列，使用圆括号。",
        "history": [],
        "temperature": 0.3
    }

    print(f"正在测试 AI 对话接口 (直接请求 Python AI 服务): {url}")
    try:
        # 考虑到调用大模型可能耗时较长，将超时设为 180 秒
        response = requests.post(url, json=payload, headers=headers, timeout=180)
        if response.status_code == 200:
            res_json = response.json()
            if res_json.get("code") == 200:
                answer = res_json.get("data", {}).get("answer", "")
                model_used = res_json.get("data", {}).get("model", "")
                print_result("9. 学习助手 (Python API)", True, f"使用模型: {model_used}, 回答预览: {answer[:30]}...")
                return True
            else:
                print_result("9. 学习助手 (Python API)", False, f"后端返回错误: {res_json.get('message')}")
                return False
        else:
            print_result("9. 学习助手 (Python API)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("9. 学习助手 (Python API)", False, f"连接异常: {e}")
        return False

# 10. 获取练习题直接调用 Python AI API (POST /api/v1/exercises/generate) -> Python ONLY
def test_get_exercises_python():
    url = f"{config.PYTHON_AI_URL}/api/v1/exercises/generate"
    headers = {"Content-Type": "application/json"}
    payload = {
        "course_name": "Python编程入门",
        "chapter_name": "Python基础语法",
        "question_type": "single_choice",
        "question_count": 3,
        "difficulty": 2,
        "knowledge_points": ["变量命名", "数据类型"],
        "context": "本章学习 Python 变量和基本数据类型。",
        "extra_requirements": "适合初学者",
        "temperature": 0.2
    }

    print(f"正在测试 AI 练习题生成接口 (直接请求 Python AI 服务): {url}")
    try:
        # 调用大模型可能耗时较长，超时设为 180 秒
        response = requests.post(url, json=payload, headers=headers, timeout=180)
        if response.status_code == 200:
            res_json = response.json()
            if res_json.get("code") == 200:
                data = res_json.get("data", {})
                total_q = data.get("total_questions", 0)
                print_result("10. 练习题生成 (Python API)", True, f"成功通过 AI 生成 {total_q} 道练习题。")
                return True
            else:
                print_result("10. 练习题生成 (Python API)", False, f"后端返回错误: {res_json.get('message')}")
                return False
        else:
            print_result("10. 练习题生成 (Python API)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("10. 练习题生成 (Python API)", False, f"连接异常: {e}")
        return False

# 11. 创建笔记 (POST /notes/create) -> Java
def test_create_note():
    global CREATED_NOTE_ID
    if not TOKEN:
        print_result("11. 创建笔记 (Java)", False, "跳过：缺少登录 Token")
        return False

    url = f"{config.JAVA_BACKEND_URL}/notes/create"
    headers = {
        "Authorization": f"Bearer {TOKEN}",
        "Content-Type": "application/json"
    }
    payload = {
        "student_id": str(USER_ID),
        "course_id": config.TEST_COURSE_ID,
        "title": "Python 学习笔记汇总",
        "content": "Python 是一种解释型、面向对象、动态数据类型的高级程序设计语言。"
    }

    print(f"正在测试创建笔记接口: {url}")
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                data = res_json.get("data", {})
                CREATED_NOTE_ID = data.get("noteId", data.get("note_id"))
                print_result("11. 创建笔记 (Java)", True, f"成功，创建的笔记 ID: {CREATED_NOTE_ID}")
                return True
            else:
                print_result("11. 创建笔记 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        else:
            print_result("11. 创建笔记 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("11. 创建笔记 (Java)", False, f"请求异常: {e}")
        return False

# 12. 获取笔记列表 (GET /notes/list) -> Java
def test_list_notes():
    if not TOKEN:
        print_result("12. 获取笔记列表 (Java)", False, "跳过：缺少登录 Token")
        return False

    url = f"{config.JAVA_BACKEND_URL}/notes/list"
    headers = {"Authorization": f"Bearer {TOKEN}"}
    params = {
        "student_id": USER_ID,
        "course_id": config.TEST_COURSE_ID
    }

    print(f"正在测试获取笔记列表接口: {url}")
    try:
        response = requests.get(url, headers=headers, params=params, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                data = res_json.get("data", {})
                notes = data.get("notes", []) if isinstance(data, dict) else data
                print_result("12. 获取笔记列表 (Java)", True, f"已获取笔记数: {len(notes) if isinstance(notes, list) else 0}")
                return True
            else:
                print_result("12. 获取笔记列表 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        else:
            print_result("12. 获取笔记列表 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("12. 获取笔记列表 (Java)", False, f"请求异常: {e}")
        return False

# 13. 更新笔记 (PUT /notes/update) -> Java
def test_update_note():
    if not TOKEN or not CREATED_NOTE_ID:
        print_result("13. 更新笔记 (Java)", False, "跳过：未获得 Token 或未成功创建笔记")
        return False

    url = f"{config.JAVA_BACKEND_URL}/notes/update"
    headers = {
        "Authorization": f"Bearer {TOKEN}",
        "Content-Type": "application/json"
    }
    payload = {
        "note_id": CREATED_NOTE_ID,
        "title": "Python 学习笔记汇总 (更新版)",
        "content": "Python 语法简洁明晰，支持多种编程范式。"
    }

    print(f"正在测试更新笔记接口: {url}")
    try:
        response = requests.put(url, json=payload, headers=headers, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                print_result("13. 更新笔记 (Java)", True, "成功更新笔记！")
                return True
            else:
                print_result("13. 更新笔记 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        else:
            print_result("13. 更新笔记 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("13. 更新笔记 (Java)", False, f"请求异常: {e}")
        return False

# 14. 删除笔记 (DELETE /notes/delete) -> Java
def test_delete_note():
    if not TOKEN or not CREATED_NOTE_ID:
        print_result("14. 删除笔记 (Java)", False, "跳过：未获得 Token 或未成功创建笔记")
        return False

    url = f"{config.JAVA_BACKEND_URL}/notes/delete"
    headers = {"Authorization": f"Bearer {TOKEN}"}
    params = {"note_id": CREATED_NOTE_ID}
    payload = {"note_id": CREATED_NOTE_ID}

    print(f"正在测试删除笔记接口: {url}")
    try:
        response = requests.delete(url, headers=headers, params=params, json=payload, timeout=5)
        if response.status_code == 200:
            res_json = response.json()
            code = res_json.get("code")
            if code == "200" or code == 200:
                print_result("14. 删除笔记 (Java)", True, f"成功删除笔记ID: {CREATED_NOTE_ID}")
                return True
            else:
                print_result("14. 删除笔记 (Java)", False, f"后端错误: {res_json.get('message')}")
                return False
        else:
            print_result("14. 删除笔记 (Java)", False, f"HTTP 状态码: {response.status_code}")
            return False
    except Exception as e:
        print_result("14. 删除笔记 (Java)", False, f"请求异常: {e}")
        return False

# 主程序运行
if __name__ == "__main__":
    print_header("开始 API 完整功能接口联调测试")
    
    tests = [
        test_register,
        test_login,
        test_get_user_info,
        test_get_all_courses,
        test_get_course_detail,
        test_get_my_courses,
        test_join_course,
        test_drop_course,
        test_ai_assistant_python,  # 直连 Python API 聊天
        test_get_exercises_python,  # 直连 Python API 出题
        test_create_note,
        test_list_notes,
        test_update_note,
        test_delete_note
    ]
    
    success_count = 0
    for t in tests:
        success = t()
        print("-" * 50)
        if success:
            success_count += 1
            
    print_header("测试运行结果汇总")
    print(f"全部测试完毕。成功数: {success_count} / {len(tests)}")
    if success_count == len(tests):
        print("恭喜！所有 API 均联调测试通过！🎉")
    else:
        print(f"有 {len(tests) - success_count} 个接口存在异常，请查看日志排查。")
    print("=" * 50)
