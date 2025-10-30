package app.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Tenista {
    private Long id; // autonumérico
    private String nombre;
    private String pais;
    private int altura; // cm
    private int peso;   // kg
    private int puntos;
    private Mano mano;
    private LocalDate fechaNacimiento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Tenista() {}

    public Tenista(Long id, String nombre, String pais, int altura, int peso, int puntos, Mano mano,
                   LocalDate fechaNacimiento, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.altura = altura;
        this.peso = peso;
        this.puntos = puntos;
        this.mano = mano;
        this.fechaNacimiento = fechaNacimiento;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String toShortString() {
        return String.format(Locale.ROOT, "#%d %s (%s) - %d pts, %d cm, %d kg", id, nombre, pais, puntos, altura, peso);
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }
    public int getPeso() { return peso; }
    public void setPeso(int peso) { this.peso = peso; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public Mano getMano() { return mano; }
    public void setMano(Mano mano) { this.mano = mano; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
