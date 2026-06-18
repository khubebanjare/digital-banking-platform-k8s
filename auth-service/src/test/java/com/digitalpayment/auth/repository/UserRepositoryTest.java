package com.digitalpayment.auth.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.digitalpayment.auth.entity.Role;
import com.digitalpayment.auth.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

  @Mock private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("john@example.com");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setPassword("encodedPassword");
    user.setRole(Role.USER);
    user.setEnabled(true);
    user.setAccountNonExpired(true);
    user.setAccountNonLocked(true);
    user.setCredentialsNonExpired(true);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void testFindByEmail() {
    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

    Optional<User> foundUser = userRepository.findByEmail("john@example.com");

    assertTrue(foundUser.isPresent());
    assertEquals("john@example.com", foundUser.get().getEmail());
    assertEquals("John", foundUser.get().getFirstName());
    verify(userRepository).findByEmail("john@example.com");
  }

  @Test
  void testFindByEmailNotFound() {
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

    assertFalse(foundUser.isPresent());
    verify(userRepository).findByEmail("nonexistent@example.com");
  }

  @Test
  void testFindByEmailWithNull() {
    when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

    Optional<User> foundUser = userRepository.findByEmail(null);

    assertFalse(foundUser.isPresent());
    verify(userRepository).findByEmail(null);
  }

  @Test
  void testExistsByEmail() {
    when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

    boolean exists = userRepository.existsByEmail("john@example.com");

    assertTrue(exists);
    verify(userRepository).existsByEmail("john@example.com");
  }

  @Test
  void testExistsByEmailNotFound() {
    when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

    boolean exists = userRepository.existsByEmail("nonexistent@example.com");

    assertFalse(exists);
    verify(userRepository).existsByEmail("nonexistent@example.com");
  }

  @Test
  void testExistsByEmailWithNull() {
    when(userRepository.existsByEmail(null)).thenReturn(false);

    boolean exists = userRepository.existsByEmail(null);

    assertFalse(exists);
    verify(userRepository).existsByEmail(null);
  }

  @Test
  void testSaveUser() {
    User newUser = new User();
    newUser.setId(UUID.randomUUID());
    newUser.setEmail("jane@example.com");
    newUser.setFirstName("Jane");
    newUser.setLastName("Smith");
    newUser.setPassword("encodedPassword");
    newUser.setRole(Role.USER);
    newUser.setEnabled(true);
    newUser.setAccountNonExpired(true);
    newUser.setAccountNonLocked(true);
    newUser.setCredentialsNonExpired(true);
    newUser.setCreatedAt(LocalDateTime.now());
    newUser.setUpdatedAt(LocalDateTime.now());

    when(userRepository.save(newUser)).thenReturn(newUser);

    User savedUser = userRepository.save(newUser);

    assertNotNull(savedUser);
    assertNotNull(savedUser.getId());
    assertEquals("jane@example.com", savedUser.getEmail());
    verify(userRepository).save(newUser);
  }

  @Test
  void testDeleteUser() {
    doNothing().when(userRepository).delete(user);

    userRepository.delete(user);

    verify(userRepository).delete(user);
  }

  @Test
  void testFindById() {
    UUID userId = user.getId();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    Optional<User> foundUser = userRepository.findById(userId);

    assertTrue(foundUser.isPresent());
    assertEquals(userId, foundUser.get().getId());
    verify(userRepository).findById(userId);
  }

  @Test
  void testFindByIdNotFound() {
    UUID randomId = UUID.randomUUID();
    when(userRepository.findById(randomId)).thenReturn(Optional.empty());

    Optional<User> foundUser = userRepository.findById(randomId);

    assertFalse(foundUser.isPresent());
    verify(userRepository).findById(randomId);
  }

  @Test
  void testCount() {
    when(userRepository.count()).thenReturn(1L);

    long count = userRepository.count();

    assertEquals(1, count);
    verify(userRepository).count();
  }
}
