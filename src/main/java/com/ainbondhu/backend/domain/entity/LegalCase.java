package com.ainbondhu.backend.domain.entity;

import com.ainbondhu.backend.domain.enums.CaseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "legal_cases")
@Getter
@Setter
public class LegalCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String caseNumber;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    private LocalDate filingDate;
    private LocalDateTime nextHearingDate;
    private String courtName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id", nullable = false)
    private Lawyer lawyer;

    // Optional link to a registered client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
