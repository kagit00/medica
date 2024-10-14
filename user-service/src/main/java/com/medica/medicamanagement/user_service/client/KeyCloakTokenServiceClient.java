package com.medica.medicamanagement.user_service.client;

import reactor.core.publisher.Mono;

// This code snippet is defining a Java interface named `KeyCloakTokenServiceClient`. This interface
// declares a method `getAdminAccessToken()` that returns a `Mono<String>`. The `Mono` class is part of
// Project Reactor, which is a reactive programming library for building non-blocking applications in
// Java. In this context, the `getAdminAccessToken()` method is likely intended to asynchronously
// retrieve an admin access token from a Keycloak token service client.
public interface KeyCloakTokenServiceClient {
    
    /**
     * The function `getAdminAccessToken` returns a Mono containing a String representing an admin
     * access token.
     * 
     * @return A `Mono` object that contains a `String` representing the admin access token.
     */
    Mono<String> getAdminAccessToken();
}
