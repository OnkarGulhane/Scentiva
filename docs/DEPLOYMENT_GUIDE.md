# SCENTIVA — Production Deployment Guide
> **Target Architecture:** Next.js 14 Frontend on **Vercel** + Spring Boot 3.3.4 (Java 21) & PostgreSQL 17 on **Render**  
> **Local Orchestration:** Docker Compose (PostgreSQL + Spring Boot + Next.js)

---

## 1. Architecture & Production Topology

```
┌─────────────────────────────────────────────────────────────┐
│                    VERCEL (Global Edge / CDN)               │
│  - SCENTIVA Next.js 14 App Router (45 Pages Prerendered)    │
│  - 3D WebGL Flacon Canvas, GSAP Motion, SSR Resilient Store │
│  - URL: https://scentiva.vercel.app (or custom domain)      │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               │ HTTPS / JSON Envelopes
                               │ Bearer JWT + X-Correlation-ID
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    RENDER (Docker Web Service)              │
│  - Spring Boot 3.3.4 (Java 21 LTS) Modular Monolith         │
│  - Port dynamically mapped via $PORT (Render auto-binding)  │
│  - Actuator & Public Health: /api/v1/health                 │
│  - URL: https://scentiva-backend.onrender.com               │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               │ JDBC Connection Pool (HikariCP)
                               │ Flyway Database Migrations (V1, V2, V3)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    RENDER MANAGED POSTGRESQL                │
│  - PostgreSQL 16/17 Database (scentiva_db)                  │
│  - NUMERIC(12,2) Exact Precision Financial Ledger           │
│  - Pessimistic Row Locks (@Lock PESSIMISTIC_WRITE)          │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Deploying Backend & Database on Render

### Method A: Automated 1-Click Deployment via Render Blueprint (`render.yaml`)
1. Log in to your [Render Dashboard](https://dashboard.render.com).
2. Click **New +** → **Blueprint**.
3. Connect your GitHub repository `OnkarGulhane/Scentiva`.
4. Render will automatically detect [render.yaml](file:///e:/Scentiva/render.yaml) and provision:
   - **PostgreSQL Database:** `scentiva-db`
   - **Web Service:** `scentiva-backend` (using [backend/Dockerfile](file:///e:/Scentiva/backend/Dockerfile))
5. Click **Apply**.

---

### Method B: Manual Service Creation on Render

#### Step 1: Create PostgreSQL Database on Render
1. In Render Dashboard, click **New +** → **PostgreSQL**.
2. **Name:** `scentiva-db`
3. **Database:** `scentiva_db`
4. **User:** `scentiva_user`
5. **Region:** Oregon (or closest to your users)
6. **PostgreSQL Version:** 16 (or 17)
7. **Plan:** Free (or Starter/Standard)
8. Click **Create Database**.
9. Once created, copy the **Internal Database URL** (e.g. `postgresql://scentiva_user:password@dpg-xxx:5432/scentiva_db`) or individual connection parameters.

#### Step 2: Create Web Service for Spring Boot Backend
1. Click **New +** → **Web Service**.
2. Connect your GitHub repository `OnkarGulhane/Scentiva`.
3. Configure the following settings:
   - **Name:** `scentiva-backend`
   - **Region:** Oregon (same as your database)
   - **Branch:** `main`
   - **Root Directory:** `backend` (or leave root with Dockerfile path `./backend/Dockerfile`)
   - **Runtime:** `Docker`
   - **Dockerfile Path:** `./backend/Dockerfile`
   - **Docker Build Context:** `./backend`
   - **Health Check Path:** `/api/v1/health`
4. Add **Environment Variables**:
   | Key | Value | Description |
   |---|---|---|
   | `SPRING_PROFILES_ACTIVE` | `prod` | Activates production profile with Flyway migrations |
   | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<RENDER_DB_HOST>:5432/scentiva_db` | JDBC connection string to your Render DB |
   | `SPRING_DATASOURCE_USERNAME` | `scentiva_user` | Database username |
   | `SPRING_DATASOURCE_PASSWORD` | `<your-db-password>` | Database password |
   | `JWT_SECRET` | *(64+ character random string)* | HMAC SHA-512 signing key |
   | `FRONTEND_URL` | `https://scentiva.vercel.app` | Vercel production URL for CORS |
   | `APP_BASE_URL` | `https://scentiva-backend.onrender.com` | Your backend URL |

5. Click **Create Web Service**. Render will automatically build the multi-stage Docker container, run Flyway migrations, and start the Spring Boot application.
6. Verify deployment by visiting: `https://scentiva-backend.onrender.com/api/v1/health`.

---

## 3. Deploying Frontend on Vercel

1. Log in to [Vercel](https://vercel.com).
2. Click **Add New...** → **Project**.
3. Import your GitHub repository: `OnkarGulhane/Scentiva`.
4. Configure the project:
   - **Framework Preset:** `Next.js` (automatically detected)
   - **Root Directory:** `./` (default)
   - **Build Command:** `npm run build`
   - **Output Directory:** `.next`
5. Configure **Environment Variables**:
   | Name | Value | Description |
   |---|---|---|
   | `NEXT_PUBLIC_API_URL` | `https://scentiva-backend.onrender.com/api/v1` | URL of your Spring Boot backend on Render |
   | `NEXT_PUBLIC_SITE_URL` | `https://scentiva.vercel.app` | Production frontend domain |
6. Click **Deploy**.
7. Vercel will prerender all 45 pages and deploy to global edge locations with instant cache invalidation.

---

## 4. Local Full-Stack Orchestration via Docker Compose

To run the entire ecosystem (PostgreSQL 17 + Spring Boot 3.3.4 + Next.js 14) locally in 1 command:

```bash
# Build and launch all 3 containers
docker compose up --build

# Run in background (detached mode)
docker compose up -d

# View real-time container logs
docker compose logs -f

# Stop and tear down containers
docker compose down
```

### Local Endpoints:
- **Next.js Storefront & Admin:** [http://localhost:3000](http://localhost:3000)
- **Spring Boot Core API:** [http://localhost:8080/api/v1](http://localhost:8080/api/v1)
- **Swagger / OpenAPI Documentation:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Backend Health Check:** [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health)
- **PostgreSQL 17 Database:** `localhost:5432` (`scentiva_db` / `scentiva_user`)

---

## 5. Production CORS & Security Verification

The Spring Boot backend is configured with `setAllowedOriginPatterns` in [WebCorsConfig.java](file:///e:/Scentiva/backend/src/main/java/com/scentiva/config/WebCorsConfig.java) which automatically authorizes:
- `http://localhost:[*]` (Local Next.js dev server)
- `https://*.vercel.app` (All Vercel preview branch deployments & production)
- `https://scentiva.vercel.app`
- Custom domains defined in `FRONTEND_URL`

Headers permitted:
`Origin`, `Content-Type`, `Accept`, `Authorization`, `X-Requested-With`, `Idempotency-Key`, `X-Session-ID`, `X-Correlation-ID`, `Access-Control-Request-Method`, `Access-Control-Request-Headers`.
