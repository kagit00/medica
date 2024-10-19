package com.medica.medicamanagement.doctor_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.AppointmentResponse;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.doctor_service.dao.DoctorApprovalRepository;
import com.medica.medicamanagement.doctor_service.model.DoctorApproval;
import com.medica.model.AppointmentStatus;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentResponseListener {
    private final ObjectMapper om;
    private final DoctorApprovalRepository doctorApprovalRepository;

    public void respondToAppointmentRequest(String response) {
        try {
            AppointmentResponse appointmentResponse = om.readValue(response, AppointmentResponse.class);
            DoctorApproval existingApproval = doctorApprovalRepository.findByAppointmentId(appointmentResponse.getId());

            if (Objects.isNull(existingApproval)) {
                DoctorApproval approval = DoctorApproval.builder()
                        .doctorId(appointmentResponse.getDoctorId()).appointmentId(appointmentResponse.getId()).doctorComments("NA")
                        .status(AppointmentStatus.PENDING.name()).createdAt(DefaultValuesPopulator.getCurrentTimestamp())
                        .updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                        .build();

                doctorApprovalRepository.save(approval);
            } else {
                existingApproval.setStatus(appointmentResponse.getStatus());
                existingApproval.setDoctorComments(appointmentResponse.getStatus());
                existingApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
                doctorApprovalRepository.save(existingApproval);
            }
        } catch (Exception e) {
            throw new InternalServerErrorException("There is an issue with receiving appointment request. Reason: " + e.getMessage());
        }
    }
}
