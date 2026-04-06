package io.github.ngtrphuc.smartphone_shop.service;

import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.ngtrphuc.smartphone_shop.model.User;
import io.github.ngtrphuc.smartphone_shop.repository.UserRepository;

@Service
public class AuthService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean register(String email, String fullName, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedFullName = normalizeFullName(fullName);
        String normalizedPassword = normalizePassword(rawPassword);

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (normalizedFullName.length() < 2) {
            throw new IllegalArgumentException("Full name must be at least 2 characters.");
        }
        if (normalizedPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return false;
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setFullName(normalizedFullName);
        user.setPassword(passwordEncoder.encode(normalizedPassword));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return true;
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeFullName(String fullName) {
        return fullName == null ? "" : fullName.trim().replaceAll("\\s+", " ");
    }

    private String normalizePassword(String rawPassword) {
        return rawPassword == null ? "" : rawPassword;
    }
}
