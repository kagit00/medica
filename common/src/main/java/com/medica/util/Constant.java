package com.medica.util;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.DoctorResponse;

public final class Constant {
    private Constant() {
        throw new UnsupportedOperationException("");
    }
    public static final String PATIENT_NOT_FOUND = "Patient not found";
    public static final String TIME_ALREADY_TAKEN_BY_PATIENT =
            "We are not able to make your appointment request to further consideration since time slot already taken by you.";

    public static String getErrorMessageForInvalidTimeRange(String name) {
        return " We are not able to make your appointment request to further consideration, since, time slot already taken by the doctor. " + name;
    }

    public static final String DOCTOR_NOT_FOUND = "Doctor not found";

}