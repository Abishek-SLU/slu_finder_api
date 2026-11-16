package com.app.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder(12);

    private PasswordUtil() {
    }

    public static String getEncryptedPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty");
        }

        return encoder.encode(password);
    }

    public static boolean matches(
            String rawPassword,
            String storedPassword) {

        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        if (!storedPassword.startsWith("$2")) {
            return false;
        }

        return encoder.matches(rawPassword, storedPassword);
    }
}