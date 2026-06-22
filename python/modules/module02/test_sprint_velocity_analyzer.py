from unittest.mock import MagicMock, patch

from sprint_velocity_analyzer import SprintVelocityAnalysis, SprintVelocityAnalyzerService


@patch("sprint_velocity_analyzer.ChatAnthropic")
def test_analyze_returns_parsed_analysis(mock_chat_anthropic_cls):
    mock_model = MagicMock()
    mock_structured_model = MagicMock()
    mock_model.with_structured_output.return_value = mock_structured_model
    mock_chat_anthropic_cls.return_value = mock_model

    mock_raw = MagicMock()
    mock_raw.usage_metadata = {
        "input_tokens": 120,
        "output_tokens": 60,
        "total_tokens": 180,
    }
    mock_parsed = SprintVelocityAnalysis(
        trend="DECLINING",
        completionRate=72.5,
        reasoning="Completion dropped significantly with two incidents reported.",
        riskFlags=["Two production incidents", "One engineer out full sprint"],
        recommendation="Reduce sprint commitment until incident load stabilises.",
    )
    mock_structured_model.invoke.return_value = {
        "raw": mock_raw,
        "parsed": mock_parsed,
        "parsing_error": None,
    }

    service = SprintVelocityAnalyzerService()
    result = service.analyze("Committed 45, completed 31, two incidents...")

    assert result.trend == "DECLINING"
    assert result.completionRate == 72.5
    assert len(result.riskFlags) == 2