package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.AuditLog;
import com.ainbondhu.backend.repository.AuditLogRepository;
import com.ainbondhu.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logChange(String entityName, String entityId, String action, String details) {
        AuditLog auditLog = AuditLog.builder()
                .entityName(entityName)
                .entityId(entityId)
                .action(action)
                .changedBy(SecurityUtils.getCurrentUsername())
                .changeTime(LocalDateTime.now())
                .details(details)
                .build();
        auditLogRepository.save(auditLog);
    }
}
