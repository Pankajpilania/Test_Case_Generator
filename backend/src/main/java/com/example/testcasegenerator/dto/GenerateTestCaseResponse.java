package com.example.testcasegenerator.dto;

import com.example.testcasegenerator.model.TestCase;

import java.util.List;

public record GenerateTestCaseResponse(
        String userStory,
        int generatedCount,
        List<TestCase> testCases
) {
}
