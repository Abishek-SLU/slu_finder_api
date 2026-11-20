
package com.app.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.InterestDAO;
import com.app.dao.StudentInterestDAO;
import com.app.dto.InterestResponseDTO;
import com.app.dto.StudentInterestRequestDTO;
import com.app.entity.Interest;
import com.app.entity.StudentInterest;
import com.app.entity.User;
import com.app.repository.UserRepository;

@Service
public class StudentInterestService
        implements IStudentInterestService {

    @Autowired
    private StudentInterestDAO studentInterestDAO;

    @Autowired
    private InterestDAO interestDAO;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InterestResponseDTO> getMyInterests(
            String username) {

        User user = getUser(username);

        List<StudentInterest> selected =
                studentInterestDAO.findByUserId(user.getId());

        List<InterestResponseDTO> response = new ArrayList<>();

        for (StudentInterest item : selected) {
            response.add(toDTO(item.getInterest()));
        }

        return response;
    }

    @Override
    @Transactional
    public List<InterestResponseDTO> saveMyInterests(
            String username,
            StudentInterestRequestDTO request) {

        if (request == null || request.getInterestIds() == null) {
            throw new IllegalArgumentException(
                    "Interest IDs are required");
        }

        User user = getUser(username);

        List<UUID> requestedIds = request.getInterestIds();

        if (requestedIds.contains(null)) {
            throw new IllegalArgumentException(
                    "Interest ID cannot be null");
        }

        Set<UUID> uniqueIds = new HashSet<>(requestedIds);

        if (uniqueIds.size() != requestedIds.size()) {
            throw new IllegalArgumentException(
                    "Duplicate interest IDs are not allowed");
        }

        List<Interest> interests =
                interestDAO.findAllByIds(requestedIds);

        if (interests.size() != uniqueIds.size()) {
            throw new IllegalArgumentException(
                    "One or more interest IDs are invalid");
        }

        studentInterestDAO.deleteByUserId(user.getId());

        List<StudentInterest> newSelections = new ArrayList<>();

        for (Interest interest : interests) {

            StudentInterest studentInterest =
                    new StudentInterest();

            studentInterest.setUser(user);
            studentInterest.setInterest(interest);

            newSelections.add(studentInterest);
        }

        studentInterestDAO.saveAll(newSelections);

        List<InterestResponseDTO> response = new ArrayList<>();

        for (Interest interest : interests) {
            response.add(toDTO(interest));
        }

        return response;
    }

    private User getUser(String username) {

        return userRepository.findByUserName(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student account not found"));
    }

    private InterestResponseDTO toDTO(Interest interest) {

        InterestResponseDTO dto = new InterestResponseDTO();

        dto.setId(interest.getId());
        dto.setName(interest.getName());
        dto.setCategory(interest.getCategory());

        return dto;
    }
}
