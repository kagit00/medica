package com.medica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserPasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmedPassword;
}
