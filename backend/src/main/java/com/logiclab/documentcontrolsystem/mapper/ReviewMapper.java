package com.logiclab.documentcontrolsystem.mapper;

import com.logiclab.documentcontrolsystem.domain.Review;
import com.logiclab.documentcontrolsystem.dto.response.ReviewResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponse response = new ReviewResponse();

        response.setId(review.getId());
        response.setDocumentVersionId(review.getDocumentVersion().getId());
        response.setDecision(review.getDecision().name());
        response.setComment(review.getComment());
        response.setReviewedAt(review.getReviewedAt());

        if (review.getReviewer() != null) {
            response.setReviewerUsername(review.getReviewer().getUsername());
        }

        return response;
    }

    public List<ReviewResponse>toResponseList(List<Review> list){
        return  list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}