# Python Syntax Notes

A running reference of Python syntax explained against the Java equivalent,
built up module by module as the Agentic AI Mastery course progresses.
Kept separate from the actual source code so the codebase itself stays
clean and production-grade.

---

## Module 1 — LLM Foundations

### From `modules/module01/llm_foundations.py` and `director_assistant.py`

| Python | What it does | Java equivalent / contrast |
|---|---|---|
| `SYSTEM_PROMPT = """..."""` at the top of the file, outside any class | A **module-level constant** — any function in this file can use it directly | Java has no true module-level fields — constants normally live inside a class as `private static final`. Python lets constants live freely at file scope |
| `"""You are EngOrgAI...has limited time."""` | A **triple-quoted string** — spans multiple lines as one string literal | Same idea as the Java 21 text block (`"""..."""`) |
| `def ask(self, director_question: str) -> str:` | `: str` and `-> str` are **type hints** — optional, not enforced at runtime | Looks like Java's typed parameters/return, but Python won't actually stop you passing the wrong type. Documentation + IDE help, not a compiler guarantee |
| `ROOT_ENV_PATH = Path(__file__).resolve().parents[3] / ".env"` | `__file__` is the current file's path; `.parents[N]` walks up N directories; `/` between `Path` objects joins path segments | No real Java equivalent for `__file__` — closest is reading a path relative to the working directory. The `/` operator overload for path-joining is very Python-specific |
| `if __name__ == "__main__":` | Only runs this block when the file is executed directly (not when imported elsewhere) | Like Java's `public static void main(String[] args)`, but every Python file *can* have one, and it's guarded so importing the file elsewhere doesn't trigger it |
| `try: ... except Exception as exc: ... raise EngOrgAiError(...) from exc` | `except Exception as exc` binds the caught exception to a name; `raise X from exc` chains the new exception to the original | Similar to Java's `catch (Exception e) { throw new X(msg, e); }` — the `from exc` is what sets the "caused by" chain, like passing `e` as the cause in Java |

### From `modules/module01/test_llm_foundations.py`

| Python | What it does | Java equivalent / contrast |
|---|---|---|
| `from llm_foundations import LlmFoundationsService` | Imports one specific name from a sibling file `llm_foundations.py` | Like `import com.manish...LlmFoundationsService;` — but Python imports *from a file*, not a fully-qualified package path |
| `@patch("llm_foundations.ChatAnthropic")` | A **decorator** — wraps the function below it, temporarily replacing `ChatAnthropic` with a fake during the test | Closest parallel: Mockito's `@Mock` annotation, but decorators are a general Python language feature (any function can wrap any other), not test-specific |
| `def test_ask_foundational_question_returns_answer_text(mock_chat_anthropic_cls):` | A plain function, no class wrapper needed. pytest auto-discovers anything starting with `test_` | JUnit 5 needs a class; pytest doesn't — the function alone *is* the test |
| `mock_model = MagicMock()` | Creates a fake object that accepts **any** attribute or method call without you defining an interface first | Very different from Mockito — Mockito mocks a real class/interface; `MagicMock()` mocks nothing in particular, it just improvises |
| `mock_response.content = "..."` | Directly assigns an attribute to an object at runtime | Java has no equivalent — you can't add a field to an object that wasn't declared in its class. Python objects are more "open" by default |
| `{"input_tokens": 15, "output_tokens": 8, ...}` | A **dict literal** — Python's built-in key-value structure | Like `Map.of("input_tokens", 15, ...)` in Java, but built into the language syntax (curly braces), not a static factory method |
| `assert result == "..."` | Python's built-in `assert` keyword | Like `assertEquals(...)` in JUnit/AssertJ, but it's a language keyword, not a library method — no import needed |

---

<!-- Module 2 notes appended below as the course progresses -->
