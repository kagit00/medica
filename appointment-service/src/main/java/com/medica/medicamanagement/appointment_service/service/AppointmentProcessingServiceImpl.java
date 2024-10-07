package com.medica.medicamanagement.appointment_service.service;

import com.medica.dto.*;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.handler.AppointmentCancellationHandler;
import com.medica.medicamanagement.appointment_service.handler.AppointmentRequestHandler;
import com.medica.medicamanagement.appointment_service.handler.DoctorApprovalHandler;
import com.medica.medicamanagement.appointment_service.handler.PaymentStatusHandler;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentProcessingServiceImpl implements AppointmentProcessingService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentRequestHandler appointmentRequestHandler;
    private final DoctorApprovalHandler doctorApprovalHandler;
    private final PaymentStatusHandler paymentStatusHandler;
    private final AppointmentCancellationHandler appointmentCancellationHandler;

    @Override
    public void handleAppointmentRequest(AppointmentRequest request) {
        this.appointmentRequestHandler.processAppointmentAndNotify(request);
    }

    @Override
    public void handleDoctorResponse(DoctorApprovalResponse approvalResponse, DoctorResponse doctorResponse) {
        if (!EnumSet.of(AppointmentStatus.CANCELED, AppointmentStatus.REJECTED, AppointmentStatus.APPROVED)
                .contains(AppointmentStatus.valueOf(approvalResponse.getStatus()))) {
            return;
        }

        UUID appointmentId = approvalResponse.getAppointmentId();
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new NoSuchElementException("Appointment Not Found.")
        );

        this.doctorApprovalHandler.updateAppointmentStatusAndNotify(doctorResponse, appointment, approvalResponse);
    }

    @Override
    public void handlePaymentStatus(String appointmentId, String status) {
        Appointment appointment = this.appointmentRepository.findById(UUID.fromString(appointmentId)).orElse(null);

        if (!Objects.isNull(appointment)) {
            this.paymentStatusHandler.updateAppointmentStatus(appointment, status);
        }
    }

    @Override
    public void cancelAppointment(String appointmentId) {
        this.appointmentCancellationHandler.cancelAppointment(appointmentId);
    }

    @Override
    public void handleRefundStatus(String appointmentId, String status) {
        this.appointmentCancellationHandler.notifyRefundStatusToPatient(appointmentId, status);
    }
}
