# Torneo Tenis (Java 17, sin streams)

## Construir
```bash
mvn -q -f pom.xml package
# JAR con dependencias: target/torneo_tenis-1.0.0-jar-with-dependencies.jar
```

## Ejecutar
```bash
java -jar target/torneo_tenis-1.0.0-jar-with-dependencies.jar fichero_entrada.csv salida.json
```
## Principios SOLID


## 1. **S – Single Responsibility Principle (SRP)**

 Cada clase hace **una sola cosa**.

* `CsvReader` → solo se encarga de leer y validar datos desde un CSV.
* `TenistaRepositorySqlite` → solo se encarga del acceso a datos en SQLite.
* `OutputWriter` → solo escribe la salida en CSV/JSON/XML.

Esto cumple SRP porque cada clase tiene una **responsabilidad única y clara**.

---

## 2. **O – Open/Closed Principle (OCP)**

 Abierto a extensión, cerrado a modificación.

* `OutputWriter` ya está diseñado para generar salida en diferentes formatos (`.csv`, `.json`, `.xml`).
  Si mañana quieres añadir YAML, no hace falta tocar el código existente: puedes extender `OutputWriter` o añadir otra implementación.
* De igual forma, `TenistaRepository` es una **interfaz** → puedes añadir un `TenistaRepositoryH2` sin modificar `Main` ni la lógica de negocio.

---

## 3. **L – Liskov Substitution Principle (LSP)**

 Se puede usar una implementación en lugar de otra sin romper el programa.

* `TenistaRepository` es la **interfaz**, y `TenistaRepositorySqlite` la implementación concreta.
* En `Main` se declara:

  ```java
  TenistaRepository repo = new TenistaRepositorySqlite();
  ```

  Podría sustituirse por otra (`TenistaRepositoryH2`) y la aplicación seguiría funcionando.
  Eso es justamente LSP: las implementaciones se pueden intercambiar sin que el código cliente se entere.

---

## 4. **I – Interface Segregation Principle (ISP)**

 Mejor varias interfaces pequeñas que una enorme.

* `TenistaRepository` solo define las operaciones que realmente necesita la aplicación:

  ```java
  findAll, findById, findByPais, findAllOrderByPuntosDesc, insert, update, delete
  ```
* No fuerza a la implementación a programar métodos inútiles.
  Esto cumple ISP porque la interfaz está **cohesionada** y no obliga a implementar más de lo necesario.

---

## 5. **D – Dependency Inversion Principle (DIP)**

 Depender de abstracciones, no de implementaciones.

* El servicio `TenistaService` no trabaja directamente con `TenistaRepositorySqlite`, sino con la **interfaz** `TenistaRepository`.
* Esto significa que el servicio depende de una **abstracción** y no de un detalle concreto de infraestructura.

De esa forma, si cambias de SQLite a H2, el servicio no cambia.

---

