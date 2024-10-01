package com.medica.medicamanagement.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationResponse {
    private Long id;
    private String message;
    private String recipient;
    private boolean read;
    private LocalDateTime createdAt;
}
