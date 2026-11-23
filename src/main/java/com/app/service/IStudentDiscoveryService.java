package com.app.service;

import java.util.List;

import com.app.dto.StudentDiscoveryDTO;

public interface IStudentDiscoveryService {

    List<StudentDiscoveryDTO> discoverStudents(
        String userName
    );
}