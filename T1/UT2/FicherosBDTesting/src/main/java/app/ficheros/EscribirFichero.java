package app.ficheros;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class EscribirFichero {
    public static void main(String[] args) {
        String rutaDelArchivo = "" +
                "file2.txt";
        List<String> lineas = Arrays.asList("Primera línea", "Segunda línea", "Tercera línea");

        try {
            Files.write(Paths.get(rutaDelArchivo), lineas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
