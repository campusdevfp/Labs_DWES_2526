package es.iesguzman.demo.unit;

import es.iesguzman.demo.models.Cliente;
import es.iesguzman.demo.models.Pedido;
import es.iesguzman.demo.repositories.ClienteRepository;
import es.iesguzman.demo.repositories.PedidoRepository;
import es.iesguzman.demo.services.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceUnitTest {

    @Mock
    private PedidoRepository pedidoRepo;

    @Mock
    private ClienteRepository clienteRepo;

    @InjectMocks
    private PedidoService service;

    private Cliente cliente;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Cliente");
        cliente.setEmail("c@d.com");

        pedido = new Pedido();
        pedido.setId(10L);
        pedido.setDescripcion("Item");
        pedido.setTotal(9.99);
        pedido.setCliente(cliente);
    }

    @Test
    void findAllShouldReturnList() {
        when(pedidoRepo.findAll()).thenReturn(List.of(pedido));

        List<Pedido> res = service.findAll();

        assertNotNull(res);
        assertEquals(1, res.size());
        verify(pedidoRepo).findAll();
    }

    @Test
    void findByIdShouldReturnWhenExists() {
        when(pedidoRepo.findById(10L)).thenReturn(Optional.of(pedido));

        Pedido p = service.findById(10L);

        assertEquals(10L, p.getId());
    }

    @Test
    void findByIdShouldThrow404WhenMissing() {
        when(pedidoRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.findById(99L));
        assertEquals(404, ex.getStatus().value());
    }

    @Test
    void createShouldThrow404WhenClienteMissing() {
        when(clienteRepo.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.create(999L, new Pedido()));
        assertEquals(404, ex.getStatus().value());
    }
}

