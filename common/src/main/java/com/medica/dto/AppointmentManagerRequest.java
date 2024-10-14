package com.medica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentManagerRequest {
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username can only contain alphanumeric characters")
    private String username;

    @NotBlank(message = "First name cannot be blank")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'’\\- ]+$", message = "Invalid First Name")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'’\\- ]+$", message = "Invalid Last Name")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid Email format")
    private String emailId;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9. ()-]{10,13}$", message = "Invalid phone number")
    private String phone;

    private int age;

    @NotBlank(message = "Address cannot be blank")
    private String address;
}
