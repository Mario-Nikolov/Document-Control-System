package com.logiclab.documentcontrolsystem.repository;

import com.logiclab.documentcontrolsystem.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    Optional<Document> findByTitle(String title);

    List<Document> findByCreatedById(Integer userId);

    boolean existsByTitle(String title);
}