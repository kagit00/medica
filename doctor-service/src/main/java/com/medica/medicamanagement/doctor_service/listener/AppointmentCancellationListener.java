package com.medica.medicamanagement.doctor_service.listener;

import com.medica.exception.BadRequestException;
import com.medica.medicamanagement.doctor_service.dao.DoctorApprovalRepository;
import com.medica.medicamanagement.doctor_service.model.DoctorApproval;
import com.medica.model.AppointmentStatus;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentCancellationListener {
    private final DoctorApprovalRepository doctorApprovalRepository;

    public void cancelAppointmentOnAppointmentSettersRequest(String appointmentId) {
        DoctorApproval doctorApproval = doctorApprovalRepository.findByAppointmentId(UUID.fromString(appointmentId));
        if (doctorApproval != null && !AppointmentStatus.REJECTED.name().equals(doctorApproval.getStatus())) {
            doctorApproval.setStatus(AppointmentStatus.CANCELED.name());
            doctorApproval.setDoctorComments("Appointment Cancelled By Patient");
            doctorApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
            doctorApprovalRepository.save(doctorApproval);
        } else {
            throw new BadRequestException("Appointment is in Rejected status. Skipping cancellation");
        }
    }
}
