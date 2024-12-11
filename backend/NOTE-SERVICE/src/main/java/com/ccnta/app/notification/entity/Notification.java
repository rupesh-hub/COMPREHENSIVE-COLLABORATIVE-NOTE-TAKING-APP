package com.ccnta.app.notification.entity;

import com.ccnta.app.notification.enums.NotificationTypes;
import com.ccnta.app.shared.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_notifications")
@ToString
public class Notification extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_notification_id_seq_generator")
    @SequenceGenerator(name = "_notification_id_seq_generator", sequenceName = "_notification_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "notification_id", nullable = false, updatable = false, unique = true)
    private String notificationId;

    private NotificationTypes type;

    private String title;
    private String message;
    private String recipient;

}
