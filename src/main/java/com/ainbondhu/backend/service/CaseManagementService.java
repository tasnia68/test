package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.CaseNote;
import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.domain.entity.LegalCase;
import com.ainbondhu.backend.domain.entity.User;
import com.ainbondhu.backend.dto.CaseNoteRequestDTO;
import com.ainbondhu.backend.dto.CaseNoteResponseDTO;
import com.ainbondhu.backend.dto.LegalCaseRequestDTO;
import com.ainbondhu.backend.dto.LegalCaseResponseDTO;
import com.ainbondhu.backend.repository.CaseNoteRepository;
import com.ainbondhu.backend.repository.LegalCaseRepository;
import com.ainbondhu.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseManagementService {

    private final LegalCaseRepository legalCaseRepository;
    private final CaseNoteRepository caseNoteRepository;
    private final UserRepository userRepository;

    @Transactional
    public LegalCaseResponseDTO createCase(Lawyer lawyer, LegalCaseRequestDTO request) {
        LegalCase legalCase = new LegalCase();
        legalCase.setLawyer(lawyer);
        legalCase.setCaseNumber(request.getCaseNumber());
        legalCase.setTitle(request.getTitle());
        legalCase.setDescription(request.getDescription());
        legalCase.setStatus(request.getStatus());
        legalCase.setFilingDate(request.getFilingDate());
        legalCase.setNextHearingDate(request.getNextHearingDate());
        legalCase.setCourtName(request.getCourtName());

        if (request.getClientId() != null) {
            User client = userRepository.findById(request.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            legalCase.setClient(client);
        }

        legalCase = legalCaseRepository.save(legalCase);
        return mapToDTO(legalCase);
    }

    @Transactional(readOnly = true)
    public List<LegalCaseResponseDTO> getLawyerCases(Lawyer lawyer) {
        return legalCaseRepository.findByLawyerId(lawyer.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LegalCaseResponseDTO getCaseDetails(UUID caseId, Lawyer lawyer) {
        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        if (!legalCase.getLawyer().getId().equals(lawyer.getId())) {
            throw new RuntimeException("Unauthorized access to case");
        }

        return mapToDTO(legalCase);
    }

    @Transactional
    public LegalCaseResponseDTO updateCase(UUID caseId, Lawyer lawyer, LegalCaseRequestDTO request) {
        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        if (!legalCase.getLawyer().getId().equals(lawyer.getId())) {
            throw new RuntimeException("Unauthorized access to case");
        }

        legalCase.setCaseNumber(request.getCaseNumber());
        legalCase.setTitle(request.getTitle());
        legalCase.setDescription(request.getDescription());
        legalCase.setStatus(request.getStatus());
        legalCase.setFilingDate(request.getFilingDate());
        legalCase.setNextHearingDate(request.getNextHearingDate());
        legalCase.setCourtName(request.getCourtName());

        if (request.getClientId() != null) {
             User client = userRepository.findById(request.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            legalCase.setClient(client);
        } else {
            legalCase.setClient(null);
        }

        legalCase = legalCaseRepository.save(legalCase);
        return mapToDTO(legalCase);
    }

    @Transactional
    public CaseNoteResponseDTO addNote(UUID caseId, Lawyer lawyer, CaseNoteRequestDTO request) {
        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        if (!legalCase.getLawyer().getId().equals(lawyer.getId())) {
            throw new RuntimeException("Unauthorized access to case");
        }

        CaseNote note = new CaseNote();
        note.setLegalCase(legalCase);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setNoteDate(request.getNoteDate() != null ? request.getNoteDate() : LocalDateTime.now());
        note.setAttachmentUrl(request.getAttachmentUrl());

        note = caseNoteRepository.save(note);
        return mapToDTO(note);
    }

    @Transactional(readOnly = true)
    public List<CaseNoteResponseDTO> getCaseNotes(UUID caseId, Lawyer lawyer) {
        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        if (!legalCase.getLawyer().getId().equals(lawyer.getId())) {
            throw new RuntimeException("Unauthorized access to case");
        }

        return caseNoteRepository.findByLegalCaseIdOrderByNoteDateDesc(caseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private LegalCaseResponseDTO mapToDTO(LegalCase legalCase) {
        return LegalCaseResponseDTO.builder()
                .id(legalCase.getId())
                .caseNumber(legalCase.getCaseNumber())
                .title(legalCase.getTitle())
                .description(legalCase.getDescription())
                .status(legalCase.getStatus())
                .filingDate(legalCase.getFilingDate())
                .nextHearingDate(legalCase.getNextHearingDate())
                .courtName(legalCase.getCourtName())
                .createdAt(legalCase.getCreatedAt())
                .updatedAt(legalCase.getUpdatedAt())
                .lawyerId(legalCase.getLawyer().getId())
                .clientId(legalCase.getClient() != null ? legalCase.getClient().getId() : null)
                .clientName(legalCase.getClient() != null ? legalCase.getClient().getFullNameBn() : null)
                .build();
    }

    private CaseNoteResponseDTO mapToDTO(CaseNote note) {
        return CaseNoteResponseDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .noteDate(note.getNoteDate())
                .attachmentUrl(note.getAttachmentUrl())
                .legalCaseId(note.getLegalCase().getId())
                .build();
    }
}
