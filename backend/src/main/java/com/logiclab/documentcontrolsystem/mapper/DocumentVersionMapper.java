package com.logiclab.documentcontrolsystem.mapper;

import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import com.logiclab.documentcontrolsystem.dto.response.DocumentVersionResponse;
import com.logiclab.documentcontrolsystem.service.ContentExtractionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DocumentVersionMapper {
    private final ContentExtractionService contentExtractionService;

    public DocumentVersionResponse toResponse(DocumentVersion version) {
        if (version == null){
            return null;
        }
        DocumentVersionResponse response = new DocumentVersionResponse();
        response.setId(version.getId());
        response.setDocumentId(version.getDocument().getId());
        response.setVersionNumber(version.getVersionNumber());
        response.setStatus(version.getStatus().name());
        response.setTitle(version.getDocument().getTitle());
        response.setDescription(version.getDocument().getDescription());
        response.setActive(version.isActive());
        response.setChangeSummary(version.getChangeSummary());
        response.setContent(version.getContent());
        response.setExtension(version.getExtension());
        response.setCreatedAt(version.getCreatedAt());
        response.setText(contentExtractionService.extractText(version.getContent(), version.getExtension()));

        if (version.getParentVersion() != null){
            response.setParentVersionId(version.getParentVersion().getId());
        }

        if (version.getCreatedBy() != null){
            response.setCreatedByUsername(version.getCreatedBy().getUsername());
        }

        return response;
    }

    public List<DocumentVersionResponse> toResponseList(List<DocumentVersion> versions) {
        return versions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
