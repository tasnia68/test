package com.ainbondhu.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LawyerDto {
    private String id;
    private String phoneNumber;
    private String fullNameBn;
    private String barLicenseNumber;
    private String verificationStatus;
    private boolean isOnline;
    private double distance; // calculated distance
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalCasesServed;
}
