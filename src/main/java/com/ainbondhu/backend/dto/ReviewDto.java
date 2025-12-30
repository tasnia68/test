package com.ainbondhu.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReviewDto {
    private UUID id;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
    private UUID lawyerId;
    private UUID clientId;
    private String clientName; // Optional: helpful for display
    private UUID caseId;
    private LocalDateTime createdAt;
}
