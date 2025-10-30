package app.productosrest01.service;


import app.productosrest01.model.Producto;
import app.productosrest01.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Aquí entra en juego @Autowired
    @Autowired
    public ProductoService(ProductoRepository productoRepository) {

        this.productoRepository = productoRepository;
    }

    public List<Producto> listar() {

        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {

        return productoRepository.findById(id);
    }

    public Producto guardar(Producto producto) {

        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {

        productoRepository.deleteById(id);
    }
}