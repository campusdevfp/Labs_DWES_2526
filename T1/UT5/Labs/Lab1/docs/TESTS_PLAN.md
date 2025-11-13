# Plan de pruebas — Unitarias, Integración y End-to-End

Este documento describe el enunciado de las pruebas que hay que implementar para la API (Clientes y Pedidos) usando JUnit5 y Mockito. Incluye: objetivos, contratos de las pruebas, casos por capa (servicios, controladores, repositorios), configuración sugerida y comandos para ejecutar.

---

## Objetivos

- Garantizar la corrección de la lógica de negocio (servicios) mediante pruebas unitarias aisladas con Mockito.
- Verificar integraciones críticas (JPA/repository, mapeos y cascade) mediante pruebas de integración con Spring Boot y una base de datos de pruebas (H2 o Testcontainers/MySQL).
- Validar el comportamiento HTTP desde la API (endpoints) con pruebas E2E que arranquen la app y realicen peticiones reales (TestRestTemplate o RestAssured).

---

## Requisitos y dependencias (pom.xml)

Asegúrate de incluir (o tener ya) en `pom.xml`:

- spring-boot-starter-test (incluye JUnit5, Mockito, AssertJ, MockMvc, TestRestTemplate)
- (Opcional para integración real) testcontainers-bom + testcontainers-mysql

Comandos útiles:

- Ejecutar tests: mvn test
- Ejecutar sólo unitarias si las separas por perfil: mvn -Dtest=*UnitTest test

---

## Convenciones y contrato de las pruebas

- Nombres de pruebas: {unidad}Should{Comportamiento}When{Condición}
  - Ejemplo: ClienteServiceShouldReturnClienteWhenExists
- Organizar tests por carpeta `src/test/java` con paquetes paralelos al código `es.iesguzman.demo`.
- Separar en paquetes: `unit`, `integration`, `e2e` (opcional) o bien usar sufijos en los nombres de clase (`*UnitTest`, `*IntegrationTest`, `*E2ETest`).
- Usar JUnit5 (annotations: @Test, @BeforeEach, @Nested, @DisplayName).
- Usar Mockito (o las utilidades de Spring Boot Test) para mocks y verificación.
- Para pruebas de integración que requieren BD, preferir Testcontainers (aisla del entorno dev); alternativa: H2 en memoria.

---

## Pruebas unitarias (Mockito) — Servicios y Controladores (simulados)

Contrato: cada test del servicio verifica salida/efecto y excepciones; los repositorios se simulan.

1) ClienteServiceUnitTest
- Escenario: findAll
  - Entrada: ninguno
  - Preparación: mock ClienteRepository.findAll() -> lista de 2 clientes
  - Expectativa: devuelve lista con tamaño 2

- Escenario: findById cuando existe
  - Preparación: mock repository.findById(id) -> Optional.of(cliente)
  - Expectativa: devuelve el cliente

- Escenario: findById cuando no existe
  - Preparación: mock repository.findById(id) -> Optional.empty()
  - Expectativa: ResponseStatusException con HttpStatus.NOT_FOUND

- Escenario: save con datos válidos
  - Preparación: repository.save(...) -> clientePersistido
  - Expectativa: retorna cliente con id, y no lanza excepción

- Escenario: save con campos nulos
  - Entrada: cliente con nombre o email nulo
  - Expectativa: ResponseStatusException con HttpStatus.BAD_REQUEST

- Escenario: save con email duplicado (DataIntegrityViolationException)
  - Preparación: repository.save(...) lanza DataIntegrityViolationException
  - Expectativa: ResponseStatusException con HttpStatus.CONFLICT

- Escenario: update éxito
  - Preparación: repository.findById(id) -> existing; repository.save -> updated
  - Expectativa: retorna cliente actualizado

- Escenario: delete con cliente existente -> repo.delete(cliente) invocado
  - Preparación: repository.findById(id) -> existing
  - Expectativa: verifica que repository.delete(cliente) fue llamado

- Escenario: delete cliente inexistente -> NOT_FOUND

Técnicas y herramientas:
- Mockito: when(repository.findById(id)).thenReturn(Optional.of(...));
- Verificación: verify(repository).delete(existing);
- Asserts: Assertions.assertEquals, assertThrows(ResponseStatusException.class,...)

2) PedidoServiceUnitTest
- Casos similares: findAll, findById (existe/no), findByCliente (cliente no existe -> NOT_FOUND), create (ok, campos nulos -> BAD_REQUEST, cliente no existe -> NOT_FOUND), delete (ok, no existe -> NOT_FOUND)
- Verificar que, en create, pedido.setCliente(cliente) es invocado antes de guardar (opcional: ArgumentCaptor)

3) ClientesRestControllerUnitTest (usar @WebMvcTest y MockMvc)
- Mockear `ClienteService` con Mockito
- Casos:
  - GET /api/clientes -> status 200 y JSON array
  - GET /api/clientes/{id} -> 200 con body válido cuando existe; 404 cuando service lanza ResponseStatusException(NOT_FOUND)
  - POST /api/clientes -> 201 con Location/Body si correcto; 400 si body inválido (service lanza BAD_REQUEST); 409 si email duplicado
  - PUT /api/clientes/{id} -> 200 con body actualizado; 404 si no existe
  - DELETE /api/clientes/{id} -> 204 si eliminado; 404 si no existe

- Herramientas: MockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath(...))
- Verificar interactions con Mockito.verify(service).save(...), etc.

4) PedidosRestControllerUnitTest
- Mismos tipos de pruebas con MockMvc y mock de `PedidoService`.
- Verificar códigos: 200, 201, 204, 404, 400 según el caso.

---

## Pruebas de integración

Objetivo: arrancar un contexto de Spring con repositorios y JPA, probar que los mapeos/constraints/cascade funcionan.

Opciones:
- Primera opción (rápida): usar H2 en memoria (configurar `application-test.properties` para el perfil de test) y @SpringBootTest o @DataJpaTest.
- Mejor (recomendado): usar Testcontainers con MySQL para replicar entorno real.

Casos de prueba sugeridos:

1) ClienteRepositoryIntegrationTest (@DataJpaTest con Testcontainers o H2)
- Guardar cliente con email único -> se persiste y se le asigna id
- Intentar guardar cliente con email duplicado -> lanzar DataIntegrityViolationException

2) PedidoRepositoryIntegrationTest (@DataJpaTest)
- Crear cliente persistido, agregar pedidos y verificar `findByClienteId(clienteId)` devuelve los pedidos

3) Cascade y orphanRemoval test
- Crear cliente con 2 pedidos (persistidos juntos). Verificar que al eliminar el cliente (entityManager.remove o repository.delete(cliente)) los pedidos también desaparecen de la BD (consulta count=0)

4) Endpoints básicos en integración (@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT))
- Arrancar app con DB de pruebas
- Usar TestRestTemplate para:
  - POST /api/clientes -> crear cliente (201) y comprobar persistencia en BD
  - POST /api/clientes/{id}/pedidos -> crear pedido y comprobar relación cliente_id
  - DELETE /api/clientes/{id} -> comprobar que pedidos relacionados se eliminan (cascade)

Configuración útil:
- `@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)` junto a Testcontainers si usas MySQL real en contenedor.
- Shared setup con `@Testcontainers` y `@Container static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withDatabaseName("testdb").withUsername("test").withPassword("test");`

---

## Pruebas End-to-End (E2E)

Objetivo: Validar el flujo completo desde HTTP hasta la persistencia usando la aplicación en ejecución (puede ser levantada en CI o por Testcontainers + SpringBootTest). Estas pruebas son más lentas pero son la última barrera.

Estrategia típica:
- Usar `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`
- Usar `TestRestTemplate` o `RestAssured` para realizar peticiones HTTP reales
- Base de datos: Testcontainers MySQL o una instancia preparada de MySQL (recomiendo Testcontainers)

Casos E2E recomendados:
- Flujo de creación y consulta
  1. POST /api/clientes -> obtener id cliente
  2. POST /api/clientes/{id}/pedidos -> crear pedido
  3. GET /api/clientes/{id}/pedidos -> comprobar que contiene el pedido creado
- Flujo de borrado en cascada
  1. Crear cliente + 2 pedidos
  2. DELETE /api/clientes/{id} -> 204
  3. GET /api/pedidos -> comprobar que ninguno de los pedidos creados sigue presente
- Casos de error HTTP (400, 404)
  - POST /api/clientes con campos faltantes -> 400
  - POST /api/clientes/999/pedidos -> 404

Tiempo estimado: cada test E2E puede tardar varios segundos (arranque del contexto + contenedores).

---

## Ejemplos de snippets (guía rápida)

- Mockito stub & verify (JUnit5):

  - when(repository.findById(id)).thenReturn(Optional.of(cliente));
  - assertThrows(ResponseStatusException.class, () -> service.findById(99L));
  - verify(repository).delete(cliente);

- MockMvc controller test:

  - @WebMvcTest(ClientesRestController.class)
  - @MockBean private ClienteService service;
  - mockMvc.perform(get("/api/clientes/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));

- Integration with Testcontainers (resumen):

  - @Testcontainers
  - @SpringBootTest
  - static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
  - System.setProperty("spring.datasource.url", mysql.getJdbcUrl()); etc. (o usar DynamicPropertySource)

- E2E con TestRestTemplate:

  - @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
  - @Autowired private TestRestTemplate restTemplate;
  - ResponseEntity<Cliente> r = restTemplate.postForEntity("/api/clientes", clienteDto, Cliente.class);

---

## Lista de pruebas mínimas obligatorias (por prioridad)

Prioridad alta (implementar primero):
- ClienteService: findById (exists / not found)
- ClienteService: save (valid / missing fields / duplicate email)
- PedidoService: create (valid / cliente no existe / campos faltantes)
- ClientesRestController: GET /api/clientes, GET /api/clientes/{id}, POST /api/clientes, PUT /api/clientes/{id}, DELETE /api/clientes/{id}
- PedidosRestController: GET /api/pedidos, GET /api/pedidos/{id}, POST /api/clientes/{id}/pedidos, DELETE /api/pedidos/{id}

Prioridad media:
- Repository tests: unique constraint email, findByClienteId
- Integration: cascade delete cliente -> pedidos

Prioridad baja (opcional):
- Tests E2E completos (flujo crear/consultar/borrar)
- Casos de concurrencia/transactional (si aplica)

---

## Cómo ejecutar y verificar localmente

1. Ejecutar tests unitarios e integración rápidos (sin contenedores):

   mvn test

2. Ejecutar tests con Testcontainers (si están configurados):

   mvn -Dtest=*IntegrationTest test

3. Ejecutar toda la suite E2E (puede tardar):

   mvn -Dtest=*E2ETest test

---

## Consejos finales

- Mantén los tests unitarios rápidos y aislados (no toques la BD allí).
- Usa Testcontainers para tener confianza en integraciones reales sin depender del entorno del desarrollador.
- Añade pruebas que cubran tanto happy-path como errores esperados (400/404/409/500). 
- Instrumenta las pruebas en CI: ejecutar unitarias en cada commit y las integraciones/E2E en pipeline nightly o en PRs más pesadas.

---

Si quieres, puedo:
- Generar plantillas de test (clases de ejemplo) para cada capa (servicio, controlador, repository, E2E) en Java usando JUnit5 + Mockito.
- Añadir configuración para Testcontainers y ejemplos concretos de implementación de pruebas.

Dime si quieres que genere las clases de test ya listas para ejecutar. 

