package com.authms.service;

import com.authms.model.RefreshToken;
import com.authms.model.User;
import com.authms.repo.RefreshTokenRepository;
import com.authms.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean validateRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(refreshToken -> refreshToken.getExpiryDate().isAfter(Instant.now()))
                .orElse(false);
    }

    public void deleteRefreshTokenByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.deleteAllByUser(user);
    }
}
