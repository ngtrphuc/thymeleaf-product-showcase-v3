package io.github.ngtrphuc.smartphone_shop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.ngtrphuc.smartphone_shop.model.User;
import io.github.ngtrphuc.smartphone_shop.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    void register_shouldNormalizeEmailBeforeSaving() {
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");

        boolean registered = authService.register("  User@Example.com  ", "  Nguyen   Phuc  ", "secret123");

        assertTrue(registered);
        Object savedArgument = org.mockito.Mockito.mockingDetails(userRepository).getInvocations().stream()
                .filter(invocation -> invocation.getMethod().getName().equals("save"))
                .map(invocation -> invocation.getArgument(0))
                .findFirst()
                .orElseThrow();
        long saveInvocationCount = org.mockito.Mockito.mockingDetails(userRepository).getInvocations().stream()
                .filter(invocation -> invocation.getMethod().getName().equals("save"))
                .count();
        User savedUser = assertInstanceOf(User.class, savedArgument);

        assertEquals(1L, saveInvocationCount);
        assertEquals("user@example.com", savedUser.getEmail());
        assertEquals("Nguyen Phuc", savedUser.getFullName());
        assertEquals("encoded-secret", savedUser.getPassword());
    }

    @Test
    void register_shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.register("not-an-email", "Tester", "secret123"));
        verifyNoInteractions(userRepository, passwordEncoder);
    }
}
