package com.medica.medicamanagement.user_service.dto;

import com.medica.medicamanagement.user_service.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserRoleDTO {
    private User user;
    private String roleId;
    private String role;
}
