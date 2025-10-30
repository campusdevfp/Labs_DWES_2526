package app.util;

import app.domain.Mano;
import app.domain.Tenista;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

public class OutputWriter {
    public static void write(String outputPath, List<Tenista> list) throws Exception {
        String lower = outputPath.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".json")) writeJson(outputPath, list);
        else if (lower.endsWith(".csv")) writeCsv(outputPath, list);
        else if (lower.endsWith(".xml")) writeXml(outputPath, list);
        else throw new IllegalArgumentException("Extensión no soportada: " + outputPath);
    }

    private static void writeJson(String path, List<Tenista> list) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), list);
    }

    private static void writeCsv(String path, List<Tenista> list) throws Exception {
        try (FileWriter fw = new FileWriter(path, false)) {
            fw.write("id,nombre,pais,altura,peso,puntos,mano,fecha_nacimiento,created_at,updated_at\n");
            for (int i = 0; i < list.size(); i++) {
                Tenista t = list.get(i);
                fw.write(
                    t.getId()+","
                    + escape(t.getNombre())+","
                    + escape(t.getPais())+","
                    + t.getAltura()+","
                    + t.getPeso()+","
                    + t.getPuntos()+","
                    + t.getMano().name()+","
                    + t.getFechaNacimiento()+","
                    + t.getCreatedAt()+","
                    + t.getUpdatedAt()
                    + "\n"
                );
            }
        }
    }

    private static String escape(String s) {
        if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static void writeXml(String path, List<Tenista> list) throws Exception {
        try (FileWriter fw = new FileWriter(path, false)) {
            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            fw.write("<tenistas>\n");
            for (int i = 0; i < list.size(); i++) {
                Tenista t = list.get(i);
                fw.write("  <tenista>\n");
                fw.write("    <id>"+t.getId()+"</id>\n");
                fw.write("    <nombre>"+escapeXml(t.getNombre())+"</nombre>\n");
                fw.write("    <pais>"+escapeXml(t.getPais())+"</pais>\n");
                fw.write("    <altura>"+t.getAltura()+"</altura>\n");
                fw.write("    <peso>"+t.getPeso()+"</peso>\n");
                fw.write("    <puntos>"+t.getPuntos()+"</puntos>\n");
                fw.write("    <mano>"+t.getMano().name()+"</mano>\n");
                fw.write("    <fecha_nacimiento>"+t.getFechaNacimiento()+"</fecha_nacimiento>\n");
                fw.write("    <created_at>"+t.getCreatedAt()+"</created_at>\n");
                fw.write("    <updated_at>"+t.getUpdatedAt()+"</updated_at>\n");
                fw.write("  </tenista>\n");
            }
            fw.write("</tenistas>\n");
        }
    }

    private static String escapeXml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&apos;");
    }
}
