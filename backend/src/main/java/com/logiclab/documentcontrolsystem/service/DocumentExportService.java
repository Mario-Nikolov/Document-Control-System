package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.Document;
import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class DocumentExportService {

    private final ContentExtractionService contentExtractionService;
    private final DocumentRepository documentRepository;

    public byte[] exportToPdf(Integer documentId){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found."));

        DocumentVersion activeVersion = document.getActiveVersion();

        String extractedText = contentExtractionService.extractText(activeVersion.getContent(),activeVersion.getExtension());

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            com.lowagie.text.Document pdfDocument = new com.lowagie.text.Document();
            PdfWriter.getInstance(pdfDocument, outputStream);

            pdfDocument.open();
            pdfDocument.add(new Paragraph(extractedText));
            pdfDocument.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export document to PDF.", e);
        }

    }

    public byte[] exportToTxt(Integer documentId){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found."));

        DocumentVersion activeVersion = document.getActiveVersion();

        String extractedText = contentExtractionService.extractText(activeVersion.getContent(),activeVersion.getExtension());

        return extractedText.getBytes(StandardCharsets.UTF_8);
    }


}
