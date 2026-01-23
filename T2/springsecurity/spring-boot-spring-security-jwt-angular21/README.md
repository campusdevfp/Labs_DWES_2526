# Spring Boot JWT Authentication with Spring Security

## 1. Explicación de JWT y Sesión en Spring Boot y Spring Security

**JWT (JSON Web Token)** es un estándar abierto (RFC 7519) que define una forma compacta y autónoma de transmitir información entre dos partes como un objeto JSON. En el contexto de Spring Boot y Spring Security, los JWT se utilizan para implementar autenticación sin estado (stateless authentication). Esto significa que no se almacena información de sesión en el servidor; en su lugar, toda la información necesaria para autenticar a un usuario se incluye en el propio token.

Un JWT consta de tres partes:
- **Header**: Contiene el tipo de token (JWT) y el algoritmo de firma.
- **Payload**: Contiene las declaraciones (claims), que son datos como el ID del usuario, roles, etc.
- **Signature**: Es una firma generada utilizando un secreto y el algoritmo especificado en el header. Garantiza la integridad del token.

[Documentación oficial de JWT](https://jwt.io/introduction/)

En Spring Security, los JWT se utilizan para autenticar solicitudes. Cuando un usuario inicia sesión, el servidor genera un JWT y lo envía al cliente. El cliente almacena este token (por ejemplo, en el almacenamiento local o en una cookie) y lo envía con cada solicitud posterior en el encabezado `Authorization`.

## 2. Registro e inicio de sesión de Spring Boot con flujo de autenticación JWT

El diagrama muestra el flujo de cómo implementamos el proceso de registro de usuario, inicio de sesión de usuario y autorización.

![spring-boot-authentication-jwt-spring-security-flow](https://bezkoder.com/wp-content/uploads/2019/10/spring-boot-authentication-jwt-spring-security-flow.png)

Se debe agregar un JWT legal al encabezado de autorización HTTP si el Cliente accede a recursos protegidos.

Necesitará implementar el token de actualización:

![spring-boot-refresh-token-jwt-example-flow](https://bezkoder.com/wp-content/uploads/2021/04/spring-boot-refresh-token-jwt-example-flow.png)

- Veremos más adelante un ejemplo de token de actualización de Spring Boot con JWT

- Para actulizar token también puedes utilizar cookies HttpOnly en su lugar

> HttpOnly es un atributo de una cookie que indica al navegador que la cookie no debe ser accesible desde JavaScript (por ejemplo, document.cookie). 
> Se controla solo en el cliente (navegador) y se pone por el servidor en la cabecera Set-Cookie.
> Uso típico: almacenar el refresh token en una cookie HttpOnly y el access token en memoria; el servidor lee la cookie en los endpoints de refresh.

## Arquitectura del servidor Spring Boot con Spring Security

Puede tener una descripción general de nuestro servidor Spring Boot con el siguiente diagrama:

![autenticación de arranque de resorte-arquitectura de seguridad de resorte](https://www.bezkoder.com/wp-content/uploads/spring-boot-authentication-spring-security-architecture.png)

Ahora lo explicaré brevemente.

**SPRING SECURITY**

– `WebSecurityConfig` es el quid de nuestra implementación de seguridad. Configura cors, csrf, gestión de sesiones, reglas para recursos protegidos. También podemos ampliar y personalizar la configuración predeterminada que contiene los elementos siguientes.  
(`WebSecurityConfigurerAdapter` está obsoleto desde Spring 2.7.0, puedes consultar el código fuente para actualizarlo. Más detalles en:  
[WebSecurityConfigurerAdapter obsoleto en Spring Boot](https://www.bezkoder.com/websecurityconfigureradapter-deprecated-spring-boot/))

– [UserDetailsService](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#tech-userdetailsservice) La interfaz tiene un método para cargar al usuario mediante _nombre de usuario_ y devuelve un `UserDetails` objeto que Spring Security puede utilizar para autenticación y validación.

– `UserDetails` contiene la información necesaria (como: nombre de usuario, contraseña, autoridades) para crear un objeto de autenticación.

– [UsernamePasswordAuthenticationToken](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/UsernamePasswordAuthenticationToken.html) obtiene {nombre de usuario, contraseña} de la solicitud de inicio de sesión, `AuthenticationManager` lo utilizará para autenticar una cuenta de inicio de sesión.

– [AuthenticationManager](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#core-services-authentication-manager) tiene un `DaoAuthenticationProvider` (con ayuda de `UserDetailsService` & `PasswordEncoder`) para validar `UsernamePasswordAuthenticationToken` objeto. Si tiene éxito, `AuthenticationManager` devuelve un objeto de autenticación completamente poblado (incluidas las autoridades otorgadas).

– [OncePerRequestFilter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html) realiza una única ejecución para cada solicitud a nuestra API. Proporciona un `doFilterInternal()` método que implementaremos analizando y validando JWT, cargando detalles del usuario (usando `UserDetailsService`), comprobando la Autorización (usando `UsernamePasswordAuthenticationToken`).

– [AuthenticationEntryPoint](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/AuthenticationEntryPoint.html) detectará un error de autenticación.

**Repositorio** contiene `UserRepository` & `RoleRepository` Para trabajar con la base de datos, se importará a **Controlador**.

**Controlador** recibe y maneja la solicitud después de que fue filtrada por `OncePerRequestFilter`.

– `AuthController` maneja solicitudes de registro/inicio de sesión

– `TestController` tiene acceso a métodos de recursos protegidos con validaciones basadas en roles.

## 3. Estructura del proyecto

Esta es la estructura de carpetas y archivos para nuestra aplicación Spring Boot:

![estructura-del-proyecto-de-seguridad-de-arranque-de-resorte](https://bezkoder.com/wp-content/uploads/2019/10/spring-boot-authentication-spring-security-project-structure.png)

**seguridad**: configuramos Spring Security e implementamos objetos de seguridad aquí.

-   `WebSecurityConfig`

(`WebSecurityConfigurerAdapter` está obsoleto desde Spring 2.7.0, puedes consultar el código fuente para actualizarlo. Más detalles en:  
[WebSecurityConfigurerAdapter obsoleto en Spring Boot](https://www.bezkoder.com/websecurityconfigureradapter-deprecated-spring-boot/))

-   `UserDetailsServiceImpl`implementa `UserDetailsService`
-   `UserDetailsImpl`implementa `UserDetails`
-   `AuthEntryPointJwt`implementa `AuthenticationEntryPoint`
-   `AuthTokenFilter`extiende `OncePerRequestFilter`
-   `JwtUtils`proporciona métodos para generar, analizar y validar JWT

**controladores** manejar solicitudes de registro/inicio de sesión y solicitudes autorizadas.

-   `AuthController`: @PostMapping(‘/signin’), @PostMapping(‘/signup’)
-   `TestController`: @GetMapping(‘/api/test/all’), @GetMapping(‘/api/test/[rol]’)

**repositorio** tiene interfaces que extienden Spring Data JPA `JpaRepository` para interactuar con la base de datos.

-   `UserRepository`extiende `JpaRepository<User, Long>`
-   `RoleRepository`extiende `JpaRepository<Role, Long>`

**modelos** define dos modelos principales para la autenticación (`User`) & Autorización (`Role`). Tienen una relación de muchos a muchos.

-   `User`: id, nombre de usuario, correo electrónico, contraseña, roles
-   `Role`: id, nombre

**carga útil** define clases para objetos de Solicitud y Respuesta

También tenemos **aplicación.propiedades** para configurar Spring Datasource, Spring Data JPA y propiedades de la aplicación (como la cadena secreta JWT o el tiempo de vencimiento del token).

## 4. Explicación de los Endpoints del Proyecto

### Endpoints
- **POST /api/auth/signup**: Registra un nuevo usuario.
- **POST /api/auth/signin**: Autentica al usuario y devuelve un JWT.
- **POST /api/auth/revoke-token**: Revoca un token JWT.
- **POST /api/auth/refresh-token**: Genera un nuevo token de acceso utilizando un `refreshToken`.
- **GET /api/test/all**: Endpoint público accesible sin autenticación.
- **GET /api/test/user**: Endpoint protegido accesible solo para usuarios autenticados.
- **GET /api/test/mod**: Endpoint protegido accesible solo para usuarios con rol `MODERATOR`.
- **GET /api/test/admin**: Endpoint protegido accesible solo para usuarios con rol `ADMIN`.

### Clases Principales
- **AuthController**: Controlador principal para manejar la autenticación y autorización.
- **JwtUtils**: Clase para generar y validar tokens JWT.
- **AuthTokenFilter**: Filtro que intercepta las solicitudes para validar los tokens JWT.
- **UserDetailsServiceImpl**: Implementación de `UserDetailsService` para cargar los detalles del usuario desde la base de datos.
- **RefreshTokenService**: Servicio para manejar la lógica de los tokens de actualización.
- **RoleRepository y UserRepository**: Repositorios para interactuar con la base de datos.


---
## 5. Infraestructura y Despliegue del Proyecto

### Requisitos Previos
- **Java 17** o superior.
- **Maven** para la gestión de dependencias.
- **Docker** (opcional) para ejecutar el proyecto en un contenedor.

### Pasos para Desplegar
1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-repositorio/spring-boot-spring-security-jwt-authentication.git
   cd spring-boot-spring-security-jwt-authentication
   ```
2. Configura las propiedades en `src/main/resources/application.properties`.
3. Construye el proyecto con Maven:
   ```bash
   ./mvnw clean install
   ```
4. Ejecuta la aplicación:
   ```bash
   java -jar target/spring-boot-spring-security-jwt-authentication-0.0.1-SNAPSHOT.jar
   ```
5. (Opcional) Usa Docker para desplegar:
   ```bash
   docker-compose up --build
   ```

### Infraestructura
- **Backend**: Spring Boot con Spring Security para la autenticación y autorización.
- **Base de Datos**: Configurable en `application.properties` (por ejemplo, MySQL, PostgreSQL).
- **Postman**: Utilizado para probar los endpoints.

## 6. Otros Detalles
- **Seguridad**: Los tokens JWT son firmados con una clave secreta para garantizar su integridad.
- **Extensibilidad**: Puedes agregar más roles y permisos según las necesidades de tu aplicación.
- **Pruebas**: Usa la colección de Postman incluida para probar los endpoints.

Para más información, consulta la documentación oficial de [Spring Security](https://spring.io/projects/spring-security) y [JSON Web Tokens](https://jwt.io/).
