package com.manish.module02;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/module02")
public class SprintVelocityAnalyzerController {

    public record AnalyzeRequest(String sprintDescription) {}

    private final SprintVelocityAnalyzerService service;

    public SprintVelocityAnalyzerController(SprintVelocityAnalyzerService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public SprintVelocityAnalysis analyze(@RequestBody AnalyzeRequest request) {
        return service.analyze(request.sprintDescription());
    }
}