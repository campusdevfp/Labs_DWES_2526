package app.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;


class Pokemon {
    private String nombre;
    private String tipo;
    private int nivel;

    public Pokemon(String valore, String valore1, int i) {
    }

    // constructor

    // getters y setters

    // toString()
}


public class LeerCSV {
    public static void main(String[] args) {
        Path ruta = Path.of(args.length > 0 ? args[0] : "archivo.csv");

        try (Stream<String> lines = Files.lines(ruta, StandardCharsets.UTF_8)) {
            lines.map(line -> line.split(","))
                    .filter(parts -> parts.length >= 3)
                    .map(parts -> {
                        try {
                            return new Pokemon(parts[0].trim(), parts[1].trim(), Integer.parseInt(parts[2].trim()));
                        } catch (NumberFormatException ex) {
                            return null;
                        }
                    })
                    .filter(p -> p != null)
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
