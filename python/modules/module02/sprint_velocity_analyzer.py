"""
Module 2: Prompt engineering for production.
Sprint Velocity Analyzer: structured output (Pydantic), few-shot prompting,
and chain-of-thought reasoning embedded in the schema — LangChain equivalent
of the Spring AI SprintVelocityAnalyzerService.
"""

import logging
import time
from pathlib import Path
from typing import List

from dotenv import load_dotenv
from langchain_anthropic import ChatAnthropic
from langchain_core.messages import AIMessage, HumanMessage, SystemMessage
from pydantic import BaseModel, Field

ROOT_ENV_PATH = Path(__file__).resolve().parents[3] / ".env"
load_dotenv(dotenv_path=ROOT_ENV_PATH)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(name)s : %(message)s",
)
logger = logging.getLogger(__name__)

SYSTEM_PROMPT = """You are a sprint velocity analyst for an engineering organization.
Given a free-text description of a sprint, reason step by step about completion
rate, trend versus prior sprints, and risk signals, then respond ONLY with JSON
matching the schema given. Be calibrated: only flag DECLINING if completion rate
drops more than 10 points versus the prior sprint, or a named risk (incidents,
attrition, scope creep) is present."""

EXAMPLE_INPUT = """This sprint we committed to 40 points and completed 39.
Last sprint was 38 committed / 36 completed. No incidents, no team changes."""

EXAMPLE_OUTPUT = """{
  "trend": "STABLE",
  "completionRate": 97.5,
  "reasoning": "Completion rate rose slightly from 94.7% to 97.5%, well within normal variance. No risk signals present.",
  "riskFlags": [],
  "recommendation": "No action needed - velocity is healthy and predictable."
}"""


class SprintVelocityAnalysis(BaseModel):
    """Structured output schema for sprint velocity analysis."""

    trend: str = Field(description="IMPROVING, STABLE, or DECLINING")
    completionRate: float = Field(description="Story points completed / committed * 100")
    reasoning: str = Field(description="Step-by-step reasoning before the verdict")
    riskFlags: List[str] = Field(description="Named risk signals present, empty list if none")
    recommendation: str = Field(description="Concise recommended action for the director")


class SprintAnalysisError(Exception):
    """Raised when sprint velocity analysis fails — never fail silently."""


class SprintVelocityAnalyzerService:
    def __init__(self) -> None:
        model = ChatAnthropic(
            model="claude-haiku-4-5-20251001",
            temperature=0,
            max_retries=3,
        )
        # include_raw=True returns both the parsed Pydantic object AND
        # the raw AIMessage — we get token usage in a single call, no double-call.
        # method="json_schema" keeps few-shot examples as plain AIMessages;
        # function_calling (the default) would require tool-call formatted
        # examples instead, which is more complex for no benefit here.
        self._structured_model = model.with_structured_output(
            SprintVelocityAnalysis,
            method="json_schema",
            include_raw=True,
        )

    def analyze(self, sprint_description: str) -> SprintVelocityAnalysis:
        start_time = time.time()

        try:
            messages = [
                SystemMessage(content=SYSTEM_PROMPT),
                HumanMessage(content=EXAMPLE_INPUT),
                AIMessage(content=EXAMPLE_OUTPUT),
                HumanMessage(content=sprint_description),
            ]

            result = self._structured_model.invoke(messages)

            latency_ms = round((time.time() - start_time) * 1000, 2)
            usage = result["raw"].usage_metadata

            logger.info(
                "Sprint velocity analysis completed - promptTokens=%s, completionTokens=%s, totalTokens=%s, latencyMs=%s",
                usage["input_tokens"],
                usage["output_tokens"],
                usage["total_tokens"],
                latency_ms,
            )

            if result["parsing_error"]:
                raise SprintAnalysisError(
                    f"Failed to parse model response: {result['parsing_error']}"
                )

            return result["parsed"]

        except SprintAnalysisError:
            raise
        except Exception as exc:
            logger.error("Sprint velocity analysis failed for input: %s", sprint_description, exc_info=True)
            raise SprintAnalysisError("Failed to analyze sprint velocity") from exc


if __name__ == "__main__":
    service = SprintVelocityAnalyzerService()
    analysis = service.analyze(
        "This sprint we committed 45 points and completed 31. "
        "Two production incidents pulled the team off planned work for 3 days each."
    )
    print(f"Trend:          {analysis.trend}")
    print(f"Completion Rate:{analysis.completionRate:.1f}%")
    print(f"Reasoning:      {analysis.reasoning}")
    print(f"Risk Flags:     {analysis.riskFlags}")
    print(f"Recommendation: {analysis.recommendation}")