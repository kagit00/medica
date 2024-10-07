package com.medica.medicamanagement.payment_service.service;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.medica.dto.PaymentStatus;
import com.medica.medicamanagement.payment_service.dao.PaymentRepository;
import com.medica.medicamanagement.payment_service.model.*;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final KafkaTemplate<String, String> kafkaTemplate;
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
                .build();
    }

    public String getClientToken(String appointmentId, String amount, Model model) {
        String clientToken = gateway.clientToken().generate();

        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("amount", amount);
        model.addAttribute("clientToken", clientToken);

        return "payment-interface";
    }

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

        kafkaTemplate.send("appointment-payment-status",
                "{" + "\"appointmentId\": \"" + appointmentId + "\"," + "\"status\": \"" + payment.getStatus().name() + "\"}"
        );

        return "result";
    }


}
