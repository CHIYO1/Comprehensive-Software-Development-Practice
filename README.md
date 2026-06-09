### 软件开发综合实践

本项目为《软件开发综合实践》课程项目，旨在通过完整的软件开发流程，设计并实现一个具有实际应用价值的软件系统。

在项目开发过程中，我们按照软件工程的方法进行需求分析、系统设计、编码实现与系统测试，提升团队的软件开发能力与工程实践能力。

本项目通过模块化设计与面向对象开发方法，实现系统的各项功能模块，并利用 GitHub 进行代码版本管理与协作开发。

本仓库包含项目源代码、系统设计文档以及相关资源文件。

#### 项目介绍

#### 项目架构

- **spring-eduSys**: 后端项目
- **vue-eduSys**: 前端项目
- **python-ai-api**: Python 大模型 API 服务，封装 SiliconFlow 直接调用能力，供 Java 后端通过 HTTP 调用
- **gpt.sql**: 数据库文件

#### 开发环境
- 前端
    - **node.js**
    - **vue3**
- 后端 
    - **jdk21**
    - **maven3.9.9**
- 大模型服务
    - **python3.10+**
    - **fastapi**
    - **uvicorn**
- 数据库
    - **mysql5.7.38**

#### 开发平台(仅推荐)
- 后端: **IntelliJ IDEA**
- 前端: **visual studio code**
- 数据库管理工具: **Navicat**
- git管理工具: **SourceTree**

#### 运行教程
1.  使用git管理工具拉取项目
2.  下载安装配置开发环境。
3.  导入sql文件到数据库，使用开发平台分别打开前后端项目。(注意修改后端**application.properties**文件中的数据库连接等配置信息)
4.  后端运行**GptApplication.java**文件、前端在终端运行**npm run serve**命令。
5.  如需使用 Python 大模型 API 服务，进入 **python-ai-api** 目录安装依赖并启动服务。

#### Python 大模型 API 服务

该服务用于直接调用 SiliconFlow 大模型，并将能力封装成可被 Java 后端调用的 HTTP 接口。

已实现接口：

- `GET /health`：健康检查
- `POST /api/v1/chat`：学习问答
- `POST /api/v1/exercises/generate`：练习题生成，支持单选、多选、判断、填空、简答

启动方式：

```powershell
cd python-ai-api
pip install -r requirements.txt
$env:SILICONFLOW_API_KEY="替换为你的SiliconFlow密钥"
$env:SILICONFLOW_MODEL="Qwen/Qwen3-32B"
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

启动后访问：

- API 文档：`http://localhost:8000/docs`
- 健康检查：`http://localhost:8000/health`

注意：真实密钥只允许通过环境变量或本地 `.env` 配置，不要提交到 GitHub。仓库只保留 `python-ai-api/.env.example`。

Java 后端可通过 HTTP 调用该服务，例如将服务地址配置为：

```properties
ai.python.base-url=http://localhost:8000
```

推荐流程：

```text
Java 后端接收前端请求
-> 查询课程、章节、知识点或知识库片段
-> 调用 python-ai-api
-> 获得大模型结构化结果
-> Java 后端写入数据库并返回前端
```

#### 其它说明
- 省略了FastGPT的docker安装及配置（非必要）
- SiliconFlow、FastGPT、Dify 等模型平台密钥均不应提交到 GitHub。
