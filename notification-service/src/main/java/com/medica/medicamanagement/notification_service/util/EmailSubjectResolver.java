package com.medica.medicamanagement.notification_service.util;

import com.medica.model.AppointmentStatus;

/**
 * The type Email subject resolver.
 */
public final class EmailSubjectResolver {
    private EmailSubjectResolver() {
        throw new UnsupportedOperationException("Unsupported Opearation");
    }

    /**
     * Gets subject for doctor mail.
     *
     * @param appointmentStatus the appointment status
     * @return the subject for doctor mail
     */
    public static String getSubjectForDoctorMail(String appointmentStatus) {
        String subject = "";

        if (AppointmentStatus.SCHEDULED.name().equals(appointmentStatus))
            subject = "You Have a New Scheduled Appointment";

        if (AppointmentStatus.APPROVED.name().equals(appointmentStatus))
            subject = "Appointment Approved";

        if (AppointmentStatus.REJECTED.name().equals(appointmentStatus))
            subject = "Appointment Rejected";

        if (AppointmentStatus.CANCELED.name().equals(appointmentStatus))
            subject = "Appointment Cancelled";

        if (AppointmentStatus.RESCHEDULED.name().equals(appointmentStatus)) {
            subject = "One of Your Appointment Has Been Rescheduled";
        }

        return subject;
    }

    /**
     * Gets subject for patient mail.
     *
     * @param appointmentStatus the appointment status
     * @return the subject for patient mail
     */
    public static String getSubjectForPatientMail(String appointmentStatus) {
        String subject = "";

        if (AppointmentStatus.SCHEDULED.name().equals(appointmentStatus))
            subject = "Your Appointment Confirmed";

        if (AppointmentStatus.APPROVED.name().equals(appointmentStatus))
            subject = "Action Required: Confirm Your Appointment";

        if (AppointmentStatus.REJECTED.name().equals(appointmentStatus))
            subject = "Your Appointment Rejected";

        if (AppointmentStatus.CANCELED.name().equals(appointmentStatus))
            subject = "Your Appointment Has Been Cancelled";

        if (AppointmentStatus.RESCHEDULED.name().equals(appointmentStatus)) {
            subject = "Your Appointment Has Been Rescheduled at Your Request";
        }

        return subject;
    }

    /**
     * Gets subject for password change.
     *
     * @return the subject for password change
     */
    public static String getSubjectForPasswordChange() {
        return "Your Password Has Been Changed";
    }

}
