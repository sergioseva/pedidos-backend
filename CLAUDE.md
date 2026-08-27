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
- `/remitos/` — Remitos (returns to distributors and consignment movements)
- `/remitos/consignacion/estadocuenta` — Outstanding consignment balance per shop and title
- `/remitos/consignacion/liquidar` — Settle a shop's account
- `/remitos/{id}/recibo` — Issue or read the payment receipt of a sale remito
- `/comercios/` — Consignment points of sale
- `/ventas/` — Counter sales
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
   ├──ManyToOne── Distribuidora   (re_tipo = DEVOLUCION)
   ├──ManyToOne── Comercio        (re_tipo = CONSIGNACION | RETIRO | VENTA_CONSIGNACION)
   └──OneToOne ── Recibo          (only on VENTA_CONSIGNACION, optional)

Venta ──OneToMany── (*) VentaItem

User ──ManyToMany── Role (ROLE_ADMIN, ROLE_USER)
```

Entities extend `DateAudit` (createdAt/updatedAt) or `UserDateAudit` (adds createdBy/updatedBy) for automatic JPA auditing. JPA metamodel classes (`Cliente_`, `Pedido_`, `PedidoItem_`) are used in Specification queries.

### Remitos: four flows, one entity

`Remito` covers four business flows, told apart by `re_tipo`:

| `re_tipo` | Meaning | Destinatario |
|---|---|---|
| `DEVOLUCION` | books returned to a distributor | `re_distribuidora_ed` |
| `CONSIGNACION` | books delivered on consignment to a point of sale | `re_comercio_cm` |
| `RETIRO` | unsold copies coming back from the shop | `re_comercio_cm` |
| `VENTA_CONSIGNACION` | copies the shop sold and now owes for | `re_comercio_cm` |

Exactly one destinatario FK is set; `RemitoService.normalizarDestinatario` nulls the other. A null `re_tipo` counts as `DEVOLUCION` (rows predating the V5 migration).

Because the destinatario is nullable, `RemitoSpecifications` **must** use `JoinType.LEFT`. An inner join drops half the remitos from the whole query, not just from that predicate — the OR-based `findByAny` would silently lose every consignment remito.

### Consignment settlement

There is **no inventory anywhere in this system** — `Catalogo` is the distributor's price list and `Venta` deducts nothing. The consignment balance is the only stock-like ledger, and it is *derived*, never stored:

```
saldo en la calle = CONSIGNACION − RETIRO − VENTA_CONSIGNACION
```

That signed aggregation is `RemitoItemRepository.estadoCuentaConsignacion`, whose `HAVING` drops settled titles. Its date filter applies **only to deliveries**: bounding the deductions too would exclude later retiros/ventas and inflate the balance.

`LiquidacionConsignacionService.liquidar` closes a shop's account in one all-or-nothing transaction. It re-reads the balance from the database (never trusting the screen, which may be stale) and aggregates **both sides by title key** before comparing, so two rows of the same book cannot each pass individually and overdraw together. The key is ISBN **and** title, matching the SQL grouping: keyed on ISBN alone, two different books that share an ISBN share a balance — not hypothetical, since half the catalog has its ISBN stored in scientific notation (`9.78987E+12`) and unrelated titles collide.

`Comercio.cm_comision` is the shop's cut (0-100). **Every new field on `Comercio` must also be copied in `ComercioService.update`** — the service predates the column and silently dropped it, so the form saved without complaint and every shop stayed at no commission, quietly billing cover price. The commission is copied onto `re_comision` at settlement time and `Recibo.rc_monto` is frozen likewise: renegotiating a percentage must never rewrite the money on an already-signed document.

A sale remito has at most one recibo (`uk_rc_remito`); payment is optional and can be issued later via `POST /remitos/{id}/recibo`. `Remito.recibo` is mapped as the inverse side so every remito carries its payment state (`pagado`) — that is what makes unpaid sales findable at all. `emitirRecibo` sets **both sides** of the association by hand: saving only the owning side leaves an already-loaded remito reporting itself unpaid.

Consignment sales deliberately do **not** touch `ve_venta` — the Ventas section stays the till.

Prices can be updated after delivery. The new value goes to `ri_precio_actual`, **never over `ri_precio`**: the delivery remito was already signed and has to keep saying what price it went out at. The balance and the settlement use `coalesce(ri_precio_actual, ri_precio)`. `POST /remitos/consignacion/{id}/precios` pulls current prices from the catalog by ISBN and reports how many titles found no match — two titles sharing an ISBN get the same price, which is the limit of that shortcut and the reason manual editing has to stay.

The balance query orders by `TRIM(ri_nombre_libro)`: a good part of the catalog has titles stored with leading spaces, and without trimming those sort ahead of everything else and the listing looks unsorted. Note the `GROUP BY` still uses the raw title, so the same book delivered under both spellings shows as two lines — a leftover of the corrupted April import, same root cause as the scientific-notation ISBNs.

`GET /remitos/consignacion/estadocuenta/reporte` returns the same detail as an `.xlsx` (`ConsignacionReporteExcel`, built with the POI already on the classpath for catalog import).

### Database

- **Production/Dev**: MySQL 8.0 on port 1218, database `librosmario`
- **Tests**: H2 in-memory (`jdbc:h2:mem:db`), Flyway disabled — schema from Hibernate, seed rows from `src/test/resources/data.sql`
- **Schema management**: `spring.jpa.hibernate.ddl-auto=update` plus **Flyway** migrations in `src/main/resources/db/migration` for what `ddl-auto` cannot do (type changes, backfills, drops)
- **Manual schema additions**: `script_db.sql` (batch stats table, initial roles, PedidoItem column additions)

Migrations to date: V2 catalog code column type · V3 Spring Batch v4→v5 tables · V4 PedidoDistribuidora join table → direct FK · V5 consignment remitos (`cm_comercio`, `re_tipo`, `re_comercio_cm`) · V6 settlement (`cm_comision`, `re_comision`, `rc_recibo`).

Two rules that cost real debugging time:

- **Flyway runs before Hibernate DDL**, so a migration that backfills a new column must add the column itself rather than leaving it to `ddl-auto`.
- **`CREATE TABLE IF NOT EXISTS` is a trap for the same race.** If `ddl-auto` created the table first, the migration finds it there, does nothing, and records itself as successful — so the table keeps whatever Hibernate inferred. That is how `br_borrador` ended up with a `TINYTEXT` content column and no unique key while V8 said `MEDIUMTEXT`; it surfaced in production two days later. Prefer a migration that fails loudly, and when a table may already exist, follow up with explicit `MODIFY`/`ADD CONSTRAINT` statements (see V9) rather than trusting the `CREATE`.
- **Column types cannot be verified by the test suite.** Tests run on H2 with a Hibernate-generated schema, so a mapping that is wrong on MySQL can pass: `@Lob` on a `String` without an explicit `length` gives an unbounded CLOB on H2 and a 255-byte `TINYTEXT` on MySQL. Always set `length` explicitly on text columns, and assert the mapping (see `BorradorRemitoTest.laColumnaDelContenidoEsGrande`).
- **Guard every `ADD COLUMN` behind an `information_schema` check** (see the `pedidos_add_column` procedure in V6). MySQL has no `ADD COLUMN IF NOT EXISTS`, and in development `ddl-auto` creates the column as soon as the entity exists. If that happens before the migration is written, the bare `ALTER` fails, Flyway records the version as failed, and **the application refuses to start** until the row is deleted by hand. This happened while V6 was being written.

### Security

Stateless JWT authentication. Public endpoints: `/api/auth/**`, `/actuator/**`. All other endpoints require a valid JWT token in the `Authorization: Bearer <token>` header. Passwords hashed with BCrypt. Roles: `ROLE_ADMIN`, `ROLE_USER`.

### Dynamic Queries

Uses JPA Specification pattern (`JpaSpecificationExecutor`) for flexible search/filtering in `PedidoSpecifications`, `ClienteSpecifications`, `CatalogoSpecifications`, `RemitoSpecifications`. Search endpoints follow `findByAny` (OR logic) and `findByAll` (AND logic) naming conventions. `/remitos/search/*` accepts `tipo` as a comma-separated list, so the consignment screen can ask for a shop's three movement types at once.

### Batch Processing

Spring Batch imports semicolon-delimited CSV files from Luongo distributor. Two CSV formats exist:
- **Old format** (CatalogoCSV / CatalogoProcessor): 14 fields, derives ISBN from barcode
- **New format** (NewCatalogoCSV / NewCatalogoProcessor): 7 fields, uses ISBN directly

Triggered via `POST /catalogos/import` multipart file upload. File upload path configured via `pedidos.luongo.path`. Chunk size: 100, skip limit: 100. `JobCompletionNotificationListener` tracks import statistics in `bt_batchstatistics` table.

### Remito drafts

`BorradorRemito` (`br_borrador`, migration V8) holds a remito being loaded, one row per user and `tipo`, upserted by the autosave. The content is stored **opaque**, exactly as the screen sends it, so adding a field to the items does not force migrating half-loaded drafts.

It deliberately lives in its own table rather than as a state on `Remito`: a draft sharing the remito table would have to be excluded from every query, and missing one would contaminate the consignment balance.

### Version reporting

`/actuator/info` is public on purpose — it is the only way to tell which version an environment is running. The chain is `release.yml` → `--build-arg BUILD_VERSION` → `ENV INFO_APP_VERSION` in the Dockerfile → `info.app.version` here; outside the image it falls back to `dev`.

Publishing `info.*` needs `management.info.env.enabled=true`: since Spring Boot 2.6 that contributor is off by default, so the properties can be defined and the endpoint still answers `{}` — which is exactly what happened, unnoticed, because an endpoint going empty breaks no build.

`ActuatorInfoTest` asserts against the properties **file** rather than the running context: `src/test/resources/application.properties` shadows the main one (same classpath location), so no context-based test can see the configuration that actually ships.

### Logging

Uses Log4j2 (Logback excluded from dependencies). Local profile uses `log4j2-local.xml` config.

## Test Configuration

Tests use H2 in-memory database (configured in `src/test/resources/application.properties`). Test classes exist for service and repository layers under `src/test/java/com/librosmario/pedidos/`. Tests cover order creation, distributor order confirmation, and repository queries.

## Deployment

Multi-stage Docker build: Eclipse Temurin JDK 17 for build (Gradle wrapper downloads Gradle), Eclipse Temurin JRE 17 Alpine for runtime. The container loads external config from `/aplicaciones/pedidos/config/application.properties`. Jenkins pipeline defined in `Jenkinsfile`.
