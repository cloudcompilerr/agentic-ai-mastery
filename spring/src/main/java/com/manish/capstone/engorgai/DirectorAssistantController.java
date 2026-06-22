package com.manish.capstone.engorgai;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/engorgai")
public class DirectorAssistantController {

    public record SprintRequest(String sprintDescription) {}

    private final DirectorAssistantService directorAssistantService;
    private final SprintVelocityCapability sprintVelocityCapability;

    public DirectorAssistantController(
            DirectorAssistantService directorAssistantService,
            SprintVelocityCapability sprintVelocityCapability) {
        this.directorAssistantService = directorAssistantService;
        this.sprintVelocityCapability = sprintVelocityCapability;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return directorAssistantService.ask(question);
    }

    @PostMapping("/sprint-velocity")
    public String sprintVelocity(@RequestBody SprintRequest request) {
        return sprintVelocityCapability.summarize(request.sprintDescription());
    }
}