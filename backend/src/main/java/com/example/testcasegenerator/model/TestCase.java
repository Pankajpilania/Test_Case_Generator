package com.example.testcasegenerator.model;

import java.util.List;

public record TestCase(
        String testCaseId,
        String title,
        String description,
        String preconditions,
        List<String> steps,
        String expectedResult,
        TestCaseType type
) {
}
