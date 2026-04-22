package com.logiclab.documentcontrolsystem.repository;

import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import com.logiclab.documentcontrolsystem.domain.Review;
import com.logiclab.documentcontrolsystem.domain.ReviewDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByReviewerId(Integer reviewerId);

    Optional<Review> findByDocumentVersion_Id(Integer documentVersionId);

    boolean existsByDocumentVersion(DocumentVersion documentVersion);

    //void deleteByVersionId(Integer versionId);

}