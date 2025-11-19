package es.iesguzman.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(name = "imagen_url")
    private String imagenUrl;

}
