package com.medica.medicamanagement.notification_service.service;

public interface EmailService {
    void sendEmailToPatient(String response);
    void sendEmailToDoctor(String response);
}
