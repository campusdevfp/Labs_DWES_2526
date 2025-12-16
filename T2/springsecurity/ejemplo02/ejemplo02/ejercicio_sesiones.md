# Ejercicio Práctico 1: Gestión de Sesiones en Spring Security

## Objetivo
En este ejercicio, practicarás los conceptos básicos de gestión de sesiones en una aplicación Spring Security. Aprenderás a realizar login, obtener información de la sesión, guardar atributos personalizados en la sesión y cerrar sesión (logout). Utilizarás herramientas como curl o Postman para interactuar con la API.

## Prerrequisitos
- Tener el proyecto Spring Security corriendo en `http://localhost:8080`.
- Usuarios de prueba disponibles: `user/password` (ROLE_USER) y `admin/admin` (ROLE_ADMIN).
- Herramientas: curl (línea de comandos) o Postman.

## Instrucciones

### 1. Realizar Login
- **Objetivo**: Iniciar sesión y obtener una cookie de sesión.
- **Endpoint**: `POST /api/auth/login`
- **Body (JSON)**:
  ```json
  {
    "username": "user",
    "password": "password"
  }
  ```
- **Tarea**:
  - Usa curl para hacer login y guarda la cookie en un archivo llamado `cookies.txt`.
  - Comando sugerido:
    ```bash
    curl -c cookies.txt -X POST -H "Content-Type: application/json" -d '{"username":"user","password":"password"}' http://localhost:8080/api/auth/login
    ```
  - Verifica la respuesta: Deberías recibir un mensaje de éxito con el `sessionId`.

### 2. Obtener Información de la Sesión
- **Objetivo**: Ver detalles de la sesión actual, como ID, usuario autenticado, roles y tiempos.
- **Endpoint**: `GET /session/info`
- **Tarea**:
  - Usa la cookie guardada para hacer una petición GET.
  - Comando sugerido:
    ```bash
    curl -b cookies.txt http://localhost:8080/session/info
    ```
  - Analiza la respuesta: ¿Qué información ves? ¿Cómo se relaciona con la autenticación?

### 3. Guardar Atributos en la Sesión
- **Objetivo**: Almacenar datos personalizados en la sesión (ej. preferencias de usuario o carrito).
- **Endpoints**:
  - Guardar: `POST /session/attribute?key=miDato&value=hola`
  - Recuperar: `GET /session/attribute/miDato`
- **Tarea**:
  - Guarda un atributo llamado `idioma` con valor `es`.
  - Comando sugerido:
    ```bash
    curl -b cookies.txt -X POST "http://localhost:8080/session/attribute?key=idioma&value=es"
    ```
  - Recupera el atributo.
  - Comando sugerido:
    ```bash
    curl -b cookies.txt http://localhost:8080/session/attribute/idioma
    ```
  - Prueba con el carrito: Agrega un producto usando `POST /ejemplos/carrito/agregar?producto=Laptop&cantidad=1` y verifica con `GET /ejemplos/carrito/ver`.

### 4. Realizar Logout
- **Objetivo**: Cerrar la sesión y limpiar el contexto de seguridad.
- **Endpoint**: `POST /session/logout`
- **Tarea**:
  - Haz logout usando la cookie.
  - Comando sugerido:
    ```bash
    curl -b cookies.txt -X POST http://localhost:8080/session/logout
    ```
  - Intenta acceder a un endpoint protegido después del logout (ej. `GET /session/info`). ¿Qué sucede? ¿Por qué?

## Preguntas de Reflexión
1. ¿Qué es una sesión HTTP y por qué es importante en aplicaciones web?
2. ¿Cómo se diferencia el `SecurityContext` de los atributos de sesión?
3. ¿Qué pasa con los datos de sesión al hacer logout? ¿Se pierden permanentemente?
4. ¿Cómo podrías implementar un carrito de compras persistente (que sobreviva al reinicio del servidor)?

## Solución Esperada
- Después de completar los pasos, deberías poder:
  - Autenticarte y mantener el estado entre peticiones.
  - Ver y modificar datos en la sesión.
  - Cerrar sesión correctamente, invalidando la cookie.

Si encuentras errores, revisa los logs del servidor o verifica que el proyecto esté corriendo correctamente.
