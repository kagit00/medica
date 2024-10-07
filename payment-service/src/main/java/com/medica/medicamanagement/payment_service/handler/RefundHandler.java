package com.medica.medicamanagement.payment_service.handler;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.medica.dto.PaymentStatus;
import com.medica.medicamanagement.payment_service.dao.PaymentRepository;
import com.medica.medicamanagement.payment_service.dao.TransactionRepository;
import com.medica.medicamanagement.payment_service.model.CustomTransaction;
import com.medica.medicamanagement.payment_service.model.Payment;
import com.medica.medicamanagement.payment_service.model.Refund;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefundHandler {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BraintreeGateway gateway;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;

    public void refundPayment(Payment payment) {
        CustomTransaction customTransaction = this.transactionRepository.findByPayment(payment);
        Result<Transaction> result = gateway.transaction().refund(customTransaction.getTransactionId());

        if (result.isSuccess()) {
            Transaction refundedTransaction = result.getTarget();

            Refund refund = Refund.builder()
                    .refundAmount(customTransaction.getAmount()).status(PaymentStatus.SUCCESS)
                    .refundTransactionId(refundedTransaction.getRefundedTransactionId()).customTransaction(customTransaction)
                    .reason("NA").createdAt(DefaultValuesPopulator.getCurrentTimestamp())
                    .build();

            customTransaction.setRefund(refund);
            customTransaction.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setCustomTransaction(customTransaction);
            payment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

            paymentRepository.save(payment);

            kafkaTemplate.send("appointment-refund-status",
                    "{" + "\"appointmentId\": \"" + payment.getAppointmentId() + "\"," + "\"refundStatus\": \"" + payment.getStatus().name() + "\"}"
            );
        } else {
            log.error("Refund Failed: {}", result.getMessage());
        }
    }
}
