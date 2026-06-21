package com.digitalpayment.auth.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.digitalpayment.auth.entity.OtpToken;
import com.digitalpayment.auth.entity.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OtpTokenRepositoryTest {

  @Mock private OtpTokenRepository otpTokenRepository;

  private User user;
  private OtpToken otpToken;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("john@example.com");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setPassword("encodedPassword");

    otpToken = new OtpToken();
    otpToken.setId(UUID.randomUUID());
    otpToken.setOtp("123456");
    otpToken.setExpiryDate(Instant.now().plusSeconds(300));
    otpToken.setVerified(false);
    otpToken.setUser(user);
  }

  @Test
  void testFindByUser_WhenTokenExists() {
    when(otpTokenRepository.findByUser(user)).thenReturn(Optional.of(otpToken));

    Optional<OtpToken> found = otpTokenRepository.findByUser(user);

    assertTrue(found.isPresent());
    assertEquals("123456", found.get().getOtp());
    assertEquals(user, found.get().getUser());
    verify(otpTokenRepository).findByUser(user);
  }

  @Test
  void testFindByUser_WhenTokenDoesNotExist() {
    when(otpTokenRepository.findByUser(user)).thenReturn(Optional.empty());

    Optional<OtpToken> found = otpTokenRepository.findByUser(user);

    assertFalse(found.isPresent());
    verify(otpTokenRepository).findByUser(user);
  }

  @Test
  void testFindByOtp_WhenTokenExists() {
    when(otpTokenRepository.findByOtp("123456")).thenReturn(Optional.of(otpToken));

    Optional<OtpToken> found = otpTokenRepository.findByOtp("123456");

    assertTrue(found.isPresent());
    assertEquals("123456", found.get().getOtp());
    verify(otpTokenRepository).findByOtp("123456");
  }

  @Test
  void testFindByOtp_WhenTokenDoesNotExist() {
    when(otpTokenRepository.findByOtp("999999")).thenReturn(Optional.empty());

    Optional<OtpToken> found = otpTokenRepository.findByOtp("999999");

    assertFalse(found.isPresent());
    verify(otpTokenRepository).findByOtp("999999");
  }

  @Test
  void testDeleteByUser() {
    doNothing().when(otpTokenRepository).deleteByUser(user);

    otpTokenRepository.deleteByUser(user);

    verify(otpTokenRepository).deleteByUser(user);
  }

  @Test
  void testSaveOtpToken() {
    when(otpTokenRepository.save(otpToken)).thenReturn(otpToken);

    OtpToken saved = otpTokenRepository.save(otpToken);

    assertNotNull(saved);
    assertNotNull(saved.getId());
    assertEquals("123456", saved.getOtp());
    assertEquals(user, saved.getUser());
    verify(otpTokenRepository).save(otpToken);
  }

  @Test
  void testFindById_WhenTokenExists() {
    UUID tokenId = otpToken.getId();
    when(otpTokenRepository.findById(tokenId)).thenReturn(Optional.of(otpToken));

    Optional<OtpToken> found = otpTokenRepository.findById(tokenId);

    assertTrue(found.isPresent());
    assertEquals(tokenId, found.get().getId());
    verify(otpTokenRepository).findById(tokenId);
  }

  @Test
  void testFindById_WhenTokenDoesNotExist() {
    UUID randomId = UUID.randomUUID();
    when(otpTokenRepository.findById(randomId)).thenReturn(Optional.empty());

    Optional<OtpToken> found = otpTokenRepository.findById(randomId);

    assertFalse(found.isPresent());
    verify(otpTokenRepository).findById(randomId);
  }

  @Test
  void testDeleteOtpToken() {
    doNothing().when(otpTokenRepository).delete(otpToken);

    otpTokenRepository.delete(otpToken);

    verify(otpTokenRepository).delete(otpToken);
  }

  @Test
  void testCount() {
    when(otpTokenRepository.count()).thenReturn(1L);

    long count = otpTokenRepository.count();

    assertEquals(1, count);
    verify(otpTokenRepository).count();
  }

  @Test
  void testUpdateOtpToken() {
    OtpToken updatedToken = new OtpToken();
    updatedToken.setId(otpToken.getId());
    updatedToken.setOtp("999999");
    updatedToken.setExpiryDate(Instant.now().plusSeconds(300));
    updatedToken.setVerified(true);
    updatedToken.setUser(user);

    when(otpTokenRepository.save(any(OtpToken.class))).thenReturn(updatedToken);

    OtpToken saved = otpTokenRepository.save(updatedToken);

    assertTrue(saved.isVerified());
    assertEquals("999999", saved.getOtp());
    verify(otpTokenRepository).save(any(OtpToken.class));
  }
}
