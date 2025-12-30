package com.ainbondhu.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaseNoteRequestDTO {
    private String title;
    private String content;
    private LocalDateTime noteDate;
    private String attachmentUrl;
}
