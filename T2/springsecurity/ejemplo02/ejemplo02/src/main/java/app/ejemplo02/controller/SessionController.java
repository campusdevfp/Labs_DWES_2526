package app.ejemplo02.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
                        .map(a -> a.getAuthority()).toList() : List.of(),
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
     * Cierra la sesión actual (logout)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            // Invalida la sesión (elimina todos los atributos)
            session.invalidate();
        }

        // Limpia el SecurityContext
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Sesión cerrada exitosamente");
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

    // DTO para información de sesión
    public static class SessionInfo {
        public String sessionId;
        public String username;
        public List<String> roles;
        public Date creationTime;
        public Date lastAccessedTime;
        public boolean isAuthenticated;

        public SessionInfo(String sessionId, String username, List<String> roles,
                          Date creationTime, Date lastAccessedTime, boolean isAuthenticated) {
            this.sessionId = sessionId;
            this.username = username;
            this.roles = roles;
            this.creationTime = creationTime;
            this.lastAccessedTime = lastAccessedTime;
            this.isAuthenticated = isAuthenticated;
        }
    }
}

