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
