package com.medica.medicamanagement.notification_service.dao;

import com.medica.medicamanagement.notification_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
