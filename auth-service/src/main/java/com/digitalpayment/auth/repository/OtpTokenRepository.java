package com.digitalpayment.auth.repository;

import com.digitalpayment.auth.entity.OtpToken;
import com.digitalpayment.auth.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {

  Optional<OtpToken> findByUser(User user);

  Optional<OtpToken> findByOtp(String otp);

  void deleteByUser(User user);
}
