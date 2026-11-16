package com.app.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entity.OTPRegister;

@Repository
public interface OTPRegisterRepository
        extends JpaRepository<OTPRegister, Long> {

    Optional<OTPRegister> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OTPRegister o WHERE o.email = :email")
    Optional<OTPRegister> findByEmailForUpdate(
            @Param("email") String email);
}