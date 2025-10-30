package app.util;

import app.domain.Mano;
import app.domain.Tenista;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public static List<Tenista> readTenistas(File csv) throws Exception {
        List<Tenista> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                row++;
                line = line.trim();
                if (line.isEmpty()) continue;
                if (row == 1 && line.toLowerCase().startsWith("nombre,")) {
                    // cabecera
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 7) {
                    throw new IllegalArgumentException("Fila " + row + ": número de columnas incorrecto");
                }
                String nombre = parts[0].trim();
                String pais = parts[1].trim();
                int altura = parsePositiveInt(parts[2].trim(), "altura", row);
                int peso = parsePositiveInt(parts[3].trim(), "peso", row);
                int puntos = parseNonNegativeInt(parts[4].trim(), "puntos", row);
                Mano mano = Mano.from(parts[5].trim());
                LocalDate fecha = LocalDate.parse(parts[6].trim()); // ISO 8601

                Tenista t = new Tenista(null, nombre, pais, altura, peso, puntos, mano, fecha, null, null);
                list.add(t);
            }
        }
        return list;
    }

    private static int parsePositiveInt(String s, String field, int row) {
        int v = Integer.parseInt(s);
        if (v <= 0) throw new IllegalArgumentException("Fila " + row + ": " + field + " debe ser > 0");
        return v;
    }

    private static int parseNonNegativeInt(String s, String field, int row) {
        int v = Integer.parseInt(s);
        if (v < 0) throw new IllegalArgumentException("Fila " + row + ": " + field + " debe ser >= 0");
        return v;
    }
}
