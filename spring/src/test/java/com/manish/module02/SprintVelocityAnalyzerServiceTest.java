package com.manish.module02;

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
import org.springframework.ai.chat.prompt.Prompt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SprintVelocityAnalyzerServiceTest {

    @Mock private ChatClient chatClient;
    @Mock private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock private ChatClient.CallResponseSpec callResponseSpec;
    @Mock private ChatResponse chatResponse;
    @Mock private Generation generation;
    @Mock private AssistantMessage assistantMessage;
    @Mock private ChatResponseMetadata metadata;
    @Mock private Usage usage;

    @Test
    void analyze_parsesJsonIntoRecord() {
        String fakeJson = """
                {
                  "trend": "DECLINING",
                  "completionRate": 72.5,
                  "reasoning": "Completion dropped from 95%% to 72.5%% with two incidents reported.",
                  "riskFlags": ["Two production incidents", "One engineer out for the full sprint"],
                  "recommendation": "Investigate incident load before committing to next sprint's full capacity."
                }
                """;

        when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.chatResponse()).thenReturn(chatResponse);

        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);
        when(assistantMessage.getText()).thenReturn(fakeJson);
        when(chatResponse.getMetadata()).thenReturn(metadata);
        when(metadata.getUsage()).thenReturn(usage);
        when(usage.getPromptTokens()).thenReturn(120);
        when(usage.getCompletionTokens()).thenReturn(60);
        when(usage.getTotalTokens()).thenReturn(180);

        SprintVelocityAnalyzerService service = new SprintVelocityAnalyzerService(chatClient);
        SprintVelocityAnalysis result = service.analyze("This sprint we committed 40, completed 29...");

        assertThat(result.trend()).isEqualTo("DECLINING");
        assertThat(result.completionRate()).isEqualTo(72.5);
        assertThat(result.riskFlags()).hasSize(2);
    }
}