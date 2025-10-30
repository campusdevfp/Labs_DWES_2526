package app.domain;

public enum Mano {
    DIESTRO, ZURDO;

    public static Mano from(String s) {
        String v = s.trim().toUpperCase();
        if (v.equals("DIESTRO")) return DIESTRO;
        if (v.equals("ZURDO")) return ZURDO;
        throw new IllegalArgumentException("Mano no válida: " + s);
    }
}
