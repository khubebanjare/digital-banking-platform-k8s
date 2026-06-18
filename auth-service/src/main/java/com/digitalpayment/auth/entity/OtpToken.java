package com.digitalpayment.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "otp_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpToken {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String otp;

  private Instant expiryDate;

  private boolean verified;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OtpToken otpToken)) return false;
    return id != null && id.equals(otpToken.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "OtpToken{"
        + "id="
        + id
        + ", otp='"
        + otp
        + '\''
        + ", expiryDate="
        + expiryDate
        + ", verified="
        + verified
        + '}';
  }
}
