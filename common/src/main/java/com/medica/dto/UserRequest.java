package com.medica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserRequest {
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "First Name must contain only letters and spaces, and be between 2 and 50 characters")
    private String firstName;
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "Last Name must contain only letters and spaces, and be between 2 and 50 characters")
    private String lastName;
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username can only contain alphanumeric characters")
    private String username;
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be between 10 and 15 digits, optionally with a '+' sign for international format")
    private String phone;
    private String address;
    private int age;
    private List<RoleReq> roles = new ArrayList<>();
}
