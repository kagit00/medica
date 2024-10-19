package com.medica.medicamanagement.user_service.controller;

import com.medica.dto.NotificationResponse;
import com.medica.dto.UserPasswordRequest;
import com.medica.dto.UserRequest;
import com.medica.dto.UserResponse;
import com.medica.medicamanagement.user_service.service.UserService;
import com.medica.medicamanagement.user_service.util.ResponseMakerUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.UUID;


/**
 * The type User controller.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    /**
     * Create user response entity.
     *
     * @param userRequest the user request
     * @return the response entity
     */
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<UserResponse>> createUser(@RequestBody @Valid  UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }


    /**
     * Update user response entity.
     *
     * @param userId      the user id
     * @param userRequest the user request
     * @return the response entity
     */
    @PutMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<UserResponse>> updateUser(@PathVariable("userId") String userId, @RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userId, userRequest));
    }


    /**
     * Gets user.
     *
     * @param userId the user id
     * @return the user
     */
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<UserResponse>> getUser(@PathVariable("userId") String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }


    /**
     * Delete user mono.
     *
     * @param userId the user id
     * @return the mono
     */
    @DeleteMapping(value = "/user/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable("userId") String userId) {
        return this.userService.deleteUser(userId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    /**
     * Change password mono.
     *
     * @param username            the username
     * @param userPasswordRequest the user password request
     * @return the mono
     */
    @PutMapping(value = "/user/{username}/password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<NotificationResponse>> changePassword(@PathVariable("username") String username, @RequestBody @Valid UserPasswordRequest userPasswordRequest) {
        return userService.changePassword(username, userPasswordRequest)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    NotificationResponse notificationResponse =
                            ResponseMakerUtility.getNotificationResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.name()).block();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(notificationResponse));
                });
    }
}
