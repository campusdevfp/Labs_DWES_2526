## 🧭 Nivel 1 — Fundamentos de Streams

### 🧩 Ejercicio 1: Filtrar mayores de edad

Dada una lista de personas (`nombre`, `edad`), obtener una nueva lista solo con los mayores de 18 años.

```java
List<Persona> personas = Arrays.asList(
    new Persona("Ana", 17),
    new Persona("Luis", 20),
    new Persona("Carlos", 15),
    new Persona("Marta", 30)
);
```

📌 **Objetivo:** practicar `.filter()` y `.collect()`
💡 **Pista:** usa `personas.stream().filter(p -> p.getEdad() >= 18)`

---

### 🧩 Ejercicio 2: Obtener solo los nombres

De la lista anterior, crear una lista con **solo los nombres**.

📌 **Objetivo:** practicar `.map()`
💡 **Resultado esperado:** `["Ana", "Luis", "Carlos", "Marta"]`

---

### 🧩 Ejercicio 3: Ordenar por edad ascendente

Ordena las personas por edad y muestra la lista ordenada.

📌 **Objetivo:** usar `.sorted(Comparator.comparing(Persona::getEdad))`

---

### 🧩 Ejercicio 4: Buscar el más joven y el más viejo

Encuentra la persona más joven y la más vieja.

📌 **Objetivo:** usar `.min()` y `.max()`
💡 **Pista:** `.min(Comparator.comparing(Persona::getEdad))`

---

### 🧩 Ejercicio 5: Comprobar si todos son mayores de edad

Devuelve `true` si **todas las personas** tienen más de 18 años.

📌 **Objetivo:** usar `.allMatch(...)`

---

## ⚙️ Nivel 2 — Transformaciones y agregaciones

### 🧩 Ejercicio 6: Calcular la media de edad

Devuelve el promedio de edad de todas las personas.

📌 **Objetivo:** usar `.mapToInt(Persona::getEdad).average()`

---

### 🧩 Ejercicio 7: Convertir nombres a mayúsculas

Genera una lista con los nombres en mayúsculas.

📌 **Objetivo:** usar `.map(p -> p.getNombre().toUpperCase())`

---

### 🧩 Ejercicio 8: Contar cuántos son mayores de 30

Cuenta cuántas personas tienen más de 30 años.

📌 **Objetivo:** usar `.filter(...)` y `.count()`

---

### 🧩 Ejercicio 9: Concatenar los nombres en un solo String

Devuelve un `String` como `"Ana, Luis, Carlos, Marta"`.

📌 **Objetivo:** usar `Collectors.joining(", ")`

---

### 🧩 Ejercicio 10: Obtener lista sin duplicados

Dada una lista de nombres con repetidos, devuelve la lista sin duplicados.

📌 **Objetivo:** usar `.distinct()`

---

## 🚀 Nivel 3 — Agrupamientos y colecciones

### 🧩 Ejercicio 11: Agrupar personas por edad

Crea un `Map<Integer, List<Persona>>` donde la clave sea la edad.

📌 **Objetivo:** usar `Collectors.groupingBy(Persona::getEdad)`

---

### 🧩 Ejercicio 12: Agrupar por rango de edad

Crea tres grupos:

- `Menores` (<18)
- `Adultos` (18–64)
- `Mayores` (65+)

📌 **Objetivo:** usar `.collect(Collectors.groupingBy(p -> { ... }))`

---

### 🧩 Ejercicio 13: Contar cuántas personas hay por nombre

Genera un `Map<String, Long>` con el número de apariciones de cada nombre.

📌 **Objetivo:** usar `Collectors.groupingBy(..., Collectors.counting())`

---

### 🧩 Ejercicio 14: Obtener la persona más joven por cada edad par

Agrupa por edad par e identifica la persona más joven en cada grupo.

📌 **Objetivo:** `groupingBy` + `collectingAndThen(minBy(...))`

---

### 🧩 Ejercicio 15: Transformar a mapa nombre→edad

Crea un `Map<String, Integer>` donde la clave sea el nombre y el valor la edad.

📌 **Objetivo:** usar `Collectors.toMap(Persona::getNombre, Persona::getEdad)`

---

## 🧠 Nivel 4 — Streams avanzados y combinaciones

### 🧩 Ejercicio 16: Encontrar el segundo más joven

Encuentra la segunda persona más joven de la lista.

📌 **Objetivo:** usar `.sorted()` + `.skip(1).findFirst()`

---

### 🧩 Ejercicio 17: Calcular la suma total de edades

📌 **Objetivo:** usar `.mapToInt(Persona::getEdad).sum()`

---

### 🧩 Ejercicio 18: Crear una lista plana a partir de listas anidadas

Dada una lista de listas de nombres:

```java
List<List<String>> nombres = Arrays.asList(
    Arrays.asList("Ana", "Luis"),
    Arrays.asList("Pedro", "María")
);
```

📌 **Objetivo:** usar `.flatMap(List::stream)` para obtener `["Ana", "Luis", "Pedro", "María"]`

---

### 🧩 Ejercicio 19: Verificar si hay alguien llamado "Juan"

📌 **Objetivo:** usar `.anyMatch(p -> p.getNombre().equals("Juan"))`

---

### 🧩 Ejercicio 20: Obtener las 3 personas más mayores

📌 **Objetivo:** usar `.sorted(...).limit(3).collect(...)`

---

## 🧩 Nivel Extra — Streams paralelos

### 🧩 Ejercicio 21: Procesamiento paralelo

Calcula la media de edad usando un stream **paralelo** y compara el tiempo con un stream secuencial.

📌 **Objetivo:** usar `.parallelStream()` y medir rendimiento.

---

### 🧩 Ejercicio 22: Crear tu propio colector

Implementa un `Collector` personalizado que concatene nombres con punto y coma `;`.

📌 **Objetivo:** extender `Collector` o usar `Collector.of(...)`

Perfecto 🔥
Continuamos la **batería de ejercicios de _Java Streams_** desde el **Ejercicio 23**, centrados ahora en filtros, búsquedas, agrupamientos y transformaciones más avanzadas (como si pasáramos de `WHERE` y `GROUP BY` a combinaciones y joins de SQL).

---

## 🚀 Nivel 5 — Filtros, búsquedas y agrupamientos avanzados

---

### 🧩 **Ejercicio 23: Filtrar por múltiples condiciones**

Obtén las personas que sean mayores de 25 años **y** cuyo nombre empiece por “M”.

```java
List<Persona> resultado = personas.stream()
    .filter(p -> p.getEdad() > 25 && p.getNombre().startsWith("M"))
    .collect(Collectors.toList());
```

📤 **Resultado esperado:**

```
[María (30), Marta (35)]
```

---

### 🧩 **Ejercicio 24: Buscar la primera persona cuyo nombre tenga más de 5 letras**

```java
Optional<Persona> primera = personas.stream()
    .filter(p -> p.getNombre().length() > 5)
    .findFirst();
```

💡 Usa `ifPresent(System.out::println)` para imprimirla solo si existe.

---

### 🧩 **Ejercicio 25: Filtrar, mapear y limitar**

Obtén los **3 primeros nombres en mayúsculas** de personas mayores de edad.

```java
List<String> nombres = personas.stream()
    .filter(p -> p.getEdad() >= 18)
    .map(p -> p.getNombre().toUpperCase())
    .limit(3)
    .collect(Collectors.toList());
```

---

### 🧩 **Ejercicio 26: Buscar si hay alguien entre 20 y 30 años**

```java
boolean existe = personas.stream()
    .anyMatch(p -> p.getEdad() >= 20 && p.getEdad() <= 30);
```

---

### 🧩 **Ejercicio 27: Agrupar por rango de edad (juvenil, adulto, senior)**

```java
Map<String, List<Persona>> grupos = personas.stream()
    .collect(Collectors.groupingBy(p -> {
        if (p.getEdad() < 18) return "Menor";
        else if (p.getEdad() < 65) return "Adulto";
        else return "Senior";
    }));
```

📤 **Salida esperada (según datos):**

```
Menor → [Ana (15)]
Adulto → [Juan (25), María (30), Pedro (40)]
Senior → [Luis (70)]
```

---

### 🧩 **Ejercicio 28: Filtrar y agrupar por inicial del nombre**

```java
Map<Character, List<Persona>> porInicial = personas.stream()
    .filter(p -> p.getEdad() >= 18)
    .collect(Collectors.groupingBy(p -> p.getNombre().charAt(0)));
```

💡 **Ejemplo de resultado:**

```
J → [Juan (25)]
M → [María (30), Marta (35)]
P → [Pedro (20)]
```

---

### 🧩 **Ejercicio 29: Filtrar y contar por grupo**

Cuenta cuántas personas hay en cada rango de edad.

```java
Map<String, Long> conteo = personas.stream()
    .collect(Collectors.groupingBy(p -> {
        if (p.getEdad() < 18) return "Menor";
        else if (p.getEdad() < 65) return "Adulto";
        else return "Senior";
    }, Collectors.counting()));
```

📤 **Salida esperada:**

```
{Menor=1, Adulto=3, Senior=1}
```

---

### 🧩 **Ejercicio 30: Buscar el nombre más largo**

```java
Optional<String> nombreLargo = personas.stream()
    .map(Persona::getNombre)
    .max(Comparator.comparingInt(String::length));
```

📤 **Resultado:** `"Francisco"` (por ejemplo)

---

### 🧩 **Ejercicio 31: Filtrar y sumar edades**

Suma las edades de todas las personas que sean mayores de 20 años.

```java
int suma = personas.stream()
    .filter(p -> p.getEdad() > 20)
    .mapToInt(Persona::getEdad)
    .sum();
```

---

### 🧩 **Ejercicio 32: Obtener el promedio de edad de nombres que empiecen por “A”**

```java
OptionalDouble promedio = personas.stream()
    .filter(p -> p.getNombre().startsWith("A"))
    .mapToInt(Persona::getEdad)
    .average();
```

---

### 🧩 **Ejercicio 33: Filtrar, ordenar y unir nombres**

Devuelve un `String` con los nombres de los mayores de edad ordenados alfabéticamente, separados por coma.

```java
String resultado = personas.stream()
    .filter(p -> p.getEdad() >= 18)
    .map(Persona::getNombre)
    .sorted()
    .collect(Collectors.joining(", "));
```

📤 **Resultado:** `"Juan, María, Marta, Pedro"`

---

### 🧩 **Ejercicio 34: Agrupar por edad y mostrar la persona más joven por grupo**

```java
Map<Integer, Persona> minPorEdad = personas.stream()
    .collect(Collectors.groupingBy(
        Persona::getEdad,
        Collectors.collectingAndThen(
            Collectors.minBy(Comparator.comparing(Persona::getEdad)),
            Optional::get
        )
    ));
```

💡 (Útil para practicar `collectingAndThen` y `Optional`).

---

### 🧩 **Ejercicio 35: Filtrar y transformar a mapa (clave: nombre, valor: edad)**

```java
Map<String, Integer> mapa = personas.stream()
    .filter(p -> p.getEdad() >= 18)
    .collect(Collectors.toMap(Persona::getNombre, Persona::getEdad));
```

📤 **Resultado:**

```
{Juan=25, María=30, Pedro=20}
```

---

### 🧩 **Ejercicio 36: Encontrar los 3 nombres más largos**

```java
List<String> top3 = personas.stream()
    .map(Persona::getNombre)
    .sorted(Comparator.comparingInt(String::length).reversed())
    .limit(3)
    .collect(Collectors.toList());
```

---

### 🧩 **Ejercicio 37: Filtrar y obtener solo las iniciales**

```java
List<Character> iniciales = personas.stream()
    .filter(p -> p.getEdad() > 18)
    .map(p -> p.getNombre().charAt(0))
    .distinct()
    .collect(Collectors.toList());
```

📤 **Resultado:** `[J, M, P]`

---

### 🧩 **Ejercicio 38: Obtener lista de nombres sin duplicados ordenada**

```java
List<String> unicos = personas.stream()
    .map(Persona::getNombre)
    .distinct()
    .sorted()
    .collect(Collectors.toList());
```

---

### 🧩 **Ejercicio 39: Buscar la persona más joven cuyo nombre empiece por “P”**

```java
Optional<Persona> jovenP = personas.stream()
    .filter(p -> p.getNombre().startsWith("P"))
    .min(Comparator.comparing(Persona::getEdad));
```

---

### 🧩 **Ejercicio 40: Filtrar por edad par y crear lista de nombres**

```java
List<String> pares = personas.stream()
    .filter(p -> p.getEdad() % 2 == 0)
    .map(Persona::getNombre)
    .collect(Collectors.toList());
```

📤 **Resultado:** `["María", "Pedro"]`

---

## 🧠 BONUS — Pensando como SQL con Streams

| SQL                  | Java Stream equivalente                                      |
| -------------------- | ------------------------------------------------------------ |
| `WHERE edad > 20`    | `.filter(p -> p.getEdad() > 20)`                             |
| `SELECT nombre`      | `.map(Persona::getNombre)`                                   |
| `ORDER BY edad DESC` | `.sorted(Comparator.comparing(Persona::getEdad).reversed())` |
| `GROUP BY edad`      | `.collect(Collectors.groupingBy(Persona::getEdad))`          |
| `COUNT(*)`           | `.count()`                                                   |
| `AVG(edad)`          | `.mapToInt(Persona::getEdad).average()`                      |
| `SUM(edad)`          | `.mapToInt(Persona::getEdad).sum()`                          |
| `DISTINCT nombre`    | `.map(Persona::getNombre).distinct()`                        |
| `LIMIT 3`            | `.limit(3)`                                                  |

---
