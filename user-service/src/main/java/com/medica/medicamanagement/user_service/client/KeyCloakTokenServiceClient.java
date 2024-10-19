package com.medica.medicamanagement.user_service.client;

import reactor.core.publisher.Mono;


/**
 * The interface Key cloak token service client.
 */
public interface KeyCloakTokenServiceClient {


    /**
     * Gets admin access token.
     *
     * @return the admin access token
     */
    Mono<String> getAdminAccessToken();
}
