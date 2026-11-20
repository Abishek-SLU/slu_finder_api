
package com.app.service;

import com.app.dto.StudentProfileRequestDTO;
import com.app.dto.StudentProfileResponseDTO;

public interface IStudentProfileService {

    StudentProfileResponseDTO getProfile(String username);

    StudentProfileResponseDTO saveProfile(
            String username,
            StudentProfileRequestDTO request
    );
}
