package com.manish.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Central ChatClient configuration. Built once here and reused across every
 * module and capstone service - one place to wire in cross-cutting concerns
 * (default advisors, logging, etc.) as the course progresses.
 */
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }
}