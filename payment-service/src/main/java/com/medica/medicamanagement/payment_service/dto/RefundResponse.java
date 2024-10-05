package com.medica.medicamanagement.payment_service.dto;

import com.medica.dto.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RefundResponse {
    private PaymentStatus status;  // SUCCESS, FAILED
    private String message;        // Additional message about the refund status
    private String refundTransactionId;  // ID for the refund transaction
}
