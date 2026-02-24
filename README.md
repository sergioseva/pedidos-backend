# pedidos-backend

## Ejecutar localmente

### 1. Levantar la base de datos MySQL con Docker

```bash
docker run -d \
  --name pedidos_database \
  -p 1218:3306 \
  -e MYSQL_ROOT_PASSWORD=nat74tam \
  -e MYSQL_DATABASE=librosmario \
  mysql:5.7
```

### 2. Ejecutar la aplicacion con el perfil local

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

La API estara disponible en http://localhost:8080

### 3. Insertar roles iniciales (solo la primera vez)

```bash
docker exec -i pedidos_database mysql -uroot -pnat74tam librosmario -e "
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER');
"
```

## Build y deploy con Docker

```bash
./gradlew clean build
docker build -t pedidos .
docker container run --rm -p 8080:8080 pedidos
```

## CI/CD

Automated via GitHub Actions (`.github/workflows/release.yml`):

- **Push to `master`** → runs tests → builds and pushes `ghcr.io/sergioseva/pedidos-backend:master` → deploys to staging (`test.librosmario.store`)
- **Push tag `v*`** → runs tests → builds and pushes `:v1.0.0` + `:latest` → deploys to production (`pedidos.librosmario.store`)

### Rollback

Go to **Actions > Rollback > Run workflow**, pick the version tag (e.g. `v1.0.0`) and environment.

### Version check

The running version is available at the actuator info endpoint:

```bash
curl https://pedidos.librosmario.store/api/actuator/info
# {"app":{"version":"v1.0.0"}}
```

On staging the version shows `master`.
