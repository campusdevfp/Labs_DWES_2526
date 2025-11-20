# Lab3: API REST de Gestión de Productos

## Descripción

Desarrolla una API REST completa para gestionar un catálogo de productos utilizando Spring Boot, integrando servicios, DTOs, validación, manejo de excepciones, JPA con MySQL en Docker, y testing unitario e integración.

---

## Objetivos

- Implementar una arquitectura en capas con Spring Boot (Controlador → Servicio → Repositorio)
- Utilizar DTOs para separar la capa de presentación de la capa de persistencia
- Aplicar validaciones con Bean Validation
- Manejar excepciones personalizadas y respuestas HTTP adecuadas
- Configurar y usar una base de datos MySQL con Docker
- Escribir tests unitarios con JUnit 5 y Mockito
- Realizar tests de integración con `@SpringBootTest`

---

## Requisitos Técnicos

- **IDE**: IntelliJ IDEA
- **Docker Desktop** instalado y funcionando
- **JDK**: 17 o superior
- **Build Tool**: Maven
- **Base de datos**: MySQL 8.0 en Docker

---

## Funcionalidades Requeridas

### 1. Modelo de Datos

Crea una entidad `Producto` con los siguientes atributos:

- `id` (Long, autogenerado)
- `nombre` (String, obligatorio, máx. 100 caracteres)
- `descripcion` (String, opcional, máx. 500 caracteres)
- `precio` (Double, obligatorio, no negativo)
- `stock` (Integer, obligatorio, no negativo)
- `categoria` (String, opcional, máx. 50 caracteres)
- `imagenUrl` (String, opcional, URL válida)
- `fechaCreacion` (LocalDateTime, autogenerado)
- `fechaActualizacion` (LocalDateTime, autogenerado)
- `activo` (Boolean, por defecto true)

### 2. Configuración de Base de Datos

- Levanta un contenedor Docker con MySQL 8.0
- Base de datos: `productos_db`
- Usuario: `root`
- Contraseña: `root1234`
- Puerto: `3306`

### 3. DTOs

Crea dos DTOs:

- **ProductoRequestDto**: Para crear y actualizar productos (con validaciones)
- **ProductoResponseDto**: Para respuestas de la API

### 4. Capa de Repositorio

Implementa un repositorio con los siguientes métodos personalizados:

- Buscar productos activos
- Buscar por categoría
- Buscar por nombre (case insensitive, contiene)
- Buscar por rango de precios

### 5. Capa de Servicio

Implementa los siguientes métodos:

- `findAll()`: Obtener todos los productos
- `findById(Long id)`: Obtener un producto por ID
- `findByCategoria(String categoria)`: Buscar por categoría
- `findByNombre(String nombre)`: Buscar por nombre
- `save(ProductoRequestDto dto)`: Crear un nuevo producto
- `update(Long id, ProductoRequestDto dto)`: Actualizar un producto
- `deleteById(Long id)`: Eliminar un producto
- `desactivar(Long id)`: Desactivar un producto (soft delete)
- `updateStock(Long id, Integer cantidad)`: Actualizar stock (puede ser + o -)

### 6. Controlador REST

Implementa los siguientes endpoints:

| Método | Endpoint                                        | Descripción                 |
| ------ | ----------------------------------------------- | --------------------------- |
| GET    | `/api/productos`                                | Obtener todos los productos |
| GET    | `/api/productos/{id}`                           | Obtener producto por ID     |
| GET    | `/api/productos/categoria/{categoria}`          | Buscar por categoría        |
| GET    | `/api/productos/buscar?nombre={nombre}`         | Buscar por nombre           |
| POST   | `/api/productos`                                | Crear producto              |
| PUT    | `/api/productos/{id}`                           | Actualizar producto         |
| DELETE | `/api/productos/{id}`                           | Eliminar producto           |
| PATCH  | `/api/productos/{id}/desactivar`                | Desactivar producto         |
| PATCH  | `/api/productos/{id}/stock?cantidad={cantidad}` | Actualizar stock            |

### 7. Manejo de Excepciones

Implementa:

- Excepciones personalizadas: `ProductoNotFoundException`, `ProductoBadRequestException`
- Manejador global de excepciones (`@RestControllerAdvice`)
- Validación de errores de Bean Validation

### 8. Validaciones

Aplica las siguientes validaciones en `ProductoRequestDto`:

- Nombre: obligatorio, entre 3 y 100 caracteres
- Descripción: máximo 500 caracteres
- Precio: obligatorio, no negativo
- Stock: obligatorio, no negativo
- Categoría: máximo 50 caracteres
- ImagenUrl: formato de URL válido

### 9. Testing

#### Tests Unitarios del Servicio (con Mockito)

- Test para `findById()` cuando el producto existe
- Test para `findById()` cuando el producto no existe (debe lanzar excepción)
- Test para `save()` creando un nuevo producto
- Test para `deleteById()` eliminando un producto existente

#### Tests de Integración del Repositorio

- Test para `findAll()`
- Test para `findById()`
- Test para `findByCategoria()`
- Test para `findByNombreContainingIgnoreCase()`
- Test para `save()`
- Test para `delete()`

#### Tests de Integración del Controlador (con MockMvc)

- Test GET `/api/productos` retorna lista de productos
- Test GET `/api/productos/{id}` retorna producto cuando existe
- Test GET `/api/productos/{id}` retorna 404 cuando no existe
- Test POST `/api/productos` crea un nuevo producto (201 Created)
- Test POST `/api/productos` retorna 400 cuando los datos son inválidos
- Test PUT `/api/productos/{id}` actualiza un producto
- Test DELETE `/api/productos/{id}` elimina un producto (204 No Content)

---

## Entregables

### 1. Código Fuente

Sube el proyecto completo a un repositorio Git (GitHub/GitLab) con:

- Estructura de paquetes organizada
- Código limpio y bien documentado
- Archivo `pom.xml` con todas las dependencias

### 2. Documentación

Incluye un archivo `README.md` en la raíz del proyecto con:

- **Descripción del proyecto**
- **Instrucciones para levantar el contenedor Docker de MySQL**
- **Cómo ejecutar la aplicación**
- **Lista completa de endpoints con ejemplos de uso (cURL o Postman)**
- **Instrucciones para ejecutar los tests**

### 3. Capturas de Pantalla

Incluye capturas que demuestren:

- **Postman o cURL**: Pruebas de todos los endpoints principales
- **IntelliJ IDEA**: Ejecución exitosa de los tests (todos en verde)
- **MySQL Workbench o consola**: Datos insertados en la base de datos

---

## Fecha de Entrega

**[A definir por el profesor]**

---

## Recursos de Ayuda

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://site.mockito.org/)
- [Docker MySQL Image](https://hub.docker.com/_/mysql)
- [Bean Validation Reference](https://beanvalidation.org/2.0/spec/)

---

## Notas Importantes

- El proyecto debe compilar sin errores
- Todos los tests deben pasar
- El código debe seguir las convenciones de Java
- No olvides añadir el archivo `.gitignore` para excluir carpetas como `target/`, `.idea/`, etc.
- La aplicación debe arrancar correctamente en `http://localhost:8080`

---

¡Éxito con tu práctica! 💻🚀