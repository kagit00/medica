package com.medica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class KeycloakCredential {

    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private String value;

    @JsonProperty("temporary")
    private boolean temporary;
}
