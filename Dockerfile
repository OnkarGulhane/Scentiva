# ==============================================================================
# SCENTIVA Frontend — Multi-Stage Production Dockerfile (Next.js 14 App Router)
# Optimized for Docker Compose, Self-Hosted Deployments, and Container Registries
# Note: For Vercel production deployment, Next.js deploys natively via Git.
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Dependency Cache
# ------------------------------------------------------------------------------
FROM node:20-alpine AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app

# Copy dependency manifests
COPY package.json package-lock.json ./

# Install exact dependencies
RUN npm ci

# ------------------------------------------------------------------------------
# Stage 2: Production Build
# ------------------------------------------------------------------------------
FROM node:20-alpine AS builder
WORKDIR /app

COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Enable standalone output & disable telemetry during build
ENV DOCKER_BUILD=1 \
    NEXT_TELEMETRY_DISABLED=1 \
    NODE_ENV=production

# Public API URL default (can be overridden via --build-arg NEXT_PUBLIC_API_URL=...)
ARG NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL

# Prerender all 45 static and dynamic pages
RUN npm run build

# ------------------------------------------------------------------------------
# Stage 3: Minimalist Lightweight Runtime Runner
# ------------------------------------------------------------------------------
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production \
    NEXT_TELEMETRY_DISABLED=1 \
    PORT=3000 \
    HOSTNAME="0.0.0.0"

# Install curl for container health checks
RUN apk add --no-cache curl

# Create unprivileged system group and user
RUN addgroup --system --gid 1001 nodejs && \
    adduser --system --uid 1001 nextjs

# Copy static assets and standalone build output
COPY --from=builder /app/public ./public

# Prepare Next.js cache directory permissions
RUN mkdir .next && chown nextjs:nodejs .next

COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

# Container health check against frontend health endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:3000/api/health || exit 1

# Launch standalone Node.js server
CMD ["node", "server.js"]
