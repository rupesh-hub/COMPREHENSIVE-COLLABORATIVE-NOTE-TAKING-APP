package com.ccnta.app.audit.entity;

import com.ccnta.app.audit.enums.AuditAction;
import com.ccnta.app.shared.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_audit_logs")
public class AuditLog extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_audit_log_id_seq_generator")
    @SequenceGenerator(name = "_audit_log_id_seq_generator", sequenceName = "_audit_log_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "audit_log_id", nullable = false, updatable = false, unique = true)
    private String auditLogId;

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    private String entityId;

//    @Enumerated(EnumType.STRING)
    private String entityType;

    @Column(columnDefinition = "TEXT", length = 5000000)
    private String details;

    private String username;
    private String projectId;

}
