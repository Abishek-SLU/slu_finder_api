
package com.app.dao;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.app.entity.StudentProfile;
import com.app.repository.StudentProfileRepository;

@Repository
public class StudentProfileDAO {

    private final StudentProfileRepository repository;

    public StudentProfileDAO(StudentProfileRepository repository) {
        this.repository = repository;
    }

    public Optional<StudentProfile> findByUserId(UUID userId) {
        return repository.findByUser_Id(userId);
    }

    public StudentProfile save(StudentProfile profile) {
        return repository.save(profile);
    }
}
