package com.medica.medicamanagement.user_service.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * The User class represents a user entity with various attributes and annotations for database mapping
 * in Java.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "users")
public class User {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String password;
    private int age;
    private String identityProviderUserId;
    private String createdAt;
    private String updatedAt;
}
