"""
性能测试脚本 (performance_test.py)

该脚本利用 Python 内置的 threading 模块，模拟多个用户同时发送请求，
用以测试 API 的响应速度和承压能力。

运行：
    python performance_test.py
"""

import sys
import time
import threading
import requests

# 导入配置
try:
    import config
except ImportError:
    print("错误: 找不到 config.py 配置文件，请确保它与本脚本在同一目录下。")
    sys.exit(1)

# 全局变量，存放登录 token 和用户 ID
TOKEN = None
USER_ID = None

# 用于收集所有线程请求的响应时间(秒)以及成功状态 (True/False)
results = []
results_lock = threading.Lock()  # 线程锁，防止多个线程写入时产生冲突

# 1. 登录前置操作，保证压测接口有 Authorization Token
def prepare_auth():
    global TOKEN, USER_ID
    url_reg = f"{config.JAVA_BACKEND_URL}/users/register"
    url_login = f"{config.JAVA_BACKEND_URL}/users/login"
    
    # 尝试自动注册（以防账号不存在）
    try:
        requests.post(url_reg, json=config.TEST_USER, headers={"Content-Type": "application/json"}, timeout=3)
    except:
        pass
    
    # 执行登录
    try:
        payload = {
            "account": config.TEST_USER["account"],
            "password": config.TEST_USER["password"]
        }
        res = requests.post(url_login, json=payload, headers={"Content-Type": "application/json"}, timeout=5)
        if res.status_code == 200:
            data = res.json().get("data", {})
            TOKEN = data.get("token")
            USER_ID = data.get("userId", data.get("user_id"))
            return True
    except Exception as e:
        print(f"前置登录准备失败，将以匿名模式进行压测。原因: {e}")
    return False

# 2. 单个线程执行任务的函数
def worker_task(url, method, payload, headers, requests_per_thread):
    for _ in range(requests_per_thread):
        start_time = time.time()
        success = False
        try:
            if method.upper() == "GET":
                response = requests.get(url, headers=headers, params=payload, timeout=5)
            elif method.upper() == "POST":
                response = requests.post(url, headers=headers, json=payload, timeout=5)
            elif method.upper() == "PUT":
                response = requests.put(url, headers=headers, json=payload, timeout=5)
            elif method.upper() == "DELETE":
                response = requests.delete(url, headers=headers, params=payload, timeout=5)
            else:
                response = None
            
            # 判断响应是否成功
            if response is not None and response.status_code == 200:
                # 检查后端 API 响应码是否为 200
                res_code = response.json().get("code")
                if res_code in [200, "200"]:
                    success = True
        except Exception:
            success = False
        
        duration = time.time() - start_time
        
        # 将测试结果记录到全局列表中
        with results_lock:
            results.append({
                "duration": duration,
                "success": success
            })

# 3. 运行性能测试的核心逻辑
def run_perf_test(api_name, url, method, payload, headers, num_threads, requests_per_thread):
    global results
    results = []  # 重置结果列表
    
    print("\n" + "=" * 50)
    print(f" 开始性能测试: {api_name} ".center(50, "="))
    print("=" * 50)
    print(f"目标地址: {url}")
    print(f"并发线程数 (模拟用户数): {num_threads}")
    print(f"每个线程发送请求数: {requests_per_thread}")
    print(f"总预期请求次数: {num_threads * requests_per_thread}")
    print("正在发送请求中，请稍候...")
    
    start_all = time.time()
    
    # 创建并启动线程
    threads = []
    for _ in range(num_threads):
        t = threading.Thread(
            target=worker_task, 
            args=(url, method, payload, headers, requests_per_thread)
        )
        threads.append(t)
        t.start()
        
    # 等待所有线程执行完毕
    for t in threads:
        t.join()
        
    total_time = time.time() - start_all
    
    # 4. 统计并计算结果
    if not results:
        print("未收集到任何测试结果。")
        return
        
    total_reqs = len(results)
    success_reqs = sum(1 for r in results if r["success"])
    fail_reqs = total_reqs - success_reqs
    success_rate = (success_reqs / total_reqs) * 100
    
    durations = [r["duration"] for r in results]
    avg_time = sum(durations) / len(durations)
    min_time = min(durations)
    max_time = max(durations)
    
    # QPS (吞吐量): 每秒处理完成的请求数
    qps = total_reqs / total_time
    
    # 输出结果报告
    print("\n" + "-" * 20 + " 压测报告 " + "-" * 20)
    print(f"测试用时:       {total_time:.2f} 秒")
    print(f"总请求次数:     {total_reqs} 次")
    print(f"成功请求次数:   {success_reqs} 次")
    print(f"失败请求次数:   {fail_reqs} 次")
    print(f"请求成功率:     {success_rate:.2f}%")
    print(f"平均响应时间:   {avg_time:.4f} 秒")
    print(f"最快响应时间:   {min_time:.4f} 秒")
    print(f"最慢响应时间:   {max_time:.4f} 秒")
    print(f"吞吐量 (QPS):   {qps:.2f} 次/秒")
    print("=" * 50 + "\n")

# 主程序入口
if __name__ == "__main__":
    print("正在连接后端获取 JWT 凭证...")
    has_token = prepare_auth()
    
    if has_token:
        print("Token 准备完毕，已成功进行身份认证。")
    else:
        print("注意: 无法正常登录，部分需要授权的接口可能会被拦截，响应可能返回 403/401 失败。")
        
    # 定义可选的压测接口列表
    api_options = [
        {
            "name": "获取全部课程 (GET /courses/all)",
            "url": f"{config.JAVA_BACKEND_URL}/courses/all",
            "method": "GET",
            "payload": {},
            "headers": {"Authorization": f"Bearer {TOKEN}"} if TOKEN else {}
        },
        {
            "name": "获取用户信息 (GET /users/info)",
            "url": f"{config.JAVA_BACKEND_URL}/users/info",
            "method": "GET",
            "payload": {"user_id": USER_ID} if USER_ID else {"user_id": 1},
            "headers": {"Authorization": f"Bearer {TOKEN}"} if TOKEN else {}
        },
        {
            "name": "获取笔记列表 (GET /notes/list)",
            "url": f"{config.JAVA_BACKEND_URL}/notes/list",
            "method": "GET",
            "payload": {"student_id": USER_ID, "course_id": config.TEST_COURSE_ID} if USER_ID else {"course_id": config.TEST_COURSE_ID},
            "headers": {"Authorization": f"Bearer {TOKEN}"} if TOKEN else {}
        }
    ]
    
    # 交互式选择
    print("\n请选择您想压测的接口:")
    for idx, opt in enumerate(api_options):
        print(f"[{idx + 1}] {opt['name']}")
    print(f"[{len(api_options) + 1}] 退出程序")
    
    try:
        choice_str = input("请输入数字序号 (默认回车选择 1): ").strip()
        if not choice_str:
            choice = 0
        else:
            choice = int(choice_str) - 1
            
        if choice == len(api_options):
            print("程序已退出。")
            sys.exit(0)
            
        if choice < 0 or choice > len(api_options):
            print("选择无效，默认压测第 1 个接口。")
            choice = 0
            
        selected_api = api_options[choice]
        
        # 线程数设置
        threads_str = input("请输入模拟并发用户数 (线程数，推荐 5 ~ 50，默认 10): ").strip()
        num_threads = int(threads_str) if threads_str else 10
        
        # 每个线程请求数设置
        reqs_str = input("请输入每个用户发送的请求次数 (推荐 5 ~ 100，默认 10): ").strip()
        requests_per_thread = int(reqs_str) if reqs_str else 10
        
        # 运行测试
        run_perf_test(
            api_name=selected_api["name"],
            url=selected_api["url"],
            method=selected_api["method"],
            payload=selected_api["payload"],
            headers=selected_api["headers"],
            num_threads=num_threads,
            requests_per_thread=requests_per_thread
        )
        
    except (KeyboardInterrupt, SystemExit):
        print("\n测试已终止。")
    except Exception as e:
        print(f"\n输入无效或运行发生错误: {e}")
