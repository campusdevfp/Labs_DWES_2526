# Enunciado de la práctica — Pruebas para API Clientes y Pedidos

Objetivo
---------
Crear una batería de pruebas que garantice el correcto funcionamiento de la API REST (Clientes y Pedidos). Debes implementar pruebas unitarias (JUnit5 + Mockito), pruebas de integración (Spring Boot + JPA, preferiblemente Testcontainers) y pruebas end-to-end (E2E) que validen los flujos HTTP completos.

Entregables obligatorios
-------------------------
1. Código de pruebas en `src/test/java` organizado en paquetes `unit`, `integration`, `e2e` (o usando sufijos de clase `*UnitTest`, `*IntegrationTest`, `*E2ETest`).
2. Un fichero README corto en `docs/TESTS_RUN.md` (o en `README.md`) con instrucciones para ejecutar las pruebas localmente.
3. (Opcional) Configuración para Testcontainers si usas MySQL real en contenedor.

Convenciones
------------
- Usa JUnit5 y Mockito.
- Nombres de clases: `ClienteServiceUnitTest`, `PedidoServiceUnitTest`, `ClientesRestControllerUnitTest`, `PedidosRestControllerUnitTest`, `RepositoryIntegrationTest`, `ApiE2ETest`, etc.
- Nombres de métodos de prueba: {Unidad}Should{Resultado}When{Condición} (ej.: findByIdShouldReturnClienteWhenExists).
- Mantén los tests unitarios rápidos y aislados (no tocan la BD).

Casos obligatorios a implementar
--------------------------------
A continuación verás la lista mínima que debe cubrir tu práctica. Cada caso indica el tipo de prueba (unit/integration/e2e) y los detalles a comprobar.

A. Tests unitarios (Mockito) — Servicios
- ClienteService
  - findAll(): devuelve lista de clientes (mock del repo). (Unit)
  - findById(id): existe -> devuelve cliente; no existe -> lanza ResponseStatusException 404. (Unit)
  - save(cliente): datos válidos -> devuelve cliente persistido; campos faltantes -> ResponseStatusException 400; email duplicado (simular DataIntegrityViolationException) -> ResponseStatusException 409. (Unit)
  - update(id, cliente): actualiza cuando existe; no existe -> 404. (Unit)
  - delete(id): elimina cuando existe (verificar llamada a repository.delete); no existe -> 404. (Unit)

- PedidoService
  - findAll(): lista de pedidos. (Unit)
  - findById(id): existe/no -> 200/404. (Unit)
  - findByCliente(clienteId): cliente inexistente -> 404; cliente existente -> lista de pedidos. (Unit)
  - create(clienteId, pedido): cliente inexistente -> 404; campos faltantes -> 400; éxito -> pedido guardado con cliente asignado. (Unit)
  - delete(id): no existe -> 404; éxito -> deleteById llamado. (Unit)

B. Tests unitarios — Controladores (MockMvc)
- `ClientesRestController` endpoints (usar `@WebMvcTest` y `@MockBean` para el servicio):
  - GET /api/clientes -> 200 y JSON array. (Unit Controller)
  - GET /api/clientes/{id} -> 200 con body cuando existe; 404 si service lanza NOT_FOUND. (Unit Controller)
  - POST /api/clientes -> 201 si ok; 400/409 según errores del servicio. (Unit Controller)
  - PUT /api/clientes/{id} -> 200 cuando se actualiza; 404 cuando no existe. (Unit Controller)
  - DELETE /api/clientes/{id} -> 204 cuando se elimina; 404 si no existe. (Unit Controller)

- `PedidosRestController` endpoints (MockMvc):
  - GET /api/pedidos -> 200 y array. (Unit Controller)
  - GET /api/pedidos/{id} -> 200 o 404. (Unit Controller)
  - GET /api/clientes/{id}/pedidos -> 200 o 404. (Unit Controller)
  - POST /api/clientes/{id}/pedidos -> 201 si ok; 400/404 según error. (Unit Controller)
  - DELETE /api/pedidos/{id} -> 204 o 404. (Unit Controller)

C. Tests de integración (Spring Boot + JPA)
- Repositorios (`@DataJpaTest` o `@SpringBootTest` con Testcontainers/H2):
  - Persistir un `Cliente` con email único -> comprobar id asignado. (Integration)
  - Intentar persistir cliente con mismo email -> DataIntegrityViolationException. (Integration)
  - Crear cliente + pedidos -> `pedidoRepository.findByClienteId(clienteId)` devuelve los pedidos. (Integration)
  - Cascade/orphanRemoval: crear cliente con pedidos, luego `repository.delete(cliente)` -> los pedidos deben eliminarse. (Integration)

- Endpoints básicos con DB real de pruebas (`@SpringBootTest(webEnvironment = RANDOM_PORT)`):
  - POST /api/clientes -> 201 y comprobar en base de datos que existe. (Integration)
  - POST /api/clientes/{id}/pedidos -> 201 y relación cliente_id en la BD. (Integration)
  - DELETE /api/clientes/{id} -> 204 y comprobar que pedidos relacionados se eliminan. (Integration)

D. Tests End-to-End (E2E)
- Flujo completo (levantar la app en puerto aleatorio):
  1. Crear cliente (POST) -> obtener id. (E2E)
  2. Crear varios pedidos para ese cliente (POST) -> 201. (E2E)
  3. GET /api/clientes/{id}/pedidos -> verificar que aparecen los pedidos. (E2E)
  4. DELETE /api/clientes/{id} -> 204 y verificar que GET /api/pedidos no devuelve los pedidos creados. (E2E)
- Validar además errores: POST /api/clientes sin campos -> 400; POST /api/clientes/999/pedidos -> 404. (E2E)

Criterios de evaluación
------------------------
- Compleción: implementados todos los casos obligatorios (A, B, C, D). (40%)
- Calidad de las pruebas: nombres claros, fixtures reproducibles, mocks correctamente usados, aserciones completas (30%).
- Robustez: pruebas de integración que demuestren la eliminación en cascada y las restricciones de la BD (20%).
- Documentación y ejecución: README con pasos para ejecutar pruebas y que las pruebas se ejecuten con `mvn test` (10%).

Entregable (estructura mínima esperada)
---------------------------------------
- src/test/java/es/iesguzman/demo/unit/ClienteServiceUnitTest.java
- src/test/java/es/iesguzman/demo/unit/PedidoServiceUnitTest.java
- src/test/java/es/iesguzman/demo/controller/ClientesRestControllerUnitTest.java
- src/test/java/es/iesguzman/demo/controller/PedidosRestControllerUnitTest.java
- src/test/java/es/iesguzman/demo/integration/RepositoryIntegrationTest.java
- src/test/java/es/iesguzman/demo/e2e/ApiE2ETest.java
- docs/TESTS_RUN.md (o README.md) con instrucciones para ejecutar `mvn test` y/o comando por categorías

Cómo ejecutar
--------------
- Ejecutar toda la suite (rápido si no usas contenedores):

```bash
mvn test
```

- Ejecutar sólo unitarias por patrón de nombre:

```bash
mvn -Dtest=*UnitTest test
```

- Ejecutar integración/E2E (si usas Testcontainers pueden tardar más):

```bash
mvn -Dtest=*IntegrationTest test
mvn -Dtest=*E2ETest test
```

Consejos y notas finales
------------------------
- Empieza por las pruebas unitarias de servicios; después añade controladores y finalmente integración/E2E.
- Si prefieres no usar Testcontainers en local, configura H2 en `application-test.properties` para integration tests.
- Si quieres, genero plantillas de tests ya implementadas para empezar rápidamente.

---

Fíjate si quieres que genere las plantillas (clases Java) para cada uno de los tests obligatorios; si me das el visto bueno las crearé en `src/test/java` y ejecutaré `mvn -DskipTests package` para comprobar compilación.

