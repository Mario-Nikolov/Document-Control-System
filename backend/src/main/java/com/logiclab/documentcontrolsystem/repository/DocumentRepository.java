package com.logiclab.documentcontrolsystem.repository;

import com.logiclab.documentcontrolsystem.domain.Document;
import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    Optional<Document> findByTitle(String title);

    List<Document> findByCreatedById(Integer userId);

    boolean existsByTitle(String title);

    @Modifying
    @Query(value = """
            UPDATE documents
            SET active_version_id = :versionId,
                updated_at = NOW()
            WHERE id = :documentId
            """, nativeQuery = true)
    int updateActiveVersionNative(@Param("documentId") Integer documentId,
                                  @Param("versionId") Integer versionId);
}