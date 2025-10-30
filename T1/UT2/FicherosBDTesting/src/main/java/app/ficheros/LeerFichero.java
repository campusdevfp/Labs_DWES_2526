package app.ficheros;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LeerFichero {
    public static void main(String[] args) {
        String rutaDelArchivo = "C:\\Users\\madrid\\ws\\Labs_DWES_2526\\T1\\UT1\\FicherosBDTesting\\src\\main\\resources\\file1.csv";
        try (Stream<String> lines = Files.lines(Paths.get(rutaDelArchivo), StandardCharsets.UTF_8)) {
            lines.forEach(x -> System.out.println(x));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

