package com.medica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class KeycloakUserRequest {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("enabled")
    private boolean enabled;

    @JsonProperty("emailVerified")
    private boolean emailVerified;

    @JsonProperty("credentials")
    private List<KeycloakCredential> credentials;

    @JsonProperty("realmRoles")
    private List<String> realmRoles;

    @JsonProperty("clientRoles")
    private List<KeycloakClientRoles> clientRoles;
}
