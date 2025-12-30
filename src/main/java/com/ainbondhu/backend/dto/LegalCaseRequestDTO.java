package com.ainbondhu.backend.dto;

import com.ainbondhu.backend.domain.enums.CaseStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LegalCaseRequestDTO {
    private String caseNumber;
    private String title;
    private String description;
    private CaseStatus status;
    private LocalDate filingDate;
    private LocalDateTime nextHearingDate;
    private String courtName;
    private UUID clientId; // Optional client linkage
}
