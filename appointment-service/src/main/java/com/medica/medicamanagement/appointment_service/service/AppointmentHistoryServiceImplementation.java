package com.medica.medicamanagement.appointment_service.service;

import com.medica.medicamanagement.appointment_service.dao.AppointmentHistoryRepository;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.medicamanagement.appointment_service.model.AppointmentHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentHistoryServiceImplementation implements AppointmentHistoryService {
    private final AppointmentHistoryRepository appointmentHistoryRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<AppointmentHistory> getHistoryByAppointmentId(UUID appointmentId) {
        return appointmentHistoryRepository.findAll();
    }

    @Override
    public AppointmentHistory createHistory(UUID appointmentId, AppointmentHistory history) {
        Appointment appointment = this.appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new NoSuchElementException("No Appointment Found With Id: " + appointmentId)
        );

        history.setAppointment(appointment);
        appointmentHistoryRepository.save(history);
        return history;
    }
}
