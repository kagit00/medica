package com.medica.medicamanagement.user_service.service;

import com.medica.dto.RoleReq;
import com.medica.medicamanagement.user_service.model.Role;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

// This code snippet is defining a Java interface named `RoleService`. It declares a method `getRoles`
// that takes a list of `RoleReq` objects as input and returns a `Mono` object wrapping a list of
// `Role` objects. The method signature indicates that it is asynchronous and will return the result as
// a reactive stream using Project Reactor's `Mono` type. The implementation of this interface will
// provide the logic to fetch roles based on the given list of role requests.
public interface RoleService {
    /**
     * The function `getRoles` takes a list of `RoleReq` objects and returns a Mono emitting a list of
     * `Role` objects.
     * 
     * @param roleRequests The `roleRequests` parameter is a list of `RoleReq` objects.
     * @return A `Mono` object containing a list of `Role` objects is being returned. The `Mono` class
     * is typically used in reactive programming to represent a single value or an asynchronous
     * computation that will produce a single value. In this case, the `getRoles` method takes a list
     * of `RoleReq` objects as input and returns a `Mono` object containing a list of `Role` objects
     */
    Mono<List<Role>> getRoles(List<RoleReq> roleRequests);
}
