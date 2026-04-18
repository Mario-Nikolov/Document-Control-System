package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.domain.Document;
import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.CreateDocumentRequest;
import com.logiclab.documentcontrolsystem.dto.response.DocumentResponse;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.dto.response.VersionDiffResponse;
import com.logiclab.documentcontrolsystem.exceptions.DocumentNotFoundException;
import com.logiclab.documentcontrolsystem.mapper.DocumentMapper;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import com.logiclab.documentcontrolsystem.service.AuthService;
import com.logiclab.documentcontrolsystem.service.DocumentExportService;
import com.logiclab.documentcontrolsystem.service.DocumentService;
import com.logiclab.documentcontrolsystem.service.differenceService.DocumentDiffService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
@AllArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentDiffService documentDiffService;
    private final DocumentMapper documentMapper;
    private final AuthService authService;
    private final DocumentExportService documentExportService;
    private  final DocumentRepository documentRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<DocumentResponse> createDocument(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam MultipartFile file,
            @RequestParam String extension,
            @RequestHeader("Authorization") String authHeader
    )throws IOException {
        User currentUser = authService.extractUserFromHeader(authHeader);

        CreateDocumentRequest request = new CreateDocumentRequest();

        request.setTitle(title);
        request.setDescription(description);
        request.setExtension(extension);
        request.setContent(file.getBytes());

        Document document = documentService.createDocument(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentMapper.toResponse(document));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable int id) {
        DocumentResponse response = documentMapper.toResponse(
                documentService.getDocumentById(id)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable String title) {
        DocumentResponse response = documentMapper.toResponse(
                documentService.getByDocumentTitle(title)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteDocument(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User currentUser = authService.extractUserFromHeader(authHeader);

        documentService.deleteDocument(id, currentUser);

        return ResponseEntity.ok(new MessageResponse("Document deleted successfully!"));
    }

    @GetMapping("/diff")
    public ResponseEntity<VersionDiffResponse> compareVersions(
            @RequestParam Integer oldVersionId,
            @RequestParam Integer newVersionId
    ) {
        VersionDiffResponse response = documentDiffService.compareVersions(oldVersionId, newVersionId);

        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<DocumentResponse>> getAllDocuments(){
        return ResponseEntity.ok(documentService.getAll());
    }

    @GetMapping("/{documentId}/export/pdf")
    public ResponseEntity<byte[]> exportToPdf(@PathVariable Integer documentId) {
        byte[] pdfBytes = documentExportService.exportToPdf(documentId);

        Document document= documentRepository.findById(documentId).
                orElseThrow(DocumentNotFoundException::new);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + document.getTitle() + ".pdf")
                .body(pdfBytes);
    }

    @GetMapping("/{documentId}/export/txt")
    public ResponseEntity<byte[]> exportToTxt(@PathVariable Integer documentId) {
        byte[] txtBytes = documentExportService.exportToTxt(documentId);

        Document document= documentRepository.findById(documentId).
                orElseThrow(DocumentNotFoundException::new);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + document.getTitle() + ".txt")
                .body(txtBytes);
    }

    @GetMapping("/get-by-author-id/{authorId}")
    public ResponseEntity<List<DocumentResponse>> getByAuthorId(@PathVariable Integer authorId){
        return ResponseEntity.ok(documentMapper.toResponseList(documentService.getByCreatedById(authorId)));
    }
}