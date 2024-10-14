package com.medica.medicamanagement.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
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

import java.util.*;


/**
 * The type Email service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {
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

            String subject = isPatient ? getSubjectForPatientMail(appointmentResponse.getStatus()) :
                    getSubjectForDoctorMail(appointmentResponse.getStatus());

            Map<String, Object> templateModel = createTemplateModel(doctorResponse, patientResponse, appointmentResponse, paymentLink, isPatient);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            Context context = new Context();
            context.setVariables(templateModel);
            canceledTemplate = isAppointmentCancelledByPatient? canceledByPatientTemplate : canceledByDoctorTemplate;

            String htmlBody = getHtmlBody(appointmentResponse.getStatus(), scheduledTemplate, rescheduledTemplate, approvedTemplate, rejectedTemplate, canceledTemplate, context);
            helper.setTo(isPatient? patientResponse.getEmail() : doctorResponse.getEmail());

            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private Map<String, Object> createTemplateModel(DoctorResponse doctorResponse, PatientResponse patientResponse, AppointmentResponse appointmentResponse, String paymentLink, boolean isPatient) {
        Map<String, Object> templateModel = new HashMap<>();
        if (isPatient) {
            templateModel.put("name", patientResponse.getFirstName());
            templateModel.put("doctorName", doctorResponse.getFirstName() + " " + doctorResponse.getLastName());
            templateModel.put("specialization", doctorResponse.getSpecialization().getName());
            templateModel.put("paymentLink", paymentLink); // Ensure paymentLink is added for patients
        } else {
            templateModel.put("name", doctorResponse.getFirstName() + " " + doctorResponse.getLastName());
            templateModel.put("patientName", patientResponse.getFirstName() + " " + patientResponse.getLastName());
        }

        templateModel.put("appointmentDate", BasicUtility.toDate(appointmentResponse.getAppointmentDate()));
        templateModel.put("startTime", appointmentResponse.getStartTime());
        templateModel.put("endTime", appointmentResponse.getEndTime());
        templateModel.put("appointmentFee", doctorResponse.getFee());

        return templateModel;
    }

    private String getHtmlBody(String status, String scheduledTemplate, String rescheduledTemplate, String approvedTemplate,
                               String rejectedTemplate, String canceledTemplate, Context context) {
        return switch (status) {
            case "SCHEDULED" -> templateEngine.process(scheduledTemplate, context);
            case "RESCHEDULED" -> templateEngine.process(rescheduledTemplate, context);
            case "APPROVED" -> approvedTemplate != null ? templateEngine.process(approvedTemplate, context) : "";
            case "REJECTED" -> templateEngine.process(rejectedTemplate, context);
            case "CANCELED" -> templateEngine.process(canceledTemplate, context);
            default -> "";
        };
    }

    private static String getSubjectForPatientMail(String appointmentStatus) {
        String subject = "";

        if (AppointmentStatus.SCHEDULED.name().equals(appointmentStatus))
            subject = "Your Appointment Confirmed";

        if (AppointmentStatus.APPROVED.name().equals(appointmentStatus))
            subject = "Action Required: Confirm Your Appointment";

        if (AppointmentStatus.REJECTED.name().equals(appointmentStatus))
            subject = "Your Appointment Rejected";

        if (AppointmentStatus.CANCELED.name().equals(appointmentStatus))
            subject = "Your Appointment Has Been Cancelled";

        return subject;
    }

    private static String getSubjectForDoctorMail(String appointmentStatus) {
        String subject = "";

        if (AppointmentStatus.SCHEDULED.name().equals(appointmentStatus))
            subject = "You Have a New Scheduled Appointment";

        if (AppointmentStatus.APPROVED.name().equals(appointmentStatus))
            subject = "Appointment Approved";

        if (AppointmentStatus.REJECTED.name().equals(appointmentStatus))
            subject = "Appointment Rejected";

        if (AppointmentStatus.CANCELED.name().equals(appointmentStatus))
            subject = "Appointment Cancelled";

        return subject;
    }
}
