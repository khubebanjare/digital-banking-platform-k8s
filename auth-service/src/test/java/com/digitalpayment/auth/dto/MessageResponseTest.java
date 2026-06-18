package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessageResponseTest {

  @Test
  void testMessageResponseCreation() {
    MessageResponse response = new MessageResponse("Operation successful");

    assertEquals("Operation successful", response.message());
  }

  @Test
  void testMessageResponseWithNullMessage() {
    MessageResponse response = new MessageResponse(null);

    assertEquals(null, response.message());
  }

  @Test
  void testMessageResponseWithEmptyMessage() {
    MessageResponse response = new MessageResponse("");

    assertEquals("", response.message());
  }

  @Test
  void testMessageResponseEquals() {
    MessageResponse response1 = new MessageResponse("Operation successful");
    MessageResponse response2 = new MessageResponse("Operation successful");

    assertEquals(response1, response2);
  }

  @Test
  void testMessageResponseHashCode() {
    MessageResponse response1 = new MessageResponse("Operation successful");
    MessageResponse response2 = new MessageResponse("Operation successful");

    assertEquals(response1.hashCode(), response2.hashCode());
  }

  @Test
  void testMessageResponseToString() {
    MessageResponse response = new MessageResponse("Operation successful");

    assertNotNull(response.toString());
  }
}
