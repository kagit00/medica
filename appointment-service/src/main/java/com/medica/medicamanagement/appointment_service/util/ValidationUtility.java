package com.medica.medicamanagement.appointment_service.util;

import com.medica.dto.*;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.util.Constant;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * The type Validation utility.
 */
@Slf4j
public final class ValidationUtility {
    private ValidationUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Is valid boolean.
     *
     * @param request               the request
     * @param doctorResponse        the doctor response
     * @param patientResponse       the patient response
     * @param appointmentRepository the appointment repository
     * @return the boolean
     */
    public static boolean isValid(AppointmentRequest request, DoctorResponse doctorResponse, PatientResponse patientResponse, AppointmentRepository appointmentRepository) {
        if (Objects.isNull(doctorResponse) || Objects.isNull(patientResponse)) {
            log.error(Constant.DOCTOR_NOT_FOUND + " Patient is null");
            return false;
        }

        if (!isAppointmentInAvailabilityRange(doctorResponse.getAvailabilities(), request.getAppointmentDate(), request.getTimeRange())) {
            log.error("Dr. {} is not available within this time range", doctorResponse.getFirstName() + " " + doctorResponse.getLastName());
            return false;
        }

        boolean isTimeSlotAlreadyTakenByPatient = appointmentRepository.existsByTimeRangePatient(UUID.fromString(request.getPatientId()),
                request.getAppointmentDate(), request.getTimeRange().getStartTime(), request.getTimeRange().getEndTime());

        if (isTimeSlotAlreadyTakenByPatient) {
            log.error(Constant.TIME_ALREADY_TAKEN_BY_PATIENT);
            return false;
        }

        boolean isTimeSlotAlreadyTakenByDoctor = appointmentRepository.existsByTimeRangeDoctor(UUID.fromString(request.getDoctorId()),
                request.getAppointmentDate(), request.getTimeRange().getStartTime(), request.getTimeRange().getEndTime());

        if (isTimeSlotAlreadyTakenByDoctor) {
            log.error(Constant.getErrorMessageForInvalidTimeRange(doctorResponse.getFirstName() + " " + doctorResponse.getLastName()));
            return false;
        }
        return true;
    }

    /**
     * Is appointment in availability range boolean.
     *
     * @param availabilityList     the availability list
     * @param appointmentReqDate   the appointment req date
     * @param appointmentTimeRange the appointment time range
     * @return the boolean
     */
    public static boolean isAppointmentInAvailabilityRange(List<DoctorAvailabilityResponse> availabilityList, Date appointmentReqDate, TimeRange appointmentTimeRange) {
        LocalDate appointmentDate = appointmentReqDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int appointmentDayOfWeek = appointmentDate.get(ChronoField.DAY_OF_WEEK);

        LocalTime appointmentStartTime = LocalTime.parse(appointmentTimeRange.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime appointmentEndTime = LocalTime.parse(appointmentTimeRange.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));

        for (DoctorAvailabilityResponse availability : availabilityList) {
            if (appointmentDayOfWeek == availability.getDayOfWeek()) {
                LocalTime availabilityStartTime = LocalTime.parse(availability.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime availabilityEndTime = LocalTime.parse(availability.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));

                boolean isStartWithinRange = !appointmentStartTime.isBefore(availabilityStartTime);
                boolean isEndWithinRange = !appointmentEndTime.isAfter(availabilityEndTime);

                if (isStartWithinRange && isEndWithinRange) {
                    return true;
                }
            }
        }
        return false;
    }

}
