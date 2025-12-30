package com.ainbondhu.backend.repository;

import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.domain.enums.VerificationStatus;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LawyerRepository extends JpaRepository<Lawyer, UUID> {

    @Query(value = "SELECT l.*, u.* FROM lawyers l " +
            "JOIN app_users u ON l.user_id = u.id " +
            "WHERE ST_DWithin(u.location::geography, :point::geography, :radiusInMeters) " +
            "AND l.verification_status = 'VERIFIED' " +
            "AND l.is_online = true", nativeQuery = true)
    List<Lawyer> findNearbyVerifiedOnlineLawyers(@Param("point") Point point, @Param("radiusInMeters") double radiusInMeters);

    List<Lawyer> findByVerificationStatus(VerificationStatus status);
}
