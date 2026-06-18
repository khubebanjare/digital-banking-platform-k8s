package com.digitalpayment.auth.service;

import com.digitalpayment.auth.entity.RefreshToken;
import com.digitalpayment.auth.entity.User;
import java.util.Optional;

public interface RefreshTokenService {

  RefreshToken createRefreshToken(User user);

  Optional<RefreshToken> findByToken(String token);

  RefreshToken verifyExpiration(RefreshToken token);

  void deleteByUser(User user);

  void revokeToken(String token);
}
