# --- Build stage ---
FROM node:20-alpine AS builder
WORKDIR /app

ARG BACKEND_URL
ENV PUBLIC_BACKEND_2=$BACKEND_URL

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# --- Runtime stage ---
FROM node:20-alpine
WORKDIR /app

RUN addgroup -S svelte && adduser -S svelte -G svelte
USER svelte

COPY --from=builder /app/build ./build
COPY --from=builder /app/.svelte-kit ./.svelte-kit
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

EXPOSE 3000
ENV NODE_ENV=production
CMD ["node", "build"]