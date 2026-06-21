package com.digitalpayment.auth.service.impl;

import com.digitalpayment.auth.config.JwtProperties;
import com.digitalpayment.auth.entity.RefreshToken;
import com.digitalpayment.auth.entity.User;
import com.digitalpayment.auth.exception.AuthenticationException;
import com.digitalpayment.auth.repository.RefreshTokenRepository;
import com.digitalpayment.auth.service.RefreshTokenService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtProperties jwtProperties;

  @Override
  public RefreshToken createRefreshToken(User user) {
    log.info("Creating refresh token for user: {}", user.getEmail());

    Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
    if (existingToken.isPresent()) {
      RefreshToken refreshToken = existingToken.get();

      refreshToken.setToken(UUID.randomUUID().toString());
      refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpiration()));
      return refreshTokenRepository.save(refreshToken);
    }
    RefreshToken refreshToken =
        RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpiration()))
            .build();

    RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
    log.info("Refresh token created successfully for user: {}", user.getEmail());
    return savedToken;
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    log.debug("Finding refresh token by token");
    return refreshTokenRepository.findByToken(token);
  }

  @Override
  public RefreshToken verifyExpiration(RefreshToken token) {

    log.debug("Verifying refresh token expiration");
    if (token.getExpiryDate().isBefore(Instant.now())) {

      log.warn("Refresh token expired, deleting token");
      refreshTokenRepository.delete(token);

      throw new AuthenticationException("Refresh token expired");
    }

    log.debug("Refresh token is valid");
    return token;
  }

  @Override
  public void deleteByUser(User user) {
    log.info("Deleting refresh tokens for user: {}", user.getEmail());
    refreshTokenRepository.deleteByUser(user);
  }

  @Override
  @Transactional
  public void revokeToken(String token) {
    log.info("Revoke token request received for token: {}", token);
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new AuthenticationException("Invalid refresh token"));

    refreshTokenRepository.delete(refreshToken);
    log.info("Token revoked successfully");
  }
}
