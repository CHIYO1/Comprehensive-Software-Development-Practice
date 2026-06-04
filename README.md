# eduSys Python AI API

这个服务把 SiliconFlow 大模型调用封装成 HTTP API，供 Java 后端直接调用。

当前已实现：

- `POST /api/v1/chat`：学习问答
- `POST /api/v1/exercises/generate`：练习题生成
- `GET /health`：服务健康检查

## 1. 环境准备

```powershell
cd python_ai_api
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

配置密钥，推荐使用环境变量：

```powershell
$env:SILICONFLOW_API_KEY="sk-你的SiliconFlow密钥"
$env:SILICONFLOW_MODEL="Qwen/Qwen3-32B"
```

不要把真实密钥提交到 Git 仓库。

## 2. 启动服务

```powershell
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

启动后可访问：

- Swagger 文档：`http://localhost:8000/docs`
- 健康检查：`http://localhost:8000/health`

## 3. 问答接口

请求：

```http
POST http://localhost:8000/api/v1/chat
Content-Type: application/json
```

示例请求体见 `examples/chat_request.json`。

响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "answer": "模型回答",
    "model": "Qwen/Qwen3-32B"
  }
}
```

## 4. 练习题生成接口

请求：

```http
POST http://localhost:8000/api/v1/exercises/generate
Content-Type: application/json
```

示例请求体见 `examples/exercise_request.json`。

支持题型：

- `single_choice`
- `multiple_choice`
- `true_false`
- `fill_blank`
- `essay`

响应格式与原接口文档保持接近：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total_questions": 3,
    "questions": [
      {
        "question_id": null,
        "question_type": "single_choice",
        "question": "以下哪个是 Python 合法变量名？",
        "options": [
          {"option_id": "A", "option_text": "2name", "is_correct": false},
          {"option_id": "B", "option_text": "my-name", "is_correct": false},
          {"option_id": "C", "option_text": "_name", "is_correct": true},
          {"option_id": "D", "option_text": "my name", "is_correct": false}
        ],
        "answer": "C",
        "analysis": "Python 变量名不能以数字开头，不能包含空格或连字符。",
        "knowledge_points": ["变量命名"],
        "difficulty": 2
      }
    ],
    "model": "Qwen/Qwen3-32B"
  }
}
```

## 5. curl 测试

```powershell
curl.exe -X POST "http://localhost:8000/api/v1/chat" `
  -H "Content-Type: application/json" `
  --data "@examples/chat_request.json"
```

```powershell
curl.exe -X POST "http://localhost:8000/api/v1/exercises/generate" `
  -H "Content-Type: application/json" `
  --data "@examples/exercise_request.json"
```

## 6. Java 后端调用建议

Spring Boot 后端可以把 Python 服务地址配置为：

```properties
ai.python.base-url=http://localhost:8000
```

然后用 `RestTemplate` 或 `WebClient` 调用：

```java
String url = "http://localhost:8000/api/v1/chat";
Map<String, Object> body = Map.of(
    "user_id", userId,
    "course_name", courseName,
    "chapter_name", chapterName,
    "question", question,
    "context", context
);
ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
```

后续如果要接入 RAG，可以让 Java 先从数据库/知识库取出课程资料片段，再放到 `context` 字段；也可以在 Python 服务里继续增加知识库上传和检索接口。
