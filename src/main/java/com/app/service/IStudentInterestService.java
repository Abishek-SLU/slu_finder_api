
package com.app.service;

import java.util.List;

import com.app.dto.InterestResponseDTO;
import com.app.dto.StudentInterestRequestDTO;

public interface IStudentInterestService {

    List<InterestResponseDTO> getMyInterests(String username);

    List<InterestResponseDTO> saveMyInterests(
            String username,
            StudentInterestRequestDTO request
    );
}
