
package com.app.service;

import com.app.entity.OTPRegister;
import com.app.repository.OTPRegisterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpRegistrationService {

    @Autowired
    private OTPRegisterRepository otpRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final SecureRandom random = new SecureRandom();

    private static final int OTP_EXPIRATION_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email is required");
        }

        String value = email.trim().toLowerCase();

        if (!value.matches("^[A-Za-z0-9._%+-]+@slu\\.edu$")) {
            throw new IllegalArgumentException(
                "Please use your SLU email address"
            );
        }

        return value;
    }

    private String generateOTP() {
        return String.format("%04d", random.nextInt(10000));
    }

    @Transactional
    public Map<String, Object> sendOtpEmail(String email) {

        String studentEmail = normalizeEmail(email);
        LocalDateTime now = LocalDateTime.now();

        OTPRegister record = otpRepo.findByEmail(studentEmail)
                .orElse(new OTPRegister());

        if (record.getLastSentAt() != null &&
                now.isBefore(record.getLastSentAt()
                        .plusSeconds(RESEND_COOLDOWN_SECONDS))) {
            throw new IllegalArgumentException(
                "Please wait 60 seconds before requesting another OTP"
            );
        }

        String otp = generateOTP();

        record.setEmail(studentEmail);
        record.setOtp(passwordEncoder.encode(otp));
        record.setStatus("PENDING");
        record.setCreatedAt(now);
        record.setExpiresAt(now.plusMinutes(OTP_EXPIRATION_MINUTES));
        record.setLastSentAt(now);
        record.setVerifiedAt(null);
        record.setAttemptCount(0);

        otpRepo.saveAndFlush(record);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(studentEmail);
        message.setSubject("SLU Finder - Email Verification");
        message.setText(
            "Hello,\n\n" +
            "Your SLU Finder verification code is: " + otp +
            "\n\nThis code expires in 5 minutes." +
            "\n\nIf you did not request this code, ignore this email." +
            "\n\nSLU Finder Team"
        );

        mailSender.send(message);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP sent successfully");
        response.put("email", studentEmail);

        return response;
    }

    @Transactional
    public boolean verifyOtp(String email, String enteredOtp) {

        String studentEmail = normalizeEmail(email);

        OTPRegister record = otpRepo.findByEmail(studentEmail)
                .orElse(null);

        if (record == null || enteredOtp == null) {
            return false;
        }

        if (!"PENDING".equals(record.getStatus())) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (record.getExpiresAt() == null ||
                now.isAfter(record.getExpiresAt())) {
            record.setStatus("EXPIRED");
            otpRepo.save(record);
            return false;
        }

        int attempts = record.getAttemptCount() == null
                ? 0 : record.getAttemptCount();

        if (attempts >= MAX_ATTEMPTS) {
            return false;
        }

        if (!enteredOtp.matches("\\d{4}") ||
                !passwordEncoder.matches(
                        enteredOtp, record.getOtp())) {

            attempts++;
            record.setAttemptCount(attempts);

            if (attempts >= MAX_ATTEMPTS) {
                record.setStatus("LOCKED");
            }

            otpRepo.save(record);
            return false;
        }

        record.setStatus("VERIFIED");
        record.setVerifiedAt(now);
        record.setOtp(null);

        otpRepo.save(record);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isEmailVerified(String email) {

        String studentEmail = normalizeEmail(email);

        return otpRepo.findByEmail(studentEmail)
                .map(record ->
                    "VERIFIED".equals(record.getStatus()) &&
                    record.getVerifiedAt() != null &&
                    LocalDateTime.now().isBefore(
                        record.getVerifiedAt().plusMinutes(10)
                    )
                )
                .orElse(false);
    }

    @Transactional
    public void consumeVerifiedOtp(String email) {

        String studentEmail = normalizeEmail(email);

        OTPRegister record = otpRepo.findByEmail(studentEmail)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Email verification is required"
                    )
                );

        if (!"VERIFIED".equals(record.getStatus()) ||
                record.getVerifiedAt() == null ||
                !LocalDateTime.now().isBefore(
                    record.getVerifiedAt().plusMinutes(10)
                )) {
            throw new IllegalArgumentException(
                "Email verification has expired or was already used"
            );
        }

        record.setStatus("USED");
        otpRepo.save(record);
    }
}
