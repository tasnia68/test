package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.CaseNote;
import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.domain.entity.LegalCase;
import com.ainbondhu.backend.domain.entity.User;
import com.ainbondhu.backend.dto.CaseNoteRequestDTO;
import com.ainbondhu.backend.domain.entity.CaseDocument;
import com.ainbondhu.backend.dto.CaseNoteResponseDTO;
import com.ainbondhu.backend.dto.CaseDocumentResponseDTO;
import com.ainbondhu.backend.dto.LegalCaseRequestDTO;
import com.ainbondhu.backend.dto.LegalCaseResponseDTO;
import com.ainbondhu.backend.repository.CaseNoteRepository;
import com.ainbondhu.backend.repository.CaseDocumentRepository;
import com.ainbondhu.backend.repository.LegalCaseRepository;
import com.ainbondhu.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseManagementService {

    private final LegalCaseRepository legalCaseRepository;
    private final CaseNoteRepository caseNoteRepository;
    private final CaseDocumentRepository caseDocumentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

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
    public Page<LegalCaseResponseDTO> getLawyerCases(Lawyer lawyer, Pageable pageable) {
        return legalCaseRepository.findByLawyerId(lawyer.getId(), pageable)
                .map(this::mapToDTO);
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

    @Transactional
    public CaseDocumentResponseDTO uploadDocument(UUID caseId, Lawyer lawyer, MultipartFile file) {
        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        if (!legalCase.getLawyer().getId().equals(lawyer.getId())) {
            throw new RuntimeException("Unauthorized access to case");
        }

        String fileName = fileStorageService.storeFile(file);

        // Build file URL (Assuming we are serving files via a static resource handler or separate controller)
        // For local storage, we can construct a download URL
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/lawyer/cases/documents/download/")
                .path(fileName)
                .toUriString();

        CaseDocument document = new CaseDocument();
        document.setLegalCase(legalCase);
        document.setFileName(fileName);
        document.setFileUrl(fileDownloadUri);
        document.setFileType(file.getContentType());
        document.setUploadedAt(LocalDateTime.now());

        document = caseDocumentRepository.save(document);
        return mapToDTO(document);
    }

    @Transactional(readOnly = true)
    public List<CaseDocumentResponseDTO> getCaseDocuments(UUID caseId, Lawyer lawyer) {
        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        if (!legalCase.getLawyer().getId().equals(lawyer.getId())) {
            throw new RuntimeException("Unauthorized access to case");
        }

        return caseDocumentRepository.findByLegalCaseIdOrderByUploadedAtDesc(caseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Resource downloadDocument(String fileName, Lawyer lawyer) {
        CaseDocument document = caseDocumentRepository.findByFileName(fileName)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Security check: Ensure the lawyer owns the case associated with this document
        if (!document.getLegalCase().getLawyer().getId().equals(lawyer.getId())) {
            throw new RuntimeException("Unauthorized access to document");
        }

        return fileStorageService.loadFileAsResource(fileName);
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

    private CaseDocumentResponseDTO mapToDTO(CaseDocument document) {
        return CaseDocumentResponseDTO.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileType(document.getFileType())
                .uploadedAt(document.getUploadedAt())
                .legalCaseId(document.getLegalCase().getId())
                .build();
    }
}
