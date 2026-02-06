# 📚 Proyecto Final: E-Commerce con Angular 21 y JWT

## 🎯 Introducción

Este proyecto consiste en desarrollar un **frontend completo de e-commerce** utilizando Angular 21, integrándose con una API REST externa (Platzi Fake Store API) e implementando autenticación JWT completa.

El proyecto está diseñado para aplicar los conocimientos de Angular moderno, incluyendo standalone components, signals, reactive forms, HTTP interceptors, guards, y despliegue con Docker.

---

## 📋 Objetivos de Aprendizaje

Al completar este proyecto, el alumno será capaz de:

1. ✅ Implementar autenticación JWT completa (login, registro, refresh tokens)
2. ✅ Gestionar estado reactivo con Signals de Angular
3. ✅ Crear y consumir servicios HTTP con HttpClient
4. ✅ Proteger rutas con Guards
5. ✅ Interceptar peticiones HTTP para añadir tokens automáticamente
6. ✅ Crear interfaces y modelos TypeScript type-safe
7. ✅ Implementar formularios reactivos con validaciones
8. ✅ Diseñar componentes standalone reutilizables
9. ✅ Integrar con API REST externa (documentada con OpenAPI/Swagger)
10. ✅ Desplegar aplicación con Docker y en plataformas cloud gratuitas
11. ✅ Implementar paginación, filtros y búsqueda en tiempo real
12. ✅ Crear panel de administración con permisos basados en roles
13. ✅ Aplicar mejores prácticas de Angular 21 (modern control flow, signals, etc.)

---

## 🏗️ Arquitectura del Proyecto

### **Stack Tecnológico**

- **Frontend**: Angular 21 (Standalone Components)
- **Lenguaje**: TypeScript 5.x
- **Estilos**: SCSS + TailwindCSS (opcional)
- **Estado**: Angular Signals
- **HTTP**: HttpClient con Interceptors
- **Routing**: Angular Router con Guards
- **Forms**: Reactive Forms
- **Auth**: JWT (Access Token + Refresh Token)
- **API Externa**: [Platzi Fake Store API](https://fakeapi.platzi.com/)
- **Deploy**: Docker + Nginx
- **Plataforma**: Render.com / Railway.app / Fly.io (gratuita)

### **Estructura de Carpetas Requerida**

```
src/app/
├── core/
│   ├── guards/
│   │   ├── auth.guard.ts
│   │   └── admin.guard.ts
│   ├── interceptors/
│   │   └── auth.interceptor.ts
│   └── services/
│       ├── auth.service.ts
│       ├── product.service.ts
│       └── category.service.ts
│
├── shared/
│   ├── components/
│   │   ├── navbar/
│   │   ├── product-card/
│   │   ├── loading-spinner/
│   │   ├── pagination/
│   │   └── confirm-dialog/
│   └── pipes/
│       └── (pipes personalizados)
│
├── features/
│   ├── auth/
│   │   ├── login/
│   │   ├── register/
│   │   └── profile/
│   │
│   ├── shop/
│   │   ├── product-list/
│   │   ├── product-detail/
│   │   ├── product-filters/
│   │   ├── category-list/
│   │   └── category-detail/
│   │
│   └── admin/
│       ├── dashboard/
│       ├── products/
│       │   ├── product-list/
│       │   └── product-form/
│       └── categories/
│
├── models/
│   ├── user.model.ts
│   ├── product.model.ts
│   └── category.model.ts
│
└── app.routes.ts
```

---

## 📦 Fases del Proyecto

### **FASE 0: Setup Inicial** (Proporcionado ✅)

**Estado**: Completo en el proyecto base

**Incluye**:

- ✅ Proyecto Angular 21 inicializado
- ✅ Autenticación JWT completa (login, register, logout)
- ✅ AuthService con Signals
- ✅ AuthGuard para protección de rutas
- ✅ AuthInterceptor para inyección automática de tokens
- ✅ LoginComponent y RegisterComponent funcionales
- ✅ NavbarComponent con estado de autenticación
- ✅ Modelos de User, LoginCredentials, RegisterData, AuthResponse
- ✅ Integración básica con la API
- ✅ Docker setup completo
- ✅ Documentación exhaustiva

**No necesitas implementar esta fase**, pero **debes entenderla completamente**.

---

### **FASE 1: Catálogo de Productos Público** ⭐ (Obligatoria)

**Duración estimada**: 2-3 sesiones  
**Peso en calificación**: 35%

#### **Objetivos**:

Crear la tienda pública donde usuarios pueden navegar productos, filtrar, buscar y ver detalles.

#### **Tareas a Implementar**:

1. **Modelos e Interfaces** (5%)
   - [ ] `models/product.model.ts` con interfaces: `Product`, `ProductFilters`, `ProductCreate`, `ProductUpdate`
   - [ ] `models/category.model.ts` con interfaces: `Category`, `CategoryCreate`, `CategoryUpdate`

2. **Servicios** (10%)
   - [ ] `ProductService` con:
     - `getProducts(filters?: ProductFilters): Observable<Product[]>`
     - `getProductById(id: number): Observable<Product>`
     - `getProductBySlug(slug: string): Observable<Product>`
     - `getRelatedProducts(id: number): Observable<Product[]>`
     - State management con signals: `products`, `loading`, `error`
   - [ ] `CategoryService` con:
     - `getCategories(): Observable<Category[]>`
     - `getCategoryById(id: number): Observable<Category>`
     - `getProductsByCategory(id: number): Observable<Product[]>`

3. **Componentes Compartidos** (5%)
   - [ ] `ProductCardComponent`: Card reutilizable para mostrar producto
   - [ ] `PaginationComponent`: Componente de paginación genérico
   - [ ] `LoadingSpinnerComponent`: Spinner o skeleton loaders

4. **Componentes de Features** (10%)
   - [ ] `ProductListComponent`: Grid de productos con paginación
   - [ ] `ProductFiltersComponent`: Búsqueda por título, categoría, rango de precio
   - [ ] `ProductDetailComponent`: Vista detalle con galería y productos relacionados
   - [ ] `CategoryListComponent`: Grid de categorías

5. **Rutas** (5%)
   ```typescript
   /shop                      → ProductListComponent
   /shop/product/:id          → ProductDetailComponent
   /categories                → CategoryListComponent
   /categories/:slug          → Products filtrados por categoría
   ```

#### **Criterios de Evaluación FASE 1**:

| Criterio                    | Puntos | Descripción                                         |
| --------------------------- | ------ | --------------------------------------------------- |
| **Modelos bien definidos**  | 5%     | Interfaces TypeScript completas y type-safe         |
| **Servicios funcionales**   | 10%    | HTTP requests correctos, manejo de errores, signals |
| **ProductList con filtros** | 8%     | Grid responsive, filtros funcionan, paginación      |
| **ProductDetail completo**  | 7%     | Galería de imágenes, info completa, relacionados    |
| **CategoryList funcional**  | 5%     | Muestra categorías correctamente                    |
| **Responsive Design**       | 5%     | Funciona en mobile, tablet, desktop                 |

#### **Entregables FASE 1**:

- ✅ Código fuente en repositorio Git
- ✅ Captura de pantalla o video demo (2-3 min)
- ✅ App desplegada en Render/Railway/Fly.io
- ✅ README actualizado con instrucciones

---

### **FASE 2: Panel de Administración** ⭐ (Obligatoria)

**Duración estimada**: 2-3 sesiones  
**Peso en calificación**: 35%

#### **Objetivos**:

Crear panel de administración donde usuarios con `role: 'admin'` pueden gestionar productos y categorías (CRUD completo).

#### **Tareas a Implementar**:

1. **Guard de Admin** (5%)
   - [ ] `AdminGuard`: Verificar que `user.role === 'admin'`
   - [ ] Redirigir a `/shop` si no es admin

2. **Servicios - Métodos CRUD** (5%)
   - [ ] `ProductService`:
     - `createProduct(data: ProductCreate): Observable<Product>`
     - `updateProduct(id: number, data: Partial<Product>): Observable<Product>`
     - `deleteProduct(id: number): Observable<boolean>`
   - [ ] `CategoryService`:
     - `createCategory(data: CategoryCreate): Observable<Category>`
     - `updateCategory(id: number, data: Partial<Category>): Observable<Category>`
     - `deleteCategory(id: number): Observable<boolean>`

3. **Componentes Admin** (20%)
   - [ ] `AdminDashboardComponent`: Panel con estadísticas básicas
   - [ ] `AdminProductsComponent`: Tabla de productos con acciones (editar, eliminar)
   - [ ] `ProductFormComponent`: Formulario reactivo para crear/editar productos
   - [ ] `AdminCategoriesComponent`: Gestión de categorías
   - [ ] `ConfirmDialogComponent`: Modal de confirmación para deletes

4. **Rutas Protegidas** (5%)
   ```typescript
   /admin                     → AdminDashboardComponent (authGuard + adminGuard)
   /admin/products            → AdminProductsComponent
   /admin/products/new        → ProductFormComponent (crear)
   /admin/products/edit/:id   → ProductFormComponent (editar)
   /admin/categories          → AdminCategoriesComponent
   ```

#### **Criterios de Evaluación FASE 2**:

| Criterio                         | Puntos | Descripción                                 |
| -------------------------------- | ------ | ------------------------------------------- |
| **AdminGuard funcional**         | 5%     | Solo admins acceden al panel                |
| **CRUD Productos completo**      | 12%    | Crear, editar, eliminar productos funciona  |
| **CRUD Categorías completo**     | 8%     | Gestionar categorías correctamente          |
| **Formularios con validaciones** | 5%     | Reactive forms con validaciones apropiadas  |
| **Confirmación de deletes**      | 3%     | Dialog de confirmación antes de eliminar    |
| **UX del panel**                 | 2%     | Interfaz clara, feedback visual de acciones |

#### **Entregables FASE 2**:

- ✅ Código actualizado en repositorio
- ✅ Usuario admin de prueba creado: `admin@mail.com` / `admin123` (documéntalo en README)
- ✅ Video demo mostrando CRUD completo (3-5 min)
- ✅ Deploy actualizado

---

### **FASE 3: Funcionalidades Adicionales** 🌟 (Opcional - Extra)

**Duración estimada**: 2-4 sesiones  
**Peso en calificación**: 30% (bonus)

#### **Elige al menos 3 de las siguientes funcionalidades**:

1. **Perfil de Usuario Editable** (10%)
   - [ ] Página `/profile` protegida
   - [ ] Formulario para editar: nombre, email, avatar
   - [ ] Integrar con `PUT /users/:id`

2. **Sistema de Favoritos** (10%)
   - [ ] `FavoritesService` con signals
   - [ ] Persistencia en localStorage
   - [ ] Botón "❤️" en ProductCard
   - [ ] Página `/favorites` con listado
   - [ ] Badge en navbar con contador

3. **Carrito de Compras** (15%)
   - [ ] `CartService` con signals
   - [ ] Añadir/Quitar/Actualizar cantidad
   - [ ] Persistencia en localStorage
   - [ ] Mini-cart en navbar (dropdown)
   - [ ] Página `/cart` con tabla y totales

4. **Upload de Imágenes** (10%)
   - [ ] Integrar con `POST /files/upload`
   - [ ] Component drag & drop
   - [ ] Preview de imágenes
   - [ ] Progress bar

5. **Búsqueda Avanzada** (8%)
   - [ ] Autocomplete mientras escribes
   - [ ] Historial de búsquedas (localStorage)
   - [ ] Sugerencias populares

6. **Dark Mode** (5%)
   - [ ] Toggle en navbar
   - [ ] Signal global para theme
   - [ ] CSS variables para colores
   - [ ] Persistencia en localStorage

7. **Infinite Scroll** (7%)
   - [ ] Reemplazar paginación tradicional
   - [ ] Cargar más productos al hacer scroll
   - [ ] Loading indicator

8. **PWA (Progressive Web App)** (10%)
   - [ ] Service Worker
   - [ ] manifest.json
   - [ ] Funciona offline (básico)
   - [ ] Instalable en dispositivos

#### **Criterios de Evaluación FASE 3**:

Cada feature implementada suma puntos extra. Se evalúa:

- **Funcionalidad**: ¿Funciona correctamente? (60%)
- **Código limpio**: ¿Sigue buenas prácticas? (20%)
- **UX**: ¿Mejora la experiencia del usuario? (20%)

---

## 📊 Rúbrica de Evaluación Global

### **Distribución de Puntos**

| Concepto              | Peso     | Descripción                                      |
| --------------------- | -------- | ------------------------------------------------ |
| **FASE 0 (Base)**     | 0%       | Proporcionada, no se evalúa pero debe entenderse |
| **FASE 1 (Shop)**     | 35%      | Catálogo público funcional                       |
| **FASE 2 (Admin)**    | 35%      | Panel de administración con CRUD                 |
| **FASE 3 (Extra)**    | 30%      | Funcionalidades adicionales (bonus)              |
| **Calidad de Código** | 10%      | Buenas prácticas, código limpio                  |
| **Documentación**     | 5%       | README actualizado, comentarios                  |
| **Despliegue**        | 5%       | App funcionando online                           |
| **Presentación**      | 10%      | Demo en vivo o video                             |
| **TOTAL BASE**        | **100%** | Fases 1 + 2 + calidad + docs                     |
| **TOTAL CON BONUS**   | **130%** | Base + Fase 3                                    |

### **Escala de Calificación**

- **90-100%**: Excelente - Todas las funcionalidades + código ejemplar
- **80-89%**: Muy Bien - Fases 1 y 2 completas + buena calidad
- **70-79%**: Bien - Fases 1 y 2 completas con algunos detalles por mejorar
- **60-69%**: Suficiente - Fase 1 completa + Fase 2 parcial
- **<60%**: Insuficiente - Incompleto o con errores graves

---

## 🎯 Entregables Finales

### **Repositorio Git**

```
📁 Tu Repositorio
├── README.md                  ← Actualizado con tu nombre, instrucciones, credenciales
├── CHANGELOG.md               ← Resumen de cambios por fase
├── src/                       ← Código fuente
├── Dockerfile                 ← Configuración Docker
├── docker-compose.yml         ← Docker Compose
└── docs/
    ├── screenshots/           ← Capturas de pantalla de cada fase
    └── demo-video.md          ← Link al video demo
```

### **README.md debe incluir**:

```markdown
# Nombre del Proyecto

**Alumno**: Tu Nombre Completo
**Fecha**: Fecha de entrega
**Curso**: Desarrollo Web con Angular

## Descripción

[Breve descripción del proyecto]

## Tecnologías Utilizadas

- Angular 21
- TypeScript
- [Otras tecnologías]

## Instalación

\`\`\`bash
npm install
npm start
\`\`\`

## Credenciales de Prueba

- Usuario regular: `john@mail.com` / `changeme`
- Usuario admin: `admin@mail.com` / `admin123`

## Despliegue

URL: https://tu-app.onrender.com

## Funcionalidades Implementadas

- [x] FASE 1: Catálogo de productos
- [x] FASE 2: Panel de administración
- [x] FASE 3: [Features opcionales implementadas]

## Capturas de Pantalla

[Inserta capturas aquí]

## Video Demo

[Link a YouTube/Loom/Drive]
```

### **Video Demo** (Obligatorio)

**Duración**: 5-10 minutos  
**Contenido mínimo**:

1. Introducción: Explicar qué hace la app
2. Demo usuario regular:
   - Navegar productos
   - Filtrar y buscar
   - Ver detalle de producto
3. Demo autenticación:
   - Registro de nuevo usuario
   - Login
4. Demo admin:
   - Login como admin
   - Crear producto
   - Editar producto
   - Eliminar producto (con confirmación)
5. Features extra (si las implementaste)
6. Código destacable: Mostrar en VS Code:
   - Un servicio con signals
   - Un guard
   - El interceptor
7. Conclusiones: Aprendizajes y dificultades

**Herramientas recomendadas**: Loom, OBS Studio, Zoom

---

## 📅 Cronograma Sugerido

### **Semana 1: Fase 0 + Planificación**

- Día 1-2: Entender el proyecto base completamente
- Día 3-4: Leer documentación de la API
- Día 5: Planificar Fase 1 (leer `ROADMAP.md`, `API-INTEGRATION.md`)

### **Semana 2-3: Fase 1**

- Día 1-2: Modelos + ProductService
- Día 3-4: CategoryService + Componentes compartidos
- Día 5-7: ProductList + Filters
- Día 8-10: ProductDetail + CategoryList
- Día 11-12: Pruebas, fixes, responsive

### **Semana 4-5: Fase 2**

- Día 1-2: AdminGuard + Servicios CRUD
- Día 3-5: AdminDashboard + AdminProducts
- Día 6-8: ProductForm (crear/editar)
- Día 9-10: AdminCategories + ConfirmDialog
- Día 11-12: Pruebas, fixes

### **Semana 6: Fase 3 (Opcional) + Entrega**

- Día 1-5: Implementar features opcionales
- Día 6-7: Tests finales, documentación
- Día 8: Deploy final, video demo

---

## 🛠️ Herramientas y Recursos

### **Documentación Esencial** (Lee antes de empezar)

- ✅ `README.md` - Documentación principal
- ✅ `ROADMAP.md` - Plan detallado con checklists
- ✅ `API-INTEGRATION.md` - Guía de integración con la API
- ✅ `DOCKER.md` - Guía de Docker
- ✅ `COMMANDS.md` - Referencia rápida de comandos

### **API y Testing**

- **Documentación API**: https://fakeapi.platzi.com/
- **Swagger UI**: https://fakeapi.platzi.com/en/rest/swagger/
- **Postman Collection**: https://fakeapi.platzi.com/en/resources/postman/

### **Angular**

- **Docs oficiales**: https://angular.dev
- **Signals**: https://angular.dev/guide/signals
- **Forms**: https://angular.dev/guide/forms

### **Deploy**

- **Render.com**: https://render.com (Recomendado)
- **Railway.app**: https://railway.app
- **Fly.io**: https://fly.io

### **Control de Versiones**

- GitHub, GitLab o Bitbucket
- Commits descriptivos: `feat:`, `fix:`, `docs:`

---

## ❓ Preguntas Frecuentes (FAQ)

### **¿Puedo usar librerías de UI como Angular Material?**

Sí, puedes usar cualquier librería de componentes UI. Recomendado: Angular Material, PrimeNG, o TailwindCSS.

### **¿Tengo que implementar tests unitarios?**

No es obligatorio, pero suma puntos extra (hasta 5% adicional).

### **¿Puedo trabajar en parejas?**

Consulta con tu profesor. Si es permitido, ambos deben contribuir equitativamente (evidenciado en commits de Git).

### **¿Qué hago si la API está caída?**

La API tiene alta disponibilidad, pero si hay problemas:

1. Verifica tu conexión
2. Revisa el status de la API en su documentación
3. Usa datos mock temporalmente en tu servicio

### **¿Cómo debugging el proyecto?**

Lee la sección "🐛 Depuración" en el README.md principal. Incluye:

- Chrome DevTools
- VS Code Debugger
- Logging en consola

### **¿Es obligatorio usar Docker?**

Docker está configurado pero no es obligatorio usarlo en desarrollo local. SÍ es recomendado para el despliegue final.

### **¿Puedo cambiar el diseño/estilos?**

Totalmente. El diseño es libre siempre que sea responsive y funcional.

### **¿Qué pasa si no termino la Fase 3?**

La Fase 3 es **opcional (bonus)**. Puedes obtener 100% solo con Fases 1 y 2. La Fase 3 permite llegar hasta 130%.

---

## 🚨 Criterios de Rechazo Automático

Tu proyecto será rechazado automáticamente si:

- ❌ No compila o tiene errores de TypeScript
- ❌ No funciona el login/registro (Fase 0 rota)
- ❌ No está desplegado y accesible online
- ❌ Código copiado sin entender (se evaluará en presentación)
- ❌ No hay commits de Git (debes tener historial)
- ❌ Entrega fuera de plazo sin justificación

---

## ✅ Checklist de Pre-entrega

Antes de entregar, verifica:

- [ ] ✅ El proyecto compila sin errores: `npm run build:prod`
- [ ] ✅ Los servicios funcionan correctamente
- [ ] ✅ Las rutas protegidas solo permiten acceso autenticado
- [ ] ✅ El panel admin solo permite acceso a usuarios admin
- [ ] ✅ Los formularios tienen validaciones
- [ ] ✅ La app es responsive (mobile, tablet, desktop)
- [ ] ✅ No hay errores en la consola del navegador
- [ ] ✅ El deploy está actualizado y funciona
- [ ] ✅ El README.md está actualizado con tu información
- [ ] ✅ Las credenciales de prueba están documentadas
- [ ] ✅ El video demo está subido y accesible
- [ ] ✅ El código está en un repositorio Git público o compartido con el profesor
- [ ] ✅ Has probado la app en navegador de incógnito (sin cache)

---

## 🎓 Objetivos Pedagógicos

Este proyecto te permitirá demostrar competencias en:

1. **Arquitectura de Frontend Moderno**
   - Organización de código por features
   - Separación de responsabilidades
   - Componentes standalone y reutilizables

2. **Gestión de Estado**
   - Signals para estado reactivo
   - Flujo unidireccional de datos
   - Computed values

3. **Integración con APIs**
   - Consumo de REST APIs
   - Manejo de autenticación JWT
   - Interceptors HTTP
   - Manejo de errores

4. **Seguridad Web**
   - Protección de rutas
   - Roles y permisos
   - Almacenamiento seguro de tokens
   - HTTPS en producción

5. **UX/UI**
   - Diseño responsive
   - Loading states
   - Feedback visual
   - Validación de formularios

6. **DevOps Básico**
   - Containerización con Docker
   - Deploy en cloud
   - Variables de entorno
   - CI/CD básico

---

## 📞 Soporte

### **Documentación del Proyecto**

Toda la información necesaria está en los archivos `.md` del proyecto base.

### **Problemas Técnicos**

1. Busca en la documentación incluida
2. Consulta la documentación oficial de Angular
3. Revisa la documentación de la API
4. Pregunta en foros (Stack Overflow, Discord del curso)
5. Contacta al profesor/tutor

### **Consultas sobre el Enunciado**

Contactar al profesor con:

- Asunto claro
- Descripción del problema
- Capturas de pantalla si aplica
- Fragmentos de código relevante

---

## 🏆 Criterios de Excelencia

Para obtener la máxima calificación:

1. **Código Limpio**:
   - TypeScript types correctos (evitar `any`)
   - Nombres descriptivos de variables y funciones
   - Principio DRY (Don't Repeat Yourself)
   - Comentarios solo donde sea necesario

2. **Arquitectura Sólida**:
   - Servicios bien organizados
   - Componentes con responsabilidades claras
   - Reutilización de código
   - Modularidad

3. **UX Excepcional**:
   - Transiciones suaves
   - Feedback claro de acciones
   - Manejo elegante de errores
   - Estados de carga informativos

4. **Deploy Profesional**:
   - Variables de entorno configuradas
   - Logs útiles
   - Health checks funcionando
   - HTTPS configurado

5. **Documentación Completa**:
   - README exhaustivo
   - Comentarios en código complejo
   - Video demo claro y conciso

---

## 📜 Licencia y Uso Académico

Este proyecto es material educativo. Se permite:

- ✅ Uso para aprendizaje personal
- ✅ Modificación y experimentación
- ✅ Compartir conocimiento con compañeros

No se permite:

- ❌ Venta del código o proyecto
- ❌ Copia directa sin entender (plagio)
- ❌ Uso comercial sin autorización

---

## 🎬 ¡Manos a la Obra!

1. **Fork o clona** el proyecto base
2. **Lee** toda la documentación incluida
3. **Planifica** tu tiempo según el cronograma
4. **Implementa** las fases en orden
5. **Testea** constantemente
6. **Documenta** tu progreso
7. **Deploy** frecuentemente
8. **Presenta** con orgullo

**¡Éxito en tu proyecto! 🚀**

---

**Fecha de publicación**: 2026-02-06  
**Versión**: 1.0  
**Profesor**: [Tu Nombre]  
**Curso**: Desarrollo Web con Angular 21
