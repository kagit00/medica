package com.medica.medicamanagement.appointment_service.service;

import com.medica.dto.*;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.handler.*;
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
    private final AppointmentScheduleHandler appointmentScheduleHandler;
    private final DoctorApprovalHandler doctorApprovalHandler;
    private final PaymentStatusHandler paymentStatusHandler;
    private final AppointmentCancellationHandler appointmentCancellationHandler;
    private final AppointmentRescheduleHandler appointmentRescheduleHandler;

    @Override
    public void handleAppointmentScheduleRequest(AppointmentRequest request) {
        this.appointmentScheduleHandler.processAppointmentAndNotify(request);
    }

    @Override
    public void handleDoctorResponse(DoctorApprovalResponse approvalResponse, DoctorResponse doctorResponse) {
        if (!EnumSet.of(AppointmentStatus.REJECTED, AppointmentStatus.APPROVED, AppointmentStatus.RESCHEDULED)
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
    public void cancelAppointment(String appointmentId, boolean isCanceledByPatient) {
        this.appointmentCancellationHandler.cancelAppointment(appointmentId, isCanceledByPatient);
    }

    @Override
    public void handleRefundStatus(String appointmentId, String status) {
        this.appointmentCancellationHandler.notifyRefundStatusToPatient(appointmentId, status);
    }

    @Override
    public void rescheduleAppointment(String appointmentId, AppointmentRescheduleRequest appointmentRescheduleRequest) {
        this.appointmentRescheduleHandler.rescheduleAppointment(appointmentId, appointmentRescheduleRequest);
    }
}
