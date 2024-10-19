package com.medica.medicamanagement.payment_service.service;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.PaymentStatus;
import com.medica.medicamanagement.payment_service.dao.PaymentRepository;
import com.medica.medicamanagement.payment_service.model.*;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * The type Payment service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PubSubTemplate pubSubTemplate;
    private final BraintreeGateway gateway;
    private final PaymentRepository paymentRepository;

    private static Payment getPayment(String paymentMethod, BigDecimal amount, String appointmentId) {
        return Payment.builder()
                .appointmentId(UUID.fromString(appointmentId)).paymentMethod(paymentMethod).status(PaymentStatus.PENDING).amount(amount)
                .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                .build();
    }

    private static CustomTransaction getTransaction(Result<Transaction> result, Payment payment) {
        return CustomTransaction.builder().transactionId(result.getTarget().getId())
                .status(result.isSuccess()? PaymentStatus.SUCCESS : PaymentStatus.FAILED).amount(payment.getAmount())
                .currency("USD").createdAt(DefaultValuesPopulator.getCurrentTimestamp()).payment(payment)
                .updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                .build();
    }

    /**
     * Gets client token.
     *
     * @param appointmentId the appointment id
     * @param amount        the amount
     * @param model         the model
     * @return the client token
     */
    public String getClientToken(String appointmentId, String amount, Model model) {
        String clientToken = gateway.clientToken().generate();

        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("amount", amount);
        model.addAttribute("clientToken", clientToken);

        return "payment-interface";
    }

    /**
     * Process payment string.
     *
     * @param nonce         the nonce
     * @param amount        the amount
     * @param paymentMethod the payment method
     * @param appointmentId the appointment id
     * @param model         the model
     * @return the string
     */
    public String processPayment(String nonce, BigDecimal amount, String paymentMethod, String appointmentId, Model model) {
        TransactionRequest request = new TransactionRequest().amount(amount)
                .paymentMethodNonce(nonce).options().submitForSettlement(true).done();

        Result<Transaction> result = gateway.transaction().sale(request);
        Payment payment = getPayment(paymentMethod, amount, appointmentId);
        CustomTransaction customTransaction = getTransaction(result, payment);

        if (result.isSuccess()) {
            model.addAttribute("message", "Payment Successful!");
        } else {
            model.addAttribute("message", "Payment Failed: " + result.getMessage());
        }

        payment.setStatus(customTransaction.getStatus());
        payment.setCustomTransaction(customTransaction);
        paymentRepository.save(payment);

        pubSubTemplate.publish("appointment-payment-status",
                "{" + "\"appointmentId\": \"" + appointmentId + "\","
                        + "\"status\": \"" + payment.getStatus().name() + "\"}"
        );

        return "result";
    }
}
