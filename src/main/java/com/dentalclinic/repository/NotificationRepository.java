package com.dentalclinic.repository;

import com.dentalclinic.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findBySentFalse();

    List<Notification> findByRecipient(String recipient);

    List<Notification> findByRelatedEntityTypeAndRelatedEntityId(String entityType, Long entityId);

    @Query("SELECT n FROM Notification n WHERE n.sent = false AND n.createdAt < :cutoff")
    List<Notification> findFailedNotifications(@Param("cutoff") LocalDateTime cutoff);
}
