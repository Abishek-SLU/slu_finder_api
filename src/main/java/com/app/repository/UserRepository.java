package com.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.config.WriteableRepository;
import com.app.entity.User;

public interface UserRepository
        extends WriteableRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserById(@Param("id") UUID id);

    @Query("SELECT u FROM User u WHERE u.userName = ?#{principal?.phoneNo}")
    Optional<User> findLoggedInUser();

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNo(String phoneNo);

    Optional<User> findByAddress(String address);

    Optional<User> findByUserNameOrEmail(
            String userName,
            String email);

    Optional<User> findByPasswordAndUserName(
            String password,
            String userName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByUserNameAndIdNot(
            String userName,
            UUID id);
}