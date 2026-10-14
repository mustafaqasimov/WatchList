# WatchList

A Spring Boot backend for a movie watchlist application. Users can search movies via TMDB, verify their email, manage their profile and avatar, and maintain a personal favorites list. Movies imported from TMDB are cached in a local database and in Redis to minimize external API calls.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language / Framework | Java 21, Spring Boot 4 |
| Database | PostgreSQL (hosted on [Neon](https://neon.tech)) |
| Caching | Redis (hosted on [Upstash](https://upstash.com)) |
| Migrations | Liquibase |
| Auth | JWT (access + refresh tokens), Spring Security |
| Email | Resend (SMTP) |
| File storage | Cloudinary |
| External API | TMDB (The Movie Database) |
| Containerization | Docker |
| Hosting | Render |
| API docs | Springdoc OpenAPI / Swagger UI |

---

## Features

- **Auth**: register, login, refresh token, logout, email verification, forgot/reset password, change password
- **Movies**: search via TMDB, view popular movies, view movie details (auto-imported and cached on first request)
- **Favorites**: add, list (paginated), remove
- **User profile**: view profile, upload avatar (Cloudinary), change password
- **Admin**: bulk-import popular movies from TMDB, import a single movie by TMDB ID, soft-delete movies
- **Caching**: TMDB responses cached in Redis to avoid hitting TMDB's rate limit
- **Rate limiting**: login endpoint is rate-limited

---

## Local Setup

### Prerequisites
- Java 21
- Docker (for local Postgres/Redis, or for running the app in a container)
- A TMDB API key ([themoviedb.org](https://www.themoviedb.org/settings/api))

### 1. Clone and configure environment

Copy `.env.example` to `.env` and fill in the values (local DB credentials, JWT secret, TMDB API key, etc.). See `.env.example` for the full list of required variables.

### 2. Start local dependencies

```bash
docker compose up -d
```

This starts a local PostgreSQL (port `5433`) and Redis (port `6380`) container.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Or, if using IntelliJ's EnvFile plugin, attach `.env` to the run configuration and run `WatchlistApplication` directly with the `dev` profile active (default).

### 4. Access Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## Running with Docker

```bash
docker build -t watchlist .
docker run -p 8080:8080 --env-file .env.prod -e SPRING_PROFILES_ACTIVE=prod watchlist
```

`.env.prod` should contain **production** values (Neon, Upstash with SSL enabled, Resend, Cloudinary, real `FRONTEND_URL`, etc.) — see `.env.example` for the full list. This is primarily useful for validating a production-like build locally before deploying.

---

## Environment Profiles

| Profile | Used for | Notes |
|---|---|---|
| `dev` (default) | Local development | Local Postgres/Redis, permissive logging |
| `prod` | Deployed environment (Render) | Neon Postgres (SSL), Upstash Redis (SSL), reduced logging |

Profile is selected via `SPRING_PROFILES_ACTIVE`. See `application-dev.yaml` / `application-prod.yaml` for the exact differences.

---

## Deployment

- **App**: Docker image deployed on [Render](https://render.com)
- **Database**: [Neon](https://neon.tech) (PostgreSQL, free tier)
- **Cache**: [Upstash](https://upstash.com) (Redis, free tier, TLS required)
- **Email**: [Resend](https://resend.com) (SMTP relay)
- **File storage**: [Cloudinary](https://cloudinary.com) (avatar images)

Deploys are triggered automatically on push to `main` (Render auto-deploy). All secrets are configured as environment variables directly in Render's dashboard — never committed to the repository.

---

## Testing

Unit tests cover the core service layer (`AuthServiceImpl`, `FavoriteServiceImpl`, `MovieServiceImpl`, `MovieImportService`, `TokenBlacklistService`, `UserServiceImpl`, `CloudinaryStorageService`) using JUnit 5 and Mockito — no database or external service calls required.

```bash
./mvnw test
```

---

## API Documentation

Full interactive API documentation is available via Swagger UI once the app is running:

```
/swagger-ui.html
```

> Note: disable `springdoc.api-docs.enabled` and `springdoc.swagger-ui.enabled` in production once the app is customer-facing, to avoid exposing the full API surface publicly.

---

## Project Structure (high level)

```
src/main/java/com/movie/watchlist/
├── config/          # Security, CORS, Redis, Cloudinary, OpenAPI config
├── controller/       # REST controllers
├── dto/              # Request/response DTOs
├── entity/            # JPA entities
├── enums/             # ActiveStatus, Role, TokenType
├── exception/         # Custom exceptions + global handler
├── mapper/            # MapStruct mappers
├── repositories/      # Spring Data JPA / Redis repositories
├── security/          # JWT filter, JwtService, CustomUserDetails
└── service/
    ├── interfaces/    # Service contracts
    └── impl/          # Service implementations
```
