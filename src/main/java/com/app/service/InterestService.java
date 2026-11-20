
package com.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.InterestDAO;
import com.app.dto.InterestResponseDTO;
import com.app.entity.Interest;

@Service
public class InterestService implements IInterestService {

    private final InterestDAO interestDAO;

    public InterestService(InterestDAO interestDAO) {
        this.interestDAO = interestDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterestResponseDTO> getAllInterests() {

        List<Interest> interests = interestDAO.getAllInterests();

        List<InterestResponseDTO> responses = new ArrayList<>();

        for (Interest interest : interests) {

            InterestResponseDTO dto = new InterestResponseDTO();

            dto.setId(interest.getId());
            dto.setName(interest.getName());
            dto.setCategory(interest.getCategory());

            responses.add(dto);
        }

        return responses;
    }
}
