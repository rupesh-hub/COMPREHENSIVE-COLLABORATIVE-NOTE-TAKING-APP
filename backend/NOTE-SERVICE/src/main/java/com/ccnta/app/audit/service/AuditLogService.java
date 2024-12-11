package com.ccnta.app.audit.service;

import com.ccnta.app.audit.entity.AuditLog;
import com.ccnta.app.audit.enums.AuditAction;
import com.ccnta.app.audit.event.AuditEvent;
import com.ccnta.app.audit.repository.AuditRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicationEventPublisher eventPublisher;
    private final AuditRepository auditRepository;

    @Transactional
    public <T> void audit(T entity, AuditAction action, String username, T oldState, String message) {
        try {
            // Prepare audit details
            Map<String, Object> auditDetails = new HashMap<>();
            auditDetails.put("old", oldState != null ? convertToMap(oldState) : null);
            auditDetails.put("new", convertToMap(entity));
            auditDetails.put("message", message);

            // Create audit log
            AuditLog auditLog = new AuditLog();
            auditLog.setAuditLogId(UUID.randomUUID().toString());
            auditLog.setEntityType(entity.getClass().getSimpleName());
            auditLog.setEntityId(extractEntityId(entity));
            auditLog.setUsername(username);
            auditLog.setAction(action);
            auditLog.setDetails(objectMapper.writeValueAsString(auditDetails));

            // Save audit log
            auditRepository.save(auditLog);

            // Publish audit event
            AuditEvent<T> event = new AuditEvent<>(this, entity, action, username, oldState, message);
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            throw new RuntimeException("Audit logging failed", e);
        }
    }

    private <T> Map<String, Object> convertToMap(T entity) {
        if (entity == null) return null;
        return objectMapper.convertValue(entity, Map.class);
    }

    // Helper method to extract entity ID
    private <T> String extractEntityId(T entity) {
        try {
            Method getId = entity.getClass().getMethod("getId");
            Object idValue = getId.invoke(entity);
            return idValue != null ? idValue.toString() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    public List<AuditLog> getAuditLogsForEntity(String entityType, String entityId) {
        return auditRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                entityType, entityId
        );
    }

}
