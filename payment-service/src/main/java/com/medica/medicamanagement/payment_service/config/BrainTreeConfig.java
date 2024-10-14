package com.medica.medicamanagement.payment_service.config;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Brain tree config.
 */
@Configuration
public class BrainTreeConfig {
    @Value("${braintree.sandbox.merchant-id}")
    private String merchantId;
    @Value("${braintree.sandbox.public-key}")
    private String publicKey;
    @Value("${braintree.sandbox.private-key}")
    private String privateKey;

    /**
     * Braintree gateway.
     *
     * @return the braintree gateway
     */
    @Bean
    public BraintreeGateway braintreeGateway() {
        return new BraintreeGateway(Environment.SANDBOX, merchantId, publicKey, privateKey);
    }
}
