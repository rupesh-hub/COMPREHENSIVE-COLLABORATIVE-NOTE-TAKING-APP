package com.ccnta.app.audit.event;

import com.ccnta.app.audit.enums.AuditAction;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class AuditEvent<T> extends ApplicationEvent {

    private final T entity;
    private final AuditAction action;
    private final String username;
    private final T oldState;
    private final String message;
    private final Map<String, Object> additionalMetadata;

    public AuditEvent(Object source, T entity, AuditAction action, String username, T oldState, String message) {
        super(source);
        this.entity = entity;
        this.action = action;
        this.username = username;
        this.oldState = oldState;
        this.message = message;
        this.additionalMetadata = new HashMap<>();
    }

}
