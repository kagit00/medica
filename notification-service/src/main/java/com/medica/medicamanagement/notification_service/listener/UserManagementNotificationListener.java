package com.medica.medicamanagement.notification_service.listener;

import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.notification_service.service.UserMgmtEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The type User management notification listener.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementNotificationListener {
    private final UserMgmtEmailService userMgmtEmailService;

    /**
     * Email regarding password change.
     *
     * @param response the response
     */
    public void emailRegardingPasswordChange(String response) {
        try {
            userMgmtEmailService.sendEmailRegardingPasswordChange(response);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
