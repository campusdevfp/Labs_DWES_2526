# 👨‍🏫 Notas para el Profesor

## 📖 Introducción

Este proyecto base ha sido diseñado como material educativo completo para enseñar desarrollo web moderno con Angular 21. Incluye:

1. ✅ **Proyecto funcional** con autenticación JWT completa (Fase 0)
2. ✅ **Documentación exhaustiva** de todos los conceptos
3. ✅ **Enunciado de proyecto** para estudiantes ([PROJECT.md](PROJECT.md))
4. ✅ **Roadmap detallado** con checklists ([ROADMAP.md](ROADMAP.md))
5. ✅ **Guías técnicas** (Docker, API, Comandos)
6. ✅ **Setup de Docker** para despliegue

---

## 🎯 Objetivos Pedagógicos

### **Competencias Técnicas**

| Competencia   | Nivel               | Aplicación en el Proyecto                           |
| ------------- | ------------------- | --------------------------------------------------- |
| Angular 21    | Avanzado            | Standalone components, Signals, Modern control flow |
| TypeScript    | Intermedio-Avanzado | Interfaces, Types, Generics                         |
| HTTP/REST     | Intermedio          | HttpClient, Interceptors, Error handling            |
| Autenticación | Avanzado            | JWT, Guards, Token refresh                          |
| Estado        | Intermedio          | Signals, Computed values                            |
| Routing       | Intermedio          | Lazy loading, Guards, Resolvers                     |
| Forms         | Intermedio          | Reactive forms, Validaciones custom                 |
| DevOps        | Básico              | Docker, Deploy en cloud                             |

### **Competencias Transversales**

- Lectura de documentación técnica
- Integración con APIs externas
- Resolución de problemas
- Debugging sistemático
- Control de versiones (Git)
- Trabajo autónomo
- Gestión de tiempo (proyecto multi-fase)

---

## 📚 Estructura Pedagógica

### **Fase 0: Base Proporcionada** (No se evalúa, pero deben entenderla)

**Qué incluye**:

- Autenticación completa
- Guards e Interceptors
- Servicios con Signals
- Componentes de login/registro
- Modelos TypeScript

**Por qué está completa**:

1. **Complejidad alta**: JWT y Interceptors son conceptos avanzados
2. **Foco en aprendizaje**: Queremos que se enfoquen en CRUD y consumo de API
3. **Referencia de código**: Pueden ver "cómo se hace bien" antes de implementar
4. **Ahorro de tiempo**: 2-3 sesiones menos de setup

**Recomendaciones**:

- Dedica 1-2 sesiones a explicar cómo funciona la Fase 0
- Haz un "code walkthrough" en vivo
- Pide que ejecuten y debugging la app base antes de empezar
- Asigna lectura del README.md como tarea previa

---

### **Fase 1: Catálogo Público** (35% - Obligatoria)

**Complejidad**: Media  
**Duración recomendada**: 2-3 semanas  
**Conceptos clave**:

- Servicios HTTP GET
- Signals para estado
- Componentes standalone
- Paginación
- Filtros con query params
- Responsive design

**Puntos de enseñanza**:

1. **Servicios**: Cómo estructurar servicios HTTP
2. **State management**: Cuándo usar signals vs observables
3. **Component communication**: Input/Output vs Services
4. **Performance**: TrackBy en ngFor, OnPush
5. **UX**: Loading states, error handling

**Posibles dificultades**:

- ❌ No entienden los observables
  - 💡 Solución: Repaso de RxJS básico
- ❌ Confusión con signals
  - 💡 Solución: Explicar diferencia con BehaviorSubject

- ❌ Filtros no se aplican
  - 💡 Solución: Revisar HttpParams, debugging de network tab

- ❌ Paginación incorrecta
  - 💡 Solución: Explicar offset/limit vs page/pageSize

**Evaluación recomendada**:

- Code review en sesión (30 min por alumno)
- Demo en vivo de funcionalidad
- Checklist de criterios técnicos

---

### **Fase 2: Panel Admin** (35% - Obligatoria)

**Complejidad**: Media-Alta  
**Duración recomendada**: 2-3 semanas  
**Conceptos clave**:

- HTTP POST/PUT/DELETE
- Reactive Forms avanzados
- Guards con roles
- Manejo de errores
- Confirmación de acciones destructivas

**Puntos de enseñanza**:

1. **CRUD completo**: Patrones de implementación
2. **Validaciones**: Built-in vs custom validators
3. **Seguridad**: Guards, roles, permisos
4. **UX Admin**: Feedback de operaciones, toasts/alerts
5. **Testing**: Verificar que solo admins acceden

**Posibles dificultades**:

- ❌ AdminGuard no funciona
  - 💡 Solución: Revisar order de guards, inject de AuthService

- ❌ Formulario no valida
  - 💡 Solución: FormBuilder, Validators, template syntax

- ❌ POST/PUT devuelve 401
  - 💡 Solución: Verificar que el token se envía, que el user es admin

- ❌ Delete sin confirmación
  - 💡 Solución: Dialog component, async confirmation

**Evaluación recomendada**:

- Demo completo de CRUD
- Revisión de código (guards, forms)
- Prueba con usuario no-admin (debe ser bloqueado)

---

### **Fase 3: Features Extra** (30% - Bonus)

**Complejidad**: Variable  
**Duración recomendada**: 2-4 semanas  
**Objetivo**: Permite a alumnos brillantes ir más allá

**Features por nivel**:

| Feature         | Dificultad  | Ideal para...              |
| --------------- | ----------- | -------------------------- |
| Dark Mode       | Fácil       | Practicar signals globales |
| Favoritos       | Fácil-Media | LocalStorage, signals      |
| Perfil editable | Media       | Forms, PUT requests        |
| Carrito         | Media-Alta  | State management complejo  |
| Upload imágenes | Media-Alta  | Multipart/form-data        |
| PWA             | Alta        | Service Workers, offline   |
| Infinite Scroll | Media       | Scroll events, performance |

**Recomendación**: Sugerir 2-3 features según el nivel del alumno

---

## 📅 Planificación de Curso

### **Opción 1: Curso Intensivo (6 semanas)**

| Semana | Contenido                                       |
| ------ | ----------------------------------------------- |
| 1      | Introducción Angular, Setup, Explicación Fase 0 |
| 2      | Servicios HTTP, Signals, Inicio Fase 1          |
| 3      | Continuación Fase 1, Components, Routing        |
| 4      | Forms, Validaciones, Inicio Fase 2              |
| 5      | Continuación Fase 2, Guards, CRUD               |
| 6      | Fase 3 (opcional), Deploy, Presentaciones       |

### **Opción 2: Curso Semestral (12-14 semanas)**

| Semanas | Contenido                                        |
| ------- | ------------------------------------------------ |
| 1-2     | Fundamentos Angular, TypeScript                  |
| 3-4     | Explicación detallada Fase 0, HTTP, Interceptors |
| 5-7     | Fase 1: Servicios, Components, State             |
| 8-10    | Fase 2: Forms, Guards, CRUD                      |
| 11-12   | Fase 3: Features avanzadas                       |
| 13-14   | Testing, Deploy, Presentaciones                  |

### **Opción 3: Proyecto Final de Curso (4 semanas)**

Asume que conocen Angular básico:

| Semana | Contenido                         |
| ------ | --------------------------------- |
| 1      | Code review Fase 0, Inicio Fase 1 |
| 2      | Completar Fase 1                  |
| 3      | Fase 2 completa                   |
| 4      | Fase 3 + Deploy + Presentación    |

---

## 🎓 Sesiones Recomendadas

### **Sesión 1: Introducción al Proyecto** (2h)

**Agenda**:

1. Presentación del e-commerce (15 min)
2. Demostración de la app base (20 min)
3. Explicación de la arquitectura (30 min)
4. Code walkthrough de Fase 0 (40 min)
5. Setup local para todos (15 min)

**Materiales**:

- Proyector para live coding
- README.md proyectado
- ChromeDevTools para mostrar tokens

### **Sesión 2: Servicios y API** (2h)

**Agenda**:

1. Repaso HTTP en Angular (20 min)
2. Documentación de la API Platzi (20 min)
3. Live coding: ProductService (40 min)
4. Signals para state management (20 min)
5. Ejercicio: CategoryService (20 min)

**Materiales**:

- Swagger UI de la API
- Postman para demos
- VS Code con proyecto abierto

### **Sesión 3: Componentes y Routing** (2h)

**Agenda**:

1. Standalone components (15 min)
2. Component communication (25 min)
3. Routing y lazy loading (20 min)
4. Live coding: ProductList (40 min)
5. Ejercicio: ProductCard (20 min)

### **Sesión 4: Forms y Validaciones** (2h)

**Agenda**:

1. Reactive Forms repaso (20 min)
2. Validadores custom (30 min)
3. Live coding: ProductForm (50 min)
4. Ejercicio: CategoryForm (20 min)

### **Sesión 5: Guards y Seguridad** (1.5h)

**Agenda**:

1. Tipos de guards (15 min)
2. Role-based access (20 min)
3. Live coding: AdminGuard (30 min)
4. Testing de permisos (15 min)
5. Q&A (10 min)

### **Sesión 6: Docker y Deploy** (1.5h)

**Agenda**:

1. Introducción a Docker (15 min)
2. Multi-stage builds (20 min)
3. Docker Compose (15 min)
4. Deploy en Render.com (30 min)
5. Troubleshooting común (10 min)

---

## ✅ Checklist para el Profesor

### **Antes del Curso**

- [ ] Probar el proyecto base localmente
- [ ] Verificar que la API Platzi funcione
- [ ] Crear tu propia cuenta admin en la API
- [ ] Preparar repositorio template (GitHub Classroom si usas)
- [ ] Configurar cuenta en Render/Railway para demos
- [ ] Revisar toda la documentación incluida
- [ ] Preparar rúbrica de evaluación adaptada a tu curso
- [ ] Definir fechas de entrega

### **Durante el Curso**

- [ ] Hacer code reviews regulares
- [ ] Responder dudas en foro/Discord
- [ ] Compartir ejemplos de código en sesiones
- [ ] Recordar fechas de entrega
- [ ] Hacer follow-up de alumnos con problemas
- [ ] Compartir recursos adicionales según necesidad

### **En la Evaluación**

- [ ] Usar rúbrica consistentemente
- [ ] Probar apps desplegadas
- [ ] Verificar que el código compile
- [ ] Revisar commits (no todo en un solo commit)
- [ ] Evaluar presentación/demo
- [ ] Dar feedback constructivo

---

## 🎯 Criterios de Evaluación Sugeridos

### **Criterios Técnicos (70%)**

#### Fase 1 - Catálogo (35%)

- **Servicios (10%)**: HTTP requests correctos, manejo de errores, signals
- **Componentes (10%)**: Bien estructurados, reutilizables, standalone
- **Filtros (8%)**: Búsqueda, categorías, precio funcionan
- **Paginación (4%)**: Offset/limit implementado
- **Responsive (3%)**: Mobile, tablet, desktop

#### Fase 2 - Admin (35%)

- **Guards (5%)**: AdminGuard funciona, redirect correcto
- **CRUD Productos (15%)**: Create, Update, Delete funcionan
- **CRUD Categorías (8%)**: Gestión completa
- **Forms (5%)**: Validaciones, error messages
- **Confirmaciones (2%)**: Dialog antes de delete

### **Criterios de Calidad (30%)**

- **Código limpio (10%)**:
  - No usar `any` sin justificación
  - Nombres descriptivos
  - Código formateado
  - No código comentado sin usar

- **Arquitectura (8%)**:
  - Carpetas organizadas según estructura sugerida
  - Separación de responsabilidades
  - Servicios reutilizables

- **UX (7%)**:
  - Loading states
  - Error handling visible
  - Feedback de acciones
  - Navegación intuitiva

- **Documentación (5%)**:
  - README actualizado
  - Credenciales de prueba
  - Instrucciones de ejecución

### **Bonus (hasta +30%)**

- Fase 3 features implementadas
- Tests unitarios
- Tests e2e
- CI/CD configurado
- i18n (multiidioma)
- Accesibilidad (ARIA, keyboard nav)

---

## 🚨 Problemas Comunes y Soluciones

### **Problema: "La API no responde"**

**Síntomas**: Errores 500, timeout, CORS

**Diagnóstico**:

```bash
# Verificar que la API esté up
curl https://api.escuelajs.co/api/v1/products?limit=1

# Si devuelve JSON, la API está bien
# Si no, puede estar caída temporalmente
```

**Soluciones**:

1. Esperar (la API tiene alta disponibilidad)
2. Usar datos mock temporalmente
3. Verificar conexión del alumno

---

### **Problema: "Token no se envía en headers"**

**Síntomas**: 401 Unauthorized en requests a endpoints protegidos

**Diagnóstico**:

```typescript
// En Chrome DevTools → Network → Headers
// Debe aparecer: Authorization: Bearer eyJhbG...

// Si no aparece, verificar:
// 1. Interceptor está registrado en app.config.ts
// 2. Token existe en localStorage
// 3. AuthService.getAccessToken() devuelve el token
```

**Soluciones**:

1. Revisar app.config.ts → `provideHttpClient(withInterceptors([authInterceptor]))`
2. Verificar que el interceptor clona el request correctamente
3. Hacer login de nuevo

---

### **Problema: "Docker build falla"**

**Síntomas**: Error al ejecutar `docker build` o `docker-compose up`

**Diagnóstico**:

```bash
# Ver logs detallados
docker-compose up --build

# Ver qué paso falla
# Común: npm install, npm run build
```

**Soluciones**:

1. Verificar que `package.json` esté correcto
2. Limpiar cache: `docker system prune -a`
3. Verificar suficiente espacio en disco
4. Revisar `.dockerignore` (no excluir carpetas necesarias)

---

### **Problema: "No entienden Signals"**

**Síntomas**: Usan Subject/BehaviorSubject en lugar de signals

**Explicación**:

```typescript
// ❌ Forma antigua (todavía válida pero no moderna)
private products$ = new BehaviorSubject<Product[]>([]);
readonly products = this.products$.asObservable();

// ✅ Forma moderna con Signals
private productsSignal = signal<Product[]>([]);
readonly products = this.productsSignal.asReadonly();

// Ventajas de Signals:
// - Más simple (menos boilerplate)
// - Mejor performance (fine-grained reactivity)
// - Mejor debugging
// - Es la dirección oficial de Angular
```

---

### **Problema: "AdminGuard permite acceso a no-admins"**

**Diagnóstico**:

```typescript
// Verificar en admin.guard.ts:
const user = authService.currentUser();
console.log('User role:', user?.role); // Debe ser 'admin'

// Si es undefined o null:
// 1. Usuario no está logueado
// 2. AuthService no cargó el perfil
// 3. Token expiró
```

**Soluciones**:

1. Asegurar que `authGuard` va ANTES de `adminGuard`
2. Verificar que `currentUser()` devuelve el usuario
3. Hacer login con un usuario admin de verdad

---

## 📊 Métricas de Éxito del Curso

### Indicadores de que el curso va bien:

- ✅ >80% de alumnos completa Fase 1
- ✅ >60% de alumnos completa Fase 2
- ✅ >30% de alumnos implementa al menos 1 feature de Fase 3
- ✅ >90% de proyectos compilan sin errores
- ✅ >70% de proyectos están desplegados online
- ✅ Feedback positivo sobre el proyecto base

### Indicadores de problemas:

- ⚠️ <50% completa Fase 1
  - Problema: Fase 1 demasiado compleja o tiempo insuficiente
  - Solución: Extender plazo o simplificar requisitos

- ⚠️ Muchas consultas sobre lo mismo
  - Problema: Documentación insuficiente
  - Solución: Sesión extra o material complementario

- ⚠️ Proyectos muy similares entre sí
  - Problema: Posible copia
  - Solución: Entrevistas presenciales, pedir explicar código

---

## 🎁 Recursos Adicionales para Compartir

### **Videos Recomendados** (compartir con alumnos)

- Angular Signals: https://www.youtube.com/results?search_query=angular+signals+tutorial
- JWT Authentication: https://www.youtube.com/results?search_query=jwt+authentication+angular
- Docker para Devs: https://www.youtube.com/results?search_query=docker+tutorial

### **Artículos Útiles**

- Angular Docs oficiales: https://angular.dev
- Best Practices: https://angular.dev/best-practices
- HTTP Guide: https://angular.dev/guide/http

### **Herramientas**

- **Postman**: Para testing de API
- **JSON Formatter**: Extensión de Chrome
- **Angular DevTools**: Extensión de Chrome
- **Prettier**: Para formateo automático

---

## 📝 Adaptaciones Sugeridas

### **Para curso más corto (4 semanas)**

- Hacer Fase 1 más simple:
  - Solo lista de productos (sin filtros avanzados)
  - Solo detalle básico
  - Sin categorías
- Hacer Fase 2 más simple:
  - Solo CRUD de productos
  - Sin dashboard
  - Sin categorías

### **Para curso más avanzado**

- Añadir requisito de tests unitarios (Jest/Vitest)
- Añadir tests e2e (Cypress/Playwright)
- Añadir CI/CD con GitHub Actions
- Añadir requisito de accesibilidad (WCAG)

### **Para bootcamp intensivo**

- Proporcionar templates de componentes
- Pair programming en clase
- Menos documentación, más hands-on
- Sprint diario con standup

---

## 🤝 Contribuciones al Proyecto Base

Si encuentras errores o mejoras:

1. Documentarlos claramente
2. Probar la mejora localmente
3. Actualizar documentación si aplica
4. Compartir con comunidad docente

---

## 📞 Contacto del Autor

**Creador del Proyecto Base**: [Tu Nombre/Email]  
**Última actualización**: 2026-02-06  
**Versión**: 1.0

---

## 📄 Licencia

Material educativo de libre uso para instituciones académicas y docentes.

- ✅ Uso educativo gratuito
- ✅ Modificación y adaptación permitida
- ✅ Compartir con otros docentes
- ⚠️ Dar crédito al autor original
- ❌ Uso comercial sin autorización

---

**¡Éxito en tu curso! Si tienes dudas o sugerencias, no dudes en contactar. 🎓**
