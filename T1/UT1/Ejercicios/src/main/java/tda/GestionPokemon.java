package tda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Ejemplo moderno (Java 21) de lectura y escritura de JSON con Gson.
 * Demuestra cómo usar Files.readString() y Files.writeString().
 */
public class GestionPokemon {

    public static void main(String[] args) {
        Path ruta = Path.of("pokemons.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // 1️⃣ Crear lista de Pokémon
        List<Pokemon> pokemons = Arrays.asList(
                new Pokemon("Pikachu", "Eléctrico", 10),
                new Pokemon("Charmander", "Fuego", 12),
                new Pokemon("Bulbasaur", "Planta", 11)
        );

        // 2️⃣ Escribir JSON (API moderna)
        try {
            Files.writeString(ruta, gson.toJson(pokemons));
            System.out.println("✅ Archivo JSON creado correctamente en: " + ruta.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Error al escribir el archivo: " + e.getMessage());
            return;
        }

        // 3️⃣ Leer JSON (API moderna)
        try {
            String json = Files.readString(ruta);
            Type listType = new TypeToken<List<Pokemon>>() {}.getType();
            List<Pokemon> listaLeida = gson.fromJson(json, listType);

            System.out.println("\n📘 Pokémon leídos desde el archivo:");
            listaLeida.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("❌ Error al leer el archivo: " + e.getMessage());
        }
    }
}
