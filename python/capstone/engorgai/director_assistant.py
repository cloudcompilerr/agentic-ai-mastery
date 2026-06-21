"""
EngOrgAI v0.1 - the foundational service behind the Engineering Director's
AI assistant. Module 1 establishes the basic ask/answer loop with full
production-bar observability; later modules add memory, RAG, tools, and
agentic behavior on top of this.
"""

import logging
import time
from pathlib import Path

from dotenv import load_dotenv
from langchain_anthropic import ChatAnthropic
from langchain_core.messages import HumanMessage, SystemMessage

ROOT_ENV_PATH = Path(__file__).resolve().parents[3] / ".env"
load_dotenv(dotenv_path=ROOT_ENV_PATH)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(name)s : %(message)s",
)
logger = logging.getLogger(__name__)

SYSTEM_PROMPT = """You are EngOrgAI, an assistant for a Software Development Manager
leading a large engineering organization in a retail/fintech context. You help
with sprint analysis, team health signals, incident summaries, and general
engineering-leadership questions. Be concise, direct, and practical - the
person you're assisting has limited time."""


class EngOrgAiError(Exception):
    """Raised when EngOrgAI fails to respond - never fail silently."""


class DirectorAssistantService:
    def __init__(self) -> None:
        self._model = ChatAnthropic(
            model="claude-haiku-4-5-20251001",
            temperature=0,
            max_retries=3,
        )

    def ask(self, director_question: str) -> str:
        start_time = time.time()

        try:
            messages = [
                SystemMessage(content=SYSTEM_PROMPT),
                HumanMessage(content=director_question),
            ]
            response = self._model.invoke(messages)

            latency_ms = round((time.time() - start_time) * 1000, 2)
            usage = response.usage_metadata

            logger.info(
                "EngOrgAI call completed - promptTokens=%s, completionTokens=%s, totalTokens=%s, latencyMs=%s",
                usage["input_tokens"],
                usage["output_tokens"],
                usage["total_tokens"],
                latency_ms,
            )

            return response.content

        except Exception as exc:
            logger.error("EngOrgAI call failed for question: %s", director_question, exc_info=True)
            raise EngOrgAiError("EngOrgAI failed to respond") from exc


if __name__ == "__main__":
    service = DirectorAssistantService()
    answer = service.ask("How does the team's velocity look this sprint?")
    print(answer)