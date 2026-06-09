import json
import urllib.error
import urllib.request
from dataclasses import dataclass
from typing import Any

from app.config import Settings


@dataclass
class ChatCompletionResult:
    content: str
    model: str
    raw: dict[str, Any]


class LLMClientError(RuntimeError):
    pass


class SiliconFlowClient:
    def __init__(self, settings: Settings):
        if not settings.siliconflow_api_key:
            raise LLMClientError("SILICONFLOW_API_KEY is not set")
        self.settings = settings

    def chat_completion(
        self,
        *,
        messages: list[dict[str, str]],
        model: str | None = None,
        temperature: float = 0.3,
        max_tokens: int = 2048,
    ) -> ChatCompletionResult:
        selected_model = model or self.settings.default_model
        payload = {
            "model": selected_model,
            "messages": messages,
            "temperature": temperature,
            "max_tokens": max_tokens,
        }

        url = self.settings.siliconflow_base_url.rstrip("/") + "/chat/completions"
        request = urllib.request.Request(
            url,
            data=json.dumps(payload, ensure_ascii=False).encode("utf-8"),
            headers={
                "Authorization": f"Bearer {self.settings.siliconflow_api_key}",
                "Content-Type": "application/json",
            },
            method="POST",
        )

        try:
            with urllib.request.urlopen(request, timeout=self.settings.request_timeout_seconds) as response:
                raw_text = response.read().decode("utf-8")
        except urllib.error.HTTPError as exc:
            detail = exc.read().decode("utf-8", errors="replace")
            raise LLMClientError(f"SiliconFlow HTTP {exc.code}: {detail}") from exc
        except urllib.error.URLError as exc:
            raise LLMClientError(f"SiliconFlow request failed: {exc}") from exc

        try:
            raw = json.loads(raw_text)
            content = raw["choices"][0]["message"]["content"]
        except (json.JSONDecodeError, KeyError, IndexError, TypeError) as exc:
            raise LLMClientError(f"Unexpected SiliconFlow response: {raw_text[:1000]}") from exc

        return ChatCompletionResult(content=content, model=selected_model, raw=raw)
