from typing import Any, Literal
from pydantic import BaseModel, Field


QuestionType = Literal[
    "single_choice",
    "multiple_choice",
    "true_false",
    "fill_blank",
    "essay",
]


class ApiResponse(BaseModel):
    code: int = 200
    message: str = "success"
    data: Any | None = None


class ChatRequest(BaseModel):
    question: str = Field(..., min_length=1, description="用户问题")
    user_id: str | None = Field(default=None, description="用户ID，可用于后续保存历史")
    course_name: str | None = Field(default=None, description="课程名")
    chapter_name: str | None = Field(default=None, description="章节名")
    context: str | None = Field(default=None, description="课程资料、检索片段或上下文")
    history: list[dict[str, str]] = Field(default_factory=list, description="历史对话")
    model: str | None = Field(default=None, description="覆盖默认模型")
    temperature: float = Field(default=0.3, ge=0, le=2)


class ChatAnswer(BaseModel):
    answer: str
    model: str


class ExerciseGenerateRequest(BaseModel):
    course_id: str | None = None
    course_name: str = Field(..., min_length=1)
    chapter_id: str | None = None
    chapter_name: str = Field(..., min_length=1)
    question_type: QuestionType
    question_count: int = Field(default=5, ge=1, le=20)
    difficulty: int = Field(default=2, ge=1, le=5)
    knowledge_points: list[str] = Field(default_factory=list)
    context: str | None = Field(default=None, description="课程资料、教材片段或本地知识库召回内容")
    extra_requirements: str | None = Field(default=None, description="额外出题要求")
    model: str | None = None
    temperature: float = Field(default=0.2, ge=0, le=2)


class ExerciseOption(BaseModel):
    option_id: str
    option_text: str
    is_correct: bool


class ExerciseQuestion(BaseModel):
    question_id: str | None = None
    question_type: QuestionType
    question: str
    options: list[ExerciseOption] = Field(default_factory=list)
    answer: str
    analysis: str
    knowledge_points: list[str] = Field(default_factory=list)
    difficulty: int = Field(default=2, ge=1, le=5)


class ExerciseGenerateResult(BaseModel):
    total_questions: int
    questions: list[ExerciseQuestion]
    model: str
