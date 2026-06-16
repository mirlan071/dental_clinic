package com.dentalclinic.service;

import com.dentalclinic.controller.dto.auth.*;
import com.dentalclinic.domain.user.User;
import com.dentalclinic.exception.DuplicateResourceException;
import com.dentalclinic.mapper.UserMapper;
import com.dentalclinic.repository.UserRepository;
import com.dentalclinic.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new DuplicateResourceException("User", "username", request.getUsername()));

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration())
                .role(user.getRole().name())
                .username(user.getUsername())
                .build();
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new DuplicateResourceException("Invalid refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessToken(username);
        String newRefreshToken = tokenProvider.generateRefreshToken(username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DuplicateResourceException("User", "username", username));

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration())
                .role(user.getRole().name())
                .username(user.getUsername())
                .build();
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = userMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        User saved = userRepository.save(user);
        log.info("User registered: {}", saved.getUsername());

        String accessToken = tokenProvider.generateAccessToken(saved.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(saved.getUsername());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration())
                .role(saved.getRole().name())
                .username(saved.getUsername())
                .build();
    }
}
