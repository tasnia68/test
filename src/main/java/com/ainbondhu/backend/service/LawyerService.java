package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.dto.LawyerDto;
import com.ainbondhu.backend.repository.LawyerRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LawyerService {

    private final LawyerRepository lawyerRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<LawyerDto> findNearbyLawyers(double lat, double lon, double radiusInMeters) {
        Point userLocation = geometryFactory.createPoint(new Coordinate(lon, lat));
        List<Lawyer> lawyers = lawyerRepository.findNearbyVerifiedOnlineLawyers(userLocation, radiusInMeters);

        return lawyers.stream().map(lawyer -> LawyerDto.builder()
                .id(lawyer.getId().toString())
                .phoneNumber(lawyer.getPhoneNumber())
                .fullNameBn(lawyer.getFullNameBn())
                .barLicenseNumber(lawyer.getBarLicenseNumber())
                .verificationStatus(lawyer.getVerificationStatus().name())
                .isOnline(lawyer.isOnline())
                // In a real app, calculate actual distance here
                .build()).collect(Collectors.toList());
    }

    public void updateOnlineStatus(String lawyerId, boolean isOnline) {
        Lawyer lawyer = lawyerRepository.findById(java.util.UUID.fromString(lawyerId))
                .orElseThrow(() -> new RuntimeException("Lawyer not found"));
        lawyer.setOnline(isOnline);
        lawyerRepository.save(lawyer);
    }
}
