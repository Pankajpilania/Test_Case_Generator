package com.example.testcasegenerator.service;

import com.example.testcasegenerator.model.TestCase;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class FileExportService {

    private static final String[] HEADER = {
            "Test Case ID", "Title", "Description", "Preconditions", "Steps", "Expected Result", "Type"
    };

    public byte[] toCsv(List<TestCase> cases) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            csvWriter.writeNext(HEADER);
            for (TestCase testCase : cases) {
                csvWriter.writeNext(new String[]{
                        testCase.testCaseId(),
                        testCase.title(),
                        testCase.description(),
                        testCase.preconditions(),
                        String.join(" | ", testCase.steps()),
                        testCase.expectedResult(),
                        testCase.type().name()
                });
            }
            writer.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate CSV", e);
        }
    }

    public byte[] toXlsx(List<TestCase> cases) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Test Cases");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADER.length; i++) {
                headerRow.createCell(i).setCellValue(HEADER[i]);
            }

            int rowIndex = 1;
            for (TestCase testCase : cases) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(testCase.testCaseId());
                row.createCell(1).setCellValue(testCase.title());
                row.createCell(2).setCellValue(testCase.description());
                row.createCell(3).setCellValue(testCase.preconditions());
                row.createCell(4).setCellValue(String.join(" | ", testCase.steps()));
                row.createCell(5).setCellValue(testCase.expectedResult());
                row.createCell(6).setCellValue(testCase.type().name());
            }

            for (int i = 0; i < HEADER.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate XLSX", e);
        }
    }
}
