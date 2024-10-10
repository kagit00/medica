package com.medica.medicamanagement.gateway_service.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/api/auth/**").permitAll() // Public auth endpoints
                        .pathMatchers("/api/patients/**").hasRole("PATIENT") // Patient access
                        .pathMatchers(HttpMethod.GET, "/api/patients/patient/**").hasAnyRole("PATIENT", "APPOINTMENT_MANAGER")
                        .pathMatchers("/api/doctors/**").hasRole("DOCTOR")
                        .pathMatchers(HttpMethod.GET, "/api/doctors/doctor/**").hasAnyRole("DOCTOR", "APPOINTMENT_MANAGER")
                        .pathMatchers("/api/appointments/**").hasRole("APPOINTMENT_MANAGER")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter());
                }))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, e) -> {
                            // Handle unauthorized errors
                            return Mono.error(e);
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            // Handle access denied errors
                            return Mono.error(denied);
                        })
                );

        return http.build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return new Converter<Jwt, Mono<AbstractAuthenticationToken>>() {
            @Override
            public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
                return Mono.just(converter.convert(jwt));
            }
        };
    }

    public static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaims().get("resource_access");

            if (resourceAccess != null && resourceAccess.containsKey("medica")) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("medica");
                Collection<String> roles = (Collection<String>) clientAccess.get("roles");

                if (roles != null) {
                    return roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        }
    }
}