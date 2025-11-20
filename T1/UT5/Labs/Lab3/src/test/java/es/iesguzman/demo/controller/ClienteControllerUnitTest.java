package es.iesguzman.demo.controller;

import es.iesguzman.demo.model.Cliente;
import es.iesguzman.demo.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerUnitTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    public void testListarClientes() {
        // Crear datos de prueba
        Cliente cliente1 = new Cliente(1L, "Juan Pérez", "juan@example.com");
        Cliente cliente2 = new Cliente(2L, "Ana García", "ana@example.com");
        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);

        // Mockear el repository
        when(clienteRepository.findAll()).thenReturn(clientes);

        // Llamar al método del controller
        List<Cliente> result = clienteController.listarClientes();

        // Verificar el resultado
        assertEquals(2, result.size());
        assertEquals("Juan Pérez", result.get(0).getNombre());
        assertEquals("Ana García", result.get(1).getNombre());
    }
}
