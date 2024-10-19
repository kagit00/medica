package com.medica.medicamanagement.payment_service.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.medicamanagement.payment_service.listener.RefundProcessListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class Subscribers {
    private final PubSubTemplate pubSubTemplate;
    private final RefundProcessListener refundProcessListener;

    @PostConstruct
    public void initializeSubscribers() {
        System.out.println("Initializing Pub/Sub subscribers...");

        // Subscribe to the process refund to patient topic
        pubSubTemplate.subscribe("process-refund-to-patient-subscription", message -> {
            String response = message.getPubsubMessage().getData().toStringUtf8();
            refundProcessListener.handleRefundProcess(response);
            message.ack();
        });
    }
}