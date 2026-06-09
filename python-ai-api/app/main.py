from fastapi import FastAPI

from app.config import get_settings
from app.json_utils import extract_json_object
from app.llm_client import LLMClientError, SiliconFlowClient
from app.prompts import build_chat_messages, build_exercise_messages
from app.schemas import (
    ApiResponse,
    ChatAnswer,
    ChatRequest,
    ExerciseGenerateRequest,
    ExerciseGenerateResult,
    ExerciseQuestion,
)


app = FastAPI(
    title="eduSys Python AI API",
    description="SiliconFlow 大模型调用封装，供 Java 后端调用。",
    version="0.1.0",
)


def dump_model(model):
    if hasattr(model, "model_dump"):
        return model.model_dump()
    return model.dict()


def get_llm_client() -> SiliconFlowClient:
    return SiliconFlowClient(get_settings())


@app.get("/health")
def health() -> ApiResponse:
    settings = get_settings()
    return ApiResponse(
        data={
            "status": "ok",
            "base_url": settings.siliconflow_base_url,
            "default_model": settings.default_model,
            "api_key_configured": bool(settings.siliconflow_api_key),
        }
    )


@app.post("/api/v1/chat", response_model=ApiResponse)
def chat(request: ChatRequest) -> ApiResponse:
    try:
        result = get_llm_client().chat_completion(
            messages=build_chat_messages(request),
            model=request.model,
            temperature=request.temperature,
            max_tokens=2048,
        )
        return ApiResponse(data=dump_model(ChatAnswer(answer=result.content, model=result.model)))
    except LLMClientError as exc:
        return ApiResponse(code=500, message=str(exc), data=None)


@app.post("/api/v1/exercises/generate", response_model=ApiResponse)
def generate_exercises(request: ExerciseGenerateRequest) -> ApiResponse:
    try:
        result = get_llm_client().chat_completion(
            messages=build_exercise_messages(request),
            model=request.model,
            temperature=request.temperature,
            max_tokens=4096,
        )
        payload = extract_json_object(result.content)
        questions = [ExerciseQuestion(**item) for item in payload.get("questions", [])]

        if len(questions) != request.question_count:
            return ApiResponse(
                code=422,
                message=f"模型返回题目数量为 {len(questions)}，期望 {request.question_count}",
                data={"raw_output": result.content},
            )

        response_data = ExerciseGenerateResult(
            total_questions=len(questions),
            questions=questions,
            model=result.model,
        )
        return ApiResponse(data=dump_model(response_data))
    except (LLMClientError, ValueError) as exc:
        return ApiResponse(code=500, message=str(exc), data=None)
    except Exception as exc:
        return ApiResponse(code=422, message=f"题目 JSON 字段校验失败: {exc}", data=None)
