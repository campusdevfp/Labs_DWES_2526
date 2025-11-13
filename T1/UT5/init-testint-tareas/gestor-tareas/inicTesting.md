- [Iniciación al testing en Spring Boot](#iniciación-al-testing-en-spring-boot)
    - [Testing](#testing)
    - [Test unitarios](#test-unitarios)
    - [Test de integración](#test-de-integración)
    - [Testeando los controladores](#testeando-los-controladores)
- [Práctica de clase, Testing](#práctica-de-clase-testing)
- [Proyecto del curso](#proyecto-del-curso)

![](../images/banner06.png)

# Iniciación al testing en Spring Boot

## Testing

El testing es una parte fundamental en el desarrollo de software. Nos permite asegurar que nuestro código funciona correctamente y que no se rompe cuando hacemos cambios en él. Para ello debe es importante que diseñemos nuestros test de forma que sean fáciles de mantener y que nos permitan detectar errores de forma rápida y cubran todos los aspectos de nuestro código.

Tenemos varios niveles de tests:

- Test unitarios: estos tests se encargan de probar una unidad de código (una clase, un método, etc.) de forma aislada. Para ello se suelen utilizar mocks para aislar la unidad de código que estamos probando de las dependencias que tiene.
- Test de integración: estos tests se encargan de probar que las distintas unidades de código funcionan correctamente cuando se integran entre ellas. Para ello se suelen utilizar bases de datos en memoria para simular el acceso a datos.
- Test End-to-End: estos tests se encargan de probar que todo el sistema funciona correctamente. Para ello se suelen utilizar herramientas que simulan un navegador web y que permiten simular las acciones que haría un usuario en la aplicación. Por ejemplo cuando usamos Postman.

## Test unitarios

Para realizar los test unitarios con Spring Boot podemos utilizar [JUnit 5](https://www.baeldung.com/junit-5) y [Mockito](https://www.baeldung.com/mockito-series). Para ello debemos añadir la dependencia de Starter de Test en nuestro proyecto:

---

### Dependencias para Test Unitarios (JUnit 5 + Mockito)

En tu fichero **`pom.xml`**, añade dentro de `<dependencies>`:

```xml
<dependencies>
    <!-- Otras dependencias del proyecto -->

    <!-- Starter de test para Spring Boot (incluye JUnit 5 y Mockito) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

### Configuración del Plugin de Test (JUnit Platform)

Maven usa por defecto Surefire para ejecutar los tests.
Asegúrate de tener este plugin dentro de `<build>`:

```xml
<build>
    <plugins>
        <!-- Plugin principal de compilación de Spring Boot -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>

        <!-- Plugin de tests con soporte para JUnit 5 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.5</version>
            <configuration>
                <useModulePath>false</useModulePath>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

### ✅ Ejecución de los tests

Para ejecutar los tests unitarios en Maven:

```bash
mvn test
```

Si quieres ver un informe más detallado (por ejemplo, con cobertura usando JaCoCo):

```bash
mvn clean verify
```

---

Para realizar los test unitarios en clases que no dependan de otras, no necesitamos realizar mocks. Podremos usar JUnit 5 para realizar los test. Por ejemplo de un repositorio o mapeador o servicio sin dependencias.

Es importante que los test sean independientes, es decir, que no dependan unos de otros. Para ello debemos asegurarnos que cada test se encarga de inicializar los datos que necesita para funcionar. Para ello podemos utilizar los métodos `@BeforeEach` y `@AfterEach` que se ejecutan antes y después de cada test. También podemos utilizar los métodos `@BeforeAll` y `@AfterAll` que se ejecutan antes y después de todos los test de la clase.

Además, usaremos las aserciones de JUnit 5 para comprobar que el resultado de la ejecución de nuestro código es el esperado. Por ejemplo:

```java
class TaskRepositoryTest {
    @Autowired
    private TaskRepository repository;

    @Test
    void findAll_returnsAllTasks() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                new Task(null, "Escribir tests", "JUnit y Mockito", "completada")
        ));

        var list = repository.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size())
        );
    }

    @Test
    void findById_returnsTask() {
        repository.deleteAll();
        var saved = repository.save(new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"));

        var task = repository.findById(saved.getId());

        assertAll(
                () -> assertTrue(task.isPresent()),
                () -> assertEquals("Configurar CI", task.get().getTitulo())
        );
    }

    @Test
    void findById_notFound_returnsEmpty() {
        repository.deleteAll();

        var task = repository.findById(999L);

        assertFalse(task.isPresent());
    }
}
```

Si estamos testeando controladores o servicios, es posible que necesitemos realizar mocks de las dependencias que tienen. Para ello podemos utilizar Mockito.

Es por ello que debemos extender nuestra clase de test con `@ExtendWith(MockitoExtension.class)` y utilizar la anotación `@Mock` para indicar que queremos que se cree un mock de la dependencia. Además, debemos indicarle a Mockito que inyecte los mocks en la clase que estamos testeando con la anotación `@InjectMocks`.

De esta manera cada vez que se use un método de la clase mockeada, se ejecutará el código que hemos definido en el mock. Des esta manera nos concentraremos en probar el código de la clase que estamos testeando y no el de las dependencias.

Para simular el comportamiento de los mocks, podemos utilizar el método `when` de Mockito. Para verificar que se ha llamado a un método de un mock, podemos utilizar el método `verify` de Mockito.

```java
@ExtendWith(MockitoExtension.class) // Extensión de Mockito para usarlo
class TaskServiceImplTest {
    // Datos de demo
    Map<Long, Task> tasks = Map.of(
            1L, new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
            2L, new Task(2L, "Escribir tests", "JUnit y Mockito", "completada")
    );
    // Creo los mocks
    @Mock
    private TaskRepository taskRepository;
    // Inyecto los mocks en la clase que voy a testear
    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        // No necesitamos setup adicional
    }


    @Test
    void findAll() {
        // Lo que vamos a simular
        when(taskRepository.findAll())
                .thenReturn(List.copyOf(tasks.values()));

        //test
        var list = taskService.findAll();

        // comprobaciones
        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size())
        );

        // verificamos que se ha llamado al método
        verify(taskRepository, times(1))
                .findAll();
    }

    @Test
    void findById() {
        // Lo que vamos a simular
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(tasks.get(1L)));

        // Test
        var task = taskService.findById(1L);

        // Comprobaciones
        assertAll(
                () -> assertNotNull(task),
                () -> assertEquals("Configurar CI", task.getTitulo()),
                () -> assertEquals("Configurar GitHub Actions", task.getDescripcion()),
                () -> assertEquals("pendiente", task.getEstado())
        );

        // Verificamos que se ha llamado al método
        verify(taskRepository, times(1))
                .findById(1L);
    }

    @Test
    void findByIdNotFound() {
        when(taskRepository.findById(-100L))
                .thenReturn(Optional.empty());

        // Salta la excepcion
        var res = assertThrows(ResponseStatusException.class, () -> {
            taskService.findById(-100L);
        });
        // Comprobamos que la excepción es la esperada
        assert (res.getMessage().contains("No se ha encontrado la tarea con id: -100"));

        // Verificamos que se ha llamado al método
        verify(taskRepository, times(1))
                .findById(-100L);
    }
}
```

## Test de integración

Para hacer los test de integration podemos usar solo JUnit con las clases con sus respectivas dependencias reales. Sin embargo, si queremos hacer los test de integración usando el contexto Spring Boot, debemos usar la anotación `@SpringBootTest` en la clase de test. De esta manera Spring Boot se encargará de inicializar el contexto de la aplicación y de inyectar las dependencias que necesitemos con `@Autowired`.

```java
@SpringBootTest
class RaquetasControllerTest {
    @Autowired
    private RaquetasController controller;

    @Test
    void findAll() {
        var list = controller.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(3, list.size())
        );
    }

    @Test
    void findById() {
        var raqueta = controller.findById(1L);

        assertAll(
                () -> assertNotNull(raqueta),
                () -> assertEquals("Babolat", raqueta.getMarca()),
                () -> assertEquals("Pure Aero", raqueta.getModelo()),
                () -> assertEquals(199.95, raqueta.getPrecio())
        );
    }

    @Test
    void findByIdNotFound() {
        var res = assertThrows(ResponseStatusException.class, () -> {
            controller.findById(-100L);
        });
        assert (res.getMessage().contains("No se ha encontrado la raqueta con id: -100"));
    }
}
```

## Testeando los controladores

Aunque podemos testear los controladores como una clase más, mockeando o integrando, tal y como hemos visto en los apartados anteriores, Spring Boot nos proporciona una serie de herramientas para testear los controladores de una manera más sencilla y se simula a lo que hemos hecho con Postman.

Para ello debemos usar la anotación `@AutoConfigureMockMvc` en la clase de test. De esta manera Spring Boot se encargará de inicializar el contexto de la aplicación y de inyectar las dependencias que necesitemos. Además, nos proporciona un objeto `MockMvc` que nos permite [simular las peticiones HTTP](https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html) y con ello testear la [capa HTTP](https://spring.io/guides/gs/testing-web/) o realizar un test de [integración completo ](https://www.baeldung.com/integration-testing-in-spring)si no usamos los mocks . Usaremos ObjectMapper para mapear los objetos a JSON y poder testear los controladores.

```java
// Indicamos que es un test de Spring
@SpringBootTest
// Configuramos el cliente MVC
@AutoConfigureMockMvc
public class TaskControllerMockMvcIntegrationTest {
    // Para mapear a JSON
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc; // Cliente MVC

    Task task = new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente");
    String myEndpoint = "/api/tasks";

    @Test
    @Order(1)
    void findAllTest() throws Exception {

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Proceso la respuesta
        ObjectMapper mapper = new ObjectMapper();
        List<Task> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Task.class));

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"titulo\":\"Configurar CI\"")),
                () -> assertTrue(res.size() > 0),
                () -> assertTrue(res.stream().anyMatch(t -> t.getTitulo().equals("Configurar CI")))
        );
    }

    @Test
    @Order(2)
    void findByIdTest() throws Exception {
        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + task.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Proceso la respuesta
        Task res = mapper.readValue(response.getContentAsString(), Task.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getId(), task.getId())
        );
    }

    @Test
    @Order(3)
    void findByIdNotFound() throws Exception {
        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + -1000L)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Proceso la respuesta
        try {
            Task res = mapper.readValue(response.getContentAsString(), Task.class);
        } catch (Exception ignored) {
        }

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value())
        );
    }
}
```

De la misma manera que en apartado anterior, podemos mockear las dependencias de los controladores para que no se conecten a la base de datos o servicios y ser solo unitario. Para ello debemos usar la anotación `@MockBean` en la clase de test. De esta manera Spring Boot se encargará de inicializar el contexto de la aplicación y de inyectar las dependencias que necesitemos.

```java
/ Indicamos que es un test de Spring
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class) // Extensión de Mockito para usarlo
public class TaskControllerE2EMockedTest {
    // Para mapear a JSON
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    TaskServiceImpl taskService;
    @Autowired
    MockMvc mockMvc; // Cliente MVC
    Task task = new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente");
    String myEndpoint = "/api/tasks";

    @Test
    void findAllTest() throws Exception {
        // Lo que voy a simular
        when(taskService.findAll())
                .thenReturn(List.of(task));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Proceso la respuesta
        ObjectMapper mapper = new ObjectMapper();
        List<Task> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Task.class));

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"titulo\":\"Configurar CI\"")),
                () -> assertTrue(res.size() > 0),
                () -> assertTrue(res.stream().anyMatch(t -> t.getTitulo().equals("Configurar CI")))
        );

        // Verifico que se ha llamado al servicio
        Mockito.verify(taskService, times(1))
                .findAll();
    }

    @Test
    void findByIdTest() throws Exception {
        // Lo que vamos a simular
        when(taskService.findById(task.getId()))
                .thenReturn(task);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + task.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Proceso la respuesta
        Task res = mapper.readValue(response.getContentAsString(), Task.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getId(), task.getId())
        );

        // Verificamos que se ha llamado al método
        verify(taskService, times(1))
                .findById(task.getId());
    }

    @Test
    void findByIdNotFound() throws Exception {
        // Lo que vamos a simular
        when(taskService.findById(-1000L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se ha encontrado la tarea con id: -1000"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + -1000L)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Proceso la respuesta
        try {
            Task res = mapper.readValue(response.getContentAsString(), Task.class);
        } catch (Exception ignored) {
        }

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value())
        );

        // Verificamos que se ha llamado al método
        verify(taskService, times(1))
                .findById(-1000L);
    }

    @Test
    void createTest() throws Exception {
        // Lo que vamos a simular
        Task nueva = new Task(null, "Nueva tarea", "Descripción", null);
        Task creada = new Task(10L, "Nueva tarea", "Descripción", "pendiente");
        when(taskService.create(nueva))
                .thenReturn(creada);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON) // Indicamos el tipo de contenido
                                .content(mapper.writeValueAsString(nueva)) // Indicamos el contenido como JSON
                .andReturn().getResponse();

        // Proceso la respuesta
        Task res = mapper.readValue(response.getContentAsString(), Task.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.CREATED.value()),
                () -> assertEquals(res.getId(), 10L),
                () -> assertEquals("pendiente", res.getEstado())
        );

        // Verificamos que se ha llamado al método
        verify(taskService, times(1))
                .create(nueva);
    }
}
```

---

# Cobertura de código con JaCoCo en el proyecto _Raquetas / Tenistas_

![](../images/banner06.png)

## ¿Qué es la cobertura de código?

La **cobertura de código** (_code coverage_) mide **qué porcentaje del código de la aplicación fue ejecutado por los tests**.
No nos dice si los tests son “buenos”, pero sí **qué partes del código no se ejecutan nunca** durante las pruebas — y por tanto, podrían esconder errores o ramas no comprobadas.

En el contexto de la API de _Tenistas_ (o _Raquetas_):

- Si nunca probamos el método `findById()` cuando el ID no existe → esa rama queda sin cubrir.
- Si no probamos el `create()` cuando falta algún campo → el bloque `catch` o la excepción no se ejecutan.
- Si un DTO nunca se convierte en entidad → el mapper tiene líneas sin ejecutar.

---

## Activar cobertura con JaCoCo

Para medir la cobertura usaremos **JaCoCo**, una herramienta que se integra directamente con Maven.
Basta añadir este plugin en el `pom.xml` dentro de `<build><plugins>`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## Cómo generar el informe

Ejecuta desde terminal:

```bash
mvn clean verify
```

Esto ejecuta todos los tests (`unitarios`, `integración` y `MockMvc`) y genera el informe de cobertura.

📂 Resultado:
El informe HTML estará en:

```
target/site/jacoco/index.html
```

Ábrelo en el navegador (doble clic) y verás algo similar a esto:

```
Coverage Summary
----------------------------------------
Package                      Coverage
----------------------------------------
com.example.raquetas.model        20%
com.example.raquetas.repository   100%
com.example.raquetas.service      92%
com.example.raquetas.controller   76%
----------------------------------------
Total                             83%
```

---

## Cómo interpretar el informe

JaCoCo muestra los paquetes y clases de tu proyecto, y para cada uno:

- Qué porcentaje de líneas se ejecutaron.
- Qué ramas (`if`, `else`) quedaron sin probar.
- Qué métodos nunca fueron llamados por ningún test.

### Colores:

| Color       | Significado                                                              |
| ----------- | ------------------------------------------------------------------------ |
| 🟩 Verde    | Código ejecutado por algún test                                          |
| 🟨 Amarillo | Parcialmente ejecutado (por ejemplo, un `if` cubierto pero no su `else`) |
| 🟥 Rojo     | Nunca ejecutado (sin test que lo llame)                                  |

---

## 🧩 Ejemplo con el proyecto _Raquetas_

Supongamos que tienes los siguientes tests implementados:

| Clase                           | Tests realizados                            | Cobertura esperada |
| ------------------------------- | ------------------------------------------- | ------------------ |
| `RaquetasRepositoryImplTest`    | findAll, findById                           | 🟩 100%            |
| `RaquetasServiceImplTest`       | findAll, findById, findByIdNotFound         | 🟩 90%             |
| `RaquetasControllerMockMvcTest` | findAll, findById, create, findByIdNotFound | 🟨 70–80%          |
| `RaquetasMapperTest`            | Ninguno                                     | 🟥 0%              |

📘 El informe de JaCoCo mostrará claramente que:

- El servicio y el repositorio están casi totalmente cubiertos.
- El controlador aún tiene ramas no ejecutadas (por ejemplo, validaciones 400, 404, 500).
- El mapper no tiene cobertura, porque nunca se probó.

---

## Excluir clases triviales

JaCoCo también cuenta los _getters/setters_ de tus entidades (`Raqueta`, `RaquetaResponseDto`), lo cual puede bajar el porcentaje total.
Para ignorarlas:

```xml
<configuration>
    <excludes>
        <exclude>**/model/**</exclude>
        <exclude>**/dto/**</exclude>
        <exclude>**/RaquetasApplication*</exclude>
    </excludes>
</configuration>
```

Así solo se mide la cobertura **de la lógica de negocio y controladores**.

---

## Niveles de cobertura esperables en el proyecto _Raquetas_

| Capa             | Cobertura esperada | Comentario                                  |
| ---------------- | ------------------ | ------------------------------------------- |
| `Repository`     | 100 %              | Métodos sencillos, probados con integración |
| `Service`        | 85–95 %            | Incluye flujos de error y éxito             |
| `Controller`     | 70–85 %            | Endpoints principales cubiertos con MockMvc |
| `Mapper / DTO`   | 0–20 %             | Solo si se prueban explícitamente           |
| **Total global** | 75–90 %            | Excelente para un proyecto educativo        |

---

## Buenas prácticas

| Recomendación                                      | Motivo                                                 |
| -------------------------------------------------- | ------------------------------------------------------ |
| ✅ Revisa el informe `index.html` tras cada cambio | Te muestra visualmente qué código falta por probar     |
| ✅ Cubre primero lógica y controladores            | Son los puntos críticos de negocio                     |
| ❌ No persigas 100% literal                        | Es preferible un 80% bien pensado que tests de relleno |
| 🧩 Combina tests unitarios, integración y MockMvc  | El conjunto da la cobertura real                       |
| 🧹 Excluye clases triviales                        | Evita distorsionar el porcentaje total                 |

---

## Ejemplo de salida en consola

```
[INFO] --- jacoco-maven-plugin:0.8.12:report (report) @ raquetas ---
[INFO] Analyzed bundle 'raquetas' with 8 classes
[INFO] All reports generated: target/site/jacoco/index.html
[INFO] BUILD SUCCESS
```

---

# Ejemplo completo de testing

1. [Gestor de Tareas](https://github.com/docenciait/Labs_DWES_2526/tree/main/T1/UT5/init-testint-tareas/gestor-tareas)

# Práctica de clase, Testing

1. Testea el repositorio de Funkos
2. Testea el mapeador de Funkos
3. Testea el servicio de Funkos, usando mocks, teniendo en cuenta las excepciones
4. Testea el controlador de Funkos, usando mocks, teniendo en cuenta las respuestas correctas, las excepciones y los códigos de respuesta HTTP. No olvides de testear las restricciones de los DTOs.
