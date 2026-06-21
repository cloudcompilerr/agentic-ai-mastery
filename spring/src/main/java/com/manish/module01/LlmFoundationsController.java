package com.manish.module01;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/module01")
public class LlmFoundationsController {

    private final LlmFoundationsService llmFoundationsService;

    public LlmFoundationsController(LlmFoundationsService llmFoundationsService) {
        this.llmFoundationsService = llmFoundationsService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return llmFoundationsService.askFoundationalQuestion(question);
    }
}