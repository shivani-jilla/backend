package com.learnhub;

import com.learnhub.dto.AuthRequest;
import com.learnhub.dto.AuthResponse;
import com.learnhub.dto.RegisterRequest;
import com.learnhub.entity.Role;
import com.learnhub.entity.User;
import com.learnhub.exception.BusinessException;
import com.learnhub.repository.UserRepository;
import com.learnhub.security.JwtService;
import com.learnhub.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Alice Student", "alice@example.com", "hashed_pwd", Role.STUDENT, true);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest("Alice Student", "alice@example.com", "password123", Role.STUDENT);

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_pwd");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtService.generateToken(any())).thenReturn("sample.jwt.token");

        AuthResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("sample.jwt.token", resp.getToken());
        assertEquals("alice@example.com", resp.getUser().getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterDuplicateEmailThrowsException() {
        RegisterRequest req = new RegisterRequest("Alice", "alice@example.com", "password123", Role.STUDENT);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterAdminRoleRejected() {
        RegisterRequest req = new RegisterRequest("Hacker", "hacker@example.com", "password123", Role.ADMIN);
        when(userRepository.existsByEmail("hacker@example.com")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(req));
        assertTrue(ex.getMessage().contains("ADMIN"));
    }

    @Test
    void testLoginSuccess() {
        AuthRequest req = new AuthRequest("alice@example.com", "password123");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleUser));
        when(jwtService.generateToken(any())).thenReturn("valid.jwt.token");

        AuthResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("valid.jwt.token", resp.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLoginBadCredentialsThrows() {
        AuthRequest req = new AuthRequest("alice@example.com", "wrongpass");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }
}
