package com.ainbondhu.backend.repository;

import com.ainbondhu.backend.domain.entity.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LegalCaseRepository extends JpaRepository<LegalCase, UUID> {
    List<LegalCase> findByLawyerId(UUID lawyerId);
    List<LegalCase> findByClientId(UUID clientId);
}
