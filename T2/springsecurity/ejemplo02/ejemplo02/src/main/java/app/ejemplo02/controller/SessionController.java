package app.ejemplo02.controller;

import app.ejemplo02.dto.ItemCarrito;
import app.ejemplo02.dto.SessionInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/session")
public class SessionController {

    /**
     * Obtiene información básica de la sesión actual
     */
    @GetMapping("/info")
    public ResponseEntity<SessionInfo> obtenerInfoSesion(HttpServletRequest request, Authentication authentication) {
        HttpSession session = request.getSession(false); // false = no crear si no existe

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SessionInfo("No hay sesión activa", null, null, null, null, false));
        }

        SessionInfo info = new SessionInfo(
                session.getId(),
                authentication != null ? authentication.getName() : "Anónimo",
                authentication != null ? authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList() : List.of(),
                new Date(session.getCreationTime()),
                new Date(session.getLastAccessedTime()),
                authentication != null && authentication.isAuthenticated()
        );

        return ResponseEntity.ok(info);
    }

    /**
     * Lista todos los atributos almacenados en la sesión
     */
    @GetMapping("/attributes")
    public ResponseEntity<Map<String, Object>> obtenerAtributosSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object value = session.getAttribute(name);

            // Si es el SecurityContext, extraemos info útil
            if (HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY.equals(name)) {
                SecurityContext securityContext = (SecurityContext) value;
                Authentication auth = securityContext.getAuthentication();
                attributes.put(name, Map.of(
                    "tipo", "SecurityContext",
                    "username", auth != null ? auth.getName() : "null",
                    "authenticated", auth != null && auth.isAuthenticated(),
                    "authorities", auth != null ? auth.getAuthorities().toString() : "[]"
                ));
            } else {
                attributes.put(name, value != null ? value.toString() : "null");
            }
        }

        return ResponseEntity.ok(attributes);
    }

    /**
     * Guarda un atributo personalizado en la sesión
     */
    @PostMapping("/attribute")
    public ResponseEntity<String> guardarAtributo(
            HttpServletRequest request,
            @RequestParam String key,
            @RequestParam String value) {

        HttpSession session = request.getSession(true); // true = crear si no existe
        session.setAttribute(key, value);

        return ResponseEntity.ok("Atributo '" + key + "' guardado con valor: " + value);
    }

    /**
     * Obtiene un atributo específico de la sesión
     */
    @GetMapping("/attribute/{key}")
    public ResponseEntity<String> obtenerAtributo(
            HttpServletRequest request,
            @PathVariable String key) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay sesión activa");
        }

        Object value = session.getAttribute(key);
        if (value == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Atributo no encontrado");
        }

        return ResponseEntity.ok(value.toString());
    }

    /**
     * Obtiene el tiempo de vida de la sesión
     */
    @GetMapping("/timeout")
    public ResponseEntity<Map<String, Object>> obtenerTimeout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> timeoutInfo = new HashMap<>();
        timeoutInfo.put("maxInactiveIntervalSegundos", session.getMaxInactiveInterval());
        timeoutInfo.put("maxInactiveIntervalMinutos", session.getMaxInactiveInterval() / 60);

        return ResponseEntity.ok(timeoutInfo);
    }

    /**
     * Configura el timeout de la sesión (en segundos)
     */
    @PostMapping("/timeout")
    public ResponseEntity<String> configurarTimeout(
            HttpServletRequest request,
            @RequestParam int segundos) {

        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(segundos);

        return ResponseEntity.ok("Timeout configurado a " + segundos + " segundos (" + (segundos/60) + " minutos)");
    }

    // ========================================
    // Métodos movidos desde EjemplosSessionController
    // Se mantienen rutas bajo /session/* para compatibilidad
    // ========================================

    @SuppressWarnings("unchecked")
    @PostMapping("/carrito/agregar")
    public ResponseEntity<String> agregarAlCarrito(
            HttpSession session,
            @RequestParam String producto,
            @RequestParam int cantidad) {

        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        carrito.add(ItemCarrito.of(producto, cantidad));
        session.setAttribute("carrito", carrito);

        return ResponseEntity.ok("✅ Producto agregado. Total items: " + carrito.size());
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    @PostMapping("/historial/agregar")
    public ResponseEntity<String> agregarAlHistorial(
            HttpSession session,
            @RequestParam String pagina) {

        List<String> historial = (List<String>) session.getAttribute("historial");
        if (historial == null) {
            historial = new ArrayList<>();
        }

        historial.add(pagina);

        if (historial.size() > 10) {
            historial.remove(0);
        }

        session.setAttribute("historial", historial);
        return ResponseEntity.ok("📄 Página agregada al historial");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/historial")
    public ResponseEntity<Map<String, Object>> verHistorial(HttpSession session) {
        List<String> historial = (List<String>) session.getAttribute("historial");

        return ResponseEntity.ok(Map.of(
                "historial", historial != null ? historial : List.of(),
                "cantidad", historial != null ? historial.size() : 0
        ));
    }

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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    @PostMapping("/registro/paso3")
    public ResponseEntity<Map<String, Object>> paso3RegistroConfirmar(HttpSession session) {
        Map<String, String> paso1 = (Map<String, String>) session.getAttribute("registroPaso1");
        Map<String, String> paso2 = (Map<String, String>) session.getAttribute("registroPaso2");

        if (paso1 == null || paso2 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Debes completar todos los pasos anteriores"));
        }

        Map<String, Object> registroCompleto = new HashMap<>();
        registroCompleto.putAll(paso1);
        registroCompleto.putAll(paso2);

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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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
}
