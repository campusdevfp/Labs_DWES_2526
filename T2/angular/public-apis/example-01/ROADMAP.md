# Plan de Desarrollo - Frontend E-commerce Completo

## Estado del Proyecto

**Versión Actual**: v1.0 - Autenticación JWT
**Siguiente**: v2.0 - Catálogo de Productos

---

## Fase 1: MVP - Catálogo Público

**Duración estimada**: 2-3 días  
**Prioridad**: Alta

### Modelos e Interfaces

- [ ] `src/app/models/product.model.ts`

  ```typescript
  export interface Product {
    id: number;
    title: string;
    slug: string;
    price: number;
    description: string;
    images: string[];
    category: Category;
    creationAt: string;
    updatedAt: string;
  }

  export interface ProductFilters {
    title?: string;
    categoryId?: number;
    categorySlug?: string;
    priceMin?: number;
    priceMax?: number;
    offset?: number;
    limit?: number;
  }

  export interface ProductCreate {
    title: string;
    price: number;
    description: string;
    categoryId: number;
    images: string[];
  }

  export interface PaginationParams {
    offset: number;
    limit: number;
  }
  ```

- [ ] `src/app/models/category.model.ts`

  ```typescript
  export interface Category {
    id: number;
    name: string;
    slug: string;
    image: string;
  }

  export interface CategoryCreate {
    name: string;
    image: string;
  }
  ```

### Servicios

- [ ] `src/app/services/product.service.ts`
  - [ ] `getAllProducts(filters?: ProductFilters): Observable<Product[]>`
  - [ ] `getProductById(id: number): Observable<Product>`
  - [ ] `getProductBySlug(slug: string): Observable<Product>`
  - [ ] `getRelatedProducts(id: number): Observable<Product[]>`
  - [ ] Estado reactivo con signals: `productsSignal`, `loadingSignal`, `errorSignal`

- [ ] `src/app/services/category.service.ts`
  - [ ] `getAllCategories(): Observable<Category[]>`
  - [ ] `getCategoryById(id: number): Observable<Category>`
  - [ ] `getProductsByCategory(id: number, pagination?: PaginationParams): Observable<Product[]>`

### Componentes Compartidos

- [ ] `src/app/shared/components/product-card/`

  ```typescript
  @Component({
    selector: 'app-product-card',
    standalone: true,
    inputs: { product: input.required<Product>() }
  })
  ```

  - [ ] Imagen con lazy loading
  - [ ] Título truncado
  - [ ] Precio formateado
  - [ ] Badge de categoría
  - [ ] Botón "Ver detalle" → router link

- [ ] `src/app/shared/components/pagination/`
  - [ ] Input: `total`, `currentPage`, `pageSize`
  - [ ] Output: `pageChange`
  - [ ] Botones anterior/siguiente
  - [ ] Números de página

- [ ] `src/app/shared/components/loading-spinner/`
  - [ ] Spinner CSS puro o SVG animado
  - [ ] Skeleton loaders para cards

### Componentes de Features

- [ ] `src/app/features/shop/product-list/`
  - [ ] Grid responsive (1-4 columnas según viewport)
  - [ ] Integración con filtros
  - [ ] Paginación
  - [ ] Loading states
  - [ ] Empty state ("No hay productos")

- [ ] `src/app/features/shop/product-filters/`
  - [ ] Input de búsqueda por título (debounce 500ms)
  - [ ] Dropdown de categorías
  - [ ] Range sliders para precio min/max
  - [ ] Botón "Limpiar filtros"
  - [ ] Counters de resultados

- [ ] `src/app/features/shop/product-detail/`
  - [ ] Galería de imágenes (carousel o thumbnails)
  - [ ] Título, precio destacado
  - [ ] Descripción completa
  - [ ] Link a categoría
  - [ ] Sección "Productos relacionados" (horizontal scroll)
  - [ ] Breadcrumbs (Home > Categoría > Producto)

- [ ] `src/app/features/shop/category-list/`
  - [ ] Grid de categorías (2-3 columnas)
  - [ ] Imagen, nombre, slug
  - [ ] Link a productos filtrados por categoría

### Rutas

```typescript
{
  path: 'shop',
  children: [
    { path: '', component: ProductListComponent },
    { path: 'product/:id', component: ProductDetailComponent },
    { path: 'product/slug/:slug', component: ProductDetailComponent },
  ]
},
{
  path: 'categories',
  children: [
    { path: '', component: CategoryListComponent },
    { path: ':slug', component: CategoryDetailComponent },
  ]
}
```

### Testing Checklist

- [ ] Listar productos correctamente
- [ ] Filtrar por título funciona
- [ ] Filtrar por categoría funciona
- [ ] Filtrar por precio funciona
- [ ] Paginación avanza/retrocede
- [ ] Detalle de producto carga datos
- [ ] Productos relacionados se muestran
- [ ] Responsive en mobile/tablet/desktop
- [ ] Loading states se muestran
- [ ] Manejo de errores (404, 500)

---

## Fase 2: Panel de Administración 🔴

**Duración estimada**: 2-3 días  
**Prioridad**: Media

### Guard Adicional

- [ ] `src/app/core/guards/admin.guard.ts`

  ```typescript
  export const adminGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const user = authService.currentUser();

    if (user?.role === 'admin') {
      return true;
    }

    return inject(Router).createUrlTree(['/shop']);
  };
  ```

### Servicios - Métodos Admin

- [ ] `product.service.ts` - Métodos CRUD
  - [ ] `createProduct(data: ProductCreate): Observable<Product>`
  - [ ] `updateProduct(id: number, data: Partial<Product>): Observable<Product>`
  - [ ] `deleteProduct(id: number): Observable<boolean>`

- [ ] `category.service.ts` - Métodos CRUD
  - [ ] `createCategory(data: CategoryCreate): Observable<Category>`
  - [ ] `updateCategory(id: number, data: Partial<Category>): Observable<Category>`
  - [ ] `deleteCategory(id: number): Observable<boolean>`

### Componentes Admin

- [ ] `src/app/features/admin/dashboard/`
  - [ ] Cards con estadísticas:
    - Total de productos
    - Total de categorías
    - Productos por categoría (gráfico opcional)
  - [ ] Enlaces rápidos a gestión

- [ ] `src/app/features/admin/products/product-list/`
  - [ ] Tabla con columnas: ID, Imagen, Título, Precio, Categoría, Acciones
  - [ ] Botón "Crear Producto"
  - [ ] Acciones por fila: Editar, Eliminar
  - [ ] Búsqueda inline
  - [ ] Paginación

- [ ] `src/app/features/admin/products/product-form/`
  - [ ] Formulario reactivo con validaciones:
    - Título (requerido, min 3 chars)
    - Precio (requerido, > 0)
    - Descripción (requerido, min 10 chars)
    - Categoría (requerido, dropdown)
    - Imágenes (array, min 1 URL)
  - [ ] Modo crear/editar (detectar por route param)
  - [ ] Preview de imágenes
  - [ ] Botones: Guardar, Cancelar

- [ ] `src/app/features/admin/categories/category-manager/`
  - [ ] Lista de categorías con edición inline o modal
  - [ ] Botón "Crear Categoría"
  - [ ] Acciones: Editar nombre, Editar imagen, Eliminar
  - [ ] Ver productos de cada categoría (contador)

- [ ] `src/app/shared/components/confirm-dialog/`
  - [ ] Modal de confirmación para deletes
  - [ ] Input: `title`, `message`
  - [ ] Output: `confirmed`, `cancelled`

### Rutas Admin

```typescript
{
  path: 'admin',
  canActivate: [authGuard, adminGuard],
  children: [
    { path: '', component: AdminDashboardComponent },
    { path: 'products', component: AdminProductsComponent },
    { path: 'products/new', component: ProductFormComponent },
    { path: 'products/edit/:id', component: ProductFormComponent },
    { path: 'categories', component: AdminCategoriesComponent },
  ]
}
```

### Testing Checklist

- [ ] Solo usuarios admin acceden
- [ ] Crear producto funciona
- [ ] Editar producto actualiza datos
- [ ] Eliminar producto confirma antes
- [ ] Crear categoría funciona
- [ ] Editar categoría funciona
- [ ] Eliminar categoría funciona
- [ ] Validaciones de formularios funcionan
- [ ] Mensajes de éxito/error se muestran

---

## Fase 3: Funcionalidades Extra 🌟

**Duración estimada**: 2-3 días  
**Prioridad**: Baja (Nice to have)

### Perfil de Usuario

- [ ] `src/app/features/auth/profile/profile.component.ts`
  - [ ] Ver datos actuales
  - [ ] Formulario de edición: nombre, email
  - [ ] Cambiar avatar (URL o upload)
  - [ ] Botón "Guardar cambios"
  - [ ] Endpoint: `PUT /users/:id`

### Sistema de Favoritos

- [ ] `src/app/services/favorites.service.ts`
  - [ ] Signal con array de IDs: `favoritesSignal`
  - [ ] Persistencia en localStorage
  - [ ] `addFavorite(productId: number)`
  - [ ] `removeFavorite(productId: number)`
  - [ ] `isFavorite(productId: number): boolean`
  - [ ] `getFavorites(): number[]`

- [ ] Botón "❤️" en ProductCard
- [ ] Badge en navbar con contador
- [ ] Página `/favorites` con listado

### Carrito de Compras (Frontend-only)

- [ ] `src/app/services/cart.service.ts`

  ```typescript
  interface CartItem {
    product: Product;
    quantity: number;
  }

  // Signals
  cartItemsSignal: WritableSignal<CartItem[]>
  totalItemsSignal: Computed<number>
  totalPriceSignal: Computed<number>

  // Methods
  addToCart(product: Product, quantity: number)
  removeFromCart(productId: number)
  updateQuantity(productId: number, quantity: number)
  clearCart()
  ```

- [ ] Botón "Añadir al carrito" en ProductDetail
- [ ] Mini cart en navbar (dropdown)
- [ ] Página `/cart` con tabla de items
- [ ] Persistencia en localStorage

### Upload de Imágenes

- [ ] Integrar con endpoint `POST /files/upload`
- [ ] Component `image-uploader`
- [ ] Drag & drop
- [ ] Preview antes de subir
- [ ] Progress bar

### Búsqueda Avanzada

- [ ] Autocomplete con resultados mientras escribes
- [ ] Historial de búsquedas (localStorage)
- [ ] Sugerencias populares

### Otras Features

- [ ] Dark mode toggle (señal global + CSS variables)
- [ ] Infinite scroll en listado (en lugar de paginación)
- [ ] Filtros en URL query params (compartir URL filtrada)
- [ ] PWA (Service Worker, manifest.json)
- [ ] i18n multiidioma (español/inglés)

---

## 📋 Checklist General de Calidad

### Performance

- [ ] Lazy loading de imágenes
- [ ] Lazy loading de rutas
- [ ] Signals en lugar de Subject/BehaviorSubject
- [ ] OnPush change detection donde sea posible
- [ ] Tree-shaking habilitado
- [ ] Build de producción optimizado

### UX/UI

- [ ] Loading states en todas las peticiones
- [ ] Skeleton loaders para mejor perceived performance
- [ ] Mensajes de error amigables
- [ ] Empty states ("No hay productos")
- [ ] Confirmaciones para acciones destructivas
- [ ] Feedback visual en botones (loading spinner)
- [ ] Transiciones suaves (CSS animations)

### Accesibilidad

- [ ] Labels en todos los inputs
- [ ] ARIA attributes donde corresponda
- [ ] Navegación por teclado funciona
- [ ] Contraste de colores adecuado
- [ ] Alt text en imágenes

### SEO

- [ ] Meta tags dinámicos por página
- [ ] Títulos descriptivos
- [ ] Canonical URLs
- [ ] Open Graph tags (opcional)

### Testing

- [ ] Unit tests para servicios críticos
- [ ] Unit tests para pipes
- [ ] E2E test para flujo de compra (opcional)

---

## 🚀 Comandos de Desarrollo

```bash
# Desarrollo local
npm start

# Build de producción
npm run build:prod

# Tests
npm test

# Docker local
npm run docker:compose:up

# Ver logs Docker
npm run docker:compose:logs

# Deploy a producción
git push origin main  # Auto-deploy en Render/Railway/Fly.io
```

---

## Notas Técnicas

### Librerías Recomendadas

- **UI Framework**: Angular Material o PrimeNG
- **Carousels**: Swiper.js
- **Gráficos**: Chart.js o Recharts (si necesitas estadísticas)
- **Forms**: Ya incluido (ReactiveFormsModule)
- **HTTP**: Ya incluido (HttpClient)

### Patrones a Seguir

- ✅ Standalone components
- ✅ Signals para estado reactivo
- ✅ Modern control flow (@if, @for)
- ✅ Functional guards y interceptors
- ✅ inject() en lugar de constructor DI
- ✅ Pipes para transformaciones visuales
- ✅ OnPush change detection
- ✅ TrackBy en \*ngFor (performance)

---

## Criterios de Éxito

**MVP (Fase 1)**:

- ✅ Usuario puede ver listado de productos
- ✅ Usuario puede filtrar productos
- ✅ Usuario puede ver detalle de producto
- ✅ Paginación funciona correctamente
- ✅ Responsive en todos los dispositivos

**Admin Panel (Fase 2)**:

- ✅ Admin puede crear/editar/eliminar productos
- ✅ Admin puede gestionar categorías
- ✅ Solo usuarios con role="admin" acceden

**Features Extra (Fase 3)**:

- ✅ Sistema de favoritos funcional
- ✅ Carrito con cálculo de totales
- ✅ Usuario puede editar su perfil

---

**Última actualización**: 2026-02-06
