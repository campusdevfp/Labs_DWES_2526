- Pimero cambiamos el puerto en el `application.properties` y vemos cómo se reinicia el servidor sin tener que pararlo. Chequeamos con postman.

# Pasos y notas

- Primero cambiamos el puerto en `application.properties` y vemos cómo se reinicia el servidor sin tener que pararlo. Comprobar con Postman.

## Qué es JPA y qué es Hibernate

- **JPA (Java Persistence API)**: especificación de Java para mapear objetos Java a tablas relacionales (ORM). Define anotaciones (`@Entity`, `@Id`, `@OneToMany`...), el ciclo de vida de entidades y la API `EntityManager`.
- **Hibernate**: implementación concreta de JPA (con características propias). Proporciona el `EntityManager`/`Session`, genera SQL, gestiona cachés, transacciones y optimizaciones.

## Cómo funciona en este proyecto (flujo básico)

1. Petición HTTP -> `ProductosRestController`
2. Controller llama a `ProductoService` (lógica de negocio)
3. `ProductoService` llama a `ProductoRepository` (Spring Data JPA)
4. `ProductoRepository` (extiende `JpaRepository`) delega en Hibernate / `EntityManager`
5. Hibernate genera y ejecuta SQL contra la base de datos configurada en `application.properties`
6. Hibernate devuelve entidades que pasan por el persistence context (primera caché) al service y al controller

## Qué hace Hibernate por ti (resumen)

- Métodos derivados de Spring Data (`findByNombre`) → Spring Data crea JPQL/HQL → Hibernate traduce a SQL y lo ejecuta.
- JPQL / HQL → consultas a nivel de entidad que Hibernate transforma a SQL.
- Criteria API → consultas programáticas que terminan en SQL.
- Native queries → SQL puro cuando necesitas funciones o sintaxis específicas del motor (`nativeQuery = true`).
- Funciones SQL → JPQL incluye funciones estándar; para funciones no estándar puedes usar `nativeQuery`, registrar la función en el dialecto o usar `function('mi_func', ...)` en JPQL.
- Dialectos y DDL → el Dialect adapta SQL (paginación, tipos, sequences). Con `spring.jpa.hibernate.ddl-auto` Hibernate puede ejecutar DDL (CREATE/ALTER).
- Ejecución → Hibernate crea sentencias SQL y las ejecuta por JDBC; ver con `spring.jpa.show-sql=true` o configurando loggers.

### Ejemplos rápidos

```java
// Método derivado -> Hibernate genera SQL
public interface ProductoRepo extends JpaRepository<Producto, Long> {
    List<Producto> findByNombre(String nombre);
}

// JPQL -> Hibernate traduce a SQL
@Query("SELECT p FROM Producto p WHERE p.precio > :min")
List<Producto> caros(@Param("min") BigDecimal min);

// Native -> SQL directo
@Query(value = "SELECT * FROM producto WHERE precio > ?1", nativeQuery = true)
List<Producto> carosNative(BigDecimal min);
```

## Recomendaciones y buenas prácticas

- Controller -> Service -> Repository (separación de capas).
- Poner `@Transactional` en la capa de servicio, no en el controller.
- Usar DTOs para respuestas REST (evita LazyInitializationException y acoplamiento).
- Usar fetch joins o `@EntityGraph` para evitar N+1 selects.
- Usar native queries sólo cuando sea necesario por el motor de BD.

> Nota: Aunque funciona usar el repository directamente en el controller, es mucho mejor llamar al Service para no romper reglas de diseño y para conseguir un código más testable y mantenible.

---

- Hay algo que hay que reimplementar, aunque funciona usar el repository en el controller es mucho mejor llamar al Service, por que si no rompes reglas de diseño y ya veremos que es más testable así.

- Poner @Autowired al Servicio y ver que tienes que poner @Service a la clase servicio.

- Cambiar el Controlador y poner el ProductosService y ver que en el lista hay que cambiarlo.

- Chequear con Postman

- Ahora probar la devolución de cabeceras y códigos de estado

---

- Creamos ahora en el controller el getProductos Id y ver que necesitamos implementar el servicio.
- En el endopoint {id} usar @Pathvariable
