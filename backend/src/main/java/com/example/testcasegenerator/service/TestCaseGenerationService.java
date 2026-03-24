package com.example.testcasegenerator.service;

import com.example.testcasegenerator.dto.GenerateTestCaseRequest;
import com.example.testcasegenerator.dto.GenerateTestCaseResponse;
import com.example.testcasegenerator.model.TestCase;
import com.example.testcasegenerator.model.TestCaseType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TestCaseGenerationService {

    private static final List<Integer> SUPPORTED_COUNTS = List.of(5, 10, 20, 40);

    public GenerateTestCaseResponse generate(GenerateTestCaseRequest request) {
        if (!SUPPORTED_COUNTS.contains(request.numberOfCases())) {
            throw new IllegalArgumentException("numberOfCases must be one of 5, 10, 20, 40");
        }

        StoryTokens tokens = extractStoryTokens(request.userStory());
        List<TestCaseType> requestedTypes = request.types().stream().distinct().toList();

        int total = request.numberOfCases();
        Map<TestCaseType, Integer> allocations = allocateCounts(total, requestedTypes);
        AtomicInteger sequence = new AtomicInteger(1);
        List<TestCase> generated = new ArrayList<>();

        requestedTypes.stream()
                .sorted(Comparator.comparing(Enum::name))
                .forEach(type -> generated.addAll(buildCases(type, allocations.get(type), tokens, sequence)));

        return new GenerateTestCaseResponse(request.userStory(), generated.size(), generated);
    }

    private List<TestCase> buildCases(TestCaseType type, int count, StoryTokens tokens, AtomicInteger sequence) {
        List<TestCase> cases = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            int id = sequence.getAndIncrement();
            String prefix = "TC-" + String.format("%03d", id);
            String title = switch (type) {
                case POSITIVE -> "Verify user can " + tokens.action() + " successfully (scenario " + i + ")";
                case NEGATIVE -> "Verify system handles invalid attempt for " + tokens.action() + " (scenario " + i + ")";
                case EDGE -> "Verify boundary condition for " + tokens.action() + " (scenario " + i + ")";
            };

            String description = switch (type) {
                case POSITIVE -> "Validate happy-path behavior when the user performs: " + tokens.action() + ".";
                case NEGATIVE -> "Validate error handling and validation messages for incorrect input/state while attempting: " + tokens.action() + ".";
                case EDGE -> "Validate system behavior for boundary or unusual but possible condition while performing: " + tokens.action() + ".";
            };

            String preconditions = "User role: " + tokens.role() + "; Feature context: " + tokens.benefit() + "; Test data set #" + i + " prepared.";

            List<String> steps = createSteps(type, tokens, i);
            String expectedResult = switch (type) {
                case POSITIVE -> "Operation completes successfully, confirmation is shown, and expected data is persisted without errors.";
                case NEGATIVE -> "Operation is blocked safely, a meaningful validation/error message is shown, and no unintended data changes occur.";
                case EDGE -> "System responds within acceptable limits, preserves data integrity, and handles boundary input without crash or corruption.";
            };

            cases.add(new TestCase(prefix, title, description, preconditions, steps, expectedResult, type));
        }
        return cases;
    }

    private List<String> createSteps(TestCaseType type, StoryTokens tokens, int variant) {
        return switch (type) {
            case POSITIVE -> List.of(
                    "Navigate to the relevant module as " + tokens.role() + ".",
                    "Provide valid input data set #" + variant + " for action: " + tokens.action() + ".",
                    "Submit the action and observe response.",
                    "Confirm resulting data/state reflects expected outcome: " + tokens.benefit() + "."
            );
            case NEGATIVE -> List.of(
                    "Navigate to the relevant module as " + tokens.role() + ".",
                    "Provide invalid/incomplete data variant #" + variant + " for action: " + tokens.action() + ".",
                    "Attempt to submit the action.",
                    "Capture validation message and verify no invalid data is saved."
            );
            case EDGE -> List.of(
                    "Navigate to the relevant module as " + tokens.role() + ".",
                    "Use boundary input variant #" + variant + " (max/min/empty/special chars) for action: " + tokens.action() + ".",
                    "Submit and monitor behavior, logs, and UI response.",
                    "Verify graceful handling and consistency of computed result for: " + tokens.benefit() + "."
            );
        };
    }

    private Map<TestCaseType, Integer> allocateCounts(int total, List<TestCaseType> selectedTypes) {
        int base = total / selectedTypes.size();
        int remainder = total % selectedTypes.size();

        AtomicInteger rem = new AtomicInteger(remainder);
        return selectedTypes.stream().collect(java.util.stream.Collectors.toMap(
                type -> type,
                type -> base + (rem.getAndDecrement() > 0 ? 1 : 0)
        ));
    }

    private StoryTokens extractStoryTokens(String userStory) {
        String normalized = userStory == null ? "" : userStory.trim().replaceAll("\\s+", " ");

        Pattern pattern = Pattern.compile("(?i)as\\s+a[n]?\\s+(.+?),\\s*i\\s+want\\s+to\\s+(.+?)\\s+so\\s+that\\s+(.+)");
        Matcher matcher = pattern.matcher(normalized);
        if (matcher.find()) {
            return new StoryTokens(clean(matcher.group(1)), clean(matcher.group(2)), clean(matcher.group(3)));
        }

        return new StoryTokens("user", normalized.isBlank() ? "perform the target action" : normalized, "achieve the expected business value");
    }

    private String clean(String value) {
        return value.replaceAll("[.]+$", "").trim();
    }

    private record StoryTokens(String role, String action, String benefit) {
    }
}
