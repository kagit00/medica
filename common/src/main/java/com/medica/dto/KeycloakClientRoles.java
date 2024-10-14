package com.medica.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class KeycloakClientRoles {

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("roles")
    private List<String> roles;
}
