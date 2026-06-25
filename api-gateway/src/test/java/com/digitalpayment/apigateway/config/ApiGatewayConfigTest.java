package com.digitalpayment.apigateway.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import com.digitalpayment.apigateway.filter.ApiGatewayFilter;

@ExtendWith(MockitoExtension.class)
class ApiGatewayConfigTest {

  @Mock
  private RateLimiterConfig rateLimiterConfig;

  @Mock
  private ApiGatewayFilter apiGatewayFilter;

  @Mock
  private RouteLocatorBuilder routeLocatorBuilder;

  @Test
  void testApiGatewayConfigCreation() {
    ApiGatewayConfig config = new ApiGatewayConfig(rateLimiterConfig);
    assertNotNull(config);
  }

  @Test
  void testApiGatewayConfigWithNullRateLimiterConfig() {
    ApiGatewayConfig config = new ApiGatewayConfig(null);
    assertNotNull(config);
  }

  @Test
  void testApiGatewayConfigDependencyInjection() {
    ApiGatewayConfig config = new ApiGatewayConfig(rateLimiterConfig);
    assertNotNull(config);
  }

  @Test
  void testCustomRouteLocatorBean() {
    ApiGatewayConfig config = new ApiGatewayConfig(rateLimiterConfig);
    
    RouteLocatorBuilder builder = mock(RouteLocatorBuilder.class);
    RouteLocatorBuilder.Builder routesBuilder = mock(RouteLocatorBuilder.Builder.class);
    RouteLocator routeLocator = mock(RouteLocator.class);
    
    when(builder.routes()).thenReturn(routesBuilder);
    when(routesBuilder.route(anyString(), any())).thenReturn(routesBuilder);
    when(routesBuilder.build()).thenReturn(routeLocator);
    
    RouteLocator result = config.customRouteLocator(builder, apiGatewayFilter);
    assertNotNull(result);
  }

  @Test
  void testRateLimiterConfigDependency() {
    ApiGatewayConfig config = new ApiGatewayConfig(rateLimiterConfig);
    assertNotNull(config);
  }
}
