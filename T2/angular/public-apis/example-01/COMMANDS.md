# 🚀 Guía de Comandos Rápidos

## Desarrollo Local

```powershell
# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm start
# App disponible en: http://localhost:4200

# Build de producción
npm run build:prod
# Output en: dist/example-01/browser/

# Ejecutar tests
npm test
```

---

## Docker - Desarrollo y Testing

```powershell
# Opción 1: Docker Compose (Recomendado)
npm run docker:compose:up          # Iniciar
npm run docker:compose:logs        # Ver logs
npm run docker:compose:down        # Detener
npm run docker:compose:rebuild     # Rebuild + start

# Opción 2: Docker directo
npm run docker:build               # Build imagen
npm run docker:run                 # Ejecutar contenedor
npm run docker:logs                # Ver logs
npm run docker:stop                # Detener y eliminar

# App disponible en: http://localhost:8080
# Health check: http://localhost:8080/health
```

---

## Despliegue en Producción

### Render.com

1. Crear cuenta en https://render.com
2. New → Web Service
3. Connect GitHub repository
4. Settings:
   - Name: `angular-jwt-app`
   - Environment: `Docker`
   - Plan: `Free`
5. Click "Create Web Service"
6. Deploy automático (~3-5 min)
7. URL generada: `https://angular-jwt-app.onrender.com`

### Railway.app

```powershell
# Instalar CLI
npm install -g @railway/cli

# Login
railway login

# Deploy
railway up

# Ver app en el navegador
railway open
```

### Fly.io

```powershell
# Instalar CLI (PowerShell como Admin)
iwr https://fly.io/install.ps1 -useb | iex

# Login
flyctl auth login

# Deploy (primera vez - crea fly.toml automáticamente)
flyctl launch

# Deploy subsiguientes
flyctl deploy

# Abrir app
flyctl open

# Ver logs
flyctl logs
```

---

## Gestión de Base de Datos (API Externa)

```powershell
# La API es externa, pero puedes probar endpoints:

# Login
curl -X POST https://api.escuelajs.co/api/v1/auth/login `
  -H "Content-Type: application/json" `
  -d '{"email":"john@mail.com","password":"changeme"}'

# Listar productos
curl https://api.escuelajs.co/api/v1/products?limit=5

# Listar categorías
curl https://api.escuelajs.co/api/v1/categories
```

---

## Debugging

### Chrome DevTools

```powershell
# 1. Iniciar app
npm start

# 2. Abrir en Chrome: http://localhost:4200

# 3. Abrir DevTools (F12)
# - Console: Ver logs con emojis 🔐
# - Application → localStorage: Ver tokens
# - Network → auth/login: Ver respuesta con token
# - Sources: Poner breakpoints en archivos TypeScript
```

### VS Code Debugger

```powershell
# 1. Iniciar app
npm start

# 2. En VS Code, presionar F5
# 3. Seleccionar "Chrome" o "Edge"
# 4. Poner breakpoints en código TypeScript
# 5. Usar la app y VS Code pausará en los breakpoints
```

---

## Mantenimiento

```powershell
# Actualizar dependencias
npm update

# Verificar dependencias desactualizadas
npm outdated

# Limpiar cache de Angular
rm -r -fo .angular

# Limpiar node_modules y reinstalar
rm -r -fo node_modules
npm install

# Analizar tamaño del bundle
npm run build:prod -- --stats-json
npx webpack-bundle-analyzer dist/example-01/browser/stats.json
```

---

## Git Workflow

```powershell
# Crear nueva feature
git checkout -b feature/product-catalog

# Commit cambios
git add .
git commit -m "feat: add product catalog component"

# Push a GitHub
git push origin feature/product-catalog

# Merge a main
git checkout main
git merge feature/product-catalog
git push origin main

# 🚀 Deploy automático en Render/Railway/Fly.io
```

---

## Troubleshooting

### Error: "Port 4200 is already in use"

```powershell
# Windows
netstat -ano | findstr :4200
taskkill /PID <PID> /F

# Luego reiniciar
npm start
```

### Error: "Cannot find module"

```powershell
# Reinstalar dependencias
rm -r -fo node_modules
npm install
```

### Error: Docker build fails

```powershell
# Limpiar todo y rebuild
docker-compose down -v
docker system prune -a
npm run docker:compose:rebuild
```

### Error: "401 Unauthorized" en API

```powershell
# 1. Verificar que el token existe en localStorage (DevTools → Application)
# 2. Hacer login de nuevo
# 3. Verificar que el interceptor está configurado en app.config.ts
```

---

## Variables de Entorno

```powershell
# Crear archivo .env
cp .env.example .env

# Editar valores (Notepad, VS Code, etc.)
notepad .env

# Ejemplo de contenido:
# NODE_ENV=production
# PORT=8080
# API_URL=https://api.escuelajs.co/api/v1
```

---

## Testing de Endpoints

```powershell
# Instalar herramientas (opcional)
# - Postman: https://www.postman.com/downloads/
# - Insomnia: https://insomnia.rest/download
# - Bruno: https://www.usebruno.com/

# O usar Swagger UI directamente:
# https://fakeapi.platzi.com/en/rest/swagger/
```

---

## Recursos Útiles

- **README principal**: `README.md`
- **Plan de desarrollo**: `ROADMAP.md`
- **Guía de Docker**: `DOCKER.md`
- **Integración con API**: `API-INTEGRATION.md`
- **Documentación API**: https://fakeapi.platzi.com/
- **Angular Docs**: https://angular.dev

---

**Última actualización**: 2026-02-06
