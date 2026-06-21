package com.manish.module01;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LlmFoundationsServiceTest {

    @Mock private ChatClient chatClient;
    @Mock private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock private ChatClient.CallResponseSpec callResponseSpec;
    @Mock private ChatResponse chatResponse;
    @Mock private Generation generation;
    @Mock private AssistantMessage assistantMessage;
    @Mock private ChatResponseMetadata metadata;
    @Mock private Usage usage;

    @Test
    void askFoundationalQuestion_returnsAnswerText() {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.chatResponse()).thenReturn(chatResponse);

        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);
        when(assistantMessage.getText()).thenReturn("A token is a unit of text.");
        when(chatResponse.getMetadata()).thenReturn(metadata);
        when(metadata.getUsage()).thenReturn(usage);
        when(usage.getPromptTokens()).thenReturn(15);
        when(usage.getCompletionTokens()).thenReturn(8);
        when(usage.getTotalTokens()).thenReturn(23);

        LlmFoundationsService service = new LlmFoundationsService(chatClient);
        String result = service.askFoundationalQuestion("What is a token?");

        assertThat(result).isEqualTo("A token is a unit of text.");
    }
}