package com.medica.medicamanagement.patient_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'’\\- ]+$", message = "Invalid First Name")
    private String firstName;
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'’\\- ]+$", message = "Invalid Last Name")
    private String lastName;
    @Column(nullable = false)
    @Pattern(regexp = "^[\\w.-]+@[\\w-]+(?:\\.[\\w-]+)*$", message = "Invalid Email")
    private String emailId;
    @Column(nullable = false)
    @Pattern(regexp = "^\\+?[0-9. ()-]{10,13}$", message = "Invalid phone number")
    private String phone;
    private Date dob;
    @Column(nullable = false)
    @NotEmpty
    @NotNull
    private String address;
    @Column(nullable = false)
    @NotEmpty
    @NotNull
    private String medicalHistory;
}
