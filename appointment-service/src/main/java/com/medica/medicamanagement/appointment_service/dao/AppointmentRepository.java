package com.medica.medicamanagement.appointment_service.dao;

import com.medica.medicamanagement.appointment_service.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
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
            "AND (a.startTime < :endTime AND a.endTime > :startTime)")
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
            "AND ((a.startTime <= :endTime) AND (a.endTime >= :startTime))")
    boolean existsByTimeRangeDoctor(@Param("doctorId") UUID doctorId, @Param("appointmentDate") Date appointmentDate, @Param("startTime") String startTime, @Param("endTime") String endTime);


}
