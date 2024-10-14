package com.medica.medicamanagement.user_service.dao;

import com.medica.medicamanagement.user_service.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.UUID;

// This code snippet is defining a Spring Data R2DBC repository interface called `UserRepository` that
// extends `R2dbcRepository` for the `User` entity with a primary key of type `UUID`.
@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {

    /**
     * This function inserts a new user record into a database table and returns the inserted user
     * object using Spring Data R2DBC.
     * 
     * @param id The `id` parameter is of type UUID and represents the unique identifier for the user
     * being inserted into the database.
     * @param firstName The `firstName` parameter is used to specify the first name of the user being
     * inserted into the `users` table. It should be a string value representing the user's first name.
     * @param lastName The `lastName` parameter in the provided code snippet corresponds to the last
     * name of a user. When inserting a new user into the `users` table, the `lastName` field is
     * expected to be provided as part of the user's information. This field typically represents the
     * family name or surname of the
     * @param username The `username` parameter in the provided code snippet is used to specify the
     * username of the user being inserted into the `users` table. It is a required field for creating
     * a new user record in the database.
     * @param email Email address of the user
     * @param phone The `phone` parameter in the provided query represents the phone number of the user
     * being inserted into the `users` table. It is a string type parameter where you would pass the
     * phone number of the user as a string value when calling the `insert` method.
     * @param address The `address` parameter in the provided code snippet refers to the address of the
     * user being inserted into the `users` table. This parameter should contain the user's physical
     * address details such as street address, city, state, and postal code. When calling the `insert`
     * method, you should provide
     * @param age The `age` parameter in the provided code snippet represents the age of a user being
     * inserted into a database table named `users`. It is an integer value that corresponds to the age
     * of the user.
     * @param identityProviderUserId The `identityProviderUserId` parameter in the provided code
     * snippet represents the unique identifier associated with the user in the identity provider
     * system. This identifier is used to link the user in your application with their corresponding
     * user in the external identity provider system. It helps in managing authentication and
     * authorization processes for the user.
     * @param createdAt The `createdAt` parameter in the provided code snippet is used to specify the
     * timestamp or date when the user record is created in the database. It is a string parameter that
     * represents the creation date and time of the user entry. This parameter is passed as an argument
     * to the `insert` method along with
     * @param updatedAt The `updatedAt` parameter in the provided code snippet is used to specify the
     * timestamp when the user record was last updated. This parameter is expected to be a string
     * representing the date and time of the update. It is used in the SQL query to set the
     * `updated_at` column value in the `
     * @return The query is returning all columns of the newly inserted user record from the "users"
     * table.
     */
    @Query("INSERT INTO users (id, first_name, last_name, username, email, phone, address, age, identity_provider_user_id, created_at, updated_at) " +
            "VALUES (:id, :firstName, :lastName, :username, :email, :phone, :address, :age, :identityProviderUserId, :createdAt, :updatedAt) " +
            "RETURNING *")
    Mono<User> insert(@Param("id") UUID id,
                      @Param("firstName") String firstName,
                      @Param("lastName") String lastName,
                      @Param("username") String username,
                      @Param("email") String email,
                      @Param("phone") String phone,
                      @Param("address") String address,
                      @Param("age") int age,
                      @Param("identityProviderUserId") String identityProviderUserId,
                      @Param("createdAt") String createdAt,
                      @Param("updatedAt") String updatedAt);
}
