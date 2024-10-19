package com.medica.medicamanagement.notification_service.service;

/**
 * The interface User mgmt email service.
 */
public interface UserMgmtEmailService {

    /**
     * Send email regarding password change.
     *
     * @param response the response
     */
    void sendEmailRegardingPasswordChange(String response);
}
