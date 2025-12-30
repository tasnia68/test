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
import com.ainbondhu.backend.repository.CaseNoteRepository;
import com.ainbondhu.backend.repository.LegalCaseRepository;
import com.ainbondhu.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    private UserRepository userRepository;

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
    void getLawyerCases_ShouldReturnListOfCases() {
        Lawyer lawyer = new Lawyer();
        lawyer.setId(UUID.randomUUID());

        LegalCase legalCase = new LegalCase();
        legalCase.setId(UUID.randomUUID());
        legalCase.setLawyer(lawyer);

        when(legalCaseRepository.findByLawyerId(lawyer.getId())).thenReturn(Collections.singletonList(legalCase));

        var cases = caseManagementService.getLawyerCases(lawyer);

        assertFalse(cases.isEmpty());
        assertEquals(1, cases.size());
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
}
