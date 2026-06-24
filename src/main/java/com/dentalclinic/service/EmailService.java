package com.dentalclinic.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("dental-clinic@clinic.kg");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public String buildAppointmentCreatedTemplate(String patientName, String doctorName, String dateTime, String clinicName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: #2563eb; color: white; padding: 20px; border-radius: 8px 8px 0 0;">
                    <h1 style="margin: 0; font-size: 20px;">🦷 %s</h1>
                </div>
                <div style="border: 1px solid #e2e8f0; border-top: none; padding: 20px; border-radius: 0 0 8px 8px;">
                    <h2 style="color: #1e293b; margin-top: 0;">Запись подтверждена</h2>
                    <p style="color: #475569;">Здравствуйте, <b>%s</b>!</p>
                    <p style="color: #475569;">Ваша запись на приём успешно создана:</p>
                    <div style="background: #f1f5f9; padding: 16px; border-radius: 8px; margin: 16px 0;">
                        <p style="margin: 4px 0;"><b>Врач:</b> %s</p>
                        <p style="margin: 4px 0;"><b>Дата и время:</b> %s</p>
                    </div>
                    <p style="color: #475569;">Пожалуйста, приходите за 10 минут до назначенного времени.</p>
                    <p style="color: #94a3b8; font-size: 12px; margin-top: 24px;">С уважением, %s</p>
                </div>
            </body>
            </html>
            """.formatted(clinicName, patientName, doctorName, dateTime, clinicName);
    }

    public String buildAppointmentReminderTemplate(String patientName, String doctorName, String dateTime, String clinicName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: #f59e0b; color: white; padding: 20px; border-radius: 8px 8px 0 0;">
                    <h1 style="margin: 0; font-size: 20px;">🦷 %s</h1>
                </div>
                <div style="border: 1px solid #e2e8f0; border-top: none; padding: 20px; border-radius: 0 0 8px 8px;">
                    <h2 style="color: #1e293b; margin-top: 0;">Напоминание о визите</h2>
                    <p style="color: #475569;">Здравствуйте, <b>%s</b>!</p>
                    <p style="color: #475569;">Напоминаем, что завтра у вас запланирован визит:</p>
                    <div style="background: #fffbeb; border: 1px solid #fde68a; padding: 16px; border-radius: 8px; margin: 16px 0;">
                        <p style="margin: 4px 0;"><b>Врач:</b> %s</p>
                        <p style="margin: 4px 0;"><b>Дата и время:</b> %s</p>
                    </div>
                    <p style="color: #475569;">Пожалуйста, не забудьте прийти!</p>
                    <p style="color: #94a3b8; font-size: 12px; margin-top: 24px;">С уважением, %s</p>
                </div>
            </body>
            </html>
            """.formatted(clinicName, patientName, doctorName, dateTime, clinicName);
    }

    public String buildAppointmentCancelledTemplate(String patientName, String doctorName, String dateTime, String clinicName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: #dc2626; color: white; padding: 20px; border-radius: 8px 8px 0 0;">
                    <h1 style="margin: 0; font-size: 20px;">🦷 %s</h1>
                </div>
                <div style="border: 1px solid #e2e8f0; border-top: none; padding: 20px; border-radius: 0 0 8px 8px;">
                    <h2 style="color: #1e293b; margin-top: 0;">Запись отменена</h2>
                    <p style="color: #475569;">Здравствуйте, <b>%s</b>!</p>
                    <p style="color: #475569;">Ваша запись на приём была отменена:</p>
                    <div style="background: #fef2f2; border: 1px solid #fecaca; padding: 16px; border-radius: 8px; margin: 16px 0;">
                        <p style="margin: 4px 0;"><b>Врач:</b> %s</p>
                        <p style="margin: 4px 0;"><b>Дата и время:</b> %s</p>
                    </div>
                    <p style="color: #475569;">Для повторной записи свяжитесь с нами.</p>
                    <p style="color: #94a3b8; font-size: 12px; margin-top: 24px;">С уважением, %s</p>
                </div>
            </body>
            </html>
            """.formatted(clinicName, patientName, doctorName, dateTime, clinicName);
    }

    public String buildPaymentReceivedTemplate(String patientName, String invoiceNumber, String amount, String clinicName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: #16a34a; color: white; padding: 20px; border-radius: 8px 8px 0 0;">
                    <h1 style="margin: 0; font-size: 20px;">🦷 %s</h1>
                </div>
                <div style="border: 1px solid #e2e8f0; border-top: none; padding: 20px; border-radius: 0 0 8px 8px;">
                    <h2 style="color: #1e293b; margin-top: 0;">Оплата получена</h2>
                    <p style="color: #475569;">Здравствуйте, <b>%s</b>!</p>
                    <p style="color: #475569;">Мы получили вашу оплату:</p>
                    <div style="background: #f0fdf4; border: 1px solid #bbf7d0; padding: 16px; border-radius: 8px; margin: 16px 0;">
                        <p style="margin: 4px 0;"><b>Счёт:</b> %s</p>
                        <p style="margin: 4px 0;"><b>Сумма:</b> %s сом</p>
                    </div>
                    <p style="color: #94a3b8; font-size: 12px; margin-top: 24px;">С уважением, %s</p>
                </div>
            </body>
            </html>
            """.formatted(clinicName, patientName, invoiceNumber, amount, clinicName);
    }
}
