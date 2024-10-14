package com.medica.medicamanagement.user_service.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The Role class is a Java entity representing roles with fields for id, name, and description.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "roles")
public class Role {
    @Id
    private String id;
    private String name;
    private String description;
}
