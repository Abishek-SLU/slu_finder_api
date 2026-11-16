
package com.app.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.dto.OtpPhoneRegisterDto;
import com.app.dto.UserUpdateDTO;
import com.app.entity.OTPRegister;
import com.app.response.ResponseGenerator;
import com.app.response.TransactionContext;
import com.app.service.OtpRegistrationService;
import com.app.service.StudentRegistrationService;

@RestController
@RequestMapping("/api/register")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RegisterController {

    private static final Logger logger =
            LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private ResponseGenerator responseGenerator;

    @Autowired
    private OtpRegistrationService otpService;

    @Autowired
    private StudentRegistrationService registrationService;

    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOtp(
            @RequestBody OtpPhoneRegisterDto request,
            @RequestHeader HttpHeaders headers) {

        TransactionContext context =
                responseGenerator.generateTransationContext(headers);

        try {
            Map<String, Object> data =
                    otpService.sendOtpEmail(request.getEmail());

            logger.info("Registration OTP requested");

            return responseGenerator.successResponse(
                    context,
                    data,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            logger.warn("OTP request failed: {}", e.getMessage());

            return responseGenerator.errorResponse(
                    context,
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestBody OTPRegister request,
            @RequestHeader HttpHeaders headers) {

        TransactionContext context =
                responseGenerator.generateTransationContext(headers);

        try {
            boolean verified = otpService.verifyOtp(
                    request.getEmail(),
                    request.getOtp()
            );

            if (!verified) {
                return responseGenerator.errorResponse(
                        context,
                        "Invalid or expired OTP",
                        HttpStatus.BAD_REQUEST
                );
            }

            return responseGenerator.successResponse(
                    context,
                    "OTP Verified Successfully",
                    HttpStatus.OK
            );

        } catch (Exception e) {
            logger.warn("OTP verification failed: {}", e.getMessage());

            return responseGenerator.errorResponse(
                    context,
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createStudent(
            @RequestBody UserUpdateDTO request,
            @RequestHeader HttpHeaders headers) {

        TransactionContext context =
                responseGenerator.generateTransationContext(headers);

        try {
            registrationService.registerStudent(request);

            logger.info(
                "Student account created at {}",
                LocalDateTime.now()
            );

            return responseGenerator.successResponse(
                    context,
                    "Student Registered Successfully",
                    HttpStatus.CREATED
            );

        } catch (IllegalArgumentException e) {
            return responseGenerator.errorResponse(
                    context,
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception e) {
            logger.error("Student registration failed", e);

            return responseGenerator.errorResponse(
                    context,
                    "Unable to create account",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
