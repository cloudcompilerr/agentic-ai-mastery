package com.manish.module01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class LlmFoundationsService {

    private static final Logger log = LoggerFactory.getLogger(LlmFoundationsService.class);

    private final ChatClient chatClient;

    public LlmFoundationsService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Demonstrates a foundational LLM call: system role, user role, low
     * temperature for factual answers, and token/latency observability
     * captured from the response metadata.
     */
    public String askFoundationalQuestion(String userQuestion) {
        long startTime = System.currentTimeMillis();

        try {
            ChatResponse response = chatClient.prompt()
                    .system("You are a concise technical assistant. Answer in 2-3 sentences.")
                    .user(userQuestion)
                    .call()
                    .chatResponse();

            long latencyMs = System.currentTimeMillis() - startTime;
            Usage usage = response.getMetadata().getUsage();

            log.info("LLM call completed - promptTokens={}, completionTokens={}, totalTokens={}, latencyMs={}",
                    usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens(), latencyMs);

            return response.getResult().getOutput().getText();

        } catch (Exception e) {
            log.error("LLM call failed for question: {}", userQuestion, e);
            throw new LlmCallException("Failed to get response from Claude", e);
        }
    }
}