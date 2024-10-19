package com.medica.medicamanagement.user_service.util;

import com.medica.dto.NotificationResponse;
import com.medica.dto.RoleReq;
import com.medica.dto.UserResponse;
import com.medica.medicamanagement.user_service.model.Role;
import com.medica.medicamanagement.user_service.model.User;
import com.medica.util.DefaultValuesPopulator;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The type Response maker utility.
 */
public final class ResponseMakerUtility {

    private ResponseMakerUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Gets user create response.
     *
     * @param user  the user
     * @param roles the roles
     * @return the user create response
     */
    public static Mono<UserResponse> getUserCreateResponse(User user, List<Role> roles) {
        List<RoleReq> roleReqs = new ArrayList<>();
        for (Role r : roles) {
            roleReqs.add(RoleReq.builder().name(r.getName()).build());
        }

        UserResponse response = UserResponse.builder()
                .id(user.getId()).age(user.getAge()).email(user.getEmail()).roles(roleReqs).phone(user.getPhone())
                .firstName(user.getFirstName()).lastName(user.getLastName()).username(user.getUsername()).address(user.getAddress())
                .password(user.getPassword())
                .build();

        return Mono.just(response);
    }

    public static Mono<NotificationResponse> getNotificationResponse(String message, String status) {
        return Mono.just(NotificationResponse.builder()
                .uid(DefaultValuesPopulator.getUid()).message(message)
                .timestamp(DefaultValuesPopulator.getCurrentTimestamp()).status(status)
                .build());
    }
}
