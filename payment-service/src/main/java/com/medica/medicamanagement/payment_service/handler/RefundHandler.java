package com.medica.medicamanagement.payment_service.handler;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.PaymentStatus;
import com.medica.medicamanagement.payment_service.dao.PaymentRepository;
import com.medica.medicamanagement.payment_service.dao.RefundRepository;
import com.medica.medicamanagement.payment_service.dao.TransactionRepository;
import com.medica.medicamanagement.payment_service.model.CustomTransaction;
import com.medica.medicamanagement.payment_service.model.Payment;
import com.medica.medicamanagement.payment_service.model.Refund;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Refund handler.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RefundHandler {
    private final PubSubTemplate pubSubTemplate;
    private final BraintreeGateway gateway;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final RefundRepository refundRepository;

    /**
     * Refund payment.
     *
     * @param payment the payment
     */
    public void refundPayment(Payment payment) {
        CustomTransaction customTransaction = this.transactionRepository.findByPayment(payment);
        Transaction transaction = gateway.transaction().find(customTransaction.getTransactionId());
        Refund refund = customTransaction.getRefund();

        if (transaction.getStatus() != Transaction.Status.SETTLED) {
            log.error("Refund Failed: Cannot refund transaction unless it is settled. Current status: {}", transaction.getStatus());

            refund = Refund.builder().refundAmount(customTransaction.getAmount()).status(PaymentStatus.FAILED)
                    .reason("Transaction not settled").customTransaction(customTransaction)
                    .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                    .build();

        } else {
            Result<Transaction> result = gateway.transaction().refund(customTransaction.getTransactionId());
            if (result.isSuccess()) {
                Transaction refundedTransaction = result.getTarget();

                refund = Refund.builder().refundAmount(customTransaction.getAmount())
                        .status(PaymentStatus.SUCCESS).refundTransactionId(refundedTransaction.getRefundedTransactionId())
                        .customTransaction(customTransaction).reason("NA").createdAt(DefaultValuesPopulator.getCurrentTimestamp())
                        .updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                        .build();

                payment.setStatus(PaymentStatus.REFUNDED);

            } else {
                log.error("Refund Failed {}", result.getMessage());
            }
        }

        customTransaction.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
        payment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
        customTransaction.setRefund(refund);
        payment.setCustomTransaction(customTransaction);
        paymentRepository.save(payment);

        pubSubTemplate.publish(
                "appointment-refund-status",
                "{" + "\"appointmentId\": \"" + payment.getAppointmentId()
                        + "\"," + "\"refundStatus\": \"" + payment.getStatus().name() + "\"}"
        );
    }

    /**
     * Process pending refunds.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processPendingRefunds() {
        int failedRefundsProcessed = 0;
        List<Refund> failedRefunds = refundRepository.findByStatus(PaymentStatus.FAILED);

        for (Refund failedRefund : failedRefunds) {
            if (failedRefundsProcessed >= 5) { // 5 transactions per night
                break;
            }
            refundPayment(failedRefund.getCustomTransaction().getPayment());
            failedRefundsProcessed++;
        }
    }
}
