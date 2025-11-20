# Lab3: API REST de Gestión de Usuarios

## Descripción

Desarrolla una API REST completa para gestionar usuarios utilizando Spring Boot, integrando servicios, DTOs, validación, manejo de excepciones, JPA con MySQL en Docker, y testing unitario e integración.

## 🚀 Ejecución rápida

1. Levantar la base de datos MySQL con Docker Compose:

```bash
docker compose up -d
```

2. Ejecutar la aplicación Spring Boot en tu IDE (por ejemplo, IntelliJ IDEA o Eclipse).

Accede a la aplicación en: [http://localhost:8080](http://localhost:8080)

Base de datos MySQL en el puerto **3306** con usuario `root` / password `root1234`.

- Conectar a MySQL:

```sql
mysql -h 127.0.0.1 -u root -p
```

- Detener la base de datos:

```bash
docker compose down
```

- Logs de MySQL:

```bash
docker compose logs -f mysql
```

- Verificar con curl:

```bash
curl http://localhost:8080/api/usuarios
```

## 🧹 Limpieza de contenedores

Para purgar y limpiar los contenedores, imágenes y volúmenes no utilizados:

- Detener y eliminar contenedores del proyecto:

```bash
docker compose down -v
```

- Purgar contenedores detenidos:

```bash
docker container prune -f
```

- Purgar imágenes no utilizadas:

```bash
docker image prune -a -f
```

- Purgar volúmenes no utilizados:

```bash
docker volume prune -f
```

- Limpieza completa del sistema Docker:

```bash
docker system prune -a -f
```

## 🔄 Cambiar el nombre de la base de datos

Para cambiar el nombre de la base de datos (por defecto `ecommerce_db`):

1. **Actualizar `docker-compose.yml`**:
   - Cambia el valor de `MYSQL_DATABASE` en el servicio `mysql` al nuevo nombre, por ejemplo `MYSQL_DATABASE: nuevadb`.

2. **Actualizar `src/main/resources/application.properties`**:
   - Cambia la URL en `spring.datasource.url` para usar el nuevo nombre, por ejemplo `jdbc:mysql://localhost:3306/nuevadb?useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true`.

3. **Actualizar el script de inicialización `mysql/init/01-init.sql`**:
   - Cambia `CREATE DATABASE IF NOT EXISTS ecommerce_db;` a `CREATE DATABASE IF NOT EXISTS nuevadb;`.
   - Cambia `USE ecommerce_db;` a `USE nuevadb;`.

4. **Reiniciar los contenedores**:
   - Detén y elimina los contenedores existentes: `docker compose down -v`.
   - Levanta nuevamente: `docker compose up -d`.

Nota: Si ya hay datos en la base de datos, asegúrate de hacer una copia de seguridad antes de cambiar el nombre, ya que esto creará una nueva base de datos.

## 📋 Endpoints de la API

| Método | Endpoint                          | Descripción              |
| ------ | --------------------------------- | ------------------------ |
| GET    | `/api/usuarios`                   | Obtener todos los usuarios |
| GET    | `/api/usuarios/{id}`              | Obtener usuario por ID   |
| POST   | `/api/usuarios`                   | Crear usuario            |
| PUT    | `/api/usuarios/{id}`              | Actualizar usuario       |
| DELETE | `/api/usuarios/{id}`              | Eliminar usuario         |
| PATCH  | `/api/usuarios/{id}/desactivar`   | Desactivar usuario       |

## 🧪 Ejecutar Tests

Para ejecutar los tests unitarios y de integración:

```bash
mvn test
```

Para ver el reporte de cobertura de JaCoCo:

```bash
mvn jacoco:report
```

El reporte estará en `target/site/jacoco/index.html`.

## 📦 Dependencias

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- MySQL Connector/J
- Spring Boot Starter Validation
- Lombok
- Spring Boot Starter Test
- JaCoCo para cobertura de código
