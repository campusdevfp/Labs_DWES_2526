## Reto Práctico: Miniproyecto  de Sesiones y Cookies

### Descripción del Reto

Para consolidar el aprendizaje sobre sesiones HTTP y cookies en Spring Security, crea un **nuevo proyecto Spring Boot independiente** llamado "TodoApp" que implemente un **sistema de "Lista de Tareas Pendientes" (To-Do List)** almacenado en la sesión del usuario. Este miniproyecto es completamente separado del proyecto actual y te permitirá practicar:

- **Almacenamiento de datos en sesiones**: Guardar listas personalizadas por usuario.
- **Manejo de cookies**: Verificar cómo se mantienen las sesiones entre peticiones.
- **Autenticación y autorización**: Asegurar que cada usuario vea solo su lista.
- **Operaciones CRUD básicas**: Crear, leer, actualizar y eliminar tareas.

**Nota**: Este es un proyecto completamente independiente. Crea un nuevo directorio y proyecto Spring Boot desde cero. **No copies código del proyecto original; implementa todo tú mismo.**

### Objetivos de Aprendizaje

- Comprender cómo Spring Security maneja las sesiones HTTP.
- Aprender a usar `HttpSession` para almacenar datos personalizados.
- Practicar el envío y recepción de cookies en clientes HTTP (Postman, curl).
- Implementar lógica de negocio en controladores REST.

### Requisitos Funcionales

1. **Crear una nueva tarea**: Endpoint `POST /todo/agregar` para añadir una tarea (título, descripción).
2. **Listar tareas**: Endpoint `GET /todo/listar` para ver todas las tareas del usuario.
3. **Marcar como completada**: Endpoint `POST /todo/completar/{id}` para cambiar el estado de una tarea.
4. **Eliminar tarea**: Endpoint `DELETE /todo/eliminar/{id}` para borrar una tarea específica.
5. **Limpiar lista**: Endpoint `DELETE /todo/limpiar` para vaciar toda la lista de tareas.
6. **Login/Logout**: Endpoints para autenticación (`/api/auth/login`, `/api/auth/logout`).
7. **Endpoint público**: `GET /api/auth/public` accesible sin login.

### Instrucciones de Implementación

#### Paso 1: Crear un Nuevo Proyecto Spring Boot

1. Crea un nuevo directorio: `C:\Users\madrid\ws\Labs_DWES_2526\T2\TodoApp`
2. Inicializa con Gradle: `gradle init --type basic --dsl groovy`
3. Configura `build.gradle` con dependencias de Spring Boot (web, security, data-jpa, h2).

#### Paso 2: Configurar la Aplicación

- Crea `TodoAppApplication.java` como clase principal:
  ```java
  @SpringBootApplication
  public class TodoAppApplication {
      public static void main(String[] args) {
          SpringApplication.run(TodoAppApplication.class, args);
      }
  }
  ```

- Configura `application.properties`:
  ```properties
  spring.application.name=TodoApp
  spring.datasource.url=jdbc:h2:mem:testdb
  spring.datasource.driverClassName=org.h2.Driver
  spring.datasource.username=sa
  spring.datasource.password=
  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
  spring.jpa.hibernate.ddl-auto=create-drop
  server.servlet.session.timeout=30m
  spring.h2.console.enabled=true
  spring.h2.console.path=/h2-console
  spring.sql.init.mode=always
  ```

- Crea `data.sql`:
  ```sql
  INSERT INTO users (username, password, role) VALUES ('user', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER');
  INSERT INTO users (username, password, role) VALUES ('admin', '{bcrypt}$2a$12$eTIoaBs2LeiMndO3SQFykuVBMkESD3m43NYBldeHTe1WLxNcXa/SC', 'ROLE_ADMIN');
  ```

#### Paso 3: Implementar Seguridad

- Crea `SecurityConfig.java`:
  ```java
  @Configuration
  @EnableMethodSecurity
  public class SecurityConfig {
      @Bean
      public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          http.csrf(csrf -> csrf.disable())
              .authorizeHttpRequests(auth -> auth
                  .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                  .anyRequest().authenticated()
              )
              .headers(headers -> headers.frameOptions(frame -> frame.disable()))
              .httpBasic(httpBasic -> {})
              .formLogin(formLogin -> {})
              .logout(logout -> logout
                  .logoutUrl("/api/auth/logout")
                  .logoutSuccessHandler((request, response, authentication) -> {
                      response.setStatus(200);
                      response.getWriter().write("Sesión cerrada exitosamente");
                  })
                  .invalidateHttpSession(true)
                  .clearAuthentication(true)
              );
          return http.build();
      }

      @Bean
      public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
          return config.getAuthenticationManager();
      }

      @Bean
      public PasswordEncoder passwordEncoder() {
          return PasswordEncoderFactories.createDelegatingPasswordEncoder();
      }
  }
  ```

- Implementa `UserEntity.java`:
  ```java
  @Entity
  @Table(name = "users")
  public class UserEntity {
      @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      private String username;
      private String password;
      private String role;
      // Getters y setters
  }
  ```

- `UserRepository.java`:
  ```java
  public interface UserRepository extends JpaRepository<UserEntity, Long> {
      Optional<UserEntity> findByUsername(String username);
  }
  ```

- `UserService.java`:
  ```java
  @Service
  public class UserService {
      private final UserRepository userRepository;
      private final PasswordEncoder passwordEncoder;

      public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
          this.userRepository = userRepository;
          this.passwordEncoder = passwordEncoder;
      }

      public UserEntity buscarPorUsername(String username) {
          return userRepository.findByUsername(username).orElse(null);
      }
  }
  ```

- `DbUserDetailsService.java`:
  ```java
  @Service
  public class DbUserDetailsService implements UserDetailsService {
      private final UserRepository userRepository;

      public DbUserDetailsService(UserRepository userRepository) {
          this.userRepository = userRepository;
      }

      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
          UserEntity user = userRepository.findByUsername(username)
              .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
          return new org.springframework.security.core.userdetails.User(
              user.getUsername(),
              user.getPassword(),
              List.of(new SimpleGrantedAuthority(user.getRole()))
          );
      }
  }
  ```

- `SecurityUtils.java`:
  ```java
  public class SecurityUtils {
      public static void setAuthenticationInSession(HttpServletRequest request, Authentication authentication) {
          SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
          securityContext.setAuthentication(authentication);
          SecurityContextHolder.setContext(securityContext);
          HttpSession session = request.getSession(true);
          session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
      }

      public static void clearAuthentication(HttpServletRequest request) {
          HttpSession session = request.getSession(false);
          if (session != null) {
              session.invalidate();
          }
          SecurityContextHolder.clearContext();
      }
  }
  ```

#### Paso 4: Crear DTO y Controladores

- Crea `TareaDTO.java`:
  ```java
  public record TareaDTO(String titulo, String descripcion, boolean completada, long id) {
      public static TareaDTO of(String titulo, String descripcion) {
          return new TareaDTO(titulo, descripcion, false, System.currentTimeMillis());
      }
  }
  ```

- Implementa `AuthController.java`:
  ```java
  @RestController
  @RequestMapping("/api/auth")
  public class AuthController {
      private final AuthenticationManager authenticationManager;

      public AuthController(AuthenticationManager authenticationManager) {
          this.authenticationManager = authenticationManager;
      }

      @PostMapping("/login")
      public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
          try {
              Authentication authentication = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
              );
              SecurityUtils.setAuthenticationInSession(request, authentication);
              return ResponseEntity.ok(Map.of("mensaje", "✅ Login exitoso", "usuario", authentication.getName()));
          } catch (AuthenticationException e) {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "❌ Credenciales inválidas"));
          }
      }

      @PostMapping("/logout")
      public ResponseEntity<?> logout(HttpServletRequest request) {
          SecurityUtils.clearAuthentication(request);
          return ResponseEntity.ok(Map.of("mensaje", "✅ Sesión cerrada exitosamente"));
      }

      @GetMapping("/public")
      public ResponseEntity<String> publico() {
          return ResponseEntity.ok("Este es un endpoint público - accesible sin autenticación");
      }

      public record LoginRequest(String username, String password) {}
  }
  ```

- Implementa `TodoController.java`:
  ```java
  @RestController
  @RequestMapping("/todo")
  public class TodoController {

      @SuppressWarnings("unchecked")
      @PostMapping("/agregar")
      public ResponseEntity<String> agregarTarea(HttpSession session, @RequestParam String titulo, @RequestParam String descripcion) {
          List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");
          if (tareas == null) {
              tareas = new ArrayList<>();
          }
          tareas.add(TareaDTO.of(titulo, descripcion));
          session.setAttribute("tareas", tareas);
          return ResponseEntity.ok("✅ Tarea agregada. Total tareas: " + tareas.size());
      }

      @SuppressWarnings("unchecked")
      @GetMapping("/listar")
      public ResponseEntity<?> verTareas(HttpSession session) {
          List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");
          if (tareas == null || tareas.isEmpty()) {
              return ResponseEntity.ok(Map.of("mensaje", "No hay tareas pendientes", "tareas", List.of()));
          }
          return ResponseEntity.ok(Map.of("tareas", tareas, "total", tareas.size()));
      }

      @SuppressWarnings("unchecked")
      @PostMapping("/completar/{id}")
      public ResponseEntity<String> completarTarea(HttpSession session, @PathVariable long id) {
          List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");
          if (tareas == null) {
              return ResponseEntity.badRequest().body("❌ No hay tareas");
          }
          for (TareaDTO tarea : tareas) {
              if (tarea.id() == id) {
                  TareaDTO completada = new TareaDTO(tarea.titulo(), tarea.descripcion(), true, tarea.id());
                  tareas.set(tareas.indexOf(tarea), completada);
                  session.setAttribute("tareas", tareas);
                  return ResponseEntity.ok("✅ Tarea completada");
              }
          }
          return ResponseEntity.notFound().build();
      }

      @SuppressWarnings("unchecked")
      @DeleteMapping("/eliminar/{id}")
      public ResponseEntity<String> eliminarTarea(HttpSession session, @PathVariable long id) {
          List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");
          if (tareas == null) {
              return ResponseEntity.badRequest().body("❌ No hay tareas");
          }
          tareas.removeIf(t -> t.id() == id);
          session.setAttribute("tareas", tareas);
          return ResponseEntity.ok("🗑️ Tarea eliminada");
      }

      @DeleteMapping("/limpiar")
      public ResponseEntity<String> limpiarTareas(HttpSession session) {
          session.removeAttribute("tareas");
          return ResponseEntity.ok("🗑️ Lista de tareas limpiada");
      }
  }
  ````