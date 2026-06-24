package com.dentalclinic.service;

import com.dentalclinic.domain.notification.Notification;
import com.dentalclinic.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    private static final String CLINIC_NAME = "Dental Clinic";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Transactional
    public void notifyAppointmentCreated(String patientEmail, String patientName, String doctorName, Long appointmentId) {
        String subject = "Запись на приём подтверждена";
        String body = emailService.buildAppointmentCreatedTemplate(
                patientName, doctorName,
                "ближайшее свободное время",
                CLINIC_NAME
        );

        saveAndSend(patientEmail, subject, body, Notification.NotificationType.APPOINTMENT_CREATED,
                Notification.NotificationChannel.EMAIL, "Appointment", appointmentId);
    }

    @Transactional
    public void notifyAppointmentReminder(String patientEmail, String patientName, String doctorName,
                                           String dateTime, Long appointmentId) {
        String subject = "Напоминание о визите завтра";
        String body = emailService.buildAppointmentReminderTemplate(
                patientName, doctorName, dateTime, CLINIC_NAME
        );

        saveAndSend(patientEmail, subject, body, Notification.NotificationType.APPOINTMENT_REMINDER,
                Notification.NotificationChannel.EMAIL, "Appointment", appointmentId);
    }

    @Transactional
    public void notifyAppointmentCancelled(String patientEmail, String patientName, String doctorName,
                                            String dateTime, Long appointmentId) {
        String subject = "Запись отменена";
        String body = emailService.buildAppointmentCancelledTemplate(
                patientName, doctorName, dateTime, CLINIC_NAME
        );

        saveAndSend(patientEmail, subject, body, Notification.NotificationType.APPOINTMENT_CANCELLED,
                Notification.NotificationChannel.EMAIL, "Appointment", appointmentId);
    }

    @Transactional
    public void notifyPaymentReceived(String patientEmail, String patientName, String invoiceNumber,
                                       String amount, Long invoiceId) {
        String subject = "Оплата получена";
        String body = emailService.buildPaymentReceivedTemplate(
                patientName, invoiceNumber, amount, CLINIC_NAME
        );

        saveAndSend(patientEmail, subject, body, Notification.NotificationType.PAYMENT_RECEIVED,
                Notification.NotificationChannel.EMAIL, "Invoice", invoiceId);
    }

    private void saveAndSend(String recipient, String subject, String body,
                              Notification.NotificationType type, Notification.NotificationChannel channel,
                              String entityType, Long entityId) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .subject(subject)
                .body(body)
                .notificationType(type)
                .channel(channel)
                .relatedEntityType(entityType)
                .relatedEntityId(entityId)
                .sent(false)
                .build();

        notificationRepository.save(notification);

        try {
            emailService.sendEmail(recipient, subject, body);
            notification.setSent(true);
            notificationRepository.save(notification);
        } catch (Exception e) {
            notification.setErrorMessage(e.getMessage());
            notificationRepository.save(notification);
            log.error("Failed to send notification to {}: {}", recipient, e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void retryFailedNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        var failed = notificationRepository.findFailedNotifications(cutoff);
        for (Notification n : failed) {
            try {
                emailService.sendEmail(n.getRecipient(), n.getSubject(), n.getBody());
                n.setSent(true);
                n.setErrorMessage(null);
                notificationRepository.save(n);
                log.info("Retry successful for notification to {}", n.getRecipient());
            } catch (Exception e) {
                n.setErrorMessage(e.getMessage());
                notificationRepository.save(n);
            }
        }
    }
}
