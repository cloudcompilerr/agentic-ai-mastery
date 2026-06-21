"""
LLM foundations: system/user roles, low temperature for factual answers,
and token/latency observability - the LangChain equivalent of the Spring AI
LlmFoundationsService.
"""

import logging
import time
from pathlib import Path

from dotenv import load_dotenv
from langchain_anthropic import ChatAnthropic
from langchain_core.messages import HumanMessage, SystemMessage

# Load .env from the project root explicitly. Not relying on any
# IDE-specific plugin tab keeps this portable to any environment
# (CI, another machine, etc.) - config loading lives in code, not the IDE.
ROOT_ENV_PATH = Path(__file__).resolve().parents[3] / ".env"
load_dotenv(dotenv_path=ROOT_ENV_PATH)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(name)s : %(message)s",
)
logger = logging.getLogger(__name__)


class LlmCallError(Exception):
    """Raised when a Claude API call fails - never fail silently."""


class LlmFoundationsService:
    def __init__(self) -> None:
        self._model = ChatAnthropic(
            model="claude-haiku-4-5-20251001",
            temperature=0,
            max_retries=3,
        )

    def ask_foundational_question(self, user_question: str) -> str:
        """
        Demonstrates a foundational LLM call: system role, user role, low
        temperature for factual answers, and token/latency observability
        captured from response.usage_metadata.
        """
        start_time = time.time()

        try:
            messages = [
                SystemMessage(content="You are a concise technical assistant. Answer in 2-3 sentences."),
                HumanMessage(content=user_question),
            ]
            response = self._model.invoke(messages)

            latency_ms = round((time.time() - start_time) * 1000, 2)
            usage = response.usage_metadata

            logger.info(
                "LLM call completed - promptTokens=%s, completionTokens=%s, totalTokens=%s, latencyMs=%s",
                usage["input_tokens"],
                usage["output_tokens"],
                usage["total_tokens"],
                latency_ms,
            )

            return response.content

        except Exception as exc:
            logger.error("LLM call failed for question: %s", user_question, exc_info=True)
            raise LlmCallError("Failed to get response from Claude") from exc


if __name__ == "__main__":
    service = LlmFoundationsService()
    answer = service.ask_foundational_question("What is a system prompt?")
    print(answer)