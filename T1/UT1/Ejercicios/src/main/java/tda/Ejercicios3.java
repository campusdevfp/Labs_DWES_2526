package tda;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;


public class Ejercicios3 {
    public static void main(String[] args) {
        // Ejemplo 1: Uso de Streams para filtrar y mapear una lista
        System.out.println("Ejemplo 1: Filtrar y mapear una lista");
        List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);

        // Filtrar los números pares
        List<Integer> pares = numeros.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());

        System.out.println(pares); // Output: [2, 4]

        // Calcular la suma de los números
        int suma = numeros.stream()
                .reduce(0, (a, b) -> a + b);

        System.out.println(suma); // Output: 15
    }
}
