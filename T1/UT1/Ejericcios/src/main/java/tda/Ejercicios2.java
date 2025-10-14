package tda;


import java.util.function.BiFunction;
import java.util.function.Function;

interface TriFunction<T,U,V,R>{
    R apply(T t, U u, V v);
}



public class
Ejercicios2 {

    // Función recursiva para calcular el factorial de un número
    public static int factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }
    public static void main(String[] args) {

        Function<Integer,Integer> func = i -> i*i;
        System.out.println("El cuadrado de 5 es: " + func.apply(5));

        BiFunction<Integer,Integer,Integer> biFunc = (i,j)->i*j;
        System.out.println("El producto de 5 y 4 es: " + biFunc.apply(5,4));

        TriFunction<Integer,Integer,Integer,Integer> triFunc = (i,j,k)->i+j+k;
        System.out.println("La suma de 5, 4 y 3 es: " + triFunc.apply(5,4,3));

        int resultado = factorial(5);
        System.out.println(resultado); // Output: 120

    }
}
