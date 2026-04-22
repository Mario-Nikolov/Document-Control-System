package com.logiclab.documentcontrolsystem.repository;

import com.logiclab.documentcontrolsystem.domain.Document;
import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import com.logiclab.documentcontrolsystem.domain.VersionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Integer> {

    List<DocumentVersion> findByDocumentId(Integer documentId);

    Optional<DocumentVersion> findByDocumentIdAndIsActiveTrue(Integer documentId);

    Optional<DocumentVersion> findTopByDocumentIdOrderByVersionNumberDesc(Integer documentId);

    Optional<DocumentVersion> findTopByDocumentOrderByVersionNumberDesc(Document document);

    List<DocumentVersion> findByIsActiveTrue();
}