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
            @RequestBody CreateVersionRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        User currentUser = extractCurrentUser(authHeader);

        DocumentVersionResponse response = documentVersionMapper.toResponse(
                documentVersionService.createDraftVersion(request, currentUser)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentVersionResponse> getVersionById(@PathVariable int id) {
        DocumentVersionResponse response = documentVersionMapper.toResponse(
                documentVersionService.getById(id)
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/submit-for-review")
    public ResponseEntity<DocumentVersionResponse> submitForReview(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User currentUser = extractCurrentUser(authHeader);

        DocumentVersionResponse response = documentVersionMapper.toResponse(
                documentVersionService.submitForReview(id, currentUser)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<DocumentVersionResponse>> getVersionsByDocumentId(@PathVariable int documentId) {
        List<DocumentVersionResponse> response = documentVersionMapper.toResponseList(
                documentVersionService.getVersionsByDocumentId(documentId)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/document/{documentId}/active")
    public ResponseEntity<DocumentVersionResponse> getActiveVersionByDocumentId(@PathVariable int documentId) {
        DocumentVersionResponse response = documentVersionMapper.toResponse(
                documentVersionService.getActiveVersionByDocumentId(documentId)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/document/{documentId}/latest")
    public ResponseEntity<DocumentVersionResponse> getLatestVersionByDocumentId(@PathVariable int documentId) {
        DocumentVersionResponse response = documentVersionMapper.toResponse(
                documentVersionService.getLatestVersionByDocumentId(documentId)
        );

        return ResponseEntity.ok(response);
    }

    private User extractCurrentUser(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            throw new MissingAuthorizationHeaderException();
        }

        if (!authHeader.startsWith("Bearer ")) {
            throw new InvalidAuthorizationHeaderException();
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " not found!"));
    }
}