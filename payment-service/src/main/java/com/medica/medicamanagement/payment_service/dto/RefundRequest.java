package com.medica.medicamanagement.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RefundRequest {
    private String transactionId;  // The ID of the transaction to be refunded
    private double refundAmount;   // The amount to be refunded
    private String reason;         // Reason for the refund
}
