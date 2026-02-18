# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Bookstore order management backend (pedidos) for "Libros Mario". Spring Boot 3.3.5 REST API with JWT authentication, built on Java 17 and Gradle.

## Build & Run Commands

```bash
# Build (skipping tests)
./gradlew clean build -x test

# Build with tests
./gradlew clean build

# Run locally (requires MySQL, see Local Development below)
./gradlew bootRun --args='--spring.profiles.active=local'

# Run tests (uses H2 in-memory, no MySQL needed)
./gradlew test

# Run a single test class
./gradlew test --tests "com.librosmario.pedidos.service.PedidoServiceTest"

# Docker build & run
docker build -t pedidos .
docker container run --rm -p 8080:8080 pedidos
```

The Gradle wrapper (`gradlew`) is included. No global Gradle install required.

## Local Development

Start MySQL with Docker, then run with the `local` profile:

```bash
docker run -d --name pedidos_database -p 1218:3306 \
  -e MYSQL_ROOT_PASSWORD=nat74tam -e MYSQL_DATABASE=librosmario mysql:5.7

# First time only: insert required roles
docker exec -i pedidos_database mysql -uroot -pnat74tam librosmario -e "
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER');
"
```

The `local` profile (`application-local.properties`) points MySQL to `localhost:1218` and sets the Luongo import path to `./data/`.

## Architecture

Layered Spring Boot application. Base package: `com.librosmario.pedidos`

```
controller/   → REST endpoints (6 controllers)
service/      → Business logic with JPA Specifications for dynamic queries
repository/   → Spring Data JPA repositories (also auto-exposed via Spring Data REST)
entity/       → JPA entities mapped to MySQL tables (prefixed: pe_, pi_, cl_, cg_, ed_, re_)
security/     → JWT authentication filter, token provider, UserDetailsService
batch/        → Spring Batch jobs for CSV catalog imports (Luongo distributor files)
config/       → Spring configs (CORS, repository ID exposure, security, auditing)
payload/      → Request/response DTOs
exception/    → Custom exceptions with @ControllerAdvice handlers
```

### Key API Routes

- `/api/auth/login`, `/api/auth/signup` — Authentication (JWT)
- `/pedidos/` — Order management (custom controller + Spring Data REST)
- `/clientes/` — Customer management
- `/catalogos/` — Book catalog with CSV import (`POST /catalogos/import`)
- `/pedidosADistribuidoras/` — Distributor order management
- `/api/user/me` — Current user info

All repositories are also auto-exposed as REST endpoints via Spring Data REST with HATEOAS at paths defined by `@RepositoryRestResource` (e.g., `/librospedidos/` for PedidoItem, `/pedidosdistribuidora/` for PedidoDistribuidora, `/distribuidoras/` for Distribuidora).

### Entity Relationships

```
Pedido (1) ──OneToOne──── (1) Cliente
   │
   └──OneToMany (cascade ALL)── (*) PedidoItem
                                      │
                                      ├──OneToOne── Distribuidora (pedidoAeditorial)
                                      └──ManyToMany── (*) PedidoDistribuidora ──ManyToOne── Distribuidora

Remito ──OneToMany── (*) RemitoItem ──ManyToOne── Catalogo
   └──ManyToOne── Distribuidora

User ──ManyToMany── Role (ROLE_ADMIN, ROLE_USER)
```

Entities extend `DateAudit` (createdAt/updatedAt) or `UserDateAudit` (adds createdBy/updatedBy) for automatic JPA auditing. JPA metamodel classes (`Cliente_`, `Pedido_`, `PedidoItem_`) are used in Specification queries.

### Database

- **Production/Dev**: MySQL on port 1218, database `librosmario`
- **Tests**: H2 in-memory (`jdbc:h2:mem:db`)
- **Schema management**: `spring.jpa.hibernate.ddl-auto=update` (auto-migration)
- **Manual schema additions**: `script_db.sql` (batch stats table, initial roles, PedidoItem column additions)

### Security

Stateless JWT authentication. Public endpoints: `/api/auth/**`, `/actuator/**`. All other endpoints require a valid JWT token in the `Authorization: Bearer <token>` header. Passwords hashed with BCrypt. Roles: `ROLE_ADMIN`, `ROLE_USER`.

### Dynamic Queries

Uses JPA Specification pattern (`JpaSpecificationExecutor`) for flexible search/filtering in `PedidoSpecifications`, `ClienteSpecifications`, `CatalogoSpecifications`. Search endpoints follow `findByAny` (OR logic) and `findByAll` (AND logic) naming conventions.

### Batch Processing

Spring Batch imports semicolon-delimited CSV files from Luongo distributor. Two CSV formats exist:
- **Old format** (CatalogoCSV / CatalogoProcessor): 14 fields, derives ISBN from barcode
- **New format** (NewCatalogoCSV / NewCatalogoProcessor): 7 fields, uses ISBN directly

Triggered via `POST /catalogos/import` multipart file upload. File upload path configured via `pedidos.luongo.path`. Chunk size: 100, skip limit: 100. `JobCompletionNotificationListener` tracks import statistics in `bt_batchstatistics` table.

### Logging

Uses Log4j2 (Logback excluded from dependencies). Local profile uses `log4j2-local.xml` config.

## Test Configuration

Tests use H2 in-memory database (configured in `src/test/resources/application.properties`). Test classes exist for service and repository layers under `src/test/java/com/librosmario/pedidos/`. Tests cover order creation, distributor order confirmation, and repository queries.

## Deployment

Multi-stage Docker build: Eclipse Temurin JDK 17 for build (Gradle wrapper downloads Gradle), Eclipse Temurin JRE 17 Alpine for runtime. The container loads external config from `/aplicaciones/pedidos/config/application.properties`. Jenkins pipeline defined in `Jenkinsfile`.
