package com.ainbondhu.backend.controller;

import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.domain.entity.User;
import com.ainbondhu.backend.domain.enums.Role;
import com.ainbondhu.backend.dto.CaseNoteRequestDTO;
import com.ainbondhu.backend.dto.CaseNoteResponseDTO;
import com.ainbondhu.backend.dto.LegalCaseRequestDTO;
import com.ainbondhu.backend.dto.LegalCaseResponseDTO;
import com.ainbondhu.backend.repository.UserRepository;
import com.ainbondhu.backend.service.CaseManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lawyer/cases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LAWYER')")
public class CaseManagementController {

    private final CaseManagementService caseManagementService;
    private final UserRepository userRepository;

    private Lawyer getLawyer(User user) {
        if (user.getRole() != Role.LAWYER) {
            throw new RuntimeException("Access Denied: Only Lawyers can access this resource.");
        }
        // In JOINED inheritance, fetching User should be castable if it's actually a Lawyer.
        // However, Spring Security principal might be a User proxy or object.
        // It's safer to fetch the Lawyer entity if needed, but if the principal is the entity itself:
        if (user instanceof Lawyer) {
            return (Lawyer) user;
        }
        // If not castable directly (e.g. detached or proxied), re-fetch or cast via repository logic
        // For simplicity assuming the principal is the entity as loaded by UserDetailsService
        return (Lawyer) userRepository.findById(user.getId()).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<LegalCaseResponseDTO> createCase(
            @AuthenticationPrincipal User currentUser,
            @RequestBody LegalCaseRequestDTO request) {
        return ResponseEntity.ok(caseManagementService.createCase(getLawyer(currentUser), request));
    }

    @GetMapping
    public ResponseEntity<List<LegalCaseResponseDTO>> getAllCases(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(caseManagementService.getLawyerCases(getLawyer(currentUser)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalCaseResponseDTO> getCaseDetails(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id) {
        return ResponseEntity.ok(caseManagementService.getCaseDetails(id, getLawyer(currentUser)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalCaseResponseDTO> updateCase(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id,
            @RequestBody LegalCaseRequestDTO request) {
        return ResponseEntity.ok(caseManagementService.updateCase(id, getLawyer(currentUser), request));
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<CaseNoteResponseDTO> addNote(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id,
            @RequestBody CaseNoteRequestDTO request) {
        return ResponseEntity.ok(caseManagementService.addNote(id, getLawyer(currentUser), request));
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<List<CaseNoteResponseDTO>> getCaseNotes(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id) {
        return ResponseEntity.ok(caseManagementService.getCaseNotes(id, getLawyer(currentUser)));
    }
}
