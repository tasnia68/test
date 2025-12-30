package com.ainbondhu.backend.repository;

import com.ainbondhu.backend.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByLawyerId(UUID lawyerId);
    boolean existsByLegalCaseId(UUID caseId);

    @Query("SELECT r.lawyer.id, AVG(r.rating), COUNT(r) FROM Review r WHERE r.lawyer.id IN :lawyerIds GROUP BY r.lawyer.id")
    List<Object[]> findAggregateRatingsForLawyers(@Param("lawyerIds") List<UUID> lawyerIds);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.lawyer.id = :lawyerId")
    Double getAverageRatingByLawyerId(@Param("lawyerId") UUID lawyerId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.lawyer.id = :lawyerId")
    Long countByLawyerId(@Param("lawyerId") UUID lawyerId);
}
