package com.medica.medicamanagement.appointment_service.dao;

import com.medica.medicamanagement.appointment_service.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The interface Appointment repository.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    /**
     * Find by status list.
     *
     * @param status the status
     * @return the list
     */
    List<Appointment> findByStatus(String status);

    /**
     * Exists by time range patient boolean.
     *
     * @param patientId       the patient id
     * @param appointmentDate the appointment date
     * @param startTime       the start time
     * @param endTime         the end time
     * @return the boolean
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Appointment a " +
            "WHERE a.appointmentDate = :appointmentDate " +
            "AND a.patientId = :patientId " +
            "AND (a.startTime < :endTime AND a.endTime > :startTime) " +
            "AND a.status NOT IN ('REJECTED', 'CANCELED')")
    boolean existsByTimeRangePatient(@Param("patientId") UUID patientId, @Param("appointmentDate") Date appointmentDate, @Param("startTime") String startTime, @Param("endTime") String endTime);


    /**
     * Exists by time range doctor boolean.
     *
     * @param doctorId        the doctor id
     * @param appointmentDate the appointment date
     * @param startTime       the start time
     * @param endTime         the end time
     * @return the boolean
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Appointment a " +
            "WHERE a.appointmentDate = :appointmentDate " +
            "AND a.doctorId = :doctorId " +
            "AND ((a.startTime <= :endTime) AND (a.endTime >= :startTime)) " +
            "AND a.status NOT IN ('REJECTED', 'CANCELED')")
    boolean existsByTimeRangeDoctor(@Param("doctorId") UUID doctorId, @Param("appointmentDate") Date appointmentDate, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * Find by patient id and date and time range optional.
     *
     * @param patientId the patient id
     * @param date      the date
     * @param startTime the start time
     * @param endTime   the end time
     * @return the optional
     */
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND cast(a.appointmentDate as date) = cast(:date as date) AND a.startTime = :startTime AND a.endTime = :endTime AND a.status NOT IN ('REJECTED', 'CANCELED')")
    Optional<Appointment> findByPatientIdAndDateAndTimeRange(UUID patientId, String date, String startTime, String endTime);

    /**
     * Find by patient id optional.
     *
     * @param patientId the patient id
     * @return the optional
     */
    Optional<List<Appointment>> findByPatientId(UUID patientId);

    /**
     * Find by doctor id optional.
     *
     * @param doctorId the doctor id
     * @return the optional
     */
    Optional<List<Appointment>> findByDoctorId(UUID doctorId);

    /**
     * Find by doctor id and date and time range optional.
     *
     * @param doctorId  the doctor id
     * @param date      the date
     * @param startTime the start time
     * @param endTime   the end time
     * @return the optional
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND cast(a.appointmentDate as date) = cast(:date as date) AND a.startTime = :startTime AND a.endTime = :endTime AND a.status NOT IN ('REJECTED', 'CANCELED')")
    Optional<Appointment> findByDoctorIdAndDateAndTimeRange(UUID doctorId, String date, String startTime, String endTime);
}
