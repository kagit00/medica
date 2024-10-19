package com.medica.medicamanagement.appointment_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.medicamanagement.appointment_service.service.AppointmentProcessingService;
import com.medica.util.BasicUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentRescheduleListener {
    private final ObjectMapper om;
    private final AppointmentProcessingService appointmentProcessingService;

    /**
     * Reschedule appointment at patient request.
     *
     * @param response the response
     */
    public void rescheduleAppointmentAtPatientReq(String response) {
        List<String> combinedValues = Arrays.asList(response.split(" <> "));
        String appointmentId = !combinedValues.isEmpty() ? combinedValues.get(0) : "";
        AppointmentRescheduleRequest appointmentRescheduleRequest = BasicUtility.deserializeJson(combinedValues, 1, AppointmentRescheduleRequest.class, om);

        appointmentProcessingService.rescheduleAppointment(appointmentId, appointmentRescheduleRequest);
    }
}
