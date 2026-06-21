from unittest.mock import MagicMock, patch

from director_assistant import DirectorAssistantService


@patch("director_assistant.ChatAnthropic")
def test_ask_returns_director_facing_answer(mock_chat_anthropic_cls):
    mock_model = MagicMock()
    mock_response = MagicMock()
    mock_response.content = "Sprint velocity looks healthy this cycle."
    mock_response.usage_metadata = {
        "input_tokens": 40,
        "output_tokens": 20,
        "total_tokens": 60,
    }
    mock_model.invoke.return_value = mock_response
    mock_chat_anthropic_cls.return_value = mock_model

    service = DirectorAssistantService()
    result = service.ask("How does the team's velocity look this sprint?")

    assert result == "Sprint velocity looks healthy this cycle."