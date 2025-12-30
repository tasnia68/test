package com.ainbondhu.backend.domain.entity;

import com.ainbondhu.backend.domain.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "lawyers")
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "user_id")
public class Lawyer extends User {

    @Column(unique = true)
    private String barLicenseNumber;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @ElementCollection
    private List<String> specializations;

    private BigDecimal hourlyRate;

    private boolean isOnline;

    private String educationalBackground;

    private Integer experienceYears;
}
