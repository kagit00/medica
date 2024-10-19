package com.medica.medicamanagement.user_service.client;

/**
 * The interface Key cloak credential service client.
 */
public interface KeyCloakCredentialServiceClient {
    /**
     * Sets user password.
     *
     * @param userId          the user id
     * @param password        the password
     * @param currentPassword the current password
     */
    void setUserPassword(String userId, String password, String currentPassword);
}
