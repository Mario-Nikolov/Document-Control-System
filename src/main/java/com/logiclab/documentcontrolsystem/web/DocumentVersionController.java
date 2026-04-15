package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.CreateVersionRequest;
import com.logiclab.documentcontrolsystem.dto.response.DocumentVersionResponse;
import com.logiclab.documentcontrolsystem.exceptions.InvalidAuthorizationHeaderException;
import com.logiclab.documentcontrolsystem.exceptions.MissingAuthorizationHeaderException;
import com.logiclab.documentcontrolsystem.exceptions.UserNotFoundException;
import com.logiclab.documentcontrolsystem.mapper.DocumentVersionMapper;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
import com.logiclab.documentcontrolsystem.service.DocumentVersionService;
import com.logiclab.documentcontrolsystem.service.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/versions")
@AllArgsConstructor
public class DocumentVersionController {

    private final DocumentVersionService documentVersionService;
    private final DocumentVersionMapper documentVersionMapper;
    private final UserRepository userRepository;
    private final JWTService jwtService;

    @PostMapping
    public ResponseEntity<DocumentVersionResponse> createVersion(
            @RequestParam Integer documentId,
            @RequestParam MultipartFile file,
            @RequestParam String extension,
            @RequestParam String changeSummary,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        User currentUser = jwtService.extractUser(authHeader);

        CreateVersionRequest request = new CreateVersionRequest();

        request.setDocumentId(documentId);
        request.setContent(file.getBytes());
        request.setExtension(extension);
        request.setChangeSummary(changeSummary);

        DocumentVersionResponse response = documentVersionMapper.toResponse(
                documentVersionService.createVersion(request, currentUser)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentVersionResponse> getVersionById(@PathVariable int id) {

        return ResponseEntity.ok(documentVersionService.getById(id));
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<DocumentVersionResponse>> getVersionsByDocumentId(@PathVariable int documentId) {
        return ResponseEntity.ok(documentVersionService.getVersionsByDocumentId(documentId));
    }

    @GetMapping("/document/{documentId}/active")
    public ResponseEntity<DocumentVersionResponse> getActiveVersionByDocumentId(@PathVariable int documentId) {
        return ResponseEntity.ok(documentVersionService.getActiveVersionByDocumentId(documentId));
    }

    @GetMapping("/document/{documentId}/latest")
    public ResponseEntity<DocumentVersionResponse> getLatestVersionByDocumentId(@PathVariable int documentId) {
        return ResponseEntity.ok(documentVersionService.getLatestVersionByDocumentId(documentId));
    }


}