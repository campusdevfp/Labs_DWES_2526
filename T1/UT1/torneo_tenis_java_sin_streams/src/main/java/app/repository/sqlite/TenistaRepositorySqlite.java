package app.repository.sqlite;

import app.domain.Mano;
import app.domain.Tenista;
import app.repository.TenistaRepository;
import app.util.FifoCache;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import app.config.Config;

public class TenistaRepositorySqlite implements TenistaRepository {
//    private static final String DB_URL = "jdbc:sqlite:data/tenistas.db";
    private static final String DB_URL = Config.get("db.url", null);  // Ejemplo de uso application.properties
    private final FifoCache<Long, Tenista> cache = new FifoCache<>(5);

    public TenistaRepositorySqlite() throws Exception {
        Files.createDirectories(Paths.get("data"));
        Class.forName("org.sqlite.JDBC");
    }

    private Connection getConnection() throws SQLException {
        System.out.println("db_url= " + DB_URL);
        return DriverManager.getConnection(DB_URL);
    }

    @Override
    public void initSchema(boolean drop) throws Exception {
        try (Connection con = getConnection();
             Statement st = con.createStatement()) {
            if (drop) {
                st.executeUpdate("DROP TABLE IF EXISTS tenistas");
            }
            st.executeUpdate("CREATE TABLE IF NOT EXISTS tenistas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "pais TEXT NOT NULL," +
                    "altura INTEGER NOT NULL," +
                    "peso INTEGER NOT NULL," +
                    "puntos INTEGER NOT NULL," +
                    "mano TEXT NOT NULL," +
                    "fecha_nacimiento TEXT NOT NULL," +
                    "created_at TEXT NOT NULL," +
                    "updated_at TEXT NOT NULL)");
        }
    }

    private Tenista map(ResultSet rs) throws SQLException {
        Tenista t = new Tenista();
        t.setId(rs.getLong("id"));
        t.setNombre(rs.getString("nombre"));
        t.setPais(rs.getString("pais"));
        t.setAltura(rs.getInt("altura"));
        t.setPeso(rs.getInt("peso"));
        t.setPuntos(rs.getInt("puntos"));
        t.setMano(Mano.from(rs.getString("mano")));
        t.setFechaNacimiento(LocalDate.parse(rs.getString("fecha_nacimiento")));
        t.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
        t.setUpdatedAt(LocalDateTime.parse(rs.getString("updated_at")));
        return t;
    }

    @Override
    public List<Tenista> findAll() throws Exception {
        List<Tenista> list = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM tenistas ORDER BY puntos DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public Tenista findById(long id) throws Exception {
        Tenista cached = cache.get(id);
        if (cached != null) return cached;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM tenistas WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tenista t = map(rs);
                    cache.put(id, t);
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public List<Tenista> findByPais(String pais) throws Exception {
        List<Tenista> list = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM tenistas WHERE pais=? ORDER BY puntos DESC")) {
            ps.setString(1, pais);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Tenista> findAllOrderByPuntosDesc() throws Exception {
        return findAll();
    }

    @Override
    public Tenista insert(Tenista t) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO tenistas(nombre,pais,altura,peso,puntos,mano,fecha_nacimiento,created_at,updated_at) " +
                             "VALUES(?,?,?,?,?,?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getPais());
            ps.setInt(3, t.getAltura());
            ps.setInt(4, t.getPeso());
            ps.setInt(5, t.getPuntos());
            ps.setString(6, t.getMano().name());
            ps.setString(7, t.getFechaNacimiento().toString());
            ps.setString(8, now.toString());
            ps.setString(9, now.toString());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    t.setId(id);
                    t.setCreatedAt(now);
                    t.setUpdatedAt(now);
                    cache.put(id, t);
                    return t;
                }
            }
        }
        throw new SQLException("No se pudo insertar el tenista");
    }

    @Override
    public Tenista update(Tenista t) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE tenistas SET nombre=?,pais=?,altura=?,peso=?,puntos=?,mano=?,fecha_nacimiento=?,updated_at=? WHERE id=?")) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getPais());
            ps.setInt(3, t.getAltura());
            ps.setInt(4, t.getPeso());
            ps.setInt(5, t.getPuntos());
            ps.setString(6, t.getMano().name());
            ps.setString(7, t.getFechaNacimiento().toString());
            ps.setString(8, now.toString());
            ps.setLong(9, t.getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                t.setUpdatedAt(now);
                cache.put(t.getId(), t);
                return t;
            }
        }
        throw new SQLException("No se pudo actualizar el tenista id=" + t.getId());
    }

    @Override
    public boolean delete(long id) throws Exception {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM tenistas WHERE id=?")) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            cache.remove(id);
            return rows > 0;
        }
    }
}
