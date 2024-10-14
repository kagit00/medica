package com.medica.medicamanagement.user_service.service;

import com.medica.dto.RoleReq;
import com.medica.exception.BadRequestException;
import com.medica.medicamanagement.user_service.dao.RoleRepository;
import com.medica.medicamanagement.user_service.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The RoleServiceImpl class in Java implements RoleService and provides a method to retrieve a list of
 * roles based on role requests.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;


    /**
     * This function retrieves a list of roles based on the provided role requests, handling errors for
     * invalid roles.
     * 
     * @param roleRequests The `roleRequests` parameter is a list of `RoleReq` objects, which are used
     * to request roles. The `getRoles` method takes this list of role requests, processes each request
     * by finding the corresponding role in the `roleRepository` based on the role name, and returns a
     * `
     * @return A `Mono` of a `List` of `Role` objects is being returned.
     */
    @Override
    public Mono<List<Role>> getRoles(List<RoleReq> roleRequests) {
        return Flux.fromIterable(roleRequests)
                .flatMap(roleReq -> roleRepository.findByName(roleReq.getName())
                        .switchIfEmpty(Mono.error(new BadRequestException("Invalid Role: " + roleReq.getName()))))
                .collectList();
    }
}
