package com.ainbondhu.backend.controller;

import com.ainbondhu.backend.domain.entity.User;
import com.ainbondhu.backend.dto.ReviewDto;
import com.ainbondhu.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReviewDto> createReview(
            @AuthenticationPrincipal User currentUser,
            @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.createReview(reviewDto, currentUser));
    }

    @GetMapping("/lawyer/{lawyerId}")
    public ResponseEntity<List<ReviewDto>> getReviewsForLawyer(@PathVariable UUID lawyerId) {
        return ResponseEntity.ok(reviewService.getReviewsForLawyer(lawyerId));
    }
}
