
package com.app.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.entity.StudentInterest;
import com.app.repository.StudentInterestRepository;

@Repository
public class StudentInterestDAO {

    @Autowired
    private StudentInterestRepository studentInterestRepository;

    public List<StudentInterest> findByUserId(UUID userId) {
        return studentInterestRepository.findByUser_Id(userId);
    }

    public void deleteByUserId(UUID userId) {
        studentInterestRepository.deleteByUser_Id(userId);
        studentInterestRepository.flush();
    }

    public List<StudentInterest> saveAll(
            List<StudentInterest> interests) {

        return studentInterestRepository.saveAll(interests);
    }
}
