from app.schemas import ChatRequest, ExerciseGenerateRequest


def build_chat_messages(request: ChatRequest) -> list[dict[str, str]]:
    system_parts = [
        "你是 eduSys 教学实训平台的学习助手。",
        "回答要准确、清晰、适合学生理解。",
        "如果用户在问课程知识，优先结合给定课程、章节和上下文回答。",
        "如果上下文不足，直接说明缺少哪些信息，不要编造教材内容。",
        "涉及代码时给出可运行示例，并指出关键步骤。",
    ]
    if request.course_name:
        system_parts.append(f"当前课程：{request.course_name}")
    if request.chapter_name:
        system_parts.append(f"当前章节：{request.chapter_name}")
    if request.context:
        system_parts.append(f"可参考资料：\n{request.context}")

    messages = [{"role": "system", "content": "\n".join(system_parts)}]

    for item in request.history[-8:]:
        role = item.get("role")
        content = item.get("content")
        if role in {"user", "assistant"} and content:
            messages.append({"role": role, "content": content})

    messages.append({"role": "user", "content": request.question})
    return messages


def build_exercise_messages(request: ExerciseGenerateRequest) -> list[dict[str, str]]:
    knowledge_text = "、".join(request.knowledge_points) if request.knowledge_points else "由模型根据章节主题合理提取"
    context_text = request.context or "无额外课程资料。"
    extra = request.extra_requirements or "无。"

    system_prompt = """
你是 eduSys 教学实训平台的出题助手。
你必须只输出一个合法 JSON 对象，不要输出 Markdown，不要输出解释性前后缀。
题目应适合高校软件实践/计算机课程教学，避免偏题、重复和含糊表述。
选择题选项必须有 A、B、C、D；判断题选项必须是 A=正确、B=错误；填空题和简答题 options 为空数组。
single_choice 只能有一个正确选项；multiple_choice 可以有多个正确选项。
answer 字段使用可读答案，例如 "C"、"A、D"、"正确"、"print()"。
analysis 字段给出简洁解析。
""".strip()

    user_prompt = f"""
请生成练习题，要求如下：
- course_id: {request.course_id or ""}
- course_name: {request.course_name}
- chapter_id: {request.chapter_id or ""}
- chapter_name: {request.chapter_name}
- question_type: {request.question_type}
- question_count: {request.question_count}
- difficulty: {request.difficulty}，1 最简单，5 最难
- knowledge_points: {knowledge_text}
- extra_requirements: {extra}

课程资料/知识库上下文：
{context_text}

返回 JSON Schema：
{{
  "questions": [
    {{
      "question_type": "{request.question_type}",
      "question": "题干",
      "options": [
        {{"option_id": "A", "option_text": "选项内容", "is_correct": true}},
        {{"option_id": "B", "option_text": "选项内容", "is_correct": false}},
        {{"option_id": "C", "option_text": "选项内容", "is_correct": false}},
        {{"option_id": "D", "option_text": "选项内容", "is_correct": false}}
      ],
      "answer": "答案",
      "analysis": "解析",
      "knowledge_points": ["知识点1"],
      "difficulty": {request.difficulty}
    }}
  ]
}}
""".strip()

    return [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_prompt},
    ]
