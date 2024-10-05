package com.medica.medicamanagement.payment_service.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentMethod {
    @NotNull(message = "Payment type cannot be null")
    @NotEmpty(message = "Payment type cannot be empty")
    private PaymentType type;
}