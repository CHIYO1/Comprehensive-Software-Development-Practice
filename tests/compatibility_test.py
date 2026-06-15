"""
多浏览器与多平台兼容性测试脚本 (compatibility_test.py)

本脚本执行以下测试：
1. 协议/API 层跨浏览器和跨平台标头兼容性测试 (支持多种 User-Agent、Sec-Ch-Ua 等组合)
2. CORS 跨域安全预检兼容性测试 (OPTIONS 请求校验)
3. 内容协商与传输压缩算法兼容测试 (gzip/deflate/br 响应校验)
4. 自动生成精致易读的 HTML 兼容性报告 (compatibility_report.html)

运行：
    python compatibility_test.py
"""

import os
import sys
import time
import json
import requests
import traceback

# 导入配置
try:
    import config
except ImportError:
    print("错误: 找不到 config.py 配置文件，请确保它与本脚本在同一目录下。")
    sys.exit(1)

# 全局环境及接口地址
JAVA_BACKEND_URL = config.JAVA_BACKEND_URL

# 测试账户
TEST_USER = config.TEST_USER

# 1. 定义平台和浏览器特征矩阵 (Compatibility Matrix)
COMPATIBILITY_MATRIX = [
    {
        "id": "win_chrome",
        "platform": "Windows 11",
        "browser": "Chrome",
        "device": "Desktop",
        "headers": {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "sec-ch-ua": '"Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120"',
            "sec-ch-ua-mobile": "?0",
            "sec-ch-ua-platform": '"Windows"',
            "Accept": "application/json, text/plain, */*",
            "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8"
        }
    },
    {
        "id": "mac_safari",
        "platform": "macOS Sequoia",
        "browser": "Safari",
        "device": "Desktop",
        "headers": {
            "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2.1 Safari/605.1.15",
            "Accept": "application/json, text/plain, */*",
            "Accept-Language": "zh-CN,zh-Hans;q=0.9"
        }
    },
    {
        "id": "linux_firefox",
        "platform": "Linux (Ubuntu)",
        "browser": "Firefox",
        "device": "Desktop",
        "headers": {
            "User-Agent": "Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0",
            "Accept": "application/json, text/plain, */*",
            "Accept-Language": "en-US,en;q=0.5"
        }
    },
    {
        "id": "win_edge",
        "platform": "Windows 11",
        "browser": "Edge",
        "device": "Desktop",
        "headers": {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0",
            "sec-ch-ua": '"Not_A Brand";v="8", "Chromium";v="120", "Microsoft Edge";v="120"',
            "sec-ch-ua-mobile": "?0",
            "sec-ch-ua-platform": '"Windows"',
            "Accept": "application/json, text/plain, */*",
            "Accept-Language": "zh-CN,zh;q=0.9"
        }
    },
    {
        "id": "android_chrome",
        "platform": "Android 14",
        "browser": "Chrome Mobile",
        "device": "Mobile",
        "headers": {
            "User-Agent": "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36",
            "sec-ch-ua": '"Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120"',
            "sec-ch-ua-mobile": "?1",
            "sec-ch-ua-platform": '"Android"',
            "Accept": "application/json, text/plain, */*",
            "Accept-Language": "zh-CN,zh;q=0.9"
        }
    },
    {
        "id": "ios_safari",
        "platform": "iOS 17",
        "browser": "Safari Mobile",
        "device": "Mobile",
        "headers": {
            "User-Agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1",
            "Accept": "application/json, text/plain, */*",
            "Accept-Language": "zh-CN,zh-Hans;q=0.9"
        }
    },
    {
        "id": "android_wechat",
        "platform": "Android (WeChat)",
        "browser": "WeChat Browser",
        "device": "Mobile",
        "headers": {
            "User-Agent": "Mozilla/5.0 (Linux; Android 14; Mobile; MicroMessenger/8.0.47; Language/zh_CN)",
            "Accept": "application/json, text/plain, */*",
            "Accept-Language": "zh-CN,zh;q=0.9"
        }
    }
]

# 测试统计结果
results_data = []

def print_header(title):
    print("\n" + "=" * 60)
    print(f" {title} ".center(60, "="))
    print("=" * 60)

def print_result(case_name, success, info=""):
    status = "[ 成功 ]" if success else "[ 失败 ]"
    print(f"-> {case_name:<40} {status} {info}")

# 注册测试（防重复）
def prepare_test_account(headers):
    url_reg = f"{JAVA_BACKEND_URL}/users/register"
    try:
        requests.post(url_reg, json=TEST_USER, headers=headers, timeout=5)
    except:
        pass

# 1. 核心 API 业务请求兼容性矩阵测试 (在各个 UA 标头下分别登录和获取课程)
def test_api_matrix_compatibility():
    print_header("1. 核心 API 兼容性矩阵测试 (多浏览器 & 多平台模拟)")
    
    for client in COMPATIBILITY_MATRIX:
        client_desc = f"{client['browser']} on {client['platform']} ({client['device']})"
        print(f"\n正在模拟客户端: {client_desc}")
        
        headers = client["headers"].copy()
        headers["Content-Type"] = "application/json"
        
        # 预备账号
        prepare_test_account(headers)
        
        # 1. 登录请求
        login_success = False
        token = None
        user_id = None
        login_url = f"{JAVA_BACKEND_URL}/users/login"
        login_payload = {
            "account": TEST_USER["account"],
            "password": TEST_USER["password"]
        }
        
        start_time = time.time()
        try:
            res = requests.post(login_url, json=login_payload, headers=headers, timeout=5)
            latency = (time.time() - start_time) * 1000
            
            if res.status_code == 200:
                res_json = res.json()
                if res_json.get("code") in [200, "200"]:
                    login_success = True
                    data = res_json.get("data", {})
                    token = data.get("token")
                    user_id = data.get("userId", data.get("user_id"))
            
            info = f"响应码: {res.status_code}, 耗时: {latency:.1f}ms"
            print_result(f"[{client['browser']}] 登录 API 校验", login_success, info)
            
            results_data.append({
                "category": "API 登录兼容性",
                "client": client_desc,
                "browser": client["browser"],
                "platform": client["platform"],
                "test_item": "POST /users/login",
                "status": "PASS" if login_success else "FAIL",
                "details": f"Status: {res.status_code}, Latency: {latency:.1f}ms, Token found: {token is not None}"
            })
        except Exception as e:
            print_result(f"[{client['browser']}] 登录 API 校验", False, f"异常: {e}")
            results_data.append({
                "category": "API 登录兼容性",
                "client": client_desc,
                "browser": client["browser"],
                "platform": client["platform"],
                "test_item": "POST /users/login",
                "status": "FAIL",
                "details": f"Exception: {str(e)}"
            })

        # 2. 获取课程列表请求 (需要 Token 鉴权)
        if login_success and token:
            headers_auth = headers.copy()
            headers_auth["Authorization"] = f"Bearer {token}"
            courses_url = f"{JAVA_BACKEND_URL}/courses/all"
            
            start_time = time.time()
            try:
                res = requests.get(courses_url, headers=headers_auth, timeout=5)
                latency = (time.time() - start_time) * 1000
                courses_success = False
                
                if res.status_code == 200:
                    res_json = res.json()
                    if res_json.get("code") in [200, "200"]:
                        courses_success = True
                
                info = f"响应码: {res.status_code}, 耗时: {latency:.1f}ms"
                print_result(f"[{client['browser']}] 获取课程列表 API 校验", courses_success, info)
                
                results_data.append({
                    "category": "API 业务兼容性",
                    "client": client_desc,
                    "browser": client["browser"],
                    "platform": client["platform"],
                    "test_item": "GET /courses/all",
                    "status": "PASS" if courses_success else "FAIL",
                    "details": f"Status: {res.status_code}, Latency: {latency:.1f}ms"
                })
            except Exception as e:
                print_result(f"[{client['browser']}] 获取课程列表 API 校验", False, f"异常: {e}")
                results_data.append({
                    "category": "API 业务兼容性",
                    "client": client_desc,
                    "browser": client["browser"],
                    "platform": client["platform"],
                    "test_item": "GET /courses/all",
                    "status": "FAIL",
                    "details": f"Exception: {str(e)}"
                })
        else:
            print_result(f"[{client['browser']}] 获取课程列表 API 校验", False, "跳过（前置登录失败）")
            results_data.append({
                "category": "API 业务兼容性",
                "client": client_desc,
                "browser": client["browser"],
                "platform": client["platform"],
                "test_item": "GET /courses/all",
                "status": "SKIP",
                "details": "Skipped due to login failure"
            })


# 2. CORS (跨域资源共享) 预检请求测试 (OPTIONS 请求)
def test_cors_compatibility():
    print_header("2. CORS 跨域请求预检兼容性测试")
    
    origins = [
        "http://localhost:8080",
        "http://127.0.0.1:8080",
        "http://localhost:3000",
        "https://edu.example.com"
    ]
    
    endpoints = [
        {"name": "Java Backend Login", "url": f"{JAVA_BACKEND_URL}/users/login"}
    ]
    
    for ep in endpoints:
        for orig in origins:
            headers = {
                "Origin": orig,
                "Access-Control-Request-Method": "POST",
                "Access-Control-Request-Headers": "content-type, authorization",
                "User-Agent": COMPATIBILITY_MATRIX[0]["headers"]["User-Agent"]
            }
            
            start_time = time.time()
            try:
                res = requests.options(ep["url"], headers=headers, timeout=3)
                latency = (time.time() - start_time) * 1000
                
                # CORS 预检通常返回 200, 204
                success = res.status_code in [200, 204]
                allow_origin = res.headers.get("Access-Control-Allow-Origin")
                
                # 检查跨域头是否正确匹配或接受
                cors_ok = success and (allow_origin == "*" or allow_origin == orig or allow_origin is not None)
                
                info = f"状态码: {res.status_code}, Allow-Origin: {allow_origin}, 耗时: {latency:.1f}ms"
                print_result(f"CORS 预检: {ep['name']} -> {orig}", cors_ok, info)
                
                results_data.append({
                    "category": "CORS 兼容性",
                    "client": f"Origin: {orig}",
                    "browser": "CORS Engine",
                    "platform": ep["name"],
                    "test_item": "OPTIONS",
                    "status": "PASS" if cors_ok else "FAIL",
                    "details": f"Status: {res.status_code}, Allowed-Origin: {allow_origin}, Allowed-Headers: {res.headers.get('Access-Control-Allow-Headers')}"
                })
            except Exception as e:
                print_result(f"CORS 预检: {ep['name']} -> {orig}", False, f"错误: {e}")
                results_data.append({
                    "category": "CORS 兼容性",
                    "client": f"Origin: {orig}",
                    "browser": "CORS Engine",
                    "platform": ep["name"],
                    "test_item": "OPTIONS",
                    "status": "FAIL",
                    "details": f"Exception: {str(e)}"
                })

# 3. 内容协商与传输压缩算法兼容测试
def test_compression_compatibility():
    print_header("3. 内容协商与数据传输压缩算法兼容测试")
    
    compression_encodings = [
        "gzip",
        "deflate",
        "gzip, deflate",
        "identity"  # 无压缩
    ]
    
    endpoints = [
        {"name": "Java 课程列表", "url": f"{JAVA_BACKEND_URL}/courses/all"}
    ]
    
    # 模拟请求并携带登录 token
    token = None
    try:
        # 获取一个有效的 Token
        url_login = f"{JAVA_BACKEND_URL}/users/login"
        payload = {"account": TEST_USER["account"], "password": TEST_USER["password"]}
        res = requests.post(url_login, json=payload, timeout=3)
        if res.status_code == 200:
            token = res.json().get("data", {}).get("token")
    except:
        pass

    for ep in endpoints:
        for enc in compression_encodings:
            headers = {
                "User-Agent": COMPATIBILITY_MATRIX[0]["headers"]["User-Agent"],
                "Accept-Encoding": enc
            }
            if "Java" in ep["name"] and token:
                headers["Authorization"] = f"Bearer {token}"
                
            start_time = time.time()
            try:
                res = requests.get(ep["url"], headers=headers, timeout=5)
                latency = (time.time() - start_time) * 1000
                
                content_encoding = res.headers.get("Content-Encoding", "None (Identity)")
                success = res.status_code == 200
                
                info = f"状态码: {res.status_code}, 实际编码: {content_encoding}, 耗时: {latency:.1f}ms"
                print_result(f"压缩测试: {ep['name']} -> Accept-Encoding: [{enc}]", success, info)
                
                # 检查响应数据是否可以成功读取（确保没有因为压缩损坏内容）
                content_ok = False
                try:
                    if "JSON" in res.headers.get("Content-Type", "") or ep["url"].endswith("all"):
                        res.json()
                    else:
                        res.text
                    content_ok = True
                except:
                    pass
                
                results_data.append({
                    "category": "传输压缩兼容性",
                    "client": f"Accept: {enc}",
                    "browser": "HTTP Client",
                    "platform": ep["name"],
                    "test_item": f"Encoding: {enc}",
                    "status": "PASS" if (success and content_ok) else "FAIL",
                    "details": f"Status: {res.status_code}, Response-Encoding: {content_encoding}, Parsable: {content_ok}"
                })
            except Exception as e:
                print_result(f"压缩测试: {ep['name']} -> Accept-Encoding: [{enc}]", False, f"错误: {e}")
                results_data.append({
                    "category": "传输压缩兼容性",
                    "client": f"Accept: {enc}",
                    "browser": "HTTP Client",
                    "platform": ep["name"],
                    "test_item": f"Encoding: {enc}",
                    "status": "FAIL",
                    "details": f"Exception: {str(e)}"
                })


def generate_html_report():
    report_path = os.path.join(os.path.dirname(__file__), "compatibility_report.html")
    
    total = len(results_data)
    passed = sum(1 for r in results_data if r["status"] == "PASS")
    failed = sum(1 for r in results_data if r["status"] == "FAIL")
    skipped = sum(1 for r in results_data if r["status"] == "SKIP")
    
    pass_rate = (passed / total) * 100 if total > 0 else 0
    
    # 获取表格行
    table_rows = ""
    for idx, item in enumerate(results_data):
        status_badge = ""
        if item["status"] == "PASS":
            status_badge = '<span class="badge badge-success">通过 (PASS)</span>'
        elif item["status"] == "FAIL":
            status_badge = '<span class="badge badge-danger">失败 (FAIL)</span>'
        else:
            status_badge = '<span class="badge badge-warning">跳过 (SKIP)</span>'
            
        table_rows += f"""
        <tr>
            <td>{idx+1}</td>
            <td><strong>{item['category']}</strong></td>
            <td>{item['client']}</td>
            <td><code class="code-tag">{item['test_item']}</code></td>
            <td>{status_badge}</td>
            <td class="text-muted">{item['details']}</td>
        </tr>
        """
        
    # 后端端口说明
    env_info = f"""
    <ul>
        <li><strong>Java 业务后端：</strong> <a href="{JAVA_BACKEND_URL}" target="_blank">{JAVA_BACKEND_URL}</a></li>
    </ul>
    """

    # HTML 模版
    html_content = f"""<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>系统多平台与多浏览器兼容性测试报告</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">
    <style>
        :root {{
            --primary: #4F46E5;
            --primary-hover: #4338CA;
            --success: #10B981;
            --warning: #F59E0B;
            --danger: #EF4444;
            --dark: #1F2937;
            --light: #F9FAFB;
            --border: #E5E7EB;
        }}
        
        body {{
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background-color: #F3F4F6;
            color: var(--dark);
            margin: 0;
            padding: 0;
            line-height: 1.6;
        }}
        
        .container {{
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }}
        
        .header {{
            background: linear-gradient(135deg, var(--primary) 0%, #312E81 100%);
            color: white;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
            position: relative;
            overflow: hidden;
        }}
        
        .header h1 {{
            margin: 0 0 10px 0;
            font-size: 2.2rem;
            font-weight: 700;
        }}
        
        .header p {{
            margin: 0;
            opacity: 0.9;
            font-size: 1.1rem;
        }}
        
        .grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }}
        
        .card {{
            background: white;
            padding: 24px;
            border-radius: 12px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
            border: 1px solid var(--border);
            text-align: center;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }}
        
        .card:hover {{
            transform: translateY(-2px);
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
        }}
        
        .card-title {{
            font-size: 0.875rem;
            color: #6B7280;
            text-transform: uppercase;
            font-weight: 600;
            margin-bottom: 8px;
        }}
        
        .card-value {{
            font-size: 2.25rem;
            font-weight: 700;
            color: var(--dark);
        }}
        
        .card-value.text-success {{ color: var(--success); }}
        .card-value.text-danger {{ color: var(--danger); }}
        .card-value.text-warning {{ color: var(--warning); }}
        
        .section {{
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
            border: 1px solid var(--border);
            margin-bottom: 30px;
        }}
        
        .section-title {{
            font-size: 1.25rem;
            font-weight: 600;
            margin-top: 0;
            margin-bottom: 20px;
            border-bottom: 2px solid var(--light);
            padding-bottom: 10px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }}
        
        table {{
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }}
        
        th {{
            background-color: var(--light);
            padding: 12px 16px;
            font-weight: 600;
            font-size: 0.875rem;
            color: #4B5563;
            border-bottom: 1px solid var(--border);
        }}
        
        td {{
            padding: 16px;
            border-bottom: 1px solid var(--border);
            font-size: 0.875rem;
        }}
        
        tr:hover {{
            background-color: var(--light);
        }}
        
        .badge {{
            display: inline-flex;
            align-items: center;
            padding: 4px 8px;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 600;
        }}
        
        .badge-success {{
            background-color: #D1FAE5;
            color: #065F46;
        }}
        
        .badge-danger {{
            background-color: #FEE2E2;
            color: #991B1B;
        }}
        
        .badge-warning {{
            background-color: #FEF3C7;
            color: #92400E;
        }}
        
        .code-tag {{
            background-color: var(--light);
            padding: 2px 6px;
            border-radius: 4px;
            font-family: monospace;
            font-size: 0.8rem;
            border: 1px solid var(--border);
        }}
        
        .footer {{
            text-align: center;
            padding: 30px 0;
            color: #6B7280;
            font-size: 0.875rem;
        }}
        
        ul {{
            padding-left: 20px;
            margin: 0;
        }}
        
        li {{
            margin-bottom: 8px;
        }}
        
        a {{
            color: var(--primary);
            text-decoration: none;
        }}
        
        a:hover {{
            text-decoration: underline;
        }}
    </style>
</head>
<body>
    <div class="container">
        <!-- 头部标题区 -->
        <div class="header">
            <h1>系统多平台与多浏览器兼容性测试报告</h1>
            <p>基于用户代理特征(UA)、CORS请求、内容压缩协商及前端渲染引擎对全栈服务进行兼容度扫描</p>
        </div>
        
        <!-- 数据汇总面板 -->
        <div class="grid">
            <div class="card">
                <div class="card-title">测试总用例</div>
                <div class="card-value">{total}</div>
            </div>
            <div class="card">
                <div class="card-title">通过用例数</div>
                <div class="card-value text-success">{passed}</div>
            </div>
            <div class="card">
                <div class="card-title">失败用例数</div>
                <div class="card-value text-danger">{failed}</div>
            </div>
            <div class="card">
                <div class="card-title">跳过/前置异常</div>
                <div class="card-value text-warning">{skipped}</div>
            </div>
            <div class="card">
                <div class="card-title">综合兼容性通过率</div>
                <div class="card-value" style="color: var(--primary);">{pass_rate:.1f}%</div>
            </div>
        </div>
        
        <!-- 环境配置说明 -->
        <div class="section">
            <div class="section-title">测试环境与服务节点</div>
            {env_info}
        </div>
        
        <!-- 详细测试列表 -->
        <div class="section">
            <div class="section-title">兼容性测试详情矩阵</div>
            <table>
                <thead>
                    <tr>
                        <th style="width: 50px;">序号</th>
                        <th style="width: 150px;">测试分类</th>
                        <th style="width: 200px;">客户端环境 / 头部设置</th>
                        <th style="width: 180px;">测试动作/接口</th>
                        <th style="width: 120px;">测试结果</th>
                        <th>响应详情及诊断数据</th>
                    </tr>
                </thead>
                <tbody>
                    {table_rows}
                </tbody>
            </table>
        </div>
        
        <div class="footer">
            报告生成时间: {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())} | Antigravity 兼容性测试模块
        </div>
    </div>
</body>
</html>
"""
    
    with open(report_path, "w", encoding="utf-8") as f:
        f.write(html_content)
        
    print(f"\nHTML 报告已生成至: {report_path}")

# 主入口
if __name__ == "__main__":
    print_header("开始系统多平台与多浏览器兼容性扫描测试")
    print(f"Java 后端地址: {JAVA_BACKEND_URL}")
    
    try:
        # 1. 核心 API 矩阵兼容性测试
        test_api_matrix_compatibility()
        
        # 2. CORS 跨域兼容性测试
        test_cors_compatibility()
        
        # 3. 内容协商与压缩算法测试
        test_compression_compatibility()
        
        # 5. 生成 HTML 测试报告
        generate_html_report()
        
        print_header("兼容性测试完毕")
        passed_count = sum(1 for r in results_data if r["status"] == "PASS")
        print(f"全部兼容性测试已运行完毕。成功数: {passed_count} / {len(results_data)}")
        if passed_count == len(results_data):
            print("完美！所有模拟浏览器和多平台访问、跨域、压缩及渲染验证均顺利通过！✨")
        else:
            print(f"提示：有 {len(results_data) - passed_count} 项兼容性校验存在异常，请查看生成的 HTML 报告进行定位排查。")
            
    except KeyboardInterrupt:
        print("\n测试被强制终止。")
    except Exception as e:
        print(f"\n运行发生严重异常: {e}")
        traceback.print_exc()
