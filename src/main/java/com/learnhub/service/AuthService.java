package com.learnhub.service;

import com.learnhub.dto.AuthRequest;
import com.learnhub.dto.AuthResponse;
import com.learnhub.dto.RegisterRequest;
import com.learnhub.dto.UserDto;
import com.learnhub.entity.Role;
import com.learnhub.entity.User;
import com.learnhub.exception.BusinessException;
import com.learnhub.repository.UserRepository;
import com.learnhub.security.JwtService;
import com.learnhub.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new BusinessException("An account with email " + request.getEmail() + " already exists");
        }

        Role assignedRole = request.getRole() != null ? request.getRole() : Role.STUDENT;
        if (assignedRole == Role.ADMIN) {
            throw new BusinessException("Public registration as ADMIN is not permitted");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(assignedRole);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(savedUser);
        String token = jwtService.generateToken(userPrincipal);

        return new AuthResponse(token, UserDto.fromEntity(savedUser));
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BusinessException("User not found"));

        if (!user.isActive()) {
            throw new BusinessException("Account is deactivated. Please contact an administrator.");
        }

        UserPrincipal userPrincipal = new UserPrincipal(user);
        String token = jwtService.generateToken(userPrincipal);

        return new AuthResponse(token, UserDto.fromEntity(user));
    }

    public UserDto getCurrentUser(UserPrincipal principal) {
        if (principal == null) {
            throw new BusinessException("No authenticated user");
        }
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BusinessException("User not found"));
        return UserDto.fromEntity(user);
    }
}
