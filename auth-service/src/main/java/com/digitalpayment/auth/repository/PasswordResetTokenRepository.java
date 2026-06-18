package com.digitalpayment.auth.repository;

import com.digitalpayment.auth.entity.PasswordResetToken;
import com.digitalpayment.auth.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

  Optional<PasswordResetToken> findByToken(String token);

  void deleteByUser(User user);
}
