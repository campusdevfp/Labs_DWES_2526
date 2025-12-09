package app.ejemplo02.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador de ejemplos prácticos de uso de sesiones
 */
@RestController
@RequestMapping("/ejemplos")
public class EjemplosSessionController {

    // ========================================
    // 1. CARRITO DE COMPRAS
    // ========================================

    @PostMapping("/carrito/agregar")
    public ResponseEntity<String> agregarAlCarrito(
            HttpSession session,
            @RequestParam String producto,
            @RequestParam int cantidad) {

        // Obtener carrito de la sesión (o crear uno nuevo)
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        // Agregar producto
        carrito.add(new ItemCarrito(producto, cantidad));
        session.setAttribute("carrito", carrito);

        return ResponseEntity.ok("✅ Producto agregado. Total items: " + carrito.size());
    }

    @GetMapping("/carrito/ver")
    public ResponseEntity<?> verCarrito(HttpSession session) {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Carrito vacío",
                    "items", List.of()
            ));
        }
        return ResponseEntity.ok(Map.of(
                "items", carrito,
                "total", carrito.size()
        ));
    }

    @DeleteMapping("/carrito/vaciar")
    public ResponseEntity<String> vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
        return ResponseEntity.ok("🗑️ Carrito vaciado");
    }

    // ========================================
    // 2. PREFERENCIAS DE USUARIO
    // ========================================

    @PostMapping("/preferencias/idioma")
    public ResponseEntity<String> cambiarIdioma(
            HttpSession session,
            @RequestParam String idioma) {

        session.setAttribute("idioma", idioma);
        return ResponseEntity.ok("🌍 Idioma cambiado a: " + idioma);
    }

    @PostMapping("/preferencias/tema")
    public ResponseEntity<String> cambiarTema(
            HttpSession session,
            @RequestParam String tema) {

        if (!tema.equals("claro") && !tema.equals("oscuro")) {
            return ResponseEntity.badRequest().body("❌ Tema debe ser 'claro' u 'oscuro'");
        }

        session.setAttribute("tema", tema);
        return ResponseEntity.ok("🎨 Tema cambiado a: " + tema);
    }

    @GetMapping("/preferencias")
    public ResponseEntity<Map<String, String>> verPreferencias(HttpSession session) {
        String idioma = (String) session.getAttribute("idioma");
        String tema = (String) session.getAttribute("tema");

        return ResponseEntity.ok(Map.of(
                "idioma", idioma != null ? idioma : "es (por defecto)",
                "tema", tema != null ? tema : "claro (por defecto)"
        ));
    }

    // ========================================
    // 3. HISTORIAL DE NAVEGACIÓN
    // ========================================

    @PostMapping("/historial/agregar")
    public ResponseEntity<String> agregarAlHistorial(
            HttpSession session,
            @RequestParam String pagina) {

        List<String> historial = (List<String>) session.getAttribute("historial");
        if (historial == null) {
            historial = new ArrayList<>();
        }

        historial.add(pagina);

        // Mantener solo últimas 10 páginas
        if (historial.size() > 10) {
            historial.remove(0);
        }

        session.setAttribute("historial", historial);
        return ResponseEntity.ok("📄 Página agregada al historial");
    }

    @GetMapping("/historial")
    public ResponseEntity<Map<String, Object>> verHistorial(HttpSession session) {
        List<String> historial = (List<String>) session.getAttribute("historial");

        return ResponseEntity.ok(Map.of(
                "historial", historial != null ? historial : List.of(),
                "cantidad", historial != null ? historial.size() : 0
        ));
    }

    // ========================================
    // 4. FORMULARIO MULTI-PASO (WIZARD)
    // ========================================

    @PostMapping("/registro/paso1")
    public ResponseEntity<String> paso1Registro(
            HttpSession session,
            @RequestParam String nombre,
            @RequestParam String email) {

        Map<String, String> datosRegistro = new HashMap<>();
        datosRegistro.put("nombre", nombre);
        datosRegistro.put("email", email);

        session.setAttribute("registroPaso1", datosRegistro);
        return ResponseEntity.ok("✅ Paso 1/3 completado. Datos guardados en sesión.");
    }

    @PostMapping("/registro/paso2")
    public ResponseEntity<String> paso2Registro(
            HttpSession session,
            @RequestParam String telefono,
            @RequestParam String ciudad) {

        Map<String, String> paso1 = (Map<String, String>) session.getAttribute("registroPaso1");
        if (paso1 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❌ Debes completar el paso 1 primero");
        }

        Map<String, String> datosRegistro = new HashMap<>();
        datosRegistro.put("telefono", telefono);
        datosRegistro.put("ciudad", ciudad);

        session.setAttribute("registroPaso2", datosRegistro);
        return ResponseEntity.ok("✅ Paso 2/3 completado. Datos guardados en sesión.");
    }

    @PostMapping("/registro/paso3")
    public ResponseEntity<Map<String, Object>> paso3RegistroConfirmar(HttpSession session) {
        Map<String, String> paso1 = (Map<String, String>) session.getAttribute("registroPaso1");
        Map<String, String> paso2 = (Map<String, String>) session.getAttribute("registroPaso2");

        if (paso1 == null || paso2 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Debes completar todos los pasos anteriores"));
        }

        // Combinar todos los datos
        Map<String, Object> registroCompleto = new HashMap<>();
        registroCompleto.putAll(paso1);
        registroCompleto.putAll(paso2);

        // Aquí normalmente guardarías en la BD
        // usuarioService.crearUsuario(registroCompleto);

        // Limpiar sesión
        session.removeAttribute("registroPaso1");
        session.removeAttribute("registroPaso2");

        return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Registro completado exitosamente",
                "datos", registroCompleto
        ));
    }

    @GetMapping("/registro/estado")
    public ResponseEntity<Map<String, Object>> estadoRegistro(HttpSession session) {
        boolean paso1Completo = session.getAttribute("registroPaso1") != null;
        boolean paso2Completo = session.getAttribute("registroPaso2") != null;

        return ResponseEntity.ok(Map.of(
                "paso1", paso1Completo ? "✅ Completado" : "❌ Pendiente",
                "paso2", paso2Completo ? "✅ Completado" : "❌ Pendiente",
                "paso3", "❌ Pendiente"
        ));
    }

    // ========================================
    // 5. FLASH MESSAGES
    // ========================================

    @PostMapping("/flash/mensaje")
    public ResponseEntity<String> guardarFlashMessage(
            HttpSession session,
            @RequestParam String mensaje,
            @RequestParam(defaultValue = "info") String tipo) {

        session.setAttribute("flashMessage", mensaje);
        session.setAttribute("flashType", tipo);

        return ResponseEntity.ok("💬 Mensaje flash guardado (se mostrará en la siguiente petición)");
    }

    @GetMapping("/flash/obtener")
    public ResponseEntity<Map<String, String>> obtenerFlashMessage(HttpSession session) {
        String mensaje = (String) session.getAttribute("flashMessage");
        String tipo = (String) session.getAttribute("flashType");

        // Eliminar de sesión después de leer (se muestra solo una vez)
        session.removeAttribute("flashMessage");
        session.removeAttribute("flashType");

        if (mensaje == null) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay mensajes flash"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", mensaje,
                "tipo", tipo != null ? tipo : "info"
        ));
    }

    // ========================================
    // 6. CONTADOR DE VISITAS
    // ========================================

    @GetMapping("/visitas")
    public ResponseEntity<Map<String, Object>> contarVisitas(HttpSession session, Authentication auth) {
        Integer visitas = (Integer) session.getAttribute("contadorVisitas");
        if (visitas == null) {
            visitas = 0;
        }
        visitas++;
        session.setAttribute("contadorVisitas", visitas);

        String username = auth != null ? auth.getName() : "Anónimo";

        return ResponseEntity.ok(Map.of(
                "usuario", username,
                "visitasEnEstaSesion", visitas,
                "mensaje", "Has visitado este endpoint " + visitas + " vez/veces en esta sesión"
        ));
    }

    // ========================================
    // 7. DATOS TEMPORALES CON TTL
    // ========================================

    @PostMapping("/temporal/guardar")
    public ResponseEntity<String> guardarDatoTemporal(
            HttpSession session,
            @RequestParam String clave,
            @RequestParam String valor,
            @RequestParam(defaultValue = "300") int segundos) {

        Map<String, Object> datoTemporal = new HashMap<>();
        datoTemporal.put("valor", valor);
        datoTemporal.put("expira", System.currentTimeMillis() + (segundos * 1000L));

        session.setAttribute("temporal_" + clave, datoTemporal);

        return ResponseEntity.ok("⏱️ Dato guardado. Expira en " + segundos + " segundos.");
    }

    @GetMapping("/temporal/obtener/{clave}")
    public ResponseEntity<?> obtenerDatoTemporal(
            HttpSession session,
            @PathVariable String clave) {

        Map<String, Object> datoTemporal = (Map<String, Object>) session.getAttribute("temporal_" + clave);

        if (datoTemporal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Dato no encontrado o ya expiró"));
        }

        long expira = (Long) datoTemporal.get("expira");
        if (System.currentTimeMillis() > expira) {
            session.removeAttribute("temporal_" + clave);
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of("error", "El dato ha expirado"));
        }

        long segundosRestantes = (expira - System.currentTimeMillis()) / 1000;
        return ResponseEntity.ok(Map.of(
                "valor", datoTemporal.get("valor"),
                "expiraEn", segundosRestantes + " segundos"
        ));
    }

    // ========================================
    // DTOs
    // ========================================

    public static class ItemCarrito {
        public String producto;
        public int cantidad;
        public Date agregadoEn;

        public ItemCarrito(String producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.agregadoEn = new Date();
        }
    }
}

