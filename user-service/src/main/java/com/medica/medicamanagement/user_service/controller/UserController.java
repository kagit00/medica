package com.medica.medicamanagement.user_service.controller;

import com.medica.dto.UserRequest;
import com.medica.dto.UserResponse;
import com.medica.medicamanagement.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * The `UserController` class in Java defines REST endpoints for creating, updating, retrieving, and
 * deleting user information.
 */

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * This Java function creates a new user by accepting a JSON request and returning a JSON response
     * using Spring's WebFlux framework.
     * 
     * @param userRequest The `userRequest` parameter in the `createUser` method is of type
     * `UserRequest`. It is annotated with `@RequestBody` to indicate that the method parameter should
     * be bound to the body of the HTTP request. Additionally, it is annotated with `@Valid` to enable
     * validation of the
     * @return A ResponseEntity containing a Mono of UserResponse is being returned.
     */
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<UserResponse>> createUser(@RequestBody @Valid  UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    /**
     * This function updates a user with the specified userId using the provided userRequest data.
     * 
     * @param userId The `userId` parameter in the `@PutMapping` annotation represents the unique
     * identifier of the user that you want to update. It is extracted from the path variable in the
     * URL `/user/{userId}`.
     * @param userRequest The `userRequest` parameter in the `updateUser` method is of type
     * `UserRequest`. It is annotated with `@RequestBody` to indicate that the data for this parameter
     * will be obtained from the request body. Additionally, `@Valid` annotation is used for validation
     * purposes, indicating that the
     * @return A `ResponseEntity` object containing a `Mono` of `UserResponse` is being returned.
     */
    @PutMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<UserResponse>> updateUser(@PathVariable("userId") String userId, @RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userId, userRequest));
    }

    /**
     * This function retrieves a user by their ID and returns a Mono of UserResponse wrapped in a
     * ResponseEntity with JSON content type.
     * 
     * @param userId The `userId` parameter in the `getUser` method is a path variable that represents
     * the unique identifier of the user whose information is being requested.
     * @return A ResponseEntity containing a Mono of UserResponse is being returned.
     */
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<UserResponse>> getUser(@PathVariable("userId") String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    /**
     * This function deletes a user by their ID and returns a response entity indicating success or an
     * internal server error.
     * 
     * @param userId The `userId` parameter in the `@DeleteMapping` annotation represents the unique
     * identifier of the user that is being targeted for deletion. This identifier is typically passed
     * in the URL path when making a DELETE request to the specified endpoint `/user/{userId}`.
     * @return This method returns a `Mono` of `ResponseEntity<Void>`. It first calls the `deleteUser`
     * method from the `userService`, then returns an `ok` response entity if the deletion is
     * successful. If an error occurs during the deletion process, it returns a response entity with an
     * internal server error status.
     */
    @DeleteMapping(value = "/user/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable("userId") String userId) {
        return this.userService.deleteUser(userId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}
