
package com.app.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.dto.LoginRequest;
import com.app.dto.LoginResponse;
import com.app.dto.PasswordUpdateDTO;
import com.app.entity.Role;
import com.app.entity.User;
import com.app.enumeration.Status;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import com.app.response.ResponseGenerator;
import com.app.response.TransactionContext;
import com.app.security.JwtTokenUtil;
import com.app.service.UserService;
import com.app.util.PasswordUtil;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ResponseGenerator responseGenerator;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            @RequestHeader HttpHeaders headers) {

        TransactionContext context =
                responseGenerator.generateTransationContext(headers);

        if (request == null ||
                request.getUserName() == null ||
                request.getPassword() == null) {

            return responseGenerator.errorResponse(
                context,
                "Username and password are required",
                HttpStatus.BAD_REQUEST
            );
        }

        String identifier = request.getUserName().trim();

        Optional<User> userOptional =
                userRepository.findByUserNameOrEmail(
                    identifier, identifier);

        if (!userOptional.isPresent()) {
            return invalidLogin(context);
        }

        User user = userOptional.get();

        if (user.getStatus() != Status.ACTIVE) {
            return invalidLogin(context);
        }

        if (!PasswordUtil.matches(
                request.getPassword(),
                user.getPassword())) {
            return invalidLogin(context);
        }

        Role role = roleRepository.findById(user.getUserRoleId())
                .orElse(null);

        if (role == null ||
                role.getRoleName() == null ||
                role.getStatus() != Status.ACTIVE) {
            return invalidLogin(context);
        }

        String token = jwtTokenUtil.generateToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("status", 1);
        data.put("message", "Logged in Successfully");
        data.put("jwt", token);
        data.put("roleName", role.getRoleName().name());
        data.put("userName", user.getUserName());
        data.put("fullName", user.getFullName());
        data.put("isOtpVerified", true);

        return responseGenerator.successResponse(
                context,
                data,
                HttpStatus.OK
        );
    }

    private ResponseEntity<?> invalidLogin(
            TransactionContext context) {

        return responseGenerator.errorResponse(
            context,
            "Invalid username or password",
            HttpStatus.UNAUTHORIZED
        );
    }

    @GetMapping("/user-details")
    public ResponseEntity<?> getUserDetails(
            Principal principal,
            @RequestHeader HttpHeaders headers) {

        TransactionContext context =
                responseGenerator.generateTransationContext(headers);

        if (principal == null) {
            return responseGenerator.errorResponse(
                context,
                "Unauthorized",
                HttpStatus.UNAUTHORIZED
            );
        }

        User user = userRepository
                .findByUserName(principal.getName())
                .orElse(null);

        if (user == null) {
            return responseGenerator.errorResponse(
                context,
                "User not found",
                HttpStatus.NOT_FOUND
            );
        }

        LoginResponse details = new LoginResponse();
        details.setUserId(user.getId());
        details.setUserName(user.getUserName());
        details.setFullName(user.getFullName());
        details.setPhone(user.getPhoneNo());
        details.setAddress(user.getAddress());
        details.setRoleId(user.getUserRoleId());
        details.setChangePassword(user.getChangePassword());

        Map<String, Object> data = new HashMap<>();
        data.put("loginObj", details);

        return responseGenerator.successResponse(
                context,
                data,
                HttpStatus.OK
        );
    }

    @PutMapping("/change/password")
    public ResponseEntity<?> changePassword(
            @RequestBody PasswordUpdateDTO request,
            Principal principal,
            @RequestHeader HttpHeaders headers) {

        TransactionContext context =
                responseGenerator.generateTransationContext(headers);

        if (principal == null) {
            return responseGenerator.errorResponse(
                context,
                "Unauthorized",
                HttpStatus.UNAUTHORIZED
            );
        }

        User user = userRepository
                .findByUserName(principal.getName())
                .orElse(null);

        if (user == null) {
            return responseGenerator.errorResponse(
                context,
                "User not found",
                HttpStatus.NOT_FOUND
            );
        }

        if (request == null ||
                request.getOldPassword() == null ||
                request.getNewPassword() == null ||
                request.getConfirmPassword() == null) {

            return responseGenerator.errorResponse(
                context,
                "All password fields are required",
                HttpStatus.BAD_REQUEST
            );
        }

        if (!PasswordUtil.matches(
                request.getOldPassword(),
                user.getPassword())) {

            return responseGenerator.errorResponse(
                context,
                "Current password is incorrect",
                HttpStatus.BAD_REQUEST
            );
        }

        if (!request.getNewPassword().equals(
                request.getConfirmPassword())) {

            return responseGenerator.errorResponse(
                context,
                "New passwords do not match",
                HttpStatus.BAD_REQUEST
            );
        }

        if (request.getNewPassword().length() < 8 ||
                request.getNewPassword().equals(
                    request.getOldPassword())) {

            return responseGenerator.errorResponse(
                context,
                "Choose a different password with at least 8 characters",
                HttpStatus.BAD_REQUEST
            );
        }

        user.setPassword(
            PasswordUtil.getEncryptedPassword(
                request.getNewPassword())
        );

        user.setChangePassword(true);
        userService.update(user);

        return responseGenerator.successResponse(
                context,
                "Password changed successfully",
                HttpStatus.OK
        );
    }
}
