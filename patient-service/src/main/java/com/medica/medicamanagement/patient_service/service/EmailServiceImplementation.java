package com.medica.medicamanagement.patient_service.service;

import com.medica.medicamanagement.patient_service.models.Patient;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendEmail(Patient patient, String appointmentStatusResponse) {
        String htmlBody = "";
        String appointmentStatus = BasicUtility.readSpecificProperty(appointmentStatusResponse, "$.message.appointmentStatus");
        String subject = "";

        if (AppointmentStatus.SCHEDULED.name().equals(appointmentStatus))
            subject = "Your Appointment Confirmed";

        if (AppointmentStatus.APPROVED.name().equals(appointmentStatus))
            subject = "Action Required: Confirm Your Appointment";

        if (AppointmentStatus.REJECTED.name().equals(appointmentStatus))
            subject = "Your Appointment Rejected";

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", patient.getFirstName());
        templateModel.put("subject", subject);
        templateModel.put("doctorName", BasicUtility.readSpecificProperty(appointmentStatusResponse, "$.message.doctorName"));
        templateModel.put("specialization", BasicUtility.readSpecificProperty(appointmentStatusResponse, "$.message.specialization"));
        String appointmentDate = BasicUtility.readSpecificProperty(appointmentStatusResponse, "$.message.appointmentDate");
        templateModel.put("appointmentDate", LocalDateTime.parse(appointmentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        templateModel.put("startTime", BasicUtility.readSpecificProperty(appointmentStatusResponse, "$.message.startTime"));
        templateModel.put("endTime", BasicUtility.readSpecificProperty(appointmentStatusResponse, "$.message.endTime"));
        templateModel.put("appointmentFee", BasicUtility.readSpecificProperty(appointmentStatusResponse, "$.message.appointmentFee"));
        templateModel.put("paymentLink", BasicUtility.readSpecificProperty(appointmentStatusResponse, "$.message.paymentLink"));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            Context context = new Context();
            context.setVariables(templateModel);

            if (AppointmentStatus.SCHEDULED.name().equals(appointmentStatus))
                htmlBody = templateEngine.process("confirmed-appointment", context);

            if (AppointmentStatus.APPROVED.name().equals(appointmentStatus))
                htmlBody = templateEngine.process("payment-action-required", context);

            if (AppointmentStatus.REJECTED.name().equals(appointmentStatus))
                htmlBody = templateEngine.process("rejected-appointment", context);

            helper.setTo(patient.getEmailId());
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
