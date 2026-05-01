# --- Build stage ---
FROM node:20-alpine AS builder
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .

# Build for production (outputs to /app/build with adapter-node)
RUN npm run build

# --- Runtime stage ---
FROM node:20-alpine
WORKDIR /app

RUN addgroup -S svelte && adduser -S svelte -G svelte
USER svelte

COPY --from=builder /app/build ./build
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

EXPOSE 3000
ENV NODE_ENV=production
CMD ["node", "build"]