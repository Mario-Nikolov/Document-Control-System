package com.logiclab.documentcontrolsystem.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class ContentExtractionService {

    public String extractText(byte[] content, String extension) {
        validateContent(content);
        String normalizedExtension = normalizeExtension(extension);

        return switch (normalizedExtension) {
            case "txt", "md", "json", "xml", "csv" -> extractPlainText(content);
            case "pdf" -> extractTextFromPdf(content);
            case "docx" -> extractTextFromDocx(content);
            case "doc" -> extractTextFromDoc(content);
            default -> throw new RuntimeException("Unsupported file type for diff: " + extension);
        };
    }

    private void validateContent(byte[] content) {
        if (content == null || content.length == 0) {
            throw new RuntimeException("File content is empty!");
        }
    }

    private String normalizeExtension(String extension) {
        if (extension == null || extension.isBlank()) {
            throw new RuntimeException("File extension is missing!");
        }

        return extension.toLowerCase().replace(".", "").trim();
    }

    private String extractPlainText(byte[] content) {
        return new String(content, StandardCharsets.UTF_8);
    }

    private String extractTextFromPdf(byte[] content) {
        try (PDDocument document = Loader.loadPDF(content)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from PDF file!", e);
        }
    }

    private String extractTextFromDocx(byte[] content) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            return extractor.getText().trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from DOCX file!", e);
        }
    }

    private String extractTextFromDoc(byte[] content) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
             HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {

            return extractor.getText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from DOC file!", e);
        }
    }
}