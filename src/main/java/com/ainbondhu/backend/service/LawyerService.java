package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.domain.entity.Review;
import com.ainbondhu.backend.dto.LawyerDto;
import com.ainbondhu.backend.repository.LawyerRepository;
import com.ainbondhu.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LawyerService {

    private final LawyerRepository lawyerRepository;
    private final ReviewRepository reviewRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<LawyerDto> findNearbyLawyers(double lat, double lon, double radiusInMeters) {
        Point userLocation = geometryFactory.createPoint(new Coordinate(lon, lat));
        List<Lawyer> lawyers = lawyerRepository.findNearbyVerifiedOnlineLawyers(userLocation, radiusInMeters);

        if (lawyers.isEmpty()) {
            return List.of();
        }

        List<UUID> lawyerIds = lawyers.stream().map(Lawyer::getId).collect(Collectors.toList());
        List<Object[]> aggregates = reviewRepository.findAggregateRatingsForLawyers(lawyerIds);

        // Map aggregates to a fast lookup map
        Map<UUID, double[]> ratingsMap = new HashMap<>();
        for (Object[] agg : aggregates) {
            UUID id = (UUID) agg[0];
            Double avg = (Double) agg[1];
            Long count = (Long) agg[2];
            ratingsMap.put(id, new double[]{avg != null ? avg : 0.0, count != null ? count.doubleValue() : 0.0});
        }

        return lawyers.stream().map(lawyer -> {
            double[] ratingData = ratingsMap.getOrDefault(lawyer.getId(), new double[]{0.0, 0.0});
            return LawyerDto.builder()
                .id(lawyer.getId().toString())
                .phoneNumber(lawyer.getPhoneNumber())
                .fullNameBn(lawyer.getFullNameBn())
                .barLicenseNumber(lawyer.getBarLicenseNumber())
                .verificationStatus(lawyer.getVerificationStatus().name())
                .isOnline(lawyer.isOnline())
                .averageRating(ratingData[0])
                .totalReviews((int) ratingData[1])
                // In a real app, calculate actual distance here
                .build();
        }).collect(Collectors.toList());
    }

    public void updateOnlineStatus(String lawyerId, boolean isOnline) {
        Lawyer lawyer = lawyerRepository.findById(java.util.UUID.fromString(lawyerId))
                .orElseThrow(() -> new RuntimeException("Lawyer not found"));
        lawyer.setOnline(isOnline);
        lawyerRepository.save(lawyer);
    }
}
