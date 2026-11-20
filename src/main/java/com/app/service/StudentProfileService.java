
package com.app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.StudentProfileDAO;
import com.app.dto.StudentProfileRequestDTO;
import com.app.dto.StudentProfileResponseDTO;
import com.app.entity.StudentProfile;
import com.app.entity.User;
import com.app.repository.UserRepository;

@Service
public class StudentProfileService implements IStudentProfileService {

    private final StudentProfileDAO studentProfileDAO;
    private final UserRepository userRepository;

    public StudentProfileService(
            StudentProfileDAO studentProfileDAO,
            UserRepository userRepository) {

        this.studentProfileDAO = studentProfileDAO;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponseDTO getProfile(String username) {

        User user = getUser(username);

        Optional<StudentProfile> existingProfile =
                studentProfileDAO.findByUserId(user.getId());

        StudentProfile profile = existingProfile.orElse(null);

        return toResponse(user, profile);
    }

    @Override
    @Transactional
    public StudentProfileResponseDTO saveProfile(
            String username,
            StudentProfileRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Profile details are required");
        }

        User user = getUser(username);

        StudentProfile profile = studentProfileDAO
                .findByUserId(user.getId())
                .orElseGet(() -> {
                    StudentProfile newProfile = new StudentProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setBio(request.getBio());
        profile.setProgram(request.getProgram());
        profile.setAcademicYear(request.getAcademicYear());
        profile.setMajor(request.getMajor());
        profile.setProfileImageUrl(request.getProfileImageUrl());

        StudentProfile savedProfile =
                studentProfileDAO.save(profile);

        return toResponse(user, savedProfile);
    }

    private User getUser(String username) {

        return userRepository.findByUserName(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student account not found"));
    }

    private StudentProfileResponseDTO toResponse(
            User user,
            StudentProfile profile) {

        StudentProfileResponseDTO response =
                new StudentProfileResponseDTO();

        response.setUserId(user.getId());
        response.setUserName(user.getUserName());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());

        if (profile != null) {
            response.setBio(profile.getBio());
            response.setProgram(profile.getProgram());
            response.setAcademicYear(profile.getAcademicYear());
            response.setMajor(profile.getMajor());
            response.setProfileImageUrl(
                    profile.getProfileImageUrl());
        }

        return response;
    }
}
