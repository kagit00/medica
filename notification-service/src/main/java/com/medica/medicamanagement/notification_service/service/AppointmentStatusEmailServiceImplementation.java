package com.medica.medicamanagement.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.notification_service.util.EmailContentResolver;
import com.medica.medicamanagement.notification_service.util.EmailSubjectResolver;
import com.medica.medicamanagement.notification_service.util.EmailTemplateModelUtility;
import com.medica.util.BasicUtility;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.*;


/**
 * The type Email service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentStatusEmailServiceImplementation implements AppointmentStatusEmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final ObjectMapper om;

    @Override
    public void sendEmailToPatient(String response) {
        sendEmail(response,
                "confirmed-appointment-patient-mail",
                "appointment-rescheduled-patient-mail",
                "payment-action-required",
                "rejected-appointment-patient-mail",
                "appointment-canceled-by-doctor-patient-mail",
                "appointment-cancelled-by-patient-patient-mail",
                true
        );
    }

    @Override
    public void sendEmailToDoctor(String response) {
        sendEmail(response,
                "confirmed-appointment-doctor-mail",
                "appointment-rescheduled-doctor-mail",
                null,
                "rejected-appointment-doctor-mail",
                "appointment-cancelled-by-doctor-doctor-mail",
                "appointment-cancelled-by-patient-doctor-mail",
                false
        );
    }

    private void sendEmail(String response, String scheduledTemplate, String rescheduledTemplate, String approvedTemplate,
                           String rejectedTemplate, String canceledByDoctorTemplate, String canceledByPatientTemplate, boolean isPatient) {
        try {
            String canceledTemplate = "";
            List<String> combinedValues = Arrays.asList(response.split(" <> "));

            DoctorResponse doctorResponse = BasicUtility.deserializeJson(combinedValues, 0, DoctorResponse.class, om);
            PatientResponse patientResponse = BasicUtility.deserializeJson(combinedValues, 1, PatientResponse.class, om);
            AppointmentResponse appointmentResponse = BasicUtility.deserializeJson(combinedValues, 2, AppointmentResponse.class, om);
            String paymentLink = combinedValues.size() > 3? combinedValues.get(3) : null;
            boolean isAppointmentCancelledByPatient = combinedValues.size() > 4 && Boolean.parseBoolean(combinedValues.get(4));

            String subject = isPatient ? EmailSubjectResolver.getSubjectForPatientMail(appointmentResponse.getStatus()) :
                    EmailSubjectResolver.getSubjectForDoctorMail(appointmentResponse.getStatus());

            Map<String, Object> templateModel = EmailTemplateModelUtility.createTemplateModelForAppointmentStatus(
                    doctorResponse, patientResponse, appointmentResponse, paymentLink, isPatient);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            Context context = new Context();
            context.setVariables(templateModel);
            canceledTemplate = isAppointmentCancelledByPatient? canceledByPatientTemplate : canceledByDoctorTemplate;

            String htmlBody = EmailContentResolver.getHtmlBodyForAppointmentStatus(templateEngine, appointmentResponse.getStatus(), scheduledTemplate, rescheduledTemplate, approvedTemplate, rejectedTemplate, canceledTemplate, context);
            helper.setTo(isPatient? patientResponse.getEmail() : doctorResponse.getEmail());

            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
