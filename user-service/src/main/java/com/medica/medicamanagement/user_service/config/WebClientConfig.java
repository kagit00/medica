package com.medica.medicamanagement.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The `WebClientConfig` class in Java configures a WebClient bean with a specified Keycloak server URL
 * and default headers.
 */
@Configuration
public class WebClientConfig {

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    /**
     * This function creates a WebClient bean with load balancing support for interacting with a
     * Keycloak server.
     * 
     * @param webClientBuilder The `webClientBuilder` parameter is a builder class provided by Spring
     * Framework for creating instances of `WebClient`. It allows you to configure various settings
     * such as base URL, default headers, timeouts, and more before building the `WebClient` instance.
     * In the given code snippet, it is used
     * @return A `WebClient` bean with load balancing enabled is being returned.
     */
    @Bean
    @LoadBalanced
    public WebClient keycloakWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(keycloakServerUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
