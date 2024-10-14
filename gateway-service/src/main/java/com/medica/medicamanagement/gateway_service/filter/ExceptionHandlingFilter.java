package com.medica.medicamanagement.gateway_service.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * The type Exception handling filter.
 */
@Component
public class ExceptionHandlingFilter extends AbstractGatewayFilterFactory<ExceptionHandlingFilter.Config> {

    /**
     * Instantiates a new Exception handling filter.
     */
    public ExceptionHandlingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange)
                .onErrorResume(throwable -> {
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

                    // Create custom error response
                    String errorResponse = "{ \"error\": \"Internal Server Error\", \"message\": \"" + throwable.getMessage() + "\" }";
                    DataBuffer dataBuffer = response.bufferFactory().wrap(errorResponse.getBytes());

                    // Return custom error response
                    return response.writeWith(Mono.just(dataBuffer));
                });
    }


    /**
     * The type Config.
     */
    public static class Config {

    }
}