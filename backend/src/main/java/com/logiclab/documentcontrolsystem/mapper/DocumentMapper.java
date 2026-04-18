package com.logiclab.documentcontrolsystem.mapper;

import com.logiclab.documentcontrolsystem.domain.Document;
import com.logiclab.documentcontrolsystem.domain.VersionStatus;
import com.logiclab.documentcontrolsystem.dto.response.DocumentResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentMapper {

    public DocumentResponse toResponse(Document document) {
        if (document == null){
            return null;
        }

        DocumentResponse documentResponse = new DocumentResponse();

        documentResponse.setId(document.getId());
        documentResponse.setTitle(document.getTitle());
        documentResponse.setDescription(document.getDescription());
        documentResponse.setCreatedAt(document.getCreatedAt());
        documentResponse.setUpdatedAt(document.getUpdatedAt());


        if (document.getCreatedBy() != null){
            documentResponse.setCreatedByUsername(document.getCreatedBy().getUsername());
        }

        var activeVersion = document.getActiveVersion();

        if (activeVersion != null){
            documentResponse.setActiveVersionId(activeVersion.getId());
            documentResponse.setVersionStatus(activeVersion.getStatus());
        } else {
            documentResponse.setVersionStatus(VersionStatus.DRAFT);
        }



        return documentResponse;

    }

    public List<DocumentResponse> toResponseList(List<Document> documents) {
        return documents.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
