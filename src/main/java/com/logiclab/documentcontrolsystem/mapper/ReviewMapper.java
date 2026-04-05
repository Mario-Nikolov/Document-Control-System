package com.logiclab.documentcontrolsystem.mapper;

import com.logiclab.documentcontrolsystem.domain.Review;
import com.logiclab.documentcontrolsystem.dto.response.ReviewResponse;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponse response = new ReviewResponse();

        response.setId(review.getId());
        response.setDocumentVersionId(review.getDocumentVersion().getId());
        response.setDecision(review.getDecision().name()); // enum -> String
        response.setComment(review.getComment());
        response.setReviewedAt(review.getReviewedAt());

        if (review.getReviewer() != null) {
            response.setReviewerUsername(review.getReviewer().getUsername());
        }

        return response;
    }
}