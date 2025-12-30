package com.ainbondhu.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "case_notes")
@Getter
@Setter
public class CaseNote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime noteDate;

    private String attachmentUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_case_id", nullable = false)
    private LegalCase legalCase;

    @PrePersist
    protected void onCreate() {
        if (noteDate == null) {
            noteDate = LocalDateTime.now();
        }
    }
}
