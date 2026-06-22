package com.manish.module02;

import java.util.List;

public record SprintVelocityAnalysis(
        String trend,
        double completionRate,
        String reasoning,
        List<String> riskFlags,
        String recommendation
) {}