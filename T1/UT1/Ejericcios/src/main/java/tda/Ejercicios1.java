package tda;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Varianza<T extends Number>{

    T elemento1;

    List<? super Integer> elemento2;
    Varianza(T elmento1){
        this.elemento1 = elmento1;

    }

    Varianza(T elemento1, List<? super Integer> elmento2){
        this.elemento1 = elemento1;
        this.elemento2 = elemento2;
    }
    public T getElemento1() {
        return elemento1;
    }

    public void setElemento1(T elemento1) {
        this.elemento1 = elemento1;
    }

    public List<? super Integer> getElemento2() {
        return elemento2;
    }
    public void setElemento2(List<? super Integer> elemento2) {
        this.elemento2 = elemento2;
    }

}
public class Ejercicios1 {

        public static void main(String[] args) {
            Varianza<Integer> varianzaInt = new Varianza<>(5);
            System.out.println("Valor Integer: " + varianzaInt.getElemento1());

            Varianza<Double> varianzaDouble = new Varianza<>(5.5);
            System.out.println("Valor Double: " + varianzaDouble.getElemento1());

            Varianza<Float> varianzaFloat = new Varianza<>(3.3f);
            System.out.println("Valor Float: " + varianzaFloat.getElemento1());

            // Aquí se puede usar cualquier clase que extienda de Number
            Varianza<Integer> varianzaNumber = new Varianza<>(10);
            System.out.println("Valor Number: " + varianzaNumber.getElemento1());

            // Sólo pueden ser tipo Intger o sus superclases
            // Double es una subclase de Number, no se puede usar
//            Varianza<Integer> varianzaDouble2 = new Varianza<>(10.3);
//            System.out.println("Valor Number: " + varianzaNumber.getElemento1());


            ArrayList lista = new ArrayList();
            Map mapa = new LinkedHashMap();




        }

}
