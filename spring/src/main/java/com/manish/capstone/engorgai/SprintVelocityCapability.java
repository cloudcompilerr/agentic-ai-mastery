package com.manish.capstone.engorgai;

import com.manish.module02.SprintVelocityAnalysis;
import com.manish.module02.SprintVelocityAnalyzerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * EngOrgAI v0.2 — Sprint Velocity Capability.
 * Wraps the Module 2 analyzer and formats its structured output
 * into a director-facing narrative summary.
 */
@Service
public class SprintVelocityCapability {

    private static final Logger log = LoggerFactory.getLogger(SprintVelocityCapability.class);

    private final SprintVelocityAnalyzerService analyzerService;

    public SprintVelocityCapability(SprintVelocityAnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    public String summarize(String sprintDescription) {
        log.info("EngOrgAI: sprint velocity capability invoked");
        SprintVelocityAnalysis analysis = analyzerService.analyze(sprintDescription);

        return """
                📊 SPRINT VELOCITY ANALYSIS
                ─────────────────────────────
                Trend:           %s
                Completion Rate: %.1f%%
                Reasoning:       %s
                Risk Flags:      %s
                Recommendation:  %s
                """.formatted(
                analysis.trend(),
                analysis.completionRate(),
                analysis.reasoning(),
                analysis.riskFlags().isEmpty() ? "None" : String.join(", ", analysis.riskFlags()),
                analysis.recommendation()
        );
    }
}