from unittest.mock import MagicMock, patch

from llm_foundations import LlmFoundationsService


@patch("llm_foundations.ChatAnthropic")
def test_ask_foundational_question_returns_answer_text(mock_chat_anthropic_cls):
    mock_model = MagicMock()
    mock_response = MagicMock()
    mock_response.content = "A token is a unit of text."
    mock_response.usage_metadata = {
        "input_tokens": 15,
        "output_tokens": 8,
        "total_tokens": 23,
    }
    mock_model.invoke.return_value = mock_response
    mock_chat_anthropic_cls.return_value = mock_model

    service = LlmFoundationsService()
    result = service.ask_foundational_question("What is a token?")

    assert result == "A token is a unit of text."