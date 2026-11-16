
package com.app.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.UserUpdateDTO;
import com.app.entity.OTPRegister;
import com.app.entity.Role;
import com.app.entity.User;
import com.app.enumeration.Status;
import com.app.enumeration.UserType;
import com.app.repository.OTPRegisterRepository;
import com.app.repository.RegisterRepository;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import com.app.util.PasswordUtil;

@Service
public class StudentRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OTPRegisterRepository otpRepository;

    @Transactional
    public void registerStudent(UserUpdateDTO request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Registration details are required");
        }

        String email = request.getEmail() == null
                ? ""
                : request.getEmail().trim().toLowerCase();

        String username = request.getUserName() == null
                ? ""
                : request.getUserName().trim();

        String fullName = request.getFullName() == null
                ? ""
                : request.getFullName().trim();

        String password = request.getPassword();

        if (!email.matches("^[A-Za-z0-9._%+-]+@slu\\.edu$")) {
            throw new IllegalArgumentException(
                    "Only SLU email addresses are allowed");
        }

        if (!username.matches("^[A-Za-z0-9._]{3,30}$")) {
            throw new IllegalArgumentException(
                    "Username must be 3 to 30 characters");
        }

        if (fullName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Full name is required");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }

        if (request.getId() != null) {
            throw new IllegalArgumentException(
                    "New registration cannot contain a user ID");
        }

        OTPRegister otpRecord = otpRepository
                .findByEmailForUpdate(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Please verify your email first"));

        if (!"VERIFIED".equals(otpRecord.getStatus())
                || otpRecord.getVerifiedAt() == null
                || !LocalDateTime.now().isBefore(
                        otpRecord.getVerifiedAt().plusMinutes(10))) {

            throw new IllegalArgumentException(
                    "Email verification is missing or expired");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        if (userRepository.existsByUserName(username)) {
            throw new IllegalArgumentException(
                    "Username is already taken");
        }

        Role studentRole = roleRepository
                .findByRoleName(UserType.STUDENT)
                .orElseThrow(() -> new IllegalStateException(
                        "STUDENT role not found in database"));

        if (studentRole.getStatus() != Status.ACTIVE) {
            throw new IllegalStateException(
                    "STUDENT role is inactive");
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName(fullName);
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(
                PasswordUtil.getEncryptedPassword(password));
        user.setPhoneNo(request.getPhoneNo());
        user.setAddress(request.getAddress());
        user.setUserRoleId(studentRole.getId());
        user.setStatus(Status.ACTIVE);
        user.setChangePassword(false);

        user.setCreatedBy("SELF_REGISTRATION");
        user.setModifiedBy("SELF_REGISTRATION");
        user.setCreatedOn(new Date());
        user.setModifiedOn(new Date());

        // Save user and consume OTP in the same transaction
        registerRepository.saveAndFlush(user);

        otpRecord.setStatus("USED");
        otpRepository.save(otpRecord);
    }
}
