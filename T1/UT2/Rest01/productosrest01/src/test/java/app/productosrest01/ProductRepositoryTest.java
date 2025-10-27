package app.productosrest01;


import app.productosrest01.model.Producto;
import app.productosrest01.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void saveAndFindById_shouldPersistProducto() {
        Producto p = new Producto("Manzana", 1.5);
        Producto saved = productoRepository.save(p);

        assertThat(saved.getId()).isNotNull();

        Optional<Producto> opt = productoRepository.findById(saved.getId());
        assertThat(opt).isPresent();
        assertThat(opt.get().getNombre()).isEqualTo("Manzana");
        assertThat(opt.get().getPrecio()).isEqualTo(1.5);
    }
}
