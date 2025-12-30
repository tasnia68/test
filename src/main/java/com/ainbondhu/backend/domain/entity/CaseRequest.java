package com.ainbondhu.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "case_requests")
@Getter
@Setter
public class CaseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String clientId;
    private String lawyerId;

    // Status: REQUESTED, ACCEPTED, REJECTED, CLOSED
    private String status;

    private LocalDateTime createdAt;
}
