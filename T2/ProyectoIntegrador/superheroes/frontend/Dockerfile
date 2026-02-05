# syntax=docker/dockerfile:1

# --- Build stage ---
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm ci

COPY . .
RUN npm run build

# --- Runtime stage ---
FROM nginx:1.27-alpine

# Config para SPA + reverse proxy /api -> backend
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Angular (builder application) suele generar en dist/<app>/browser
COPY --from=build /app/dist/superheroes/browser/ /usr/share/nginx/html/

EXPOSE 80
