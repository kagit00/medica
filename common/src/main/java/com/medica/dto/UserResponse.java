package com.medica.dto;

import lombok.*;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private String address;
    private int age;
    private List<RoleReq> roles = new ArrayList<>();
}
