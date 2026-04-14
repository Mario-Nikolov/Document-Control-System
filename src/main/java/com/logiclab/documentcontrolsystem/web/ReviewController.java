package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.dto.request.CreateReviewRequest;
import com.logiclab.documentcontrolsystem.dto.response.ReviewResponse;
import com.logiclab.documentcontrolsystem.mapper.ReviewMapper;
import com.logiclab.documentcontrolsystem.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateReviewRequest request) {

        ReviewResponse response = reviewMapper.toResponse(
                reviewService.createReview(request, authHeader)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/version/{documentVersionId}")
    public ResponseEntity<ReviewResponse> getReviewByDocumentVersionId(@PathVariable Integer documentVersionId) {
        ReviewResponse response = reviewMapper.toResponse(
                reviewService.getReviewByDocumentVersionId(documentVersionId)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByReviewerId(@PathVariable int reviewerId) {
        return ResponseEntity.ok(reviewService.getReviewsByReviewerId(reviewerId));
    }

}