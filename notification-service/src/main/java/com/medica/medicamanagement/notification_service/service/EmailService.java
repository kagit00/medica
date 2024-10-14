package com.medica.medicamanagement.notification_service.service;

/**
 * The interface Email service.
 */
public interface EmailService {
    /**
     * Send email to patient.
     *
     * @param response the response
     */
    void sendEmailToPatient(String response);

    /**
     * Send email to doctor.
     *
     * @param response the response
     */
    void sendEmailToDoctor(String response);
}
