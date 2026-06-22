"""
EngOrgAI v0.2 — Sprint Velocity Capability.
Wraps the Module 2 analyzer and formats its structured output
into a director-facing narrative summary.
"""

import logging
import sys
from pathlib import Path

# Add the python/ root to sys.path so we can import across modules/capstone.
# In a production package this would be resolved via proper pip install -e .
# For this course project, explicit sys.path is the honest, transparent approach.
PYTHON_ROOT = Path(__file__).resolve().parents[2]
if str(PYTHON_ROOT) not in sys.path:
    sys.path.insert(0, str(PYTHON_ROOT))

from modules.module02.sprint_velocity_analyzer import (
    SprintVelocityAnalyzerService,
)

logger = logging.getLogger(__name__)


class SprintVelocityCapability:
    """
    EngOrgAI sprint velocity capability — wraps the module02 analyzer
    and formats its output into a director-facing narrative summary.
    """

    def __init__(self) -> None:
        self._analyzer = SprintVelocityAnalyzerService()

    def summarize(self, sprint_description: str) -> str:
        logger.info("EngOrgAI: sprint velocity capability invoked")
        analysis = self._analyzer.analyze(sprint_description)

        risk_str = "None" if not analysis.riskFlags else ", ".join(analysis.riskFlags)

        return (
            f"📊 SPRINT VELOCITY ANALYSIS\n"
            f"─────────────────────────────\n"
            f"Trend:           {analysis.trend}\n"
            f"Completion Rate: {analysis.completionRate:.1f}%\n"
            f"Reasoning:       {analysis.reasoning}\n"
            f"Risk Flags:      {risk_str}\n"
            f"Recommendation:  {analysis.recommendation}\n"
        )


if __name__ == "__main__":
    capability = SprintVelocityCapability()
    summary = capability.summarize(
        "Committed 45 points, completed 31. "
        "Two production incidents pulled the team off planned work for 3 days each."
    )
    print(summary)