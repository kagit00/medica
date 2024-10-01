package com.medica.medicamanagement.notification_service.service;

import com.medica.medicamanagement.notification_service.dao.NotificationRepository;
import com.medica.medicamanagement.notification_service.dto.NotificationResponse;
import com.medica.medicamanagement.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class NotificationServiceImplementation implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationResponse> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream().map(this::mapToResponse).toList();
    }

    @Override
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("No Notification Found By Id: " + id)
        );

        return mapToResponse(notification);
    }

    @Override
    public NotificationResponse createNotification(Notification notification) {
        this.notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .read(notification.isRead()).recipient(notification.getRecipient())
                .message(notification.getMessage())
                .build();
    }
}
