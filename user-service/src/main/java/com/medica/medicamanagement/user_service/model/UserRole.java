package com.medica.medicamanagement.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * The UserRole class represents a user role entity with fields for ID, user ID, role ID, creation
 * timestamp, and update timestamp.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table("user_roles")
public class UserRole {
    @Id
    private UUID id;
    private UUID userId;
    private String roleId;
    private String createdAt;
    private String updatedAt;
}
