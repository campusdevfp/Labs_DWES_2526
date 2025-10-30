package app.productosrest01.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "productos")
//@Getter
//@Setter
//@EqualsAndHashCode
//@ToString
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String nombre;
    private Double precio;

    // Constructor por defecto requerido por JPA/Hibernate
    public Producto() {
    }
    public Producto(Long id, String nombre, Double precio) {}
    protected Producto(Object nombre, String tecladoMecánico, double v) { }

    public Producto(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
}
