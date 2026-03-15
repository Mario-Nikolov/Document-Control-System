package com.logiclab.documentcontrolsystem.repository;

import com.logiclab.documentcontrolsystem.domain.Review;
import com.logiclab.documentcontrolsystem.domain.VersionDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByDocumentVersionId(Integer documentVersionId);

    List<Review> findByReviewerId(Integer reviewerId);

    Optional<Review> findByDocumentVersionIdAndReviewerId(Integer documentVersionId, Integer reviewerId);

    List<Review> findByDecision(VersionDecision decision);
}