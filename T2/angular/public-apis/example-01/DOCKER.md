# Guía Rápida de Docker

## Comandos Esenciales

### Build y Ejecución

```bash
# Opción 1: Docker Compose (Recomendado)
docker-compose up -d              # Iniciar en segundo plano
docker-compose up --build -d      # Rebuild y iniciar
docker-compose down               # Detener y eliminar contenedores
docker-compose logs -f            # Ver logs en tiempo real

# Opción 2: Docker directo
docker build -t angular-jwt-app .
docker run -d -p 8080:80 --name angular-app angular-jwt-app
docker stop angular-app && docker rm angular-app
```

### Acceso

- **Local**: http://localhost:8080
- **Health Check**: http://localhost:8080/health

### Troubleshooting

```bash
# Ver logs
docker-compose logs -f frontend

# Entrar al contenedor
docker exec -it angular-jwt-frontend sh

# Ver estado
docker-compose ps

# Ver uso de recursos
docker stats angular-jwt-frontend

# Rebuild completo (si hay problemas)
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## Despliegue en Proveedores

### Render.com (Más fácil)

1. Crear cuenta en https://render.com
2. New → Web Service
3. Connect GitHub repository
4. Settings:
   - Environment: Docker
   - Plan: Free
5. Deploy automático

### Railway.app

```bash
npm i -g @railway/cli
railway login
railway up
```

### Fly.io

```bash
curl -L https://fly.io/install.sh | sh
flyctl auth login
flyctl launch
flyctl deploy
```

## Variables de Entorno

```bash
# Copiar template
cp .env.example .env

# Editar valores
NODE_ENV=production
PORT=8080
API_URL=https://api.escuelajs.co/api/v1
```

## Optimización de Imagen

- ✅ Multi-stage build (Node → Nginx)
- ✅ Alpine Linux (imagen pequeña)
- ✅ .dockerignore (excluye node_modules)
- ✅ Compresión Gzip en Nginx
- ✅ Cache de assets estáticos

**Tamaño final**: ~50MB (vs ~1GB sin optimizar)
