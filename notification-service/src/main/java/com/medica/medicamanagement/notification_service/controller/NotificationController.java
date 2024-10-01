package com.medica.medicamanagement.notification_service.controller;

import com.medica.medicamanagement.notification_service.dto.NotificationResponse;
import com.medica.medicamanagement.notification_service.model.Notification;
import com.medica.medicamanagement.notification_service.service.NotificationServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationServiceImplementation notificationService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        List<NotificationResponse> responses = notificationService.getAllNotifications();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping(value = "/notification/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody Notification notification) {
        NotificationResponse response = notificationService.createNotification(notification);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
