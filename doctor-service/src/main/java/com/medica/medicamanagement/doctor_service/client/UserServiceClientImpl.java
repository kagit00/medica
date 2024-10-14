package com.medica.medicamanagement.doctor_service.client;

import com.medica.dto.UserRequest;
import com.medica.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class UserServiceClientImpl implements UserServiceClient {
    private final WebClient webClient;
    private final Environment environment;

    public UserServiceClientImpl(WebClient.Builder webClientBuilder, Environment environment) {
        this.webClient = webClientBuilder.build();
        this.environment = environment;
    }

    @Override
    public Mono<UserResponse> createUser(UserRequest userRequest) {
        try {
            String usersServerDomain = environment.getProperty("users.server.domain");
            return webClient.post()
                    .uri(usersServerDomain + "/api/users/")
                    .bodyValue(userRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Client Error on User Creation: {} - {}", clientResponse.statusCode(), errorBody);
                                        return Mono.error(new RuntimeException("Client error: " + errorBody));
                                    }))
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("500 Server Error on User Creation: {} - {}", clientResponse.statusCode(), errorBody);
                                        return Mono.error(new RuntimeException("Server error: " + errorBody));
                                    }))
                    .bodyToMono(UserResponse.class)
                    .subscribeOn(Schedulers.boundedElastic());

        } catch (Exception e) {
            log.error("Error occurred while sending request: {}", e.getMessage(), e);
            return Mono.empty();
        }
    }

    @Override
    public Mono<UserResponse> updateUser(String userId, UserRequest userRequest) {
        String usersServerDomain = environment.getProperty("users.server.domain");
        return webClient.put()
                .uri(usersServerDomain + "/api/users/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Client Error on User Update: {} - {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Client error: " + errorBody));
                                }))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Server Error on User Update: {} - {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Server error: " + errorBody));
                                }))
                .bodyToMono(UserResponse.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UserResponse> getUser(String userId) {
        String usersServerDomain = environment.getProperty("users.server.domain");
        return webClient.get()
                .uri(usersServerDomain + "/api/users/user/{userId}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Client Error on User Retrieval: {} - {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Client error: " + errorBody));
                                }))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Server Error on User Retrieval: {} - {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Server error: " + errorBody));
                                }))
                .bodyToMono(UserResponse.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteUser(String userId) {
        String usersServerDomain = environment.getProperty("users.server.domain");
        return webClient.delete()
                .uri(usersServerDomain + "/api/users/user/{userId}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Client Error on Deletion of User: {} - {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Client error: " + errorBody));
                                }))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Server Error on Deletion of User: {} - {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Server error: " + errorBody));
                                }))
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

}
