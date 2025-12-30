package com.ainbondhu.backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phoneNumber;
    private String fullNameBn;
    private double latitude;
    private double longitude;

    // Lawyer specific (optional)
    private String barLicenseNumber;
    private String educationalBackground;
    private Integer experienceYears;
}
