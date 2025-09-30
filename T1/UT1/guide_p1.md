Perfecto 👌. Vamos a desmenuzar **la sección `<build>` de tu `pom.xml`**, que es donde Maven controla **cómo compilar, empaquetar y hacer ejecutable el proyecto**.

---

# 📂 Sección `<build>` de tu POM

```xml
<build>
  <plugins>
    <!-- Compilador Java -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.11.0</version>
      <configuration>
        <source>17</source>
        <target>17</target>
      </configuration>
    </plugin>

    <!-- Empaquetado en un JAR ejecutable con dependencias -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-assembly-plugin</artifactId>
      <version>3.6.0</version>
      <configuration>
        <archive>
          <manifest>
            <mainClass>app.Main</mainClass>
          </manifest>
        </archive>
        <descriptorRefs>
          <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
      </configuration>
      <executions>
        <execution>
          <id>make-assembly</id>
          <phase>package</phase>
          <goals>
            <goal>single</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

---

## 🔹 1. `maven-compiler-plugin`

Este plugin indica a Maven **cómo compilar el código fuente Java**.

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.11.0</version>
  <configuration>
    <source>17</source>
    <target>17</target>
  </configuration>
</plugin>
```

- **`<source>`** → versión del lenguaje Java que admite el compilador (sintaxis).
- **`<target>`** → versión mínima de JVM necesaria para ejecutar el `.class`.
- Con `17/17`, tu código usa sintaxis de Java 17 y el bytecode solo funciona en JVM 17+.
- Si lo subes a `21/21`, obligas a que todo se ejecute en Java 21.

👉 Beneficio: te aseguras de que todos compilan con el mismo nivel de Java.

---

## 🔹 2. `maven-assembly-plugin`

Este es el que se encarga de **empaquetar el proyecto en un JAR ejecutable con todas las dependencias incluidas**.

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-assembly-plugin</artifactId>
  <version>3.6.0</version>
  <configuration>
    <archive>
      <manifest>
        <mainClass>app.Main</mainClass>
      </manifest>
    </archive>
    <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>
    </descriptorRefs>
  </configuration>
  <executions>
    <execution>
      <id>make-assembly</id>
      <phase>package</phase>
      <goals>
        <goal>single</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### a) `<archive><manifest><mainClass>>`

Define la clase que contiene el método `public static void main(String[] args)` para que el JAR sea ejecutable:

```bash
java -jar torneo_tenis-...-with-dependencies.jar entrada.csv salida.json
```

En tu caso → `app.Main`.

---

### b) `<descriptorRefs>`

Elige un “descriptor” predefinido:

- **`jar-with-dependencies`** → empaqueta _tu código + todas las librerías_ (SQLite, Jackson…) en un solo archivo `.jar`.
- Esto es lo que genera el famoso `-jar-with-dependencies.jar`.

---

### c) `<executions>`

Indica **cuándo** ejecutar este plugin dentro del ciclo de vida Maven:

- `<phase>package</phase>` → se activa al hacer `mvn package`.
- `<goal>single</goal>` → genera el “fat jar” único.

---

## 📊 Resultado de este build

Cuando ejecutas `mvn package` obtienes en `target/`:

1. `torneo_tenis-1.0-SNAPSHOT.jar`
   → solo tu código compilado (sin dependencias).

2. `torneo_tenis-1.0-SNAPSHOT-jar-with-dependencies.jar`
   → tu código + todas las librerías → **este es el ejecutable real**.

---

## 🔹 3. Alternativa: `maven-shade-plugin`

Si quieres **un solo jar ejecutable** (sin duplicados), puedes sustituir `assembly` por `shade`:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.5.0</version>
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>shade</goal>
      </goals>
      <configuration>
        <transformers>
          <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
            <mainClass>app.Main</mainClass>
          </transformer>
        </transformers>
      </configuration>
    </execution>
  </executions>
</plugin>
```

👉 Esto crea directamente `torneo_tenis-1.0-SNAPSHOT.jar` ya con dependencias y listo para `java -jar`.

---

# ✅ Resumen

- **`maven-compiler-plugin`** → controla la versión de Java para compilar/ejecutar.
- **`maven-assembly-plugin`** → crea un “fat jar” con dependencias, ejecutable desde consola.
- Se generan **dos jars**: el normal y el con dependencias.
- Si quieres **solo uno**, usa `maven-shade-plugin`.

---

Sí 👍, en **IntelliJ IDEA** no estás obligado a depender solo del `assembly` o `shade` en el `pom.xml`.
El IDE tiene varias formas de configurar y ejecutar tu **.jar** sin complicarte.

---

# 🔹 Opciones en IntelliJ para configurar el JAR

## 1. **Artifact desde Project Structure**

1. Ve a: `File → Project Structure → Artifacts`.
2. Clic en `+` → _Jar_ → _From modules with dependencies_.
3. Elige:

   - **Main class**: `app.Main`.
   - Marca la opción _Include in project build_.

4. Aplica cambios.
5. Para generar el jar:
   `Build → Build Artifacts → torneo_tenis.jar → Build`.

👉 Esto te crea un jar ejecutable en la carpeta `out/artifacts/...` **con las dependencias incluidas** (si marcas `Extract to the target JAR`).

---

## 2. **Run/Debug Configurations**

1. Arriba, en la barra de configuraciones, pulsa `Add Configuration…`.
2. Elige tipo **Application**.
3. Rellena:

   - **Main class** = `app.Main`.
   - **Program arguments** = `entrada.csv salida.json`.
   - **Working directory** = raíz del proyecto.

4. Con esto puedes **ejecutar y depurar** sin generar JAR todavía.
5. Luego, cuando quieras el JAR, usas `Build Artifact` o `mvn package`.

---

## 3. **Build → Build Artifacts** (manual)

Una vez creado el _artifact_ (opción 1), puedes:

- `Build → Build Artifacts → Rebuild`.
- IntelliJ te genera el `.jar` listo en `out/artifacts/`.
- Lo ejecutas con:

  ```bash
  java -jar out/artifacts/torneo_tenis.jar entrada.csv salida.json
  ```

---

## 4. **Plugin Shade en Maven (opcional, desde IntelliJ)**

Ya lo comentamos antes: si quieres que **Maven genere directamente un solo jar ejecutable**, puedes configurar `maven-shade-plugin`. IntelliJ lo detecta porque integra Maven, así que con un clic en `Lifecycle → package` se crea el jar final.

---

# ✅ Resumen para clase

- **Método Maven (`assembly` o `shade`)** → reproducible, automatizado, ideal para entrega.
- **Método Artifact de IntelliJ** → rápido para demos, no dependes de Maven, jar sale en `out/artifacts/`.
- **Run/Debug Config** → no crea jar, pero es la forma correcta para **depurar con breakpoints**.

---

¡Perfecto! Vamos a desglosar **todas las dependencias de tu `pom.xml`** y qué papel cumplen en el proyecto `torneo_tenis`.

---

# 📦 Dependencias del proyecto

```xml
<dependencies>
  <dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.46.0.0</version>
  </dependency>

  <dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.0</version>
  </dependency>

  <dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.17.0</version>
  </dependency>
</dependencies>
```

---

## 🔹 1. SQLite JDBC Driver

```xml
<dependency>
  <groupId>org.xerial</groupId>
  <artifactId>sqlite-jdbc</artifactId>
  <version>3.46.0.0</version>
</dependency>
```

- Librería oficial para conectar **Java con SQLite** usando JDBC.
- Permite abrir conexiones con:

  ```java
  Connection conn = DriverManager.getConnection("jdbc:sqlite:data/tenistas.db");
  ```

- Sin ella, `java.sql.DriverManager` no sabría interpretar `jdbc:sqlite:...`.
- Se encarga de crear el archivo físico `tenistas.db` en la carpeta `data/`.

👉 Es fundamental porque el enunciado exige almacenar los datos en SQLite.

---

## 🔹 2. Jackson Databind

```xml
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
  <version>2.17.0</version>
</dependency>
```

- Parte del ecosistema **Jackson** (la librería más usada en Java para JSON).
- `jackson-databind` combina:

  - **jackson-core** → procesador JSON de bajo nivel.
  - **jackson-annotations** → soporte de anotaciones como `@JsonProperty`.
  - **databind** → mapea objetos Java ↔ JSON automáticamente.

Ejemplo en tu proyecto:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(new File("torneo_tenis.json"), listaTenistas);
```

👉 Permite exportar los datos a **JSON** como pide el enunciado.

---

## 🔹 3. Jackson Dataformat XML

```xml
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-xml</artifactId>
  <version>2.17.0</version>
</dependency>
```

- Extiende Jackson para soportar **XML**.
- Usa `XmlMapper` en lugar de `ObjectMapper`.

Ejemplo:

```java
XmlMapper xmlMapper = new XmlMapper();
xmlMapper.writeValue(new File("torneo_tenis.xml"), listaTenistas);
```

👉 Gracias a esta dependencia, además de JSON puedes exportar también a **XML**, cumpliendo el requisito de salida múltiple (`.csv`, `.json`, `.xml`).

---

# 📊 Relación con el enunciado

1. **SQLite JDBC** → persistencia de datos en BD (`tenistas.db`).
2. **Jackson Databind** → exportar a JSON.
3. **Jackson Dataformat XML** → exportar a XML.

El CSV de entrada/salida se maneja con **Java estándar (I/O y `String.split`)**, por eso no hay dependencia adicional como _OpenCSV_.

---

# ✅ Resumen rápido

- **sqlite-jdbc** → “puente” entre Java y SQLite (leer/escribir BD).
- **jackson-databind** → convierte listas de objetos Java en JSON.
- **jackson-dataformat-xml** → convierte listas de objetos Java en XML.
- Todas juntas → permiten importar CSV → guardar en BD → consultas → exportar a CSV/JSON/XML.

---

Muy buena cuestión 👌. Te explico **la estructura del proyecto `torneo_tenis`**, por qué está diseñada así y cómo se relaciona con **principios de arquitectura de software** que verías en un curso profesional.

---

# 📂 Estructura del proyecto

```
src/main/java/
  app/
    Main.java
    config/
      Config.java
      LogConfig.java
    domain/
      Tenista.java
      Mano.java
    repository/
      TenistaRepository.java
      sqlite/
        TenistaRepositorySqlite.java
    util/
      FifoCache.java

src/main/resources/
  application.properties

pom.xml
```

---

# 🔎 Justificación de la estructura

## 1. Paquete `app`

- Punto de entrada (`Main`).
- Aquí orquestas todo el flujo: cargar configuración, preparar BD, importar CSV, lanzar consultas, exportar salida.

👉 Separa la “aplicación” del dominio e infraestructura.

---

## 2. Paquete `config`

- `Config.java`: gestión de `application.properties`.
- `LogConfig.java`: configuración de logging.

👉 Son **infraestructura transversal**: no forman parte del dominio, pero son necesarias para que la app funcione correctamente.

---

## 3. Paquete `domain`

- `Tenista.java`: **Entidad** de dominio.
- `Mano.java`: **Value Object** (enum).

👉 Representan la realidad del negocio (jugadores, mano dominante).
👉 Se alinean con **Domain-Driven Design (DDD)**: el núcleo del sistema está en el dominio.

---

## 4. Paquete `repository`

- `TenistaRepository`: **interfaz** que define el contrato de persistencia.
- `sqlite/TenistaRepositorySqlite`: implementación concreta con SQLite y caché FIFO.

👉 Ejemplo de **Dependency Inversion Principle (DIP)**:

- El dominio depende de una abstracción (`TenistaRepository`).
- La infraestructura implementa esa abstracción.

Esto refleja el patrón **Repository** típico en DDD.

---

## 5. Paquete `util`

- `FifoCache`: clase de utilidad para la caché FIFO.

👉 No es parte del dominio ni de la aplicación, sino un recurso reutilizable.

---

## 6. `resources/`

- `application.properties`: archivo de configuración externo (aunque en este proyecto aún no se usa en serio).

👉 Permite separar **configuración** del código.

---

# 📊 Correlación con Arquitecturas de Software

1. **Arquitectura en capas (Layered Architecture)**

   - **Presentación / Entrada**: `Main.java` (interfaz de consola).
   - **Aplicación**: orquestación (cargar CSV, ejecutar consultas, exportar resultados).
   - **Dominio**: `Tenista`, `Mano`.
   - **Infraestructura**: repositorio SQLite, logging, config, caché.

2. **Arquitectura Hexagonal (Ports & Adapters)**

   - **Dominio** = centro (`Tenista`, `Mano`).
   - **Puerto** = interfaz `TenistaRepository`.
   - **Adaptador** = `TenistaRepositorySqlite` (infraestructura que conecta BD real).
   - **Entradas** = `Main` (entrada por consola).
   - **Salidas** = exportación a CSV/JSON/XML.

3. **Principios SOLID**

   - **SRP**: cada clase tiene una única responsabilidad (ej. `LogConfig` solo logging).
   - **OCP**: se pueden añadir otros formatos de exportación sin modificar lo existente.
   - **DIP**: el servicio depende de la interfaz, no de SQLite concreto.

---

# ✅ Resumen

- La estructura separa **dominio, aplicación e infraestructura**.
- Está inspirada en **arquitectura hexagonal** y **en capas**.
- Cumple con principios **SOLID** para que el código sea extensible y mantenible.
- Así los alumnos ven en un proyecto sencillo cómo se aplican **patrones de arquitectura de software profesional**.

---

Perfecto 👌. Te explico con detalle **qué hace `TenistaRepositorySqlite.java`**, cómo está organizado y cómo encaja en la arquitectura.

---

# 📄 Clase `TenistaRepositorySqlite`

## 📌 Rol en la arquitectura

- Es la **implementación concreta** de la interfaz `TenistaRepository`.
- Está en el paquete `repository.sqlite` → **adaptador de infraestructura** en una arquitectura hexagonal.
- Usa **JDBC** para conectarse a **SQLite** y además implementa la **caché FIFO** para optimizar `findById`.

---

## 📂 Estructura general

```java
public class TenistaRepositorySqlite implements TenistaRepository {
    private static final String DB_URL = "jdbc:sqlite:data/tenistas.db";
    private final FifoCache<Long, Tenista> cache = new FifoCache<>(5);

    public TenistaRepositorySqlite() {
        initSchema(true);
    }

    // Métodos principales:
    public List<Tenista> findAll()
    public Optional<Tenista> findById(long id)
    public List<Tenista> findByPais(String pais)
    public List<Tenista> findAllOrderByPuntosDesc()
    public Tenista insert(Tenista t)
    public Tenista update(Tenista t)
    public boolean delete(long id)
}
```

---

## 🔹 Atributos principales

- **`DB_URL`**: conexión a la BD SQLite (hardcodeado en el proyecto, aunque lo ideal sería leerlo de `Config`).
- **`cache`**: instancia de `FifoCache<Long, Tenista>` con tamaño 5, como pedía el enunciado.

---

## 🔹 Conexión a la BD

```java
private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL);
}
```

👉 Cada operación abre su propia conexión con `DriverManager`.
SQLite es embebido y rápido, así que esto es suficiente (aunque en proyectos grandes se usaría un pool de conexiones).

---

## 🔹 Inicialización del esquema

```java
public void initSchema(boolean reset) {
    try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
        if (reset) {
            stmt.executeUpdate("DROP TABLE IF EXISTS tenistas");
        }
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS tenistas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                pais TEXT NOT NULL,
                altura INTEGER NOT NULL,
                peso INTEGER NOT NULL,
                puntos INTEGER NOT NULL,
                mano TEXT NOT NULL,
                fecha_nacimiento TEXT NOT NULL,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )
        """);
    }
}
```

- Al arrancar, **resetea la tabla** (si `reset = true`).
- Cumple el enunciado: “la BD debe vaciarse al iniciarse la aplicación”.

---

## 🔹 Operaciones CRUD

### ➤ `findAll()`

- Ejecuta `SELECT * FROM tenistas`.
- Convierte cada fila en un objeto `Tenista`.
- Devuelve lista ordenada (por defecto en orden de inserción).

### ➤ `findById(long id)`

1. Busca primero en la **caché FIFO**:

   ```java
   Tenista cached = cache.get(id);
   if (cached != null) return Optional.of(cached);
   ```

2. Si no está, consulta en la BD.
3. Si lo encuentra, lo mete en la caché (`cache.put(id, t)`).

👉 Aquí se cumple el requisito de usar caché.

### ➤ `findByPais(String pais)`

- Consulta todos los tenistas con ese país.
- Devuelve lista de objetos `Tenista`.

### ➤ `findAllOrderByPuntosDesc()`

- Consulta con `ORDER BY puntos DESC`.
- Útil para ranking.

### ➤ `insert(Tenista t)`

- Inserta en BD con `INSERT INTO`.
- Recupera la **clave generada (id autoincrement)**.
- Actualiza `created_at` y `updated_at`.
- Mete el objeto en la caché.

### ➤ `update(Tenista t)`

- Ejecuta `UPDATE tenistas SET ... WHERE id = ?`.
- Actualiza `updated_at`.
- Refresca la caché.

### ➤ `delete(long id)`

- Borra de BD con `DELETE`.
- Quita de la caché también.

---

## 🔹 Conversión ResultSet → Tenista

El mapeo de filas SQL a objetos se hace manualmente con `rs.getXXX(columna)`.

```java
Tenista t = new Tenista(
    rs.getLong("id"),
    rs.getString("nombre"),
    rs.getString("pais"),
    rs.getInt("altura"),
    rs.getInt("peso"),
    rs.getInt("puntos"),
    Mano.valueOf(rs.getString("mano")),
    LocalDate.parse(rs.getString("fecha_nacimiento")),
    LocalDateTime.parse(rs.getString("created_at")),
    LocalDateTime.parse(rs.getString("updated_at"))
);
```

👉 Aquí entra en juego el `enum Mano` y las fechas ISO 8601.

---

# 📊 Relación con la arquitectura

- **Dominio**: `Tenista` (modelo de negocio).
- **Puerto**: `TenistaRepository` (interfaz).
- **Adaptador**: `TenistaRepositorySqlite` (infraestructura con JDBC + caché).

Esto es **arquitectura hexagonal (ports & adapters)** aplicada.
El servicio y el resto del sistema trabajan con la interfaz `TenistaRepository` → no dependen de SQLite directamente.

---

# 📄 Explicación de `TenistaService`

```java
public class TenistaService {
    private final TenistaRepository repo;

    public TenistaService(TenistaRepository repo) {
        this.repo = repo;
    }
```

- Tiene un único atributo: el repositorio (`repo`), inyectado por el constructor.
- Así desacopla la lógica de negocio de la infraestructura (ejemplo: SQLite).

---

## 🔹 `getAllOrderByPuntosDesc()`

```java
public List<Tenista> getAllOrderByPuntosDesc() throws Exception {
    return repo.findAllOrderByPuntosDesc();
}
```

- Devuelve todos los tenistas ordenados por **puntos de mayor a menor**.
- Delegación directa al repositorio.
- Útil para mostrar el **ranking general**.

---

## 🔹 `mediaAltura()`

```java
public double mediaAltura() throws Exception {
    List<Tenista> list = repo.findAll();
    if (list.isEmpty()) return 0.0;
    long sum = 0;
    for (int i = 0; i < list.size(); i++) sum += list.get(i).getAltura();
    return (double) sum / list.size();
}
```

- Recupera todos los tenistas.
- Si no hay, devuelve `0.0`.
- Calcula la **media aritmética de altura** (cm).

---

## 🔹 `mediaPeso()`

```java
public double mediaPeso() throws Exception {
    List<Tenista> list = repo.findAll();
    if (list.isEmpty()) return 0.0;
    long sum = 0;
    for (int i = 0; i < list.size(); i++) sum += list.get(i).getPeso();
    return (double) sum / list.size();
}
```

- Igual que `mediaAltura`, pero con el **peso en kg**.

---

## 🔹 `tenistaMasAlto()`

```java
public Tenista tenistaMasAlto() throws Exception {
    List<Tenista> list = repo.findAll();
    Tenista best = null;
    for (int i = 0; i < list.size(); i++) {
        Tenista t = list.get(i);
        if (best == null || t.getAltura() > best.getAltura()) best = t;
    }
    return best;
}
```

- Busca el **tenista más alto** de la lista.
- Si la lista está vacía → devuelve `null`.
- Usa una comparación simple entre alturas.

---

## 🔹 `tenistasPorPais(String pais)`

```java
public List<Tenista> tenistasPorPais(String pais) throws Exception {
    return repo.findByPais(pais);
}
```

- Devuelve todos los tenistas cuyo país coincide con el parámetro.
- Delegación directa al repositorio.

---

## 🔹 `printTenistasAgrupadosPorPais()`

```java
public void printTenistasAgrupadosPorPais() throws Exception {
    List<Tenista> list = repo.findAll();
    Map<String, List<Tenista>> map = new HashMap<>();
    for (int i = 0; i < list.size(); i++) {
        Tenista t = list.get(i);
        List<Tenista> group = map.get(t.getPais());
        if (group == null) {
            group = new ArrayList<>();
            map.put(t.getPais(), group);
        }
        group.add(t);
    }
    for (Map.Entry<String, List<Tenista>> e : map.entrySet()) {
        System.out.println("- " + e.getKey());
        List<Tenista> gl = e.getValue();
        for (int i = 0; i < gl.size(); i++) {
            System.out.println("   " + gl.get(i).toShortString());
        }
    }
}
```

- Agrupa tenistas en un `Map<pais, listaTenistas>`.
- Luego imprime cada país y sus jugadores.
- Usa `toShortString()` (seguramente muestra nombre+puntos).

👉 Relacionado con el enunciado: _“tenistas agrupados por país”_.

---

## 🔹 `printNumTenistasPorPaisOrdenadosPorPuntos()`

```java
public void printNumTenistasPorPaisOrdenadosPorPuntos() throws Exception {
    List<Tenista> list = repo.findAll();
    Map<String, Integer> count = new HashMap<>();
    Map<String, List<Tenista>> map = new HashMap<>();
    for (int i = 0; i < list.size(); i++) {
        Tenista t = list.get(i);
        Integer c = count.get(t.getPais());
        count.put(t.getPais(), c == null ? 1 : c + 1);
        List<Tenista> gl = map.get(t.getPais());
        if (gl == null) {
            gl = new ArrayList<>();
            map.put(t.getPais(), gl);
        }
        gl.add(t);
    }
    for (Map.Entry<String, Integer> e : count.entrySet()) {
        System.out.println(String.format(Locale.ROOT, "%s -> %d tenistas", e.getKey(), e.getValue()));
        List<Tenista> gl = map.get(e.getKey());
        for (int i = 0; i < gl.size(); i++) {
            System.out.println("   " + gl.get(i).toShortString());
        }
    }
}
```

- Cuenta cuántos tenistas hay por país (`count`).
- También guarda sus listas (`map`).
- Imprime: _“España -> 3 tenistas”_ seguido de la lista.

👉 Relacionado con: _“número de tenistas agrupados por país y ordenados por puntos descendente”_.

---

## 🔹 `printTenistasPorManoConMedia()`

```java
public void printTenistasPorManoConMedia() throws Exception {
    List<Tenista> list = repo.findAll();
    Map<Mano, Integer> count = new HashMap<>();
    Map<Mano, Long> sum = new HashMap<>();
    for (int i = 0; i < list.size(); i++) {
        Tenista t = list.get(i);
        Mano m = t.getMano();
        count.put(m, (count.get(m) == null ? 0 : count.get(m)) + 1);
        sum.put(m, (sum.get(m) == null ? 0L : sum.get(m)) + t.getPuntos());
    }
    for (Map.Entry<Mano, Integer> e : count.entrySet()) {
        Mano m = e.getKey();
        int c = e.getValue();
        long s = sum.get(m) == null ? 0L : sum.get(m);
        double media = c == 0 ? 0.0 : (double) s / c;
        System.out.println(m + " -> n=" + c + ", media puntos=" + String.format(Locale.ROOT, "%.2f", media));
    }
}
```

- Agrupa tenistas por **mano dominante (DIESTRO/ZURDO)**.
- Cuenta cuántos hay de cada tipo.
- Calcula la media de sus puntos.
- Imprime resumen.

👉 Relacionado con: _“número de tenistas agrupados por mano dominante y puntuación media de ellos”_.

---

## 🔹 `printPuntuacionTotalPorPais()`

```java
public void printPuntuacionTotalPorPais() throws Exception {
    List<Tenista> list = repo.findAll();
    Map<String, Long> sum = new HashMap<>();
    for (int i = 0; i < list.size(); i++) {
        Tenista t = list.get(i);
        String k = t.getPais();
        sum.put(k, (sum.get(k) == null ? 0L : sum.get(k)) + t.getPuntos());
    }
    for (Map.Entry<String, Long> e : sum.entrySet()) {
        System.out.println(e.getKey() + " -> total=" + e.getValue());
    }
}
```

- Calcula la **suma de puntos por país**.
- Imprime algo como: _“España -> total=18500”_.

👉 Relacionado con: _“puntuación total de los tenistas agrupados por país”_.

---

## 🔹 `paisConMasPuntuacionTotal()`

```java
public String paisConMasPuntuacionTotal() throws Exception {
    List<Tenista> list = repo.findAll();
    Map<String, Long> sum = new HashMap<>();
    for (int i = 0; i < list.size(); i++) {
        Tenista t = list.get(i);
        String k = t.getPais();
        sum.put(k, (sum.get(k) == null ? 0L : sum.get(k)) + t.getPuntos());
    }
    String bestPais = null;
    long best = Long.MIN_VALUE;
    for (Map.Entry<String, Long> e : sum.entrySet()) {
        if (e.getValue() > best) {
            best = e.getValue();
            bestPais = e.getKey();
        }
    }
    return bestPais == null ? "N/A" : bestPais + " (" + best + ")";
}
```

- Calcula qué país tiene la **mayor puntuación acumulada**.
- Devuelve un `String` como _“España (18500)”_.

👉 Relacionado con: _“país con más puntuación total”_.

---

## 🔹 `mejorRankingDePais(String pais)`

```java
public Tenista mejorRankingDePais(String pais) throws Exception {
    List<Tenista> list = repo.findByPais(pais);
    if (list.isEmpty()) return null;
    return list.get(0); // ya ordenado por puntos desc
}
```

- Recupera todos los tenistas de un país.
- Devuelve el primero (suponiendo que `repo.findByPais` ya devuelve ordenados por puntos DESC).
- Es el **mejor jugador de ese país en el ranking**.

👉 Relacionado con: _“tenista con mejor ranking de España”_.

---

# ✅ Resumen para explicar en clase

- `TenistaService` implementa **todas las consultas del enunciado**.
- **Agrupaciones** → por país y por mano.
- **Estadísticas** → medias de altura, peso, puntos.
- **Búsquedas** → más alto, mejor ranking de un país.
- **Totales** → puntuación total por país y país con mayor puntuación.

👉 Es la capa que **convierte datos crudos** en información útil para el problema del torneo.
👉 Relaciona directamente el enunciado con código → gran ejemplo didáctico.

---

¡Muy bien traído! 👌 Vamos a analizar `CsvReader.java` en detalle, porque es una de las piezas clave: **lee el fichero de entrada** y además **valida los datos antes de insertarlos en la base de datos** (tal y como exige el enunciado).

---

# 📄 Clase `CsvReader`

## 🔹 Método principal `readTenistas(File csv)`

```java
public static List<Tenista> readTenistas(File csv) throws Exception {
    List<Tenista> list = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
        String line;
        int row = 0;
        while ((line = br.readLine()) != null) {
            row++;
            line = line.trim();
            if (line.isEmpty()) continue;
            if (row == 1 && line.toLowerCase().startsWith("nombre,")) {
                // cabecera
                continue;
            }
            String[] parts = line.split(",", -1);
            if (parts.length != 7) {
                throw new IllegalArgumentException("Fila " + row + ": número de columnas incorrecto");
            }

            String nombre = parts[0].trim();
            String pais = parts[1].trim();
            int altura = parsePositiveInt(parts[2].trim(), "altura", row);
            int peso = parsePositiveInt(parts[3].trim(), "peso", row);
            int puntos = parseNonNegativeInt(parts[4].trim(), "puntos", row);
            Mano mano = Mano.from(parts[5].trim());
            LocalDate fecha = LocalDate.parse(parts[6].trim()); // ISO 8601

            Tenista t = new Tenista(null, nombre, pais, altura, peso, puntos, mano, fecha, null, null);
            list.add(t);
        }
    }
    return list;
}
```

### Paso a paso:

1. Abre el CSV con `BufferedReader`.
2. Recorre línea a línea (`while`).
3. **Salta la cabecera** si la primera fila empieza con `nombre,`.
4. Divide cada línea por comas:

   - Si no hay **7 columnas exactas**, lanza `IllegalArgumentException`.

5. Lee y valida cada campo:

   - `nombre`: texto obligatorio (se hace `trim()`).
   - `pais`: texto obligatorio.
   - `altura`: debe ser `> 0` (usa `parsePositiveInt`).
   - `peso`: debe ser `> 0`.
   - `puntos`: debe ser `>= 0` (usa `parseNonNegativeInt`).
   - `mano`: convierte con `Mano.from(...)`, que valida `DIESTRO` o `ZURDO`.
   - `fecha`: `LocalDate.parse(...)` exige formato **ISO 8601** (`YYYY-MM-DD`).

6. Crea un `Tenista` (sin `id`, `created_at`, `updated_at` todavía).
7. Añade a la lista.
8. Devuelve la lista completa de tenistas.

---

## 🔹 Métodos auxiliares de validación

### `parsePositiveInt`

```java
private static int parsePositiveInt(String s, String field, int row) {
    int v = Integer.parseInt(s);
    if (v <= 0) throw new IllegalArgumentException("Fila " + row + ": " + field + " debe ser > 0");
    return v;
}
```

- Convierte `s` a entero.
- Si es `<= 0`, lanza excepción con mensaje detallado (incluye número de fila y nombre del campo).

---

### `parseNonNegativeInt`

```java
private static int parseNonNegativeInt(String s, String field, int row) {
    int v = Integer.parseInt(s);
    if (v < 0) throw new IllegalArgumentException("Fila " + row + ": " + field + " debe ser >= 0");
    return v;
}
```

- Igual, pero acepta `0`.
- Usado para el campo **puntos**.

---

# ✅ Validaciones que cumple `CsvReader`

- **Número correcto de columnas** (7).
- **Formato correcto de fecha** (ISO 8601).
- **Altura > 0**.
- **Peso > 0**.
- **Puntos >= 0**.
- **Mano válida** (`DIESTRO` o `ZURDO`, gracias a `Mano.from`).
- Mensajes de error claros con número de fila → muy didáctico.

---

# 📊 Relación con el enunciado

> _“El fichero de entrada debe ser abierto en modo lectura y debes analizar que todos los datos son válidos y están en formato correcto antes de ser insertados en la base de datos.”_

👉 `CsvReader` cumple esto al 100%.

- Si hay un error → lanza excepción y se detiene.
- Solo tenistas correctos pasan al siguiente paso (inserción en BD).

---

¡Muy bien! 👌 Ahora vamos a analizar con lupa **`OutputWriter.java`**, que es la pieza que se encarga de **escribir el fichero de salida** en el formato que el usuario haya indicado: `.json`, `.csv` o `.xml`.

---

# 📄 Clase `OutputWriter`

### 🔹 Método principal: `write`

```java
public static void write(String outputPath, List<Tenista> list) throws Exception {
    String lower = outputPath.toLowerCase(Locale.ROOT);
    if (lower.endsWith(".json")) writeJson(outputPath, list);
    else if (lower.endsWith(".csv")) writeCsv(outputPath, list);
    else if (lower.endsWith(".xml")) writeXml(outputPath, list);
    else throw new IllegalArgumentException("Extensión no soportada: " + outputPath);
}
```

- Recibe la **ruta de salida** y la lista de `Tenista`.
- Detecta la extensión (`.json`, `.csv`, `.xml`).
- Llama al método específico.
- Si la extensión no es válida → lanza excepción.

👉 Aquí se cumple el requisito del enunciado: _“fichero_salida.xxx solo puede tener de extensión .csv, .json o .xml”_.

---

## 🔹 Salida en JSON

```java
private static void writeJson(String path, List<Tenista> list) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), list);
}
```

- Usa **Jackson** (`ObjectMapper`).
- `JavaTimeModule` → para soportar correctamente `LocalDate` y `LocalDateTime`.
- `disable(WRITE_DATES_AS_TIMESTAMPS)` → fechas se escriben como texto ISO 8601, no como números.
- `writerWithDefaultPrettyPrinter()` → salida bonita (indentada).

👉 Genera un archivo JSON válido con todos los campos de cada `Tenista`.

---

## 🔹 Salida en CSV

```java
private static void writeCsv(String path, List<Tenista> list) throws Exception {
    try (FileWriter fw = new FileWriter(path, false)) {
        fw.write("id,nombre,pais,altura,peso,puntos,mano,fecha_nacimiento,created_at,updated_at\n");
        for (int i = 0; i < list.size(); i++) {
            Tenista t = list.get(i);
            fw.write(
                t.getId()+","
                + escape(t.getNombre())+","
                + escape(t.getPais())+","
                + t.getAltura()+","
                + t.getPeso()+","
                + t.getPuntos()+","
                + t.getMano().name()+","
                + t.getFechaNacimiento()+","
                + t.getCreatedAt()+","
                + t.getUpdatedAt()
                + "\n"
            );
        }
    }
}
```

- Abre un `FileWriter` sobre la ruta indicada.
- Escribe primero la **cabecera** de columnas.
- Recorre cada `Tenista` y lo imprime en formato CSV.
- Usa `escape(...)` para proteger cadenas con comas o comillas.

### ➤ Método `escape`

```java
private static String escape(String s) {
    if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    return s;
}
```

- Si el campo contiene **coma o comillas** → lo encierra entre `"` y duplica las comillas internas (`CSV estándar`).
- Ejemplo:

  - `Roger "King" Federer` → `"Roger ""King"" Federer"`.

---

## 🔹 Salida en XML

```java
private static void writeXml(String path, List<Tenista> list) throws Exception {
    try (FileWriter fw = new FileWriter(path, false)) {
        fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        fw.write("<tenistas>\n");
        for (int i = 0; i < list.size(); i++) {
            Tenista t = list.get(i);
            fw.write("  <tenista>\n");
            fw.write("    <id>"+t.getId()+"</id>\n");
            fw.write("    <nombre>"+escapeXml(t.getNombre())+"</nombre>\n");
            fw.write("    <pais>"+escapeXml(t.getPais())+"</pais>\n");
            fw.write("    <altura>"+t.getAltura()+"</altura>\n");
            fw.write("    <peso>"+t.getPeso()+"</peso>\n");
            fw.write("    <puntos>"+t.getPuntos()+"</puntos>\n");
            fw.write("    <mano>"+t.getMano().name()+"</mano>\n");
            fw.write("    <fecha_nacimiento>"+t.getFechaNacimiento()+"</fecha_nacimiento>\n");
            fw.write("    <created_at>"+t.getCreatedAt()+"</created_at>\n");
            fw.write("    <updated_at>"+t.getUpdatedAt()+"</updated_at>\n");
            fw.write("  </tenista>\n");
        }
        fw.write("</tenistas>\n");
    }
}
```

- Crea un documento XML básico.
- Cada `Tenista` está dentro de `<tenista>...</tenista>`.
- Usa `escapeXml(...)` para que no se rompa el XML si aparecen caracteres especiales.

### ➤ Método `escapeXml`

```java
private static String escapeXml(String s) {
    return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
            .replace("\"","&quot;").replace("'","&apos;");
}
```

- Convierte caracteres especiales en entidades XML.
- Ejemplo: `España & Nadal` → `España &amp; Nadal`.

---

# 📊 Relación con el enunciado

> _“El programa debe generar un fichero de salida con la información de la base de datos. fichero_salida.xxx solo puede tener de extensión .csv, .json o .xml. Si no escribes un path, el fichero se guardará en el directorio actual, con json como formato por defecto y con el nombre torneo_tenis.json.”_

👉 `OutputWriter` cumple esta parte:

- Acepta `.csv`, `.json`, `.xml`.
- Valida extensión → lanza error si no es soportada.
- Produce archivos en los **tres formatos estándar**.

---

¡Perfecto! Vamos a analizar **`FifoCache.java`** en detalle. Es una clase corta pero muy interesante porque implementa un patrón típico: **caché de reemplazo FIFO (First In, First Out)**.

---

# 📄 Clase `FifoCache`

```java
public class FifoCache<K,V> extends LinkedHashMap<K,V> {
    private final int maxSize;

    public FifoCache(int maxSize) {
        super(16, 0.75f, false);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
```

---

## 🔹 1. Extiende `LinkedHashMap`

- `LinkedHashMap` es como un `HashMap`, pero conserva el orden de los elementos.
- El tercer parámetro del constructor (`false`) significa: **orden de inserción**, no de acceso.
- Esto es fundamental: mantiene los elementos en el orden en que se añadieron → perfecto para FIFO.

---

## 🔹 2. Constructor

```java
public FifoCache(int maxSize) {
    super(16, 0.75f, false);
    this.maxSize = maxSize;
}
```

- `16` → capacidad inicial (número de buckets).
- `0.75f` → _load factor_ (cuando llega al 75% de ocupación, se redimensiona).
- `false` → usa **orden de inserción** (no LRU).
- `maxSize` → tamaño máximo de la caché (en este proyecto = 5).

👉 Así se asegura que, aunque se añadan más de 5 elementos, solo se conserven los últimos 5 en orden de llegada.

---

## 🔹 3. Sobrescritura de `removeEldestEntry`

```java
@Override
protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > maxSize;
}
```

- Se ejecuta automáticamente cada vez que haces un `put()`.
- Si el tamaño supera `maxSize`, devuelve `true` y se elimina la **entrada más antigua** (`eldest`).
- Esto es lo que implementa el **FIFO real**.

---

## 🔹 Ejemplo de uso

```java
FifoCache<Integer, String> cache = new FifoCache<>(3);

cache.put(1, "A");
cache.put(2, "B");
cache.put(3, "C");
System.out.println(cache); // {1=A, 2=B, 3=C}

cache.put(4, "D");
// Se supera el límite (3) → se borra el más viejo (1=A).
System.out.println(cache); // {2=B, 3=C, 4=D}
```

👉 Siempre que se pasa del tamaño máximo, desaparece el **primer insertado**.

---

## 🔹 Relación con tu proyecto

En `TenistaRepositorySqlite`:

```java
private final FifoCache<Long, Tenista> cache = new FifoCache<>(5);
```

- Guarda los últimos **5 tenistas accedidos por ID**.
- En `findById`:

  - Primero busca en la caché (`cache.get(id)`).
  - Si no está → va a BD, lo mete en la caché.

- En `insert/update/delete`: se actualiza o invalida la caché para coherencia.

👉 Cumple el enunciado: _“Además, tendremos una caché FIFO de la base de datos con tamaño de 5 elementos”_.

---
