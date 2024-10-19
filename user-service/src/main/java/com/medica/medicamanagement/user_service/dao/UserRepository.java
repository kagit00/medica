package com.medica.medicamanagement.user_service.dao;

import com.medica.medicamanagement.user_service.dto.UserRoleDTO;
import com.medica.medicamanagement.user_service.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.UUID;


@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {


    @Query("INSERT INTO users (id, first_name, last_name, username, password, email, phone, address, age, identity_provider_user_id, created_at, updated_at) " +
            "VALUES (:id, :firstName, :lastName, :username, :password, :email, :phone, :address, :age, :identityProviderUserId, :createdAt, :updatedAt) " +
            "RETURNING *")
    Mono<User> insert(@Param("id") UUID id,
                      @Param("firstName") String firstName,
                      @Param("lastName") String lastName,
                      @Param("username") String username,
                      @Param("password") String password,
                      @Param("email") String email,
                      @Param("phone") String phone,
                      @Param("address") String address,
                      @Param("age") int age,
                      @Param("identityProviderUserId") String identityProviderUserId,
                      @Param("createdAt") String createdAt,
                      @Param("updatedAt") String updatedAt);

    @Query("SELECT u.* " +
            "FROM users u " +
            "WHERE u.username = :username")
    Mono<User> findByUserName(@Param("username") String username);
}
