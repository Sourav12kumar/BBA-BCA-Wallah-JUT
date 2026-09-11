# HTTPS Production Deployment

Production traffic is handled by Caddy before reaching Spring Boot.

## Architecture

```text
Internet -> Caddy (80/443) -> Spring Boot (private :8080) -> MySQL
```

The production Compose file no longer publishes Spring Boot port 8080 directly.

## DNS

Point the production hostname to the server public IP before starting Caddy. Add IPv6 DNS only if the server is actually reachable over IPv6.

## Firewall

Allow public inbound traffic on TCP 80 and TCP 443. UDP 443 may also be allowed for HTTP/3. Do not expose MySQL or Spring Boot directly.

## Environment

Set these existing environment variables in the production `.env` file:

```text
SITE_DOMAIN
SITE_URL
MYSQL_ROOT_PASSWORD
ADMIN_USERNAME
ADMIN_PASSWORD
APP_IMAGE
```

`SITE_DOMAIN` contains only the hostname. `SITE_URL` contains the complete HTTPS public origin.

Primary cloud-file storage remains configured through the `STORAGE_S3_*` values documented in `OBJECT_STORAGE.md`.

## Start production

```bash
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

Caddy automatically obtains and renews public TLS certificates when DNS and ports are configured correctly.

## Verify

```bash
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs -f caddy app
```

Verify these URLs from a browser or HTTP client:

```text
https://<your-domain>/
https://<your-domain>/actuator/health
```

The HTTP version should redirect to HTTPS.

## Forwarded headers

Spring Boot is configured with:

```properties
server.forward-headers-strategy=framework
```

This lets Spring correctly recognize the original HTTPS scheme and public hostname supplied by Caddy.

## Security headers

The `Caddyfile` adds HSTS, nosniff, SAMEORIGIN framing protection, a strict referrer policy, and a restrictive browser permissions policy.

## Updating

Pull the new application image and recreate the app service. Caddy can remain running while the Spring Boot container is replaced.

## Rollback

Set `APP_IMAGE` to a previously published immutable image tag, pull it, and recreate the app service.

## Certificate persistence

Caddy state is stored in the `caddy_data` and `caddy_config` Docker volumes. Keep these volumes during normal releases.

## Pre-launch checklist

- DNS resolves to the production server.
- Public ports 80 and 443 are open.
- MySQL and Spring Boot are not publicly exposed.
- `SITE_DOMAIN` and `SITE_URL` match the production hostname.
- `/actuator/health` reports healthy through HTTPS.
- HTTP redirects to HTTPS.
- Admin login and resource downloads work over HTTPS.
- Backup and restore procedures have been tested.
