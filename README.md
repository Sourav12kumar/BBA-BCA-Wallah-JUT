# BBA/BCA Wallah — JUT

A student-focused academic information and resource portal for **BBA and BCA students under Jharkhand University of Technology (JUT), Ranchi**.

## Core Navigation

```text
Course
  ↓
Semester
  ↓
Subject
  ↓
Subject Dashboard
  ├── Syllabus
  ├── Notes
  ├── PYQ
  ├── Sample / Model Questions
  ├── Important Questions
  ├── E-Books
  └── Other Resources
```

## Student Features

- No student login required
- BBA/BCA selection
- Semester 1–6 navigation
- Subject-wise dashboard
- Syllabus, PYQs, notes, e-books and important questions
- Search and filters
- Notices and university updates
- Direct file downloads and external resource links
- Per-resource download counter
- Most Downloaded Resources section
- Featured Resources section
- Pinned Important Notices section
- Dedicated Placements and Internships pages
- Combined `/opportunities` career browser
- Opportunity filters by type, course, batch, location and keyword
- Pagination and sorting for resources and opportunities
- Shareable public detail pages
- Web Share / Copy Link actions
- Dynamic sitemap and social metadata

## Admin Features

- Protected admin login
- Add/edit/delete subjects, notices and academic resources
- Direct document uploads plus external URL support
- Featured resources and pinned notices
- Lifetime download analytics
- Date-range download analytics
- Placement/internship opportunity management
- Resource course/semester automatically derived from the selected subject

## Primary Resource Storage

Admin uploads support two storage modes:

```text
STORAGE_PROVIDER=local
STORAGE_PROVIDER=s3
```

With `s3`, uploaded files are stored in a private S3-compatible object bucket. Existing `/files/{uuid.ext}` URLs remain unchanged; when a student downloads a cloud-backed file, the application issues a short-lived presigned object URL.

Supported provider styles include AWS S3 and compatible services such as Cloudflare R2, Backblaze B2 S3 API and MinIO.

See [OBJECT_STORAGE.md](OBJECT_STORAGE.md) for configuration, permissions, migration and security guidance.

## Download Analytics

Every tracked resource download records a lightweight timestamped download event while preserving the existing lifetime counter.

Admin analytics page:

```text
/admin/analytics
```

It provides downloads today, last 7 days, last 30 days, custom date ranges, lifetime totals and top resources for the selected period.

## SEO & Shareable Pages

Resources: `/resource/{id}`

Career opportunities: `/opportunity/{id}`

Dynamic sitemap: `/sitemap.xml`

Configure production canonical URLs with:

```text
SITE_URL=https://your-domain.example
```

## CI/CD & Deployment Readiness

GitHub Actions validates Java 21 builds, H2-backed Spring Boot tests, local/production Compose files, the application image, recovery images and backup/restore scripts.

Production application image:

```text
ghcr.io/sourav12kumar/bba-bca-wallah-jut:latest
```

Health endpoint:

```text
/actuator/health
```

See [DEPLOYMENT.md](DEPLOYMENT.md) for production deployment and rollback.

## Backup & Disaster Recovery

The project includes scheduled local backups, full-site recovery bundles and optional S3-compatible off-server backup synchronization.

See [DISASTER_RECOVERY.md](DISASTER_RECOVERY.md), [BACKUP_RESTORE.md](BACKUP_RESTORE.md), and [REMOTE_BACKUP.md](REMOTE_BACKUP.md).

## Tech Stack

- Java 21
- Spring Boot
- Spring MVC
- Thymeleaf
- Spring Data JPA
- Spring Security
- Spring Boot Actuator
- MySQL
- H2 for isolated CI tests
- AWS SDK v2 S3 client/presigner
- Docker / Docker Compose
- GitHub Actions
- GitHub Container Registry
- S3-compatible object storage and backup storage
- HTML + CSS

## Environment Setup

Copy `.env.example` to `.env` and configure the required application/database values. Never commit the real `.env` file or storage credentials.

## Local Docker Run

```bash
docker compose up --build
```

Open:

- Student website: http://localhost:8080
- Admin panel: http://localhost:8080/admin
- Download analytics: http://localhost:8080/admin/analytics
- Health: http://localhost:8080/actuator/health

## Project Status

See [PROJECT_STATUS.md](PROJECT_STATUS.md) for the current completion report, completed modules, remaining work and recommended release sequence.

## Roadmap

- Mobile/PWA improvements
- Reverse-proxy HTTPS production example
- Contribution/contact workflow
- Final end-to-end production smoke test and release hardening
- Optional migration utility for existing local files to object storage

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
