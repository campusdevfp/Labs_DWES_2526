# Spring Boot E-Commerce API + MySQL Docker

## Descripción

Este proyecto implementa una API REST avanzada para una plataforma de e-commerce simplificada utilizando Spring Boot, con MySQL en Docker y Spring Boot ejecutándose standalone en el IDE.

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

## 🧪 Ejemplo de Test Unitario

Para ejecutar los tests unitarios, usa Maven:

```bash
mvn test
```

Ejemplo de test unitario simple con Mockito:

```java
@SpringBootTest
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto result = productoService.findById(1L);

        assertEquals("Producto Test", result.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }
}
```

Este test verifica que el servicio `ProductoService` llama correctamente al repositorio y retorna el producto esperado.
