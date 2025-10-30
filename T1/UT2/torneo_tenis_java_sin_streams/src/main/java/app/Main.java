package app;

import app.config.Config;
import app.config.LogConfig;
import app.domain.Mano;
import app.domain.Tenista;
import app.repository.TenistaRepository;
import app.repository.sqlite.TenistaRepositorySqlite;
import app.service.TenistaService;
import app.util.CsvReader;
import app.util.OutputWriter;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            LogConfig.setup(); // Console + file logging
            Config.load();     // Load properties



            if (args.length < 1 || args.length > 2) {
                System.out.println("Uso: java -jar torneo_tenis.jar <fichero_entrada.csv> [fichero_salida.(csv|json|xml)]");
                System.exit(1);
            }

            String inputPath = args[0];
            File input = new File(inputPath);
            if (!input.exists() || !input.isFile() || !input.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
                System.out.println("El fichero de entrada debe existir y tener extensión .csv");
                System.exit(1);
            }

            String outputPath;
            if (args.length == 2) {
                outputPath = args[1];
                String lower = outputPath.toLowerCase(Locale.ROOT);
                if (!(lower.endsWith(".csv") || lower.endsWith(".json") || lower.endsWith(".xml"))) {
                    System.out.println("La salida debe tener extensión .csv, .json o .xml");
                    System.exit(1);
                }
            } else {
                outputPath = Paths.get(System.getProperty("user.dir"), "torneo_tenis.json").toString();
            }


            logger.info("Iniciando aplicación");
            TenistaRepository repo = new TenistaRepositorySqlite();
            repo.initSchema(true); // Vaciar BD

            // Leer CSV y validar
            List<Tenista> tenistas = CsvReader.readTenistas(input);
            logger.info("Leídos " + tenistas.size() + " tenistas del CSV");

            // Insertar en BD
            for (int i = 0; i < tenistas.size(); i++) {
                Tenista t = tenistas.get(i);
                Tenista inserted = repo.insert(t);
                tenistas.set(i, inserted); // actualizar con id/created/updated
            }

            // Consultas en consola (sin streams)
            TenistaService service = new TenistaService(repo);

            System.out.println("\n== Ranking (puntos desc) ==");
            List<Tenista> ranking = service.getAllOrderByPuntosDesc();
            printSimpleList(ranking);

            System.out.println("\n== Media de altura ==");
            System.out.printf(Locale.ROOT, "%.2f cm%n", service.mediaAltura());

            System.out.println("\n== Media de peso ==");
            System.out.printf(Locale.ROOT, "%.2f kg%n", service.mediaPeso());

            System.out.println("\n== Tenista más alto ==");
            Tenista masAlto = service.tenistaMasAlto();
            System.out.println(masAlto == null ? "N/A" : masAlto.toShortString());

            System.out.println("\n== Tenistas de España ==");
            printSimpleList(service.tenistasPorPais("España"));

            System.out.println("\n== Tenistas agrupados por país ==");
            service.printTenistasAgrupadosPorPais();

            System.out.println("\n== Número de tenistas por país (ordenados por puntos desc dentro de cada país) ==");
            service.printNumTenistasPorPaisOrdenadosPorPuntos();

            System.out.println("\n== Tenistas agrupados por mano y puntuación media ==");
            service.printTenistasPorManoConMedia();

            System.out.println("\n== Puntuación total por país ==");
            service.printPuntuacionTotalPorPais();

            System.out.println("\n== País con más puntuación total ==");
            System.out.println(service.paisConMasPuntuacionTotal());

            System.out.println("\n== Mejor ranking de España ==");
            Tenista mejorEsp = service.mejorRankingDePais("España");
            System.out.println(mejorEsp == null ? "N/A" : mejorEsp.toShortString());

            // Escribir salida
            OutputWriter.write(outputPath, repo.findAll());
            System.out.println("\nSalida generada en: " + outputPath);

            logger.info("Finalizado correctamente " + LocalDateTime.now());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en la ejecución", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void printSimpleList(List<Tenista> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toShortString());
        }
    }
}
