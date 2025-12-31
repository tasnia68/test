package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.AuditLog;
import com.ainbondhu.backend.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    void logChange_ShouldSaveAuditLog_WhenCalled() {
        // Arrange
        String entityName = "LegalCase";
        String entityId = "123";
        String action = "CREATE";
        String details = "Test details";
        String username = "testuser";

        // Mock Security Context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        // Act
        auditService.logChange(entityName, entityId, action, details);

        // Assert
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertEquals(entityName, savedLog.getEntityName());
        assertEquals(entityId, savedLog.getEntityId());
        assertEquals(action, savedLog.getAction());
        assertEquals(details, savedLog.getDetails());
        assertEquals(username, savedLog.getChangedBy());
        assertNotNull(savedLog.getChangeTime());
    }
}
