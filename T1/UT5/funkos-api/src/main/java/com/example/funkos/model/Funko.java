package com.example.funkos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Funko {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(length = 50, nullable = false, unique = true)

    private UUID id;

    @NotBlank private String nombre;
    @NotBlank private String modelo;
    @DecimalMin("0.0") private double precio;
    @Min(0) private int cantidad;
    private String imagen;
    private String categoria;
    private LocalDate fechaLanzamiento;
    private LocalDate fechaCreacion;
    private LocalDate fechaActualizacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDate.now();
        fechaActualizacion = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDate.now();
    }

    // ✅ Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(LocalDate fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDate getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDate fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
