package com.example.funkos.config;

import com.example.funkos.model.Funko;
import com.example.funkos.repository.FunkoRepository;
import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Objects;

@Component
public class DataLoader {
    private final FunkoRepository repository;

    public DataLoader(FunkoRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        if (repository.count() == 0) {
            try (CSVReader reader = new CSVReader(new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream("/data/funkos.csv"))))) {

                reader.readNext(); // saltar cabecera
                String[] line;
                while ((line = reader.readNext()) != null) {
                    if (line.length < 5) continue;

                    Funko f = new Funko();
                    // ❌ No seteamos ID → lo genera Hibernate automáticamente
                    f.setNombre(line[1].trim());
                    f.setModelo(line[2].trim());
                    f.setPrecio(Double.parseDouble(line[3].trim()));
                    f.setFechaLanzamiento(LocalDate.parse(line[4].trim()));
                    f.setCantidad(0);
                    f.setImagen("sin_imagen.jpg");
                    f.setCategoria("general");
                    repository.save(f);
                }

                System.out.println("✅ Funkos cargados desde CSV: " + repository.count());
            } catch (Exception e) {
                System.err.println("⚠️ Error leyendo funkos.csv");
                e.printStackTrace();
            }
        }
    }
}
