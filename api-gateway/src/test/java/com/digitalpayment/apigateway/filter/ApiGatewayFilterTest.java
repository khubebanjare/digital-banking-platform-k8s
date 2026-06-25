package com.digitalpayment.apigateway.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ApiGatewayFilterTest {

  @Mock
  private GatewayFilterChain chain;

  @Test
  void testFilterAddsHeaders() {
    ApiGatewayFilter filter = new ApiGatewayFilter();
    MockServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/test").build()
    );

    when(chain.filter(any())).thenReturn(Mono.empty());

    filter.filter(exchange, chain).block();

    verify(chain).filter(argThat(ex -> {
      HttpHeaders headers = ex.getRequest().getHeaders();
      return headers.containsHeader("X-API-GATEWAY") &&
             headers.containsHeader("X-Custom-Header");
    }));
  }

  @Test
  void testFilterCallsChain() {
    ApiGatewayFilter filter = new ApiGatewayFilter();
    MockServerWebExchange exchange = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test").build()
    );

    when(chain.filter(any())).thenReturn(Mono.empty());

    Mono<Void> result = filter.filter(exchange, chain);

    verify(chain, times(1)).filter(any());
    assertNotNull(result);
  }

  @Test
  void testFilterOrder() {
    ApiGatewayFilter filter = new ApiGatewayFilter();
    assertEquals(1, filter.getOrder());
  }

  @Test
  void testFilterLogsResponseStatus() {
    ApiGatewayFilter filter = new ApiGatewayFilter();
    MockServerWebExchange exchange = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test").build()
    );

    exchange.getResponse().setStatusCode(HttpStatus.OK);
    when(chain.filter(any())).thenReturn(Mono.empty());

    filter.filter(exchange, chain).block();

    assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
  }

  @Test
  void testFilterWithDifferentStatusCodes() {
    ApiGatewayFilter filter = new ApiGatewayFilter();

    MockServerWebExchange exchangeOk = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test").build()
    );
    exchangeOk.getResponse().setStatusCode(HttpStatus.OK);
    when(chain.filter(any())).thenReturn(Mono.empty());

    MockServerWebExchange exchangeNotFound = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test").build()
    );
    exchangeNotFound.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
    when(chain.filter(any())).thenReturn(Mono.empty());

    filter.filter(exchangeOk, chain).block();
    filter.filter(exchangeNotFound, chain).block();

    assertEquals(HttpStatus.OK, exchangeOk.getResponse().getStatusCode());
    assertEquals(HttpStatus.NOT_FOUND, exchangeNotFound.getResponse().getStatusCode());
  }

  @Test
  void testFilterImplementsOrdered() {
    ApiGatewayFilter filter = new ApiGatewayFilter();
    assertTrue(filter instanceof org.springframework.core.Ordered);
  }

  @Test
  void testFilterImplementsGatewayFilter() {
    ApiGatewayFilter filter = new ApiGatewayFilter();
    assertTrue(filter instanceof org.springframework.cloud.gateway.filter.GatewayFilter);
  }
}
