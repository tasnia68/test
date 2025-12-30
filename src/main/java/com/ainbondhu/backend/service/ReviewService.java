package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.domain.entity.LegalCase;
import com.ainbondhu.backend.domain.entity.Review;
import com.ainbondhu.backend.domain.entity.User;
import com.ainbondhu.backend.domain.enums.CaseStatus;
import com.ainbondhu.backend.dto.ReviewDto;
import com.ainbondhu.backend.repository.LegalCaseRepository;
import com.ainbondhu.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LegalCaseRepository legalCaseRepository;

    @Transactional
    public ReviewDto createReview(ReviewDto reviewDto, User currentUser) {
        // 1. Fetch the case
        LegalCase legalCase = legalCaseRepository.findById(reviewDto.getCaseId())
                .orElseThrow(() -> new RuntimeException("Case not found"));

        // 2. Validate user is the client of the case
        if (!legalCase.getClient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the client of this case can submit a review.");
        }

        // 3. Validate case status is CLOSED
        if (legalCase.getStatus() != CaseStatus.CLOSED) {
            throw new RuntimeException("Reviews can only be submitted for CLOSED cases.");
        }

        // 4. Check if review already exists
        if (reviewRepository.existsByLegalCaseId(legalCase.getId())) {
            throw new RuntimeException("A review already exists for this case.");
        }

        // 5. Create Review
        Review review = new Review();
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setLegalCase(legalCase);
        review.setClient(currentUser);
        review.setLawyer(legalCase.getLawyer());

        Review savedReview = reviewRepository.save(review);

        return mapToDto(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForLawyer(UUID lawyerId) {
        return reviewRepository.findByLawyerId(lawyerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ReviewDto mapToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setLawyerId(review.getLawyer().getId());
        dto.setClientId(review.getClient().getId());
        dto.setClientName(review.getClient().getFullNameBn());
        dto.setCaseId(review.getLegalCase().getId());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
