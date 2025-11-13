# Tests del proyecto TaskManager: explicados con ejemplos

Este README explica en detalle las versiones de tests que hemos creado para el proyecto TaskManager. Cada tipo de test (unitario, integración, E2E) tiene variantes con mocks (usando Mockito) y sin mocks (usando componentes reales como H2 o el servidor HTTP). Te digo qué hace cada uno, por qué lo usamos, cuándo elegirlo y cómo ejecutarlo en Windows (cmd).

## 1. Tests unitarios (capa de servicio)

Estos tests se centran en la lógica del `TaskServiceImpl` (servicio de tareas). No tocan el controlador ni la API HTTP.

### 1.1 Con mocks (aislan el servicio del repositorio)

**Qué hacen:** Usan Mockito para simular el `TaskRepository`. El servicio cree que interactúa con una BD real, pero en realidad es un mock que devuelve datos fijos o lanza excepciones controladas. Verifican que el servicio llama correctamente al repositorio y maneja bien los datos.

**Por qué:** Son rápidos (no levantan BD ni contexto Spring pesado). Ideales para validar reglas de negocio, como validaciones de entrada o asignación de valores por defecto, sin depender de infraestructura externa.

**Cuándo usar:** Para feedback rápido en desarrollo diario. Si cambias la lógica del servicio, estos tests fallan inmediatamente.

**Ejemplos de casos:**
- `findAll`: El mock devuelve una lista de 2 tareas; el servicio las devuelve sin cambios.
- `findById`: Si el mock devuelve una tarea, el servicio la retorna; si lanza `Optional.empty()`, el servicio lanza `ResponseStatusException` con 404.
- `create`: Si el título es null, lanza 400; si estado es null, asigna "pendiente" y llama a `repository.save`.

**Archivos:**
- `app.service.TaskServiceImplWithMocksTest` (usa `@ExtendWith(MockitoExtension)` para inyección automática).
- `service.TaskServiceImplTest` (equivalente, con anotaciones).
- `service.TaskServiceImplSinInyeccionTest` (mocks creados a mano con `Mockito.mock()`).

**Cómo ejecutar:**
```cmd
mvn -q -Dtest=app.service.TaskServiceImplWithMocksTest,service.TaskServiceImplTest,service.TaskServiceImplSinInyeccionTest test
```

### 1.2 Sin mocks (usando repositorio real H2)

**Qué hacen:** Usan `@DataJpaTest` para levantar JPA con H2 en memoria. El `TaskRepository` es real, así que insertan datos en BD y verifican que el servicio los lee/escribe correctamente. El servicio se instancia con `new TaskServiceImpl(repository)`.

**Por qué:** Más realistas que los mocks, porque ejercitan SQL real y mapeo JPA. Detectan errores de queries o configuración de entidades que los mocks no ven.

**Cuándo usar:** Cuando quieres confianza extra en la capa de datos, o para probar comportamientos que dependen de JPA (como IDs autogenerados).

**Ejemplos de casos:** Idénticos a los con mocks, pero los datos se persisten en H2 y se verifican con `repository.findById()`.

**Archivo:** `app.service.TaskServiceImplNoMocksTest`

**Cómo ejecutar:**
```cmd
mvn -q -Dtest=app.service.TaskServiceImplNoMocksTest test
```

## 2. Tests de integración (capa de controlador)

Estos tests validan el `TaskController` junto con el servicio y repositorio. No tocan la capa HTTP (no hay JSON ni requests).

### 2.1 Sin mocks (componentes reales)

**Qué hacen:** Levantan Spring Boot completo con `@SpringBootTest`. Inyectan el `TaskController` real y llaman directamente a sus métodos (ej. `controller.findAll()`). Siembran datos con el `TaskRepository` real.

**Por qué:** Valida el wiring de Spring (que el controlador inyecta bien el servicio) y la integración entre capas. Más rápido que E2E, pero cubre más que unitarios.

**Cuándo usar:** Para asegurar que el controlador delega correctamente al servicio y maneja excepciones.

**Ejemplos de casos:**
- `findAll`: Inserta 2 tareas en BD, llama a `controller.findAll()` y verifica que devuelve lista de 2.
- `findById`: Inserta una tarea, la busca por ID y verifica campos.
- `findByIdNotFound`: Busca ID inexistente, verifica que lanza `ResponseStatusException` con mensaje "No se ha encontrado la tarea con id: -100".
- `create`: Crea tarea con estado null, verifica que se guarda con "pendiente" y que existe en BD.

**Archivo:** `app.controller.TaskControllerSpringBootTest`

**Cómo ejecutar:**
```cmd
mvn -q -Dtest=app.controller.TaskControllerSpringBootTest test
```

### 2.2 Con mocks (servicio simulado)

**Qué hacen:** Levantan Spring Boot, inyectan `TaskController` real, pero mockean `TaskServiceImpl` con `@MockBean`. El controlador cree que llama al servicio real, pero es simulado.

**Por qué:** Aísla el controlador del servicio/repositorio. Verifica que el controlador mapea bien parámetros y excepciones, sin depender de lógica de negocio.

**Cuándo usar:** Si cambias el contrato del servicio, estos tests fallan si el controlador no se adapta.

**Ejemplos de casos:** Idénticos a los sin mocks, pero el mock controla qué devuelve el servicio (ej. lista de tareas o excepciones).

**Archivo:** `app.controller.TaskControllerSpringBootWithMocksTest`

**Cómo ejecutar:**
```cmd
mvn -q -Dtest=app.controller.TaskControllerSpringBootWithMocksTest test
```

## 3. Tests end-to-end (E2E, API HTTP completa)

Estos tests levantan el servidor Spring Boot en un puerto aleatorio y hacen peticiones HTTP reales con `TestRestTemplate`.

### 3.1 Sin mocks (servidor real)

**Qué hacen:** Arrancan la app completa (controlador + servicio + repositorio + H2). Siembran datos con `TaskRepository` y hacen GET/POST a `/api/tasks` vía HTTP.

**Por qué:** Valida toda la pila: serialización JSON, rutas, status codes, headers. Detecta problemas de configuración o bugs que solo aparecen en HTTP.

**Cuándo usar:** Para confianza máxima antes de deploy. Simulan lo que haría un cliente real (Postman, frontend).

**Ejemplos de casos:**
- `GET /api/tasks`: Devuelve JSON con array de 2 tareas.
- `GET /api/tasks/{id}`: 200 con JSON de la tarea; 404 si no existe.
- `POST /api/tasks`: Envía JSON, recibe 201 con tarea creada (estado "pendiente" si era null).

**Archivo:** `app.e2e.TaskControllerE2ERealTest`

**Cómo ejecutar:**
```cmd
mvn -q -Dtest=app.e2e.TaskControllerE2ERealTest test
```

### 3.2 Con mocks (servidor real, servicio simulado)

**Qué hacen:** Levantan servidor, pero mockean `TaskServiceImpl`. Las peticiones HTTP pasan por el controlador y Spring, pero el servicio devuelve datos simulados.

**Por qué:** Combina velocidad de mocks con realismo de HTTP. Verifica que el controlador serializa/deserializa JSON bien, sin depender de BD.

**Cuándo usar:** Para tests de API que no quieren setup de datos, o para simular errores del servicio.

**Ejemplos de casos:** Idénticos a E2E sin mocks, pero el mock controla respuestas (ej. devuelve tareas fijas o lanza excepciones).

**Archivo:** `app.e2e.TaskControllerE2EMockedTest`

**Cómo ejecutar:**
```cmd
mvn -q -Dtest=app.e2e.TaskControllerE2EMockedTest test
```

## Resumen y recomendaciones

- **Con mocks:** Rápidos, aislados, para lógica pura. Usa Mockito para simular dependencias.
- **Sin mocks:** Lentos, realistas, para integración. Usa componentes reales (H2, servidor HTTP).
- **Mezcla ideal:** Unitarios con mocks para desarrollo, + algunos sin mocks/E2E para calidad.
- **Ejecutar todo:** `mvn test` (JaCoCo exige 80% cobertura).
- **Errores comunes:** En Spring Boot 3, usa `getStatusCode()` en excepciones. Limpia BD con `repository.deleteAll()` en tests sin mocks.

Si algo no queda claro o quieres añadir un caso específico, dime y lo ajusto.
