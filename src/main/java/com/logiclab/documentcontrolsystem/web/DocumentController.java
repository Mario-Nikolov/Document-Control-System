package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.CreateDocumentRequest;
import com.logiclab.documentcontrolsystem.dto.response.DocumentResponse;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.dto.response.VersionDiffResponse;
import com.logiclab.documentcontrolsystem.mapper.DocumentMapper;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
import com.logiclab.documentcontrolsystem.service.DocumentService;
import com.logiclab.documentcontrolsystem.service.JWTService;
import com.logiclab.documentcontrolsystem.service.differenceService.DocumentDiffService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
@AllArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentDiffService documentDiffService;
    private final DocumentMapper documentMapper;
    private final UserRepository userRepository;
    private final JWTService jwtService;

    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(
            @RequestBody CreateDocumentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {

        User currentUser = extractCurrentUser(authHeader);

        DocumentResponse response = documentMapper.toResponse(
                documentService.createDraft(request, currentUser)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable int id) {
        DocumentResponse response = documentMapper.toResponse(
                documentService.getDocumentById(id)
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<DocumentResponse> publishDocument(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User currentUser = extractCurrentUser(authHeader);

        DocumentResponse response = documentMapper.toResponse(
                documentService.publishDocument(id, currentUser)
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteDocument(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User currentUser = extractCurrentUser(authHeader);

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

    private User extractCurrentUser(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            throw new RuntimeException("Authorization header is missing!");
        }

        if (!authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header format!");
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }
}