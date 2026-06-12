from functools import lru_cache
from pydantic import BaseModel
import os


class Settings(BaseModel):
    siliconflow_api_key: str = ""
    siliconflow_base_url: str = "https://api.siliconflow.cn/v1"
    default_model: str = "Qwen/Qwen3-32B"
    request_timeout_seconds: int = 120


@lru_cache
def get_settings() -> Settings:
    return Settings(
        siliconflow_api_key=os.getenv("SILICONFLOW_API_KEY", ""),
        siliconflow_base_url=os.getenv("SILICONFLOW_BASE_URL", "https://api.siliconflow.cn/v1"),
        default_model=os.getenv("SILICONFLOW_MODEL", "Qwen/Qwen3-32B"),
        request_timeout_seconds=int(os.getenv("LLM_REQUEST_TIMEOUT_SECONDS", "120")),
    )
