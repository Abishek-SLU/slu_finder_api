package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.StudentDiscoveryDAO;
import com.app.dto.StudentDiscoveryDTO;
import com.app.entity.User;
import com.app.repository.UserRepository;

@Service
public class StudentDiscoveryService
        implements IStudentDiscoveryService {

    @Autowired
    private StudentDiscoveryDAO discoveryDAO;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<StudentDiscoveryDTO> discoverStudents(
            String userName) {

        User user = userRepository
            .findByUserName(userName)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Student not found"
                )
            );

        return discoveryDAO.findStudents(
            user.getId().toString()
        );
    }
}