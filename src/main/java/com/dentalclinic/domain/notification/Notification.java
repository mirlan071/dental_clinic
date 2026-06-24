package com.dentalclinic.domain.notification;

import com.dentalclinic.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notif_recipient", columnList = "recipient"),
        @Index(name = "idx_notif_type", columnList = "notification_type"),
        @Index(name = "idx_notif_sent", columnList = "sent")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Column(name = "recipient", nullable = false, length = 255)
    private String recipient;

    @Column(name = "subject", nullable = false, length = 500)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(name = "sent", nullable = false)
    private boolean sent = false;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    public enum NotificationType {
        APPOINTMENT_CREATED,
        APPOINTMENT_REMINDER,
        APPOINTMENT_CANCELLED,
        APPOINTMENT_STATUS_CHANGED,
        PAYMENT_RECEIVED,
        INVOICE_CREATED,
        GENERAL
    }

    public enum NotificationChannel {
        EMAIL,
        SMS
    }
}
