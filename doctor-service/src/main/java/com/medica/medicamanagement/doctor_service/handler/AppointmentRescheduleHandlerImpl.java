package com.medica.medicamanagement.doctor_service.handler;

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

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentRescheduleHandlerImpl implements AppointmentRescheduleHandler {
    private final ObjectMapper om;
    private final DoctorApprovalRepository doctorApprovalRepository;

    @Override
    public void handleRescheduleAppointmentAtPatientReq(String response) {
        try {
            AppointmentResponse appointmentResponse = om.readValue(response, AppointmentResponse.class);
            DoctorApproval doctorApproval = this.doctorApprovalRepository.findByAppointmentId(appointmentResponse.getId());

            doctorApproval.setDoctorComments("Reschedule Request Initiated");
            doctorApproval.setStatus(AppointmentStatus.RESCHEDULE_REQUESTED.name());
            doctorApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

            this.doctorApprovalRepository.save(doctorApproval);

        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
