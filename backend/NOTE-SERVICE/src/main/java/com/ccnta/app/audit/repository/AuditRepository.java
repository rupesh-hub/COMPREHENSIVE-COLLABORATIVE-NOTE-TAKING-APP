package com.ccnta.app.audit.repository;

import com.ccnta.app.audit.entity.AuditLog;
import com.ccnta.app.audit.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {

    // Standard method derived from method name
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, String entityId
    );

    // JPQL Query with additional flexibility
    @Query("SELECT al FROM AuditLog al " +
            "WHERE al.entityType = :entityType " +
            "AND al.entityId = :entityId " +
            "ORDER BY al.createdAt DESC")
    List<AuditLog> findAuditLogsByEntityTypeAndId(
            @Param("entityType") String entityType,
            @Param("entityId") String entityId
    );

    // Native SQL Query for complex scenarios
    @Query(value = "SELECT * FROM _audit_logs " +
            "WHERE entity_type = :entityType " +
            "AND entity_id = :entityId " +
            "ORDER BY createdAt DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<AuditLog> findRecentAuditLogs(
            @Param("entityType") String entityType,
            @Param("entityId") String entityId,
            @Param("limit") int limit
    );

    // Composite method with additional filtering
    @Query("SELECT al FROM AuditLog al " +
            "WHERE al.entityType = :entityType " +
            "AND al.entityId = :entityId " +
            "AND al.action = :action " +
            "ORDER BY al.createdAt DESC")
    List<AuditLog> findByEntityTypeAndIdWithAction(
            @Param("entityType") String entityType,
            @Param("entityId") String entityId,
            @Param("action") AuditAction action
    );

}
