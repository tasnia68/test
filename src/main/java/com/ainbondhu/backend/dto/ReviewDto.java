package com.ainbondhu.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReviewDto {
    private UUID id;
    private Integer rating;
    private String comment;
    private UUID lawyerId;
    private UUID clientId;
    private String clientName; // Optional: helpful for display
    private UUID caseId;
    private LocalDateTime createdAt;
}
