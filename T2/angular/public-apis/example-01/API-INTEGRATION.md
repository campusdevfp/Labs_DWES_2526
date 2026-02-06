# Integración con API REST - Platzi Fake Store

## Información de la API

- **Base URL**: `https://api.escuelajs.co/api/v1`
- **Tipo**: REST API
- **Autenticación**: JWT (Bearer Token)
- **Documentación**: https://fakeapi.platzi.com/
- **Swagger UI**: https://fakeapi.platzi.com/en/rest/swagger/
- **GraphQL** (alternativa): https://api.escuelajs.co/graphql

---

## Endpoints Disponibles

### Autenticación (Implementados ✅)

| Método | Endpoint              | Descripción                | Auth | Estado |
| ------ | --------------------- | -------------------------- | ---- | ------ |
| POST   | `/auth/login`         | Login con email/password   | ❌   | ✅     |
| POST   | `/auth/refresh-token` | Renovar access token       | ❌   | ✅     |
| GET    | `/auth/profile`       | Obtener perfil del usuario | ✅   | ✅     |

**Ejemplo de uso**:

```typescript
// Login
POST https://api.escuelajs.co/api/v1/auth/login
Content-Type: application/json

{
  "email": "john@mail.com",
  "password": "changeme"
}

// Respuesta
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// Obtener perfil (requiere token)
GET https://api.escuelajs.co/api/v1/auth/profile
Authorization: Bearer {access_token}
```

---

### Usuarios

| Método | Endpoint              | Descripción                | Auth | Estado |
| ------ | --------------------- | -------------------------- | ---- | ------ |
| GET    | `/users`              | Listar todos los usuarios  | ❌   | ✅     |
| GET    | `/users/:id`          | Obtener usuario por ID     | ❌   | ✅     |
| POST   | `/users`              | Crear nuevo usuario        | ❌   | ✅     |
| PUT    | `/users/:id`          | Actualizar usuario         | ✅   | 📋     |
| DELETE | `/users/:id`          | Eliminar usuario           | ✅   | 📋     |
| POST   | `/users/is-available` | Verificar email disponible | ❌   | ✅     |

**Schemas**:

```typescript
// GET /users → User[]
interface User {
  id: number;
  email: string;
  password?: string; // Solo en creación
  name: string;
  role: 'customer' | 'admin';
  avatar: string;
}

// POST /users/is-available
{
  "email": "test@mail.com"
}
// Respuesta
{
  "isAvailable": true
}
```

---

### Productos

| Método | Endpoint                | Descripción                 | Auth | Estado |
| ------ | ----------------------- | --------------------------- | ---- | ------ |
| GET    | `/products`             | Listar productos (paginado) | ❌   | 📋     |
| GET    | `/products/:id`         | Obtener producto por ID     | ❌   | 📋     |
| GET    | `/products/slug/:slug`  | Obtener producto por slug   | ❌   | 📋     |
| POST   | `/products`             | Crear producto              | ✅   | 📋     |
| PUT    | `/products/:id`         | Actualizar producto         | ✅   | 📋     |
| DELETE | `/products/:id`         | Eliminar producto           | ✅   | 📋     |
| GET    | `/products/:id/related` | Productos relacionados      | ❌   | 📋     |

**Query Parameters (Filtros)**:

```typescript
GET /products?offset=0&limit=10          // Paginación
GET /products?title=shirt                // Búsqueda por título
GET /products?categoryId=1               // Filtrar por categoría
GET /products?categorySlug=clothes       // Filtrar por slug de categoría
GET /products?price=100                  // Precio exacto
GET /products?price_min=50&price_max=200 // Rango de precio

// Combinados
GET /products?title=shirt&categoryId=1&price_min=10&price_max=50&offset=0&limit=20
```

**Schemas**:

```typescript
interface Product {
  id: number;
  title: string;
  slug: string; // Auto-generado (URL-friendly)
  price: number;
  description: string;
  images: string[]; // Array de URLs
  category: Category; // Objeto anidado
  creationAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
}

// POST /products - Body
interface ProductCreate {
  title: string; // Requerido
  price: number; // Requerido, > 0
  description: string; // Requerido
  categoryId: number; // Requerido, debe existir
  images: string[]; // Requerido, min 1 URL
}

// PUT /products/:id - Body (todos opcionales)
interface ProductUpdate {
  title?: string;
  price?: number;
  description?: string;
  categoryId?: number;
  images?: string[];
}
```

**Ejemplos**:

```bash
# Listar productos
curl https://api.escuelajs.co/api/v1/products?limit=5

# Crear producto (requiere autenticación)
curl -X POST https://api.escuelajs.co/api/v1/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "New T-Shirt",
    "price": 25,
    "description": "A comfortable cotton t-shirt",
    "categoryId": 1,
    "images": ["https://placehold.co/600x400"]
  }'

# Actualizar producto
curl -X PUT https://api.escuelajs.co/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "Updated Title",
    "price": 30
  }'

# Eliminar producto
curl -X DELETE https://api.escuelajs.co/api/v1/products/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Categorías

| Método | Endpoint                   | Descripción                | Auth | Estado |
| ------ | -------------------------- | -------------------------- | ---- | ------ |
| GET    | `/categories`              | Listar categorías          | ❌   | 📋     |
| GET    | `/categories/:id`          | Obtener categoría por ID   | ❌   | 📋     |
| GET    | `/categories/slug/:slug`   | Obtener categoría por slug | ❌   | 📋     |
| POST   | `/categories`              | Crear categoría            | ✅   | 📋     |
| PUT    | `/categories/:id`          | Actualizar categoría       | ✅   | 📋     |
| DELETE | `/categories/:id`          | Eliminar categoría         | ✅   | 📋     |
| GET    | `/categories/:id/products` | Productos de categoría     | ❌   | 📋     |

**Schemas**:

```typescript
interface Category {
  id: number;
  name: string;
  slug: string; // Auto-generado
  image: string; // URL de imagen
}

// POST /categories - Body
interface CategoryCreate {
  name: string; // Requerido
  image: string; // Requerido, URL válida
}

// PUT /categories/:id - Body (todos opcionales)
interface CategoryUpdate {
  name?: string;
  image?: string;
}
```

**Ejemplos**:

```bash
# Listar categorías
curl https://api.escuelajs.co/api/v1/categories

# Crear categoría
curl -X POST https://api.escuelajs.co/api/v1/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "name": "Sports",
    "image": "https://placehold.co/600x400"
  }'

# Productos de una categoría
curl https://api.escuelajs.co/api/v1/categories/1/products
```

---

### Archivos (Upload)

| Método | Endpoint        | Descripción  | Auth | Estado |
| ------ | --------------- | ------------ | ---- | ------ |
| POST   | `/files/upload` | Subir imagen | ✅   | 💡     |

**Ejemplo**:

```bash
# Upload de imagen
curl -X POST https://api.escuelajs.co/api/v1/files/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@image.jpg"

# Respuesta
{
  "originalname": "image.jpg",
  "filename": "https://api.escuelajs.co/uploads/image-123.jpg",
  "location": "https://api.escuelajs.co/uploads/image-123.jpg"
}
```

---

## Implementación en Angular

### Service Base Pattern

```typescript
// src/app/core/services/base-api.service.ts
import { inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export abstract class BaseApiService {
  protected http = inject(HttpClient);
  protected readonly API_URL = 'https://api.escuelajs.co/api/v1';

  protected buildParams(filters: Record<string, any>): HttpParams {
    let params = new HttpParams();
    for (const [key, value] of Object.entries(filters)) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(key, value.toString());
      }
    }
    return params;
  }
}
```

### Product Service Implementation

```typescript
// src/app/core/services/product.service.ts
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { BaseApiService } from './base-api.service';
import { Product, ProductCreate, ProductFilters } from '../../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService extends BaseApiService {
  // State management con Signals
  private productsSignal = signal<Product[]>([]);
  private loadingSignal = signal<boolean>(false);
  private errorSignal = signal<string | null>(null);

  readonly products = this.productsSignal.asReadonly();
  readonly loading = this.loadingSignal.asReadonly();
  readonly error = this.errorSignal.asReadonly();

  // GET /products con filtros
  getProducts(filters?: ProductFilters): Observable<Product[]> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    const params = this.buildParams(filters || {});

    return this.http.get<Product[]>(`${this.API_URL}/products`, { params }).pipe(
      tap({
        next: (products) => {
          this.productsSignal.set(products);
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message);
          this.loadingSignal.set(false);
        },
      }),
    );
  }

  // GET /products/:id
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.API_URL}/products/${id}`);
  }

  // GET /products/slug/:slug
  getProductBySlug(slug: string): Observable<Product> {
    return this.http.get<Product>(`${this.API_URL}/products/slug/${slug}`);
  }

  // POST /products (requiere autenticación)
  createProduct(data: ProductCreate): Observable<Product> {
    return this.http.post<Product>(`${this.API_URL}/products`, data);
  }

  // PUT /products/:id (requiere autenticación)
  updateProduct(id: number, data: Partial<Product>): Observable<Product> {
    return this.http.put<Product>(`${this.API_URL}/products/${id}`, data);
  }

  // DELETE /products/:id (requiere autenticación)
  deleteProduct(id: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.API_URL}/products/${id}`);
  }

  // GET /products/:id/related
  getRelatedProducts(id: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.API_URL}/products/${id}/related`);
  }
}
```

### Category Service Implementation

```typescript
// src/app/core/services/category.service.ts
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { BaseApiService } from './base-api.service';
import { Category, CategoryCreate } from '../../models/category.model';
import { Product } from '../../models/product.model';

@Injectable({ providedIn: 'root' })
export class CategoryService extends BaseApiService {
  private categoriesSignal = signal<Category[]>([]);
  private loadingSignal = signal<boolean>(false);

  readonly categories = this.categoriesSignal.asReadonly();
  readonly loading = this.loadingSignal.asReadonly();

  // GET /categories
  getCategories(): Observable<Category[]> {
    this.loadingSignal.set(true);

    return this.http.get<Category[]>(`${this.API_URL}/categories`).pipe(
      tap({
        next: (categories) => {
          this.categoriesSignal.set(categories);
          this.loadingSignal.set(false);
        },
        error: () => this.loadingSignal.set(false),
      }),
    );
  }

  // GET /categories/:id
  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.API_URL}/categories/${id}`);
  }

  // GET /categories/slug/:slug
  getCategoryBySlug(slug: string): Observable<Category> {
    return this.http.get<Category>(`${this.API_URL}/categories/slug/${slug}`);
  }

  // POST /categories (requiere autenticación)
  createCategory(data: CategoryCreate): Observable<Category> {
    return this.http.post<Category>(`${this.API_URL}/categories`, data);
  }

  // PUT /categories/:id (requiere autenticación)
  updateCategory(id: number, data: Partial<Category>): Observable<Category> {
    return this.http.put<Category>(`${this.API_URL}/categories/${id}`, data);
  }

  // DELETE /categories/:id (requiere autenticación)
  deleteCategory(id: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.API_URL}/categories/${id}`);
  }

  // GET /categories/:id/products
  getProductsByCategory(id: number, offset = 0, limit = 10): Observable<Product[]> {
    const params = this.buildParams({ offset, limit });
    return this.http.get<Product[]>(`${this.API_URL}/categories/${id}/products`, { params });
  }
}
```

---

## Testing con Postman/Insomnia

### Colecciones Pre-configuradas

1. **Descargar Postman Collection**:
   - https://fakeapi.platzi.com/en/resources/postman/

2. **Descargar Insomnia Collection**:
   - https://fakeapi.platzi.com/en/resources/insomnia/

3. **Importar en tu herramienta favorita**

### Variables de Entorno

```json
{
  "base_url": "https://api.escuelajs.co/api/v1",
  "access_token": "{{tu_token_aqui}}",
  "user_email": "john@mail.com",
  "user_password": "changeme"
}
```

---

## Limitaciones y Consideraciones

### Rate Limiting

- No hay rate limiting explícito
- Úsala responsablemente para no saturar

### Persistencia

- ⚠️ Los datos pueden resetearse periódicamente
- No usar para datos de producción reales
- Solo para desarrollo/aprendizaje

### CORS

- CORS habilitado para todos los orígenes
- Sin problemas desde navegadores

### Timeout

- Algunos endpoints pueden tardar 1-3 segundos
- Implementa loading states en tu UI

### Autenticación

- Los tokens no expiran en el corto plazo (20 días)
- Refresh token disponible para renovar

---

## Recursos Adicionales

- **Documentación Oficial**: https://fakeapi.platzi.com/
- **Swagger Interactive**: https://fakeapi.platzi.com/en/rest/swagger/
- **GraphQL Playground**: https://api.escuelajs.co/graphql
- **GitHub Repo**: https://github.com/PlatziLabs/fake-api-backend
- **Postman Collection**: https://fakeapi.platzi.com/en/resources/postman/

---
