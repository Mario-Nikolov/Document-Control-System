package com.logiclab.documentcontrolsystem.mapper;

import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import com.logiclab.documentcontrolsystem.dto.response.DocumentVersionResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentVersionMapper {

    public DocumentVersionResponse toResponse(DocumentVersion version) {
        if (version == null){
            return null;
        }
        DocumentVersionResponse response = new DocumentVersionResponse();
        response.setId(version.getId());
        response.setDocumentId(version.getDocument().getId());
        response.setVersionNumber(version.getVersionNumber());
        response.setStatus(version.getStatus().name()); //ENUM->String
        response.setActive(version.isActive());
        response.setChangeSummary(version.getChangeSummary());
        response.setContent(version.getContent());
        response.setExtension(version.getExtension());
        response.setCreatedAt(version.getCreatedAt());

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
