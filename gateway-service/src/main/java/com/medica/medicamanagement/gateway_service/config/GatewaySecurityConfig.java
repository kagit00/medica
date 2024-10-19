package com.medica.medicamanagement.gateway_service.config;

import com.medica.medicamanagement.gateway_service.util.Constant;
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

/**
 * The type Gateway security config.
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    /**
     * Security filter chain security web filter chain.
     *
     * @param http the http
     * @return the security web filter chain
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                                .pathMatchers(HttpMethod.POST, "/api/users/", "/api/patients/", "/api/doctors/")
                                .permitAll()

                                .pathMatchers("/api/users/user/**")
                                .hasAnyRole(Constant.DOCTOR, Constant.PATIENT, Constant.APPOINTMENT_MANAGER)

                                .pathMatchers("/api/patients/patient/**", "/api/patients/appointments/**")
                                .hasRole(Constant.PATIENT)

                                .pathMatchers(HttpMethod.GET, "/api/patients/patient/**")
                                .hasAnyRole(Constant.APPOINTMENT_MANAGER, Constant.PATIENT)

                                .pathMatchers("/api/doctors/doctor/**", "/api/doctors/appointments/**")
                                .hasRole(Constant.DOCTOR)

                                .pathMatchers(HttpMethod.GET, "/api/doctors/doctor/**")
                                .hasAnyRole(Constant.APPOINTMENT_MANAGER, Constant.DOCTOR)

                                .pathMatchers("/api/appointments/patient/**")
                                .hasAnyRole(Constant.PATIENT, Constant.APPOINTMENT_MANAGER)

                                .pathMatchers("/api/appointments/doctor/**")
                                .hasAnyRole(Constant.DOCTOR, Constant.APPOINTMENT_MANAGER)

                                .pathMatchers("/payment/**")
                                .permitAll()

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

    /**
     * Jwt authentication converter converter.
     *
     * @return the converter
     */
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

    /**
     * The type Keycloak role converter.
     */
    public static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            // First, check resource_access for roles
            Map<String, Object> resourceAccess = safeCastToMap(jwt.getClaims().get("resource_access"));
            if (resourceAccess != null && resourceAccess.containsKey("medica")) {
                Map<String, Object> clientAccess = safeCastToMap(resourceAccess.get("medica"));
                Collection<String> roles = safeCastToCollection(clientAccess.get("roles"));

                if (roles != null) {
                    return roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                }
            }

            // If no roles in resource_access, fall back to realm_access
            Map<String, Object> realmAccess = safeCastToMap(jwt.getClaims().get("realm_access"));
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                Collection<String> roles = safeCastToCollection(realmAccess.get("roles"));
                return roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        }
    }

    /**
     * Safe cast to map.
     *
     * @param obj the obj
     * @return the map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> safeCastToMap(Object obj) {
        if (obj instanceof Map<?, ?>) {
            return (Map<String, Object>) obj;
        }
        return null;
    }

    /**
     * Safe cast to collection collection.
     *
     * @param obj the obj
     * @return the collection
     */
    @SuppressWarnings("unchecked")
    public static Collection<String> safeCastToCollection(Object obj) {
        if (obj instanceof Collection<?>) {
            return (Collection<String>) obj;
        }
        return null;
    }

}