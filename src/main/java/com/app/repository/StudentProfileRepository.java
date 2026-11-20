
package com.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entity.StudentProfile;

@Repository
public interface StudentProfileRepository
        extends JpaRepository<StudentProfile, UUID> {

    Optional<StudentProfile> findByUser_Id(UUID userId);

    boolean existsByUser_Id(UUID userId);
}
