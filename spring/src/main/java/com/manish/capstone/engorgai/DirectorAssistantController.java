package com.manish.capstone.engorgai;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/engorgai")
public class DirectorAssistantController {

    private final DirectorAssistantService directorAssistantService;

    public DirectorAssistantController(DirectorAssistantService directorAssistantService) {
        this.directorAssistantService = directorAssistantService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return directorAssistantService.ask(question);
    }
}