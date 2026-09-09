# Deployment Guide

This project can be deployed with Docker Compose using the image published by GitHub Actions to GitHub Container Registry (GHCR).

## 1. Prepare the server

Install:

- Docker Engine
- Docker Compose plugin

Clone the repository or copy these files to the server:

- `docker-compose.prod.yml`
- `.env`

## 2. Create production environment file

Copy `.env.example` to `.env` and set real values:

```text
MYSQL_ROOT_PASSWORD=<strong database password>
ADMIN_USERNAME=<admin username>
ADMIN_PASSWORD=<strong admin password>
SITE_URL=https://your-domain.com
APP_PORT=8080
APP_IMAGE=ghcr.io/sourav12kumar/bba-bca-wallah-jut:latest
JAVA_OPTS=-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError
```

Never commit the real `.env` file.

## 3. Pull and start

```bash
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

## 4. Check status

```bash
docker compose -f docker-compose.prod.yml ps
```

Application health endpoint:

```text
http://SERVER_HOST:APP_PORT/actuator/health
```

Expected response contains:

```json
{"status":"UP"}
```

## 5. View logs

```bash
docker compose -f docker-compose.prod.yml logs -f app
```

MySQL logs:

```bash
docker compose -f docker-compose.prod.yml logs -f mysql
```

## 6. Deploy a newer release

After a successful push to `main`, GitHub Actions publishes a new `latest` container image.

On the server:

```bash
docker compose -f docker-compose.prod.yml pull app
docker compose -f docker-compose.prod.yml up -d app
```

Uploaded files and MySQL data remain in Docker volumes.

## 7. Deploy an exact image version

GitHub Actions also publishes commit-SHA tags such as:

```text
ghcr.io/sourav12kumar/bba-bca-wallah-jut:sha-abcdef1
```

Set `APP_IMAGE` in `.env` to that exact tag and run:

```bash
docker compose -f docker-compose.prod.yml pull app
docker compose -f docker-compose.prod.yml up -d app
```

Using an immutable SHA tag makes rollback predictable.

## 8. Rollback

Change `APP_IMAGE` back to a previously known-good SHA tag, then run:

```bash
docker compose -f docker-compose.prod.yml pull app
docker compose -f docker-compose.prod.yml up -d app
```

## CI/CD Flow

On every push or pull request to `main`:

1. Java 21 is configured.
2. Spring Boot tests run with an isolated H2 test database.
3. The application JAR is built.
4. Docker Compose configuration is validated.
5. The Docker image is built.

On pushes to `main` and version tags (`v*`):

1. Docker Buildx builds the production image.
2. The image is published to GHCR.
3. `latest`, commit SHA, and version-tag image tags are generated as appropriate.

## Recommended production front end

For public internet deployment, place Nginx, Caddy, Traefik, Cloudflare Tunnel, or your hosting platform's HTTPS proxy in front of port 8080. Set `SITE_URL` to the final HTTPS domain so canonical URLs, sitemap URLs, and social-sharing metadata are correct.
