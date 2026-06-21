package com.digitalpayment.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String token;

  private Instant expiryDate;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}
