package com.manish.module02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SprintVelocityAnalyzerService {

    private static final Logger log = LoggerFactory.getLogger(SprintVelocityAnalyzerService.class);

    private static final String SYSTEM_PROMPT = """
            You are a sprint velocity analyst for an engineering organization.
            Given a free-text description of a sprint, reason step by step
            about completion rate, trend versus prior sprints, and risk
            signals, then respond ONLY with JSON matching the schema given
            below. Be calibrated: only flag DECLINING if completion rate
            drops more than 10 points versus the prior sprint, or a named
            risk (incidents, attrition, scope creep) is present.
            """;

    // Few-shot example as a real prior exchange - anchors calibration for
    // trend/risk judgment, not just JSON shape.
    private static final String EXAMPLE_INPUT = """
            This sprint we committed to 40 points and completed 39.
            Last sprint was 38 committed / 36 completed. No incidents,
            no team changes.
            """;

    private static final String EXAMPLE_OUTPUT = """
            {
              "trend": "STABLE",
              "completionRate": 97.5,
              "reasoning": "Completion rate rose slightly from 94.7% to 97.5%, well within normal variance. No risk signals present.",
              "riskFlags": [],
              "recommendation": "No action needed - velocity is healthy and predictable."
            }
            """;

    private final ChatClient chatClient;
    private final BeanOutputConverter<SprintVelocityAnalysis> outputConverter;

    public SprintVelocityAnalyzerService(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.outputConverter = new BeanOutputConverter<>(SprintVelocityAnalysis.class);
    }

    public SprintVelocityAnalysis analyze(String sprintDescription) {
        long startTime = System.currentTimeMillis();

        try {
            String formatInstructions = outputConverter.getFormat();

            List<Message> messages = List.of(
                    new SystemMessage(SYSTEM_PROMPT + "\n" + formatInstructions),
                    new UserMessage(EXAMPLE_INPUT),
                    new AssistantMessage(EXAMPLE_OUTPUT),
                    new UserMessage(sprintDescription)
            );

            // ONE call gives us both usage metadata and raw text to convert.
            // Calling .entity() separately here would trigger a second,
            // duplicate API call - a known Spring AI gotcha.
            ChatResponse response = chatClient.prompt(new Prompt(messages))
                    .call()
                    .chatResponse();

            long latencyMs = System.currentTimeMillis() - startTime;
            Usage usage = response.getMetadata().getUsage();

            log.info("Sprint velocity analysis completed - promptTokens={}, completionTokens={}, totalTokens={}, latencyMs={}",
                    usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens(), latencyMs);

            String rawText = response.getResult().getOutput().getText();
            return outputConverter.convert(rawText);

        } catch (Exception e) {
            log.error("Sprint velocity analysis failed for input: {}", sprintDescription, e);
            throw new SprintAnalysisException("Failed to analyze sprint velocity", e);
        }
    }
}