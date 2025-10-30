import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;

public class Main {

    public static void main(String[] args) {

        List<Persona> personas = Arrays.asList(
            new Persona("Ana", 17),
            new Persona("Luis", 70),
            new Persona("Carlos", 15),
            new Persona("Marta", 35),
            new Persona("María", 30),
            new Persona("Pedro", 20),
            new Persona("Juan", 25),
            new Persona("Francisco", 40)
        );

        // 🧭 Nivel 1
        System.out.println("1️⃣ Mayores de edad: " +
            personas.stream().filter(p -> p.getEdad() >= 18).collect(toList()));

        System.out.println("2️⃣ Solo nombres: " +
            personas.stream().map(Persona::getNombre).toList()); 

        System.out.println("3️⃣ Ordenados por edad: " +
            personas.stream().sorted(Comparator.comparing(Persona::getEdad)).collect(toList()));

        System.out.println("4️⃣ Más joven: " +
            personas.stream().min(Comparator.comparing(Persona::getEdad)).get());
        System.out.println("4️⃣ Más viejo: " +
            personas.stream().max(Comparator.comparing(Persona::getEdad)).get());

        System.out.println("5️⃣ ¿Todos mayores? " +
            personas.stream().allMatch(p -> p.getEdad() > 18));

        // ⚙️ Nivel 2
        System.out.println("6️⃣ Promedio edad: " +
            personas.stream().mapToInt(Persona::getEdad).average().orElse(0));

        System.out.println("7️⃣ Mayúsculas: " +
            personas.stream().map(p -> p.getNombre().toUpperCase()).collect(toList()));

        System.out.println("8️⃣ Mayores de 30: " +
            personas.stream().filter(p -> p.getEdad() > 30).count());

        System.out.println("9️⃣ Nombres concatenados: " +
            personas.stream().map(Persona::getNombre).collect(joining(", ")));

        List<String> nombresDuplicados = Arrays.asList("Ana", "Luis", "Luis", "Ana", "Pedro");
        System.out.println("🔟 Sin duplicados: " +
            nombresDuplicados.stream().distinct().collect(toList()));

        // 🚀 Nivel 3
        System.out.println("11️⃣ Agrupar por edad: " +
            personas.stream().collect(groupingBy(Persona::getEdad)));

        System.out.println("12️⃣ Agrupar por rango: " +
            personas.stream().collect(groupingBy(p -> {
                if (p.getEdad() < 18) return "Menores";
                else if (p.getEdad() < 65) return "Adultos";
                else return "Mayores";
            })));

        System.out.println("13️⃣ Contar por nombre: " +
            personas.stream().collect(groupingBy(Persona::getNombre, counting())));

        System.out.println("14️⃣ Persona más joven por edad par: " +
            personas.stream()
                .filter(p -> p.getEdad() % 2 == 0)
                .collect(groupingBy(Persona::getEdad,
                    collectingAndThen(minBy(Comparator.comparing(Persona::getEdad)), Optional::get))));

            /*
             * personas.stream() — Crea un stream de la lista de personas.
                .filter(p -> p.getEdad() % 2 == 0) — Filtra solo las personas cuya edad es par.
                .collect(groupingBy(...)) — Agrupa las personas por edad (la clave del mapa será la edad).
                Para cada grupo (cada edad par), aplica:
                minBy(Comparator.comparing(Persona::getEdad)) — Busca la persona más joven en ese grupo (aunque todas tienen la misma edad, así que será el único elemento).
                collectingAndThen(..., Optional::get) — Extrae el valor de Optional (la persona encontrada).
             */

        System.out.println("15️⃣ Mapa nombre→edad: " +
            personas.stream().collect(toMap(Persona::getNombre, Persona::getEdad, (a, b) -> a)));

        // 🧠 Nivel 4
        System.out.println("16️⃣ Segundo más joven: " +
            personas.stream().sorted(Comparator.comparing(Persona::getEdad)).skip(1).findFirst().get());

        System.out.println("17️⃣ Suma total edades: " +
            personas.stream().mapToInt(Persona::getEdad).sum());

        List<List<String>> nombresAnidados = Arrays.asList(
            Arrays.asList("Ana", "Luis"),
            Arrays.asList("Pedro", "María"));
        System.out.println("18️⃣ Lista plana: " +
            nombresAnidados.stream().flatMap(List::stream).collect(toList()));

        System.out.println("19️⃣ ¿Existe Juan?: " +
            personas.stream().anyMatch(p -> p.getNombre().equals("Juan")));

        System.out.println("20️⃣ Top 3 mayores: " +
            personas.stream().sorted(Comparator.comparing(Persona::getEdad).reversed()).limit(3).collect(toList()));

        // 🧠 Nivel 5
        System.out.println("23️⃣ Mayores de 25 y nombre M: " +
            personas.stream().filter(p -> p.getEdad() > 25 && p.getNombre().startsWith("M")).collect(toList()));

        personas.stream().filter(p -> p.getNombre().length() > 5)
            .findFirst().ifPresent(p -> System.out.println("24️⃣ Primera >5 letras: " + p));

        System.out.println("25️⃣ 3 nombres mayores edad: " +
            personas.stream().filter(p -> p.getEdad() >= 18)
                .map(p -> p.getNombre().toUpperCase()).limit(3).collect(toList()));

        System.out.println("26️⃣ ¿Hay alguien entre 20 y 30? " +
            personas.stream().anyMatch(p -> p.getEdad() >= 20 && p.getEdad() <= 30));

        System.out.println("27️⃣ Grupos edad: " +
            personas.stream().collect(groupingBy(p -> {
                if (p.getEdad() < 18) return "Menor";
                else if (p.getEdad() < 65) return "Adulto";
                else return "Senior";
            })));

        System.out.println("28️⃣ Por inicial: " +
            personas.stream().filter(p -> p.getEdad() >= 18)
                .collect(groupingBy(p -> p.getNombre().charAt(0))));

        System.out.println("29️⃣ Conteo por grupo: " +
            personas.stream().collect(groupingBy(p -> {
                if (p.getEdad() < 18) return "Menor";
                else if (p.getEdad() < 65) return "Adulto";
                else return "Senior";
            }, counting())));

        System.out.println("30️⃣ Nombre más largo: " +
            personas.stream().map(Persona::getNombre)
                .max(Comparator.comparingInt(String::length)).orElse("N/A"));

        System.out.println("31️⃣ Suma >20 años: " +
            personas.stream().filter(p -> p.getEdad() > 20).mapToInt(Persona::getEdad).sum());

        System.out.println("32️⃣ Promedio nombres A: " +
            personas.stream().filter(p -> p.getNombre().startsWith("A"))
                .mapToInt(Persona::getEdad).average());

        System.out.println("33️⃣ Mayores de edad ordenados: " +
            personas.stream().filter(p -> p.getEdad() >= 18)
                .map(Persona::getNombre).sorted().collect(joining(", ")));

        System.out.println("34️⃣ Más joven por edad: " +
            personas.stream().collect(groupingBy(
                Persona::getEdad,
                collectingAndThen(minBy(Comparator.comparing(Persona::getEdad)), Optional::get)
            )));

        System.out.println("35️⃣ Mapa filtrado: " +
            personas.stream().filter(p -> p.getEdad() >= 18)
                .collect(toMap(Persona::getNombre, Persona::getEdad, (a, b) -> a)));

        System.out.println("36️⃣ Top 3 nombres más largos: " +
            personas.stream().map(Persona::getNombre)
                .sorted(Comparator.comparingInt(String::length).reversed()).limit(3).collect(toList()));

        System.out.println("37️⃣ Iniciales únicas: " +
            personas.stream().filter(p -> p.getEdad() > 18)
                .map(p -> p.getNombre().charAt(0)).distinct().collect(toList()));

        System.out.println("38️⃣ Nombres únicos ordenados: " +
            personas.stream().map(Persona::getNombre)
                .distinct().sorted().collect(toList()));

        System.out.println("39️⃣ Más joven con 'P': " +
            personas.stream().filter(p -> p.getNombre().startsWith("P"))
                .min(Comparator.comparing(Persona::getEdad)));

        System.out.println("40️⃣ Nombres edad par: " +
            personas.stream().filter(p -> p.getEdad() % 2 == 0)
                .map(Persona::getNombre).collect(toList()));
    }
}
