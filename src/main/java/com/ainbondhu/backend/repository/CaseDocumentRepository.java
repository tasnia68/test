package com.ainbondhu.backend.repository;

import com.ainbondhu.backend.domain.entity.CaseDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseDocumentRepository extends JpaRepository<CaseDocument, UUID> {
    List<CaseDocument> findByLegalCaseIdOrderByUploadedAtDesc(UUID legalCaseId);
    Optional<CaseDocument> findByFileName(String fileName);
}
