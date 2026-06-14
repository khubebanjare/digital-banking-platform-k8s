package com.digitalpayment.auth.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

  private String token;
  private String tokenType = "Bearer";
  private UUID id;
  private String email;
  private String firstName;
  private String lastName;
}
