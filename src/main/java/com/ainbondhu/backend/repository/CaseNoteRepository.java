package com.ainbondhu.backend.repository;

import com.ainbondhu.backend.domain.entity.CaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseNoteRepository extends JpaRepository<CaseNote, UUID> {
    List<CaseNote> findByLegalCaseIdOrderByNoteDateDesc(UUID legalCaseId);
}
