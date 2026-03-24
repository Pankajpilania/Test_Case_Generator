package com.example.testcasegenerator.dto;

import com.example.testcasegenerator.model.TestCaseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GenerateTestCaseRequest(
        @NotBlank(message = "User story is required")
        @Size(max = 5000, message = "User story is too long")
        String userStory,

        @NotNull(message = "numberOfCases is required")
        Integer numberOfCases,

        @NotEmpty(message = "At least one test case type is required")
        List<TestCaseType> types
) {
}
