package com.ainbondhu.backend.repository;

import com.ainbondhu.backend.domain.entity.CaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseRequestRepository extends JpaRepository<CaseRequest, UUID> {
    List<CaseRequest> findByLawyerIdAndStatus(String lawyerId, String status);
    List<CaseRequest> findByClientId(String clientId);
}
