package com.medica.medicamanagement.user_service.dao;

import com.medica.medicamanagement.user_service.model.Role;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

// This Java code snippet defines a repository interface `RoleRepository` that extends
// `R2dbcRepository` interface. The `@Repository` annotation indicates that this interface is a Spring
// Data repository.
@Repository
public interface RoleRepository extends R2dbcRepository<Role, String> {
    
    
    /**
     * This function returns a Mono object containing a Role based on the provided name.
     * 
     * @param name The "name" parameter is a String type that represents the name of a role. The method
     * "findByName" is expected to search for a role based on the provided name and return a Mono
     * object that may contain the found role.
     * @return The method `findByName` returns a `Mono` object containing a `Role` entity with the
     * specified name.
     */
    Mono<Role> findByName(String name);
    
    
    /**
     * This function returns a Flux of Role objects based on the provided list of role names.
     * 
     * @param roleNames The `roleNames` parameter is a list of strings that contains the names of roles
     * you want to search for in the database. The method `findByNameIn` will return a Flux (reactive
     * stream) of Role objects that match the names provided in the `roleNames` list.
     * @return A Flux of Role objects that have names matching the values in the provided list of
     * roleNames.
     */
    Flux<Role> findByNameIn(List<String> roleNames);
}
