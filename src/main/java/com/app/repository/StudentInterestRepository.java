
package com.app.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entity.StudentInterest;

@Repository
public interface StudentInterestRepository
        extends JpaRepository<StudentInterest, UUID> {

    List<StudentInterest> findByUser_Id(UUID userId);

    void deleteByUser_Id(UUID userId);
}
