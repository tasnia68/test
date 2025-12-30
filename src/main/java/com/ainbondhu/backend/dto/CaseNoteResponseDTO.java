package com.ainbondhu.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CaseNoteResponseDTO {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime noteDate;
    private String attachmentUrl;
    private UUID legalCaseId;
}
