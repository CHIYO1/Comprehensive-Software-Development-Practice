import json
import urllib.request


BASE_URL = "http://127.0.0.1:8000"


def post_json(path, payload, timeout=120):
    request = urllib.request.Request(
        BASE_URL + path,
        data=json.dumps(payload, ensure_ascii=False).encode("utf-8"),
        headers={"Content-Type": "application/json; charset=utf-8"},
        method="POST",
    )
    with urllib.request.urlopen(request, timeout=timeout) as response:
        return json.loads(response.read().decode("utf-8"))


def get_json(path, timeout=30):
    with urllib.request.urlopen(BASE_URL + path, timeout=timeout) as response:
        return json.loads(response.read().decode("utf-8"))


def main():
    print("health:", json.dumps(get_json("/health"), ensure_ascii=False))

    chat_payload = {
        "user_id": "2024003",
        "course_name": "Python编程入门",
        "chapter_name": "Python基础语法",
        "question": "请用一句话回答：Python列表和元组的核心区别是什么？",
        "context": "列表list是可变序列，元组tuple是不可变序列。",
        "history": [],
    }
    chat_response = post_json("/api/v1/chat", chat_payload)
    print("chat:", chat_response["code"], chat_response["data"]["answer"][:80])

    for question_type in ["single_choice", "multiple_choice", "true_false", "fill_blank", "essay"]:
        exercise_payload = {
            "course_name": "Python编程入门",
            "chapter_name": "Python基础语法",
            "question_type": question_type,
            "question_count": 1,
            "difficulty": 2,
            "knowledge_points": ["变量", "列表"],
            "context": "本章学习 Python 变量、数据类型、列表和元组。",
            "extra_requirements": "只生成一道题。",
        }
        response = post_json("/api/v1/exercises/generate", exercise_payload)
        print(question_type, response["code"], response["message"])
        if response["code"] == 200:
            question = response["data"]["questions"][0]
            print("  question:", question["question"][:80])
            print("  answer:", question["answer"])


if __name__ == "__main__":
    main()
