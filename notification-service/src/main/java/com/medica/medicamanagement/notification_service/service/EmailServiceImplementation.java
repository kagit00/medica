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
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void sendEmailToPatient(String response) {
        sendEmail(response, "confirmed-appointment-patient-mail", "payment-action-required",
                "rejected-appointment-patient-mail", "appointment-cancelled-patient-mail", true);
    }

    @Override
    public void sendEmailToDoctor(String response) {
        sendEmail(response, "confirmed-appointment-doctor-mail", null,
                "rejected-appointment-doctor-mail", "appointment-cancelled-doctor-mail", false);
    }

    private void sendEmail(String response, String scheduledTemplate, String approvedTemplate,
                           String rejectedTemplate, String canceledTemplate, boolean isPatient) {
        try {
            String[] combinedValues = response.split(" <> ");

            DoctorResponse doctorResponse = (combinedValues.length > 0) ?
                    om.readValue(combinedValues[0], DoctorResponse.class) : DoctorResponse.builder().build();

            PatientResponse patientResponse = (combinedValues.length > 1) ?
                    om.readValue(combinedValues[1], PatientResponse.class) : PatientResponse.builder().build();

            AppointmentResponse appointmentResponse = (combinedValues.length > 2) ?
                    om.readValue(combinedValues[2], AppointmentResponse.class) : AppointmentResponse.builder().build();

            String paymentLink = (combinedValues.length > 3) ? combinedValues[3] : null; // paymentLink logic restored
            String subject = isPatient ? getSubjectForPatientMail(appointmentResponse.getStatus()) :
                    getSubjectForDoctorMail(appointmentResponse.getStatus());

            Map<String, Object> templateModel = createTemplateModel(doctorResponse, patientResponse, appointmentResponse, paymentLink, isPatient);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            Context context = new Context();
            context.setVariables(templateModel);

            String htmlBody = getHtmlBody(appointmentResponse.getStatus(), scheduledTemplate, approvedTemplate, rejectedTemplate, canceledTemplate, context);

            if (isPatient) {
                helper.setTo(patientResponse.getEmailId());
            } else {
                helper.setTo(doctorResponse.getEmail());
            }
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
            templateModel.put("doctorName", doctorResponse.getName());
            templateModel.put("specialization", doctorResponse.getSpecialization().getName());
            templateModel.put("paymentLink", paymentLink); // Ensure paymentLink is added for patients
        } else {
            templateModel.put("name", doctorResponse.getName());
            templateModel.put("patientName", patientResponse.getFirstName() + " " + patientResponse.getLastName());
        }

        templateModel.put("appointmentDate", BasicUtility.toDate(appointmentResponse.getAppointmentDate()));
        templateModel.put("startTime", appointmentResponse.getStartTime());
        templateModel.put("endTime", appointmentResponse.getEndTime());
        templateModel.put("appointmentFee", doctorResponse.getFee());

        return templateModel;
    }

    private String getHtmlBody(String status, String scheduledTemplate, String approvedTemplate,
                               String rejectedTemplate, String canceledTemplate, Context context) {
        return switch (status) {
            case "SCHEDULED" -> templateEngine.process(scheduledTemplate, context);
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
            subject = "Your Appointment Has Been Cancelled Successfully";

        return subject;
    }

    private static String getSubjectForDoctorMail(String appointmentStatus) {
        String subject = "";

        if (AppointmentStatus.SCHEDULED.name().equals(appointmentStatus))
            subject = "You Have a New Scheduled Appointment";

        if (AppointmentStatus.APPROVED.name().equals(appointmentStatus))
            subject = "Appointment Approved Successfully";

        if (AppointmentStatus.REJECTED.name().equals(appointmentStatus))
            subject = "Appointment Rejected Successfully";

        if (AppointmentStatus.CANCELED.name().equals(appointmentStatus))
            subject = "Patient Has Cancelled Appointment";

        return subject;
    }
}
