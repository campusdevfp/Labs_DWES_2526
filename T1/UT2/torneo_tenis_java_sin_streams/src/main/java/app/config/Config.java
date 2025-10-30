package app.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties props;

    public static void load() throws Exception {
        if (props != null) return;
        props = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        }
    }

    public static String get(String key, String def) {
        if (props == null) return def;
        return props.getProperty(key, def);
    }
}
