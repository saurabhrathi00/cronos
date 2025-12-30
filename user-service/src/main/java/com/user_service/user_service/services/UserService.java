package com.user_service.user_service.services;

import com.user_service.user_service.enums.RoleEnum;
import com.user_service.user_service.exceptions.BadRequestException;
import com.user_service.user_service.exceptions.ConflictException;
import com.user_service.user_service.exceptions.RoleNotFoundException;
import com.user_service.user_service.models.ResolveUsersResponse;
import com.user_service.user_service.models.SignupRequest;
import com.user_service.user_service.models.SignupResponse;
import com.user_service.user_service.models.dao.RoleEntity;
import com.user_service.user_service.models.dao.UserEntity;
import com.user_service.user_service.repository.RoleRepository;
import com.user_service.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse registerUser(SignupRequest request) {
        // --- Validate input ---
        String username = normalize(request.getUsername());
        String email = normalizeEmail(request.getEmail());
        validatePassword(request.getPassword());

        // --- Check existing records (pre-check for UX, not authority) ---
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already registered");
        }

        // --- Fetch role (prefer caching or enum if roles are static) ---
        RoleEntity defaultRole = roleRepository.findById(RoleEnum.ROLE_USER.name())
                .orElseThrow(() -> new RoleNotFoundException(RoleEnum.ROLE_USER.name()));

        // --- Build entity ---
        UserEntity user = UserEntity.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(defaultRole))
                .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Handles race condition on concurrent signups
            throw new ConflictException("Username or email already registered");
        }

        log.info("User registered successfully: [id={}, username={}]", user.getId(), user.getUsername());
        return SignupResponse.builder()
                .status("SUCCESS")
                .message("User registered successfully")
                .data(SignupResponse.UserData.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    // --- Helpers ---

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Username cannot be empty");
        }
        return value.trim();
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email cannot be empty");
        }
        return email.trim().toLowerCase();
    }

    private void validatePassword(String password)  {
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters");
        }
    }

    @Transactional(readOnly = true)
    public String getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserEntity::getId)
                .orElseThrow(() -> new ConflictException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public List<ResolveUsersResponse.UserMapping> resolveUserIds(List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            throw new BadRequestException("Usernames list cannot be empty");
        }

        // Fetch all matching users
        List<UserEntity> users = userRepository.findByUsernameIn(usernames);

        // Map username -> ULID
        Map<String, String> foundMap = users.stream()
                .collect(Collectors.toMap(UserEntity::getUsername, UserEntity::getId));

        // Build response preserving input order
        return usernames.stream()
                .map(username -> ResolveUsersResponse.UserMapping.builder()
                        .username(username)
                        .userId(foundMap.getOrDefault(username, null))
                        .build())
                .collect(Collectors.toList());
    }

}

