package com.example.testcasegenerator.controller;

import com.example.testcasegenerator.dto.GenerateTestCaseRequest;
import com.example.testcasegenerator.dto.GenerateTestCaseResponse;
import com.example.testcasegenerator.service.FileExportService;
import com.example.testcasegenerator.service.TestCaseGenerationService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class TestCaseController {

    private final TestCaseGenerationService generationService;
    private final FileExportService exportService;

    public TestCaseController(TestCaseGenerationService generationService, FileExportService exportService) {
        this.generationService = generationService;
        this.exportService = exportService;
    }

    @PostMapping("/generate-testcases")
    public ResponseEntity<GenerateTestCaseResponse> generate(@Valid @RequestBody GenerateTestCaseRequest request) {
        return ResponseEntity.ok(generationService.generate(request));
    }

    @PostMapping("/generate-testcases/export/csv")
    public ResponseEntity<byte[]> exportCsv(@Valid @RequestBody GenerateTestCaseRequest request) {
        GenerateTestCaseResponse response = generationService.generate(request);
        byte[] bytes = exportService.toCsv(response.testCases());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("test-cases.csv").build().toString())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @PostMapping("/generate-testcases/export/xlsx")
    public ResponseEntity<byte[]> exportXlsx(@Valid @RequestBody GenerateTestCaseRequest request) {
        GenerateTestCaseResponse response = generationService.generate(request);
        byte[] bytes = exportService.toXlsx(response.testCases());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("test-cases.xlsx").build().toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
