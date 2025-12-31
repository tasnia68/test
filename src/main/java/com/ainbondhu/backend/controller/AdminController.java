package com.ainbondhu.backend.controller;

import com.ainbondhu.backend.domain.entity.AuditLog;
import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.domain.enums.VerificationStatus;
import com.ainbondhu.backend.repository.AuditLogRepository;
import com.ainbondhu.backend.repository.LawyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final LawyerRepository lawyerRepository;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(auditLogRepository.findAll(pageable));
    }

    @PatchMapping("/lawyers/{id}/verify")
    public ResponseEntity<Void> verifyLawyer(@PathVariable String id) {
        Lawyer lawyer = lawyerRepository.findById(java.util.UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Lawyer not found"));

        lawyer.setVerificationStatus(VerificationStatus.VERIFIED);
        lawyerRepository.save(lawyer);
        return ResponseEntity.ok().build();
    }
}
