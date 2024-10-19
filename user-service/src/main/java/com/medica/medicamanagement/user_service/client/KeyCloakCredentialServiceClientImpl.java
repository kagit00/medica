package com.medica.medicamanagement.user_service.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.keycloak.admin.client.Keycloak;

@Service
@Slf4j
public class KeyCloakCredentialServiceClientImpl implements KeyCloakCredentialServiceClient {

    @Autowired
    private  Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public void setUserPassword(String userId, String password, String currentPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(StringUtils.isEmpty(currentPassword));
        keycloak.realm(realm).users().get(userId).resetPassword(credential);
    }
}
