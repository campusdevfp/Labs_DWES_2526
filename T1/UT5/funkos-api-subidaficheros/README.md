Perfecto 👏 — aquí tienes un **resumen profesional y completo** del proyecto tal y como ha quedado implementado, con las **decisiones técnicas justificadas**, los **problemas detectados** y las **soluciones aplicadas paso a paso**.

---

## 🧩 Proyecto: *Mi primera API REST — Funkos API (Spring Boot + H2 + JPA)*

**Objetivo:**
Crear una API REST básica en Spring Boot que gestione un catálogo de Funkos con operaciones CRUD completas y carga inicial de datos desde un fichero CSV.

---

## 🧠 Estructura final del proyecto

```
src/
 ├── main/java/com/example/funkos/
 │    ├── FunkoApplication.java           → Clase principal Spring Boot
 │    ├── controller/
 │    │     └── FunkosRestController.java → Endpoints CRUD (GET, POST, PUT, PATCH, DELETE)
 │    ├── model/
 │    │     └── Funko.java                → Entidad JPA
 │    ├── repository/
 │    │     └── FunkoRepository.java      → Repositorio JPA con métodos personalizados
 │    ├── service/
 │    │     └── FunkoService.java         → Lógica de negocio CRUD
 │    └── config/
 │          └── DataLoader.java           → Carga inicial de Funkos desde CSV
 ├── main/resources/
 │    ├── application.properties          → Configuración H2, JPA
 │    └── data/funkos.csv                 → Fichero de datos iniciales
 └── test/java/…                          → Tests (opcional)
```

---

## ⚙️ 1️⃣ **Carga de datos desde CSV (DataLoader)**

### Problemas iniciales

* Errores `ArrayIndexOutOfBounds` → columnas del CSV no coincidían con el código.
* `NumberFormatException` → intentos de parsear fechas o textos como números.
* `IllegalArgumentException: UUID string too large` → UUIDs con formato incorrecto o duplicados.

### Decisiones tomadas

✅ Se confirmó que los UUIDs del CSV eran **válidos y correctos**, pero no era necesario conservarlos manualmente.
✅ Se decidió **dejar que Hibernate genere automáticamente los UUIDs**, eliminando `f.setId(...)` del código.
✅ Se estableció que los campos `modelo` y `fecha_lanzamiento` se lean correctamente según el orden del CSV.
✅ Se añadió una categoría derivada automáticamente del modelo (`marvel`, `disney`, `anime`, `otros`).

### Implementación final

```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

→ Hibernate genera IDs únicos válidos (UUID v4 estándar).

En `DataLoader.java`, los Funkos se cargan sin asignar manualmente el `id`, y el campo `categoria` se genera desde `modelo`.

---

## 🧱 2️⃣ **Modelo de dominio (Funko.java)**

### Campos finales

```java
UUID id
String nombre
String modelo
Double precio
Integer cantidad
String imagen
String categoria
LocalDate fechaLanzamiento
LocalDate fechaCreacion
LocalDate fechaActualizacion
```

### Validaciones

* `@NotBlank` en nombre y modelo
* `@DecimalMin("0.0")` en precio
* `@Min(0)` en cantidad

### Hooks de persistencia

```java
@PrePersist  → fechaCreacion = LocalDate.now();
@PreUpdate   → fechaActualizacion = LocalDate.now();
```

---

## 🧮 3️⃣ **Repositorio (FunkoRepository.java)**

### Implementación

```java
public interface FunkoRepository extends JpaRepository<Funko, UUID> {
    List<Funko> findByCategoriaIgnoreCase(String categoria);
}
```

👉 Permite búsquedas por categoría sin distinguir mayúsculas/minúsculas.

---

## 🔧 4️⃣ **Controlador (FunkosRestController.java)**

### Endpoints implementados

| Método                | Ruta                                                  | Descripción |
| --------------------- | ----------------------------------------------------- | ----------- |
| `GET /funkos`         | Devuelve todos los Funkos, o filtra por `?categoria=` |             |
| `GET /funkos/{id}`    | Devuelve un Funko por UUID                            |             |
| `POST /funkos`        | Crea un nuevo Funko                                   |             |
| `PUT /funkos/{id}`    | Actualiza un Funko completo                           |             |
| `PATCH /funkos/{id}`  | Actualiza solo campos específicos                     |             |
| `DELETE /funkos/{id}` | Elimina un Funko                                      |             |

### Ejemplo de filtro funcional:

```java
@GetMapping
public List<Funko> getAll(@RequestParam(required = false) String categoria) {
    if (categoria != null)
        return repository.findByCategoriaIgnoreCase(categoria);
    return repository.findAll();
}
```

---

## 🧪 5️⃣ **Base de datos (H2 en memoria)**

### Configuración final (`application.properties`)

```properties
spring.datasource.url=jdbc:h2:mem:funkosdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.hibernate.use-new-id-generator-mappings=true
spring.h2.console.enabled=true
```

Acceso a consola H2:
👉 [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
JDBC URL: `jdbc:h2:mem:funkosdb`

---

## 📬 6️⃣ **Pruebas con Postman**

### Colección básica

1. `GET http://localhost:8080/funkos`
2. `GET http://localhost:8080/funkos?categoria=disney`
3. `GET http://localhost:8080/funkos/{uuid}`
4. `POST http://localhost:8080/funkos`

   ```json
   {
     "nombre": "Nuevo Funko",
     "modelo": "OTROS",
     "precio": 19.99
   }
   ```
5. `PUT / PATCH / DELETE` para verificar actualización y eliminación.

✅ Confirmado que el filtro por `?categoria=` funciona tras derivar el valor desde `modelo`.

---

## 📁 8️⃣ **Subida de ficheros**

### Implementación añadida

Se ha implementado la funcionalidad de subida de ficheros para almacenar imágenes de los Funkos. Esto incluye:

- **Interfaz `StorageService`**: Define métodos para almacenar, cargar y gestionar ficheros.
- **Implementación `FileSystemStorageService`**: Almacena ficheros en el sistema de archivos local.
- **Controlador `FilesController`**: Maneja la subida de ficheros genéricos y su descarga.
- **Configuración `StorageConfig`**: Inicializa el servicio de almacenamiento al arrancar la aplicación.
- **Endpoint en `FunkosRestController`**: `PATCH /funkos/imagen/{id}` para subir una imagen a un Funko específico.

#### Detalles de la implementación del Storage

- **StorageService**: Interfaz con métodos como `init()`, `store(MultipartFile)`, `loadAll()`, `load(String)`, `loadAsResource(String)`, `deleteAll()`, `getUrl(String)`.
- **FileSystemStorageService**: Implementa el almacenamiento en disco. Usa `Paths.get(path)` para el directorio raíz, copia ficheros con `Files.copy()`, genera URLs con `MvcUriComponentsBuilder`.
- **StorageNotFoundException**: Excepción personalizada para errores de almacenamiento.
- **StorageConfig**: Bean `CommandLineRunner` que inicializa el storage y opcionalmente borra ficheros si `upload.delete=true`.

##### Estructura de clases clave

**Interfaz StorageService**:
```java
public interface StorageService {
    void init();
    String store(MultipartFile file);
    Stream<Path> loadAll();
    Path load(String filename);
    Resource loadAsResource(String filename);
    void deleteAll();
    String getUrl(String filename);
}
```

**Implementación FileSystemStorageService**:
```java
@Service
public class FileSystemStorageService implements StorageService {
    private final Path rootLocation;

    public FileSystemStorageService(@Value("${upload.root-location}") String path) {
        this.rootLocation = Paths.get(path);
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageNotFoundException("No se puede inicializar el almacenamiento", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageNotFoundException("Fichero vacío " + file.getOriginalFilename());
            }
            String filename = file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new StorageNotFoundException("Fallo al almacenar fichero " + file.getOriginalFilename(), e);
        }
    }

    // Otros métodos: loadAll, load, loadAsResource, deleteAll, getUrl
}
```

**Excepción StorageNotFoundException**:
```java
public class StorageNotFoundException extends RuntimeException {
    public StorageNotFoundException(String message) {
        super(message);
    }

    public StorageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Configuración StorageConfig**:
```java
@Configuration
public class StorageConfig {
    private static final Logger log = Logger.getLogger(StorageConfig.class.getName());

    @Bean
    public CommandLineRunner init(StorageService storageService, @Value("${upload.delete}") String deleteAll) {
        return args -> {
            if (deleteAll.equals("true")) {
                log.info("Borrando ficheros de almacenamiento...");
                storageService.deleteAll();
            }
            storageService.init();
        };
    }
}
```

**Controlador FilesController**:
```java
@RestController
@RequestMapping("/api/files")
public class FilesController {
    private StorageService storageService;

    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        Resource file = storageService.loadAsResource(filename);
        // Lógica para determinar contentType y devolver ResponseEntity
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestPart("file") MultipartFile file) {
        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            String urlImagen = storageService.getUrl(imagen);
            Map<String, Object> response = Map.of("url", urlImagen);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede subir un fichero vacío");
        }
    }
}
```

**Modificación en FunkosRestController** (añadiendo el endpoint de imagen):
```java
@RestController
@RequestMapping("/funkos")
public class FunkosRestController {
    private final FunkoService service;
    private final StorageService storageService;

    public FunkosRestController(FunkoService service, StorageService storageService) {
        this.service = service;
        this.storageService = storageService;
    }

    // Otros métodos CRUD...

    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Funko> updateImagen(@PathVariable UUID id, @RequestPart("file") MultipartFile file) {
        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            String urlImagen = storageService.getUrl(imagen);

            Funko funko = service.getById(id);
            funko.setImagen(urlImagen);
            return ResponseEntity.ok(service.update(id, funko));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado la imagen");
        }
    }
}
```
````
