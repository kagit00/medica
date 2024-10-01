package com.medica.medicamanagement.notification_service.service;

import com.medica.medicamanagement.notification_service.dto.NotificationResponse;
import com.medica.medicamanagement.notification_service.model.Notification;
import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getAllNotifications();
    NotificationResponse getNotificationById(Long id);
    NotificationResponse createNotification(Notification notification);
}
