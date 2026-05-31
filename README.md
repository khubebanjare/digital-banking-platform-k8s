# Digital Banking Config Repository

Centralized Git-backed configuration repository for the Digital Banking Platform microservices ecosystem using Spring Cloud Config Server.

This repository stores externalized configuration files for all microservices, environments, and deployment platforms including local development, Eureka-based architecture, and Kubernetes-native deployments.

---

# 🚀 Purpose

This repository acts as the centralized configuration source for all services in the Digital Banking Platform.

It enables:

* centralized configuration management
* environment-specific configuration
* Kubernetes configuration support
* cloud-native deployment configuration
* externalized Spring Boot configuration
* Git-based configuration versioning

The repository is consumed dynamically by:

* Spring Cloud Config Server
* API Gateway
* Authentication Service
* Wallet Service
* Payment Processing Service
* Kubernetes deployments

---

# 🏗️ Architecture Flow

```text id="u2i9c3"
GitHub Config Repository
           ↓
Spring Cloud Config Server
           ↓
Microservices
```

---

# 📁 Repository Structure

```text id="80w98r"
digital-banking-config-repo/
│
├── shared/
│   ├── application.yml
│   ├── kafka.yml
│   ├── redis.yml
│   └── monitoring.yml
│
├── local/
│   ├── api-gateway-local.yml
│   ├── auth-service-local.yml
│   ├── wallet-service-local.yml
│   ├── payment-processing-service-local.yml
│   ├── transaction-service-local.yml
│   ├── notification-service-local.yml
│   ├── fraud-detection-service-local.yml
│   └── analytics-service-local.yml
│
├── kubernetes/
│   ├── api-gateway-k8s.yml
│   ├── auth-service-k8s.yml
│   ├── wallet-service-k8s.yml
│   ├── payment-processing-service-k8s.yml
│   ├── transaction-service-k8s.yml
│   ├── notification-service-k8s.yml
│   ├── fraud-detection-service-k8s.yml
│   └── analytics-service-k8s.yml
│
├── dev/
├── qa/
├── prod/
│
└── README.md
```

---

# 🌍 Supported Environments

This repository supports multiple environments:

| Environment | Purpose                        |
| ----------- | ------------------------------ |
| local       | Local development              |
| kubernetes  | Kubernetes-native deployment   |
| dev         | Shared development environment |
| qa          | QA testing environment         |
| prod        | Production environment         |

---

# ⚙️ Configuration Responsibilities

This repository manages:

* application ports
* database configuration
* Redis configuration
* Kafka configuration
* JWT configuration
* Eureka configuration
* Kubernetes service configuration
* monitoring configuration
* environment-specific properties
* external service URLs

---

# ☸️ Kubernetes Support

The repository includes Kubernetes-specific configuration files for:

* Kubernetes DNS-based service discovery
* ConfigMaps
* containerized deployments
* cloud-native networking
* scalable deployments

Example:

```yaml id="3j7jly"
service:
  discovery:
    type: kubernetes
```

---

# 🔄 Spring Cloud Config Integration

Microservices connect to Config Server using:

```yaml id="w33lbv"
spring:
  config:
    import: optional:configserver:http://localhost:8888
```

The Config Server dynamically fetches configuration files from this repository.

---

# 📌 Example Configuration

## auth-service-local.yml

```yaml id="ll7l8x"
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: postgres
    password: postgres

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

---

# 📌 Example Kubernetes Configuration

## auth-service-k8s.yml

```yaml id="9pkvzt"
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://postgres-service:5432/auth_db
    username: postgres
    password: postgres

service:
  discovery:
    type: kubernetes
```

---

# 🔐 Security Recommendations

Sensitive production secrets should not be stored directly inside Git repositories.

Recommended enterprise approaches:

* HashiCorp Vault
* Kubernetes Secrets
* AWS Secrets Manager
* encrypted Config Server properties
* environment variables

---

# 🧠 Enterprise Concepts Demonstrated

* Spring Cloud Config Server
* Externalized Configuration
* Git-backed Configuration Management
* Environment-based Configuration
* Kubernetes Configuration Management
* Cloud-Native Architecture
* Distributed Systems Configuration
* Centralized Configuration Management


# 📄 License

MIT License
