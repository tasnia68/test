package com.ainbondhu.backend.dto;

import com.ainbondhu.backend.domain.enums.CaseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LegalCaseResponseDTO {
    private UUID id;
    private String caseNumber;
    private String title;
    private String description;
    private CaseStatus status;
    private LocalDate filingDate;
    private LocalDateTime nextHearingDate;
    private String courtName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID lawyerId;
    private UUID clientId;
    private String clientName;
}
