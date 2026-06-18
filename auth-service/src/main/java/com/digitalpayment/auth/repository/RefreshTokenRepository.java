package com.digitalpayment.auth.repository;

import com.digitalpayment.auth.entity.RefreshToken;
import com.digitalpayment.auth.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByUser(User user);

  void deleteByUser(User user);
}
