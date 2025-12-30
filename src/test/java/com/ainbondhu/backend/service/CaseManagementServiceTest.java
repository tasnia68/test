package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.CaseNote;
import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.domain.entity.LegalCase;
import com.ainbondhu.backend.domain.entity.User;
import com.ainbondhu.backend.domain.enums.CaseStatus;
import com.ainbondhu.backend.dto.CaseNoteRequestDTO;
import com.ainbondhu.backend.dto.CaseNoteResponseDTO;
import com.ainbondhu.backend.dto.LegalCaseRequestDTO;
import com.ainbondhu.backend.dto.LegalCaseResponseDTO;
import com.ainbondhu.backend.domain.entity.CaseDocument;
import com.ainbondhu.backend.dto.CaseDocumentResponseDTO;
import com.ainbondhu.backend.repository.CaseNoteRepository;
import com.ainbondhu.backend.repository.CaseDocumentRepository;
import com.ainbondhu.backend.repository.LegalCaseRepository;
import com.ainbondhu.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CaseManagementServiceTest {

    @Mock
    private LegalCaseRepository legalCaseRepository;

    @Mock
    private CaseNoteRepository caseNoteRepository;

    @Mock
    private CaseDocumentRepository caseDocumentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CaseManagementService caseManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCase_ShouldReturnResponseDTO() {
        Lawyer lawyer = new Lawyer();
        lawyer.setId(UUID.randomUUID());

        LegalCaseRequestDTO request = new LegalCaseRequestDTO();
        request.setCaseNumber("123");
        request.setTitle("Test Case");
        request.setStatus(CaseStatus.OPEN);

        LegalCase savedCase = new LegalCase();
        savedCase.setId(UUID.randomUUID());
        savedCase.setLawyer(lawyer);
        savedCase.setCaseNumber("123");
        savedCase.setTitle("Test Case");
        savedCase.setStatus(CaseStatus.OPEN);

        when(legalCaseRepository.save(any(LegalCase.class))).thenReturn(savedCase);

        LegalCaseResponseDTO response = caseManagementService.createCase(lawyer, request);

        assertNotNull(response);
        assertEquals("123", response.getCaseNumber());
        assertEquals("Test Case", response.getTitle());
    }

    @Test
    void getLawyerCases_ShouldReturnPageOfCases() {
        Lawyer lawyer = new Lawyer();
        lawyer.setId(UUID.randomUUID());

        LegalCase legalCase = new LegalCase();
        legalCase.setId(UUID.randomUUID());
        legalCase.setLawyer(lawyer);

        Page<LegalCase> page = new PageImpl<>(Collections.singletonList(legalCase));

        when(legalCaseRepository.findByLawyerId(eq(lawyer.getId()), any(Pageable.class))).thenReturn(page);

        var cases = caseManagementService.getLawyerCases(lawyer, Pageable.unpaged());

        assertFalse(cases.isEmpty());
        assertEquals(1, cases.getTotalElements());
    }

    @Test
    void addNote_ShouldReturnNoteResponse() {
        Lawyer lawyer = new Lawyer();
        lawyer.setId(UUID.randomUUID());

        LegalCase legalCase = new LegalCase();
        legalCase.setId(UUID.randomUUID());
        legalCase.setLawyer(lawyer);

        CaseNoteRequestDTO request = new CaseNoteRequestDTO();
        request.setTitle("Note Title");
        request.setContent("Content");

        CaseNote savedNote = new CaseNote();
        savedNote.setId(UUID.randomUUID());
        savedNote.setLegalCase(legalCase);
        savedNote.setTitle("Note Title");
        savedNote.setContent("Content");

        when(legalCaseRepository.findById(legalCase.getId())).thenReturn(Optional.of(legalCase));
        when(caseNoteRepository.save(any(CaseNote.class))).thenReturn(savedNote);

        CaseNoteResponseDTO response = caseManagementService.addNote(legalCase.getId(), lawyer, request);

        assertNotNull(response);
        assertEquals("Note Title", response.getTitle());
    }

    @Test
    void uploadDocument_ShouldReturnDocumentResponse() {
        // Mock Request Context for ServletUriComponentsBuilder
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(8080);
        mockRequest.setContextPath("");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        Lawyer lawyer = new Lawyer();
        lawyer.setId(UUID.randomUUID());

        LegalCase legalCase = new LegalCase();
        legalCase.setId(UUID.randomUUID());
        legalCase.setLawyer(lawyer);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());
        String fileName = "uuid_test.pdf";

        when(legalCaseRepository.findById(legalCase.getId())).thenReturn(Optional.of(legalCase));
        when(fileStorageService.storeFile(any())).thenReturn(fileName);

        CaseDocument savedDocument = new CaseDocument();
        savedDocument.setId(UUID.randomUUID());
        savedDocument.setLegalCase(legalCase);
        savedDocument.setFileName(fileName);
        savedDocument.setFileUrl("http://localhost:8080/api/lawyer/cases/documents/download/" + fileName);
        savedDocument.setFileType("application/pdf");

        when(caseDocumentRepository.save(any(CaseDocument.class))).thenReturn(savedDocument);

        CaseDocumentResponseDTO response = caseManagementService.uploadDocument(legalCase.getId(), lawyer, file);

        assertNotNull(response);
        assertEquals(fileName, response.getFileName());
        assertEquals("application/pdf", response.getFileType());

        RequestContextHolder.resetRequestAttributes();
    }
}
