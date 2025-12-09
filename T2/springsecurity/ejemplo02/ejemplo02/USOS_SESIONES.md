# 📚 Usos Prácticos de las Sesiones HTTP

## ¿Para qué sirven las sesiones?

Las sesiones permiten **mantener estado entre múltiples peticiones HTTP** del mismo usuario. HTTP es un protocolo sin estado (stateless), pero las sesiones nos permiten "recordar" información del usuario.

---

## 🎯 Casos de Uso Principales

### 1. **Autenticación y Autorización**
**Problema:** No queremos que el usuario ingrese usuario/contraseña en cada petición.

**Solución con sesión:**
- Usuario hace login → credenciales validadas → sesión creada con `Authentication`
- Siguientes peticiones → el servidor reconoce al usuario por su JSESSIONID
- No necesita volver a autenticarse hasta que expire la sesión o haga logout

**Ejemplo:**
```java
// Primera petición: login con user:password
// Spring Security guarda Authentication en sesión

// Siguientes peticiones: solo envía la cookie JSESSIONID
// Spring Security recupera Authentication de la sesión automáticamente
```

---

### 2. **Carrito de Compras (E-commerce)**
**Escenario:** Usuario navega por productos y los agrega al carrito antes de crear cuenta.

```java
@PostMapping("/carrito/agregar")
public ResponseEntity<String> agregarAlCarrito(
        HttpSession session,
        @RequestParam Long productoId,
        @RequestParam int cantidad) {
    
    // Obtener carrito de la sesión (o crear uno nuevo)
    List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
    if (carrito == null) {
        carrito = new ArrayList<>();
    }
    
    // Agregar producto
    carrito.add(new ItemCarrito(productoId, cantidad));
    
    // Guardar en sesión
    session.setAttribute("carrito", carrito);
    
    return ResponseEntity.ok("Producto agregado. Total items: " + carrito.size());
}

@GetMapping("/carrito/ver")
public ResponseEntity<List<ItemCarrito>> verCarrito(HttpSession session) {
    List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
    return ResponseEntity.ok(carrito != null ? carrito : List.of());
}
```

**Ventajas:**
- Usuario no necesita estar autenticado para agregar productos
- Carrito persiste mientras navega por la tienda
- Al hacer login, puedes migrar el carrito de sesión a la base de datos

---

### 3. **Formularios Multi-Paso (Wizards)**
**Escenario:** Proceso de registro, solicitud de préstamo, reserva de hotel con varios pasos.

```java
// Paso 1: Datos personales
@PostMapping("/registro/paso1")
public ResponseEntity<String> paso1(HttpSession session, @RequestBody DatosPersonales datos) {
    session.setAttribute("datosPersonales", datos);
    return ResponseEntity.ok("Paso 1 completado");
}

// Paso 2: Dirección
@PostMapping("/registro/paso2")
public ResponseEntity<String> paso2(HttpSession session, @RequestBody Direccion direccion) {
    session.setAttribute("direccion", direccion);
    return ResponseEntity.ok("Paso 2 completado");
}

// Paso 3: Confirmar y guardar todo
@PostMapping("/registro/confirmar")
public ResponseEntity<String> confirmar(HttpSession session) {
    DatosPersonales datos = (DatosPersonales) session.getAttribute("datosPersonales");
    Direccion direccion = (Direccion) session.getAttribute("direccion");
    
    // Guardar en BD
    usuarioService.crearUsuario(datos, direccion);
    
    // Limpiar sesión
    session.removeAttribute("datosPersonales");
    session.removeAttribute("direccion");
    
    return ResponseEntity.ok("Usuario creado exitosamente");
}
```

---

### 4. **Preferencias y Configuración Temporal**
**Escenario:** Usuario cambia idioma, tema oscuro/claro, filtros de búsqueda.

```java
@PostMapping("/preferencias/idioma")
public ResponseEntity<String> cambiarIdioma(
        HttpSession session,
        @RequestParam String idioma) {
    
    session.setAttribute("idioma", idioma);
    return ResponseEntity.ok("Idioma cambiado a: " + idioma);
}

@GetMapping("/productos")
public ResponseEntity<List<Producto>> listarProductos(HttpSession session) {
    String idioma = (String) session.getAttribute("idioma");
    if (idioma == null) idioma = "es"; // por defecto español
    
    List<Producto> productos = productoService.listarEnIdioma(idioma);
    return ResponseEntity.ok(productos);
}
```

---

### 5. **Flash Messages (Mensajes Temporales)**
**Escenario:** Mostrar mensaje de éxito/error después de una acción (ej: "Producto guardado correctamente").

```java
@PostMapping("/producto/guardar")
public ResponseEntity<String> guardarProducto(
        HttpSession session,
        @RequestBody Producto producto) {
    
    productoService.guardar(producto);
    
    // Guardar mensaje flash en sesión
    session.setAttribute("flashMessage", "Producto guardado exitosamente");
    session.setAttribute("flashType", "success");
    
    return ResponseEntity.ok("OK");
}

@GetMapping("/dashboard")
public ResponseEntity<Map<String, Object>> dashboard(HttpSession session) {
    Map<String, Object> response = new HashMap<>();
    
    // Obtener y eliminar mensaje flash
    String message = (String) session.getAttribute("flashMessage");
    String type = (String) session.getAttribute("flashType");
    
    if (message != null) {
        response.put("message", message);
        response.put("type", type);
        session.removeAttribute("flashMessage");
        session.removeAttribute("flashType");
    }
    
    return ResponseEntity.ok(response);
}
```

---

### 6. **Prevención de Double Submit (Formularios Duplicados)**
**Escenario:** Evitar que el usuario envíe el mismo formulario dos veces (ej: doble clic en "Pagar").

```java
@GetMapping("/pedido/nuevo")
public ResponseEntity<Map<String, String>> formularioPedido(HttpSession session) {
    // Generar token único
    String token = UUID.randomUUID().toString();
    session.setAttribute("pedidoToken", token);
    
    return ResponseEntity.ok(Map.of("token", token));
}

@PostMapping("/pedido/confirmar")
public ResponseEntity<String> confirmarPedido(
        HttpSession session,
        @RequestParam String token,
        @RequestBody Pedido pedido) {
    
    String sessionToken = (String) session.getAttribute("pedidoToken");
    
    if (sessionToken == null || !sessionToken.equals(token)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Token inválido o pedido ya procesado");
    }
    
    // Procesar pedido
    pedidoService.crear(pedido);
    
    // Eliminar token para evitar reenvío
    session.removeAttribute("pedidoToken");
    
    return ResponseEntity.ok("Pedido confirmado");
}
```

---

### 7. **Caché Temporal de Datos**
**Escenario:** Evitar consultas repetidas a la base de datos para datos que no cambian frecuentemente.

```java
@GetMapping("/categorias")
public ResponseEntity<List<Categoria>> listarCategorias(HttpSession session) {
    // Intentar obtener de sesión
    List<Categoria> categorias = (List<Categoria>) session.getAttribute("categorias");
    
    if (categorias == null) {
        // No está en sesión, consultar BD
        categorias = categoriaService.listarTodas();
        
        // Guardar en sesión por 10 minutos
        session.setAttribute("categorias", categorias);
        session.setMaxInactiveInterval(600);
    }
    
    return ResponseEntity.ok(categorias);
}
```

---

### 8. **Tracking de Actividad del Usuario**
**Escenario:** Registrar qué páginas visitó, qué productos vio (para recomendaciones).

```java
@GetMapping("/producto/{id}")
public ResponseEntity<Producto> verProducto(
        HttpSession session,
        @PathVariable Long id) {
    
    // Obtener historial de productos vistos
    List<Long> historial = (List<Long>) session.getAttribute("productosVistos");
    if (historial == null) {
        historial = new ArrayList<>();
    }
    
    // Agregar producto actual
    if (!historial.contains(id)) {
        historial.add(id);
        if (historial.size() > 10) {
            historial.remove(0); // Mantener solo últimos 10
        }
    }
    
    session.setAttribute("productosVistos", historial);
    
    Producto producto = productoService.buscarPorId(id);
    return ResponseEntity.ok(producto);
}

@GetMapping("/recomendaciones")
public ResponseEntity<List<Producto>> obtenerRecomendaciones(HttpSession session) {
    List<Long> historial = (List<Long>) session.getAttribute("productosVistos");
    if (historial == null || historial.isEmpty()) {
        return ResponseEntity.ok(List.of());
    }
    
    List<Producto> recomendaciones = productoService.recomendarBasadoEn(historial);
    return ResponseEntity.ok(recomendaciones);
}
```

---

### 9. **Límite de Intentos (Rate Limiting)**
**Escenario:** Prevenir ataques de fuerza bruta limitando intentos de login.

```java
@PostMapping("/login")
public ResponseEntity<String> login(
        HttpSession session,
        @RequestParam String username,
        @RequestParam String password) {
    
    // Obtener contador de intentos fallidos
    Integer intentos = (Integer) session.getAttribute("intentosFallidos");
    if (intentos == null) intentos = 0;
    
    if (intentos >= 3) {
        Long bloqueadoHasta = (Long) session.getAttribute("bloqueadoHasta");
        if (bloqueadoHasta != null && System.currentTimeMillis() < bloqueadoHasta) {
            long segundosRestantes = (bloqueadoHasta - System.currentTimeMillis()) / 1000;
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Demasiados intentos. Espera " + segundosRestantes + " segundos");
        } else {
            // Reset después del bloqueo
            intentos = 0;
            session.removeAttribute("bloqueadoHasta");
        }
    }
    
    // Validar credenciales
    if (authService.validar(username, password)) {
        session.removeAttribute("intentosFallidos");
        session.setAttribute("usuario", username);
        return ResponseEntity.ok("Login exitoso");
    } else {
        intentos++;
        session.setAttribute("intentosFallidos", intentos);
        
        if (intentos >= 3) {
            // Bloquear por 5 minutos
            session.setAttribute("bloqueadoHasta", System.currentTimeMillis() + 300000);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Cuenta bloqueada por 5 minutos");
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Credenciales inválidas. Intentos restantes: " + (3 - intentos));
    }
}
```

---

### 10. **Estado de Aplicación Multi-Usuario (Colaboración)**
**Escenario:** Mostrar qué usuarios están editando un documento actualmente.

```java
@PostMapping("/documento/{id}/editar")
public ResponseEntity<String> comenzarEdicion(
        HttpSession session,
        @PathVariable Long id,
        Authentication auth) {
    
    String username = auth.getName();
    
    // Registrar que este usuario está editando
    session.setAttribute("editandoDocumento", id);
    
    // En un Map global (no en sesión)
    documentoService.registrarEditor(id, username, session.getId());
    
    return ResponseEntity.ok("Comenzaste a editar el documento");
}

@GetMapping("/documento/{id}/editores")
public ResponseEntity<List<String>> verEditoresActivos(@PathVariable Long id) {
    List<String> editores = documentoService.obtenerEditoresActivos(id);
    return ResponseEntity.ok(editores);
}
```

---

## ⚖️ Sesión vs. Base de Datos

| Aspecto | Sesión | Base de Datos |
|---------|--------|---------------|
| **Velocidad** | ⚡ Muy rápida (memoria) | 🐢 Más lenta (disco/red) |
| **Persistencia** | ❌ Se pierde al reiniciar | ✅ Permanente |
| **Tamaño** | 📏 Pequeños datos (< 5 KB) | 📦 Sin límite real |
| **Duración** | ⏱️ Temporal (minutos/horas) | ♾️ Indefinida |
| **Uso típico** | Estado temporal, caché | Datos del usuario, pedidos |

---

## 🛡️ Buenas Prácticas

### ✅ Usa sesiones para:
- Información de autenticación (automático en Spring Security)
- Datos temporales del flujo de trabajo (wizards)
- Carrito de compras antes de login
- Preferencias de visualización (idioma, tema)
- Flash messages
- Tokens anti-CSRF
- Rate limiting temporal

### ❌ NO uses sesiones para:
- Datos que deben persistir permanentemente
- Información sensible en texto plano (contraseñas, tarjetas)
- Archivos grandes o binarios
- Datos que deben compartirse entre múltiples servidores (sin Redis/JDBC)
- Lógica de negocio que debe auditarse

---

## 🔄 Migrar de Sesión a BD

**Patrón común:** Comenzar en sesión, guardar en BD al confirmar.

```java
// Usuario agrega productos al carrito (sin login)
session.setAttribute("carrito", items);

// Usuario hace login
Authentication auth = ...;

// Migrar carrito de sesión a BD
List<ItemCarrito> carritoSesion = (List<ItemCarrito>) session.getAttribute("carrito");
if (carritoSesion != null) {
    carritoService.migrarCarrito(auth.getName(), carritoSesion);
    session.removeAttribute("carrito"); // Limpiar sesión
}
```

---

## 📊 Cuándo usar cada solución

```
┌─────────────────────────────────────────────────────────────┐
│                    Flujo de decisión                         │
└─────────────────────────────────────────────────────────────┘

¿Los datos deben sobrevivir al cierre del navegador?
├─ SÍ  → Base de Datos
└─ NO  → ¿Son específicos de este usuario en esta visita?
    ├─ SÍ  → Sesión HTTP
    └─ NO  → ¿Son configuración global de la app?
        ├─ SÍ  → application.properties / BD
        └─ NO  → LocalStorage (frontend) / Cookie
```

---

## 💡 Resumen Ejecutivo

Las sesiones son perfectas para **mantener contexto temporal entre peticiones** del mismo usuario. Son rápidas, fáciles de usar y automáticas en Spring Security para autenticación. Úsalas para datos que:

1. **Son temporales** (duran solo esta visita)
2. **Son pequeños** (pocos KB)
3. **Son específicos del usuario** (no globales)
4. **No necesitan persistir** después del logout/cierre

Para todo lo demás: usa la base de datos, caché (Redis), o almacenamiento del cliente (LocalStorage).

---

**¡Las sesiones son una herramienta poderosa cuando se usan correctamente!** 🚀

