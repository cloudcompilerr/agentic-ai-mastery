package com.manish.capstone.engorgai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

/**
 * EngOrgAI v0.1 - the foundational service behind the Engineering Director's
 * AI assistant. Module 1 establishes the basic ask/answer loop with full
 * production-bar observability; later modules add memory, RAG, tools, and
 * agentic behavior on top of this.
 */
@Service
public class DirectorAssistantService {

    private static final Logger log = LoggerFactory.getLogger(DirectorAssistantService.class);

    private static final String SYSTEM_PROMPT = """
            You are EngOrgAI, an assistant for a Software Development Manager
            leading a large engineering organization in a retail/fintech
            context. You help with sprint analysis, team health signals,
            incident summaries, and general engineering-leadership questions.
            Be concise, direct, and practical - the person you're assisting
            has limited time.
            """;

    private final ChatClient chatClient;

    public DirectorAssistantService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String ask(String directorQuestion) {
        long startTime = System.currentTimeMillis();

        try {
            ChatResponse response = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(directorQuestion)
                    .call()
                    .chatResponse();

            long latencyMs = System.currentTimeMillis() - startTime;
            Usage usage = response.getMetadata().getUsage();

            log.info("EngOrgAI call completed - promptTokens={}, completionTokens={}, totalTokens={}, latencyMs={}",
                    usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens(), latencyMs);

            return response.getResult().getOutput().getText();

        } catch (Exception e) {
            log.error("EngOrgAI call failed for question: {}", directorQuestion, e);
            throw new EngOrgAiException("EngOrgAI failed to respond", e);
        }
    }
}