package com.medica.medicamanagement.appointment_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.DoctorResponse;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.appointment_service.service.AppointmentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorResponseListener {
    private final ObjectMapper om;
    private final AppointmentProcessingService appointmentProcessingService;

    /**
     * Handle doctor response.
     *
     * @param response the response
     */
    public void handleDoctorResponse(String response) {
        try {
            String[] combinedValues = response.split(" <> ");
            DoctorApprovalResponse approvalResponse = om.readValue(combinedValues[0], DoctorApprovalResponse.class);
            DoctorResponse doctorResponse = om.readValue(combinedValues[1], DoctorResponse.class);
            appointmentProcessingService.handleDoctorResponse(approvalResponse, doctorResponse);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error processing doctor response: " + e.getMessage());
        }
    }
}
