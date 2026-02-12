# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Bookstore order management backend (pedidos) for "Libros Mario". Spring Boot 2.1.6 REST API with JWT authentication, built on Java 8 and Maven.

## Build & Run Commands

```bash
# Build (skipping tests)
./mvnw clean package -DskipTests

# Build with tests
./mvnw clean package

# Run locally
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=PedidoServiceTest

# Docker build & run
docker build -t pedidos .
docker container run --rm -p 8080:8080 pedidos
```

The Maven wrapper (`mvnw`) is included. No global Maven install required.

## Architecture

Layered Spring Boot application following standard patterns:

```
controller/   → REST endpoints (6 controllers)
service/      → Business logic with JPA Specifications for dynamic queries
repository/   → Spring Data JPA repositories (also auto-exposed via Spring Data REST)
entity/       → JPA entities mapped to MySQL tables (prefixed: pe_, pi_, cl_, cg_, ed_, re_)
security/     → JWT authentication filter, token provider, UserDetailsService
batch/        → Spring Batch jobs for CSV catalog imports (Luongo distributor files)
config/       → Spring configs (CORS, repository ID exposure, security)
payload/      → Request/response DTOs
exception/    → Custom exceptions with @ControllerAdvice handlers
```

Base package: `com.librosmario.pedidos`

### Key API Routes

- `/api/auth/login`, `/api/auth/signup` — Authentication (JWT)
- `/pedidos/` — Order management (custom controllers + Spring Data REST)
- `/clientes/` — Customer management
- `/catalogos/` — Book catalog with CSV import (`POST /catalogos/import`)
- `/pedidosADistribuidoras/` — Distributor order management

All repositories are also auto-exposed as REST endpoints via Spring Data REST with HATEOAS.

### Database

- **Production/Dev**: MySQL on port 1218, database `librosmario`
- **Tests**: H2 in-memory (`jdbc:h2:mem:db`)
- **Schema management**: `spring.jpa.hibernate.ddl-auto=update` (auto-migration)
- **Auditing**: Entities extend `DateAudit`/`UserDateAudit` for automatic `createdAt`/`updatedAt`/`createdBy` fields

### Security

Stateless JWT authentication. Public endpoints: `/api/auth/**`, `/actuator/**`. All other endpoints require a valid JWT token in the `Authorization` header. Passwords hashed with BCrypt. Roles: `ROLE_ADMIN`, `ROLE_USER`.

### Dynamic Queries

Uses JPA Specification pattern (`JpaSpecificationExecutor`) for flexible search/filtering in `PedidoSpecifications`, `ClienteSpecifications`, `CatalogoSpecifications`. Search endpoints follow `findByAny` (OR logic) and `findByAll` (AND logic) naming conventions.

### Batch Processing

Spring Batch imports semicolon-delimited CSV files from Luongo distributor. File upload path configured via `pedidos.luongo.path`. Batch schema auto-created (`spring.batch.initialize-schema=ALWAYS`), jobs disabled on startup (`spring.batch.job.enabled=false`) and triggered via controller.

## Test Configuration

Tests use H2 in-memory database (configured in `src/test/resources/application.properties`). Test classes exist for service and repository layers under `src/test/java/com/librosmario/pedidos/`.

## Deployment

Multi-stage Docker build: Maven 3.5.2 + JDK 8 for build, OpenJDK 8 JRE Alpine for runtime. The container loads external config from `/aplicaciones/pedidos/config/application.properties`. Jenkins pipeline defined in `Jenkinsfile`. Database setup script: `script_db.sql`.
