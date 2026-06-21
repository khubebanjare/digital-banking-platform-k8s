package com.digitalpayment.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "email_verification_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID emailId;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private Instant expiryDate;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}
