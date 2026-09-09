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
- Download analytics
- Placement/internship opportunity management

## SEO & Shareable Pages

Resources: `/resource/{id}`

Career opportunities: `/opportunity/{id}`

Dynamic sitemap: `/sitemap.xml`

Configure production canonical URLs with:

```text
SITE_URL=https://your-domain.example
```

## CI/CD & Deployment Readiness

GitHub Actions validates Java 21 builds, H2-backed Spring Boot tests, local/production Compose files, the application image, the recovery image, and all backup/restore shell scripts.

Production application image:

```text
ghcr.io/sourav12kumar/bba-bca-wallah-jut:latest
```

Health endpoint:

```text
/actuator/health
```

See [DEPLOYMENT.md](DEPLOYMENT.md) for production deployment and rollback.

## Full Backup & Disaster Recovery

Production data is protected as two coordinated parts:

1. MySQL application data
2. Uploaded PDFs/documents from `resource_uploads`

The scheduled backup service creates synchronized backups and a combined recovery bundle:

```text
bba_bca_wallah_YYYY-MM-DD_HH-MM-SS.sql.gz
uploads_YYYY-MM-DD_HH-MM-SS.tar.gz
full_YYYY-MM-DD_HH-MM-SS.tar.gz
manifest_YYYY-MM-DD_HH-MM-SS.txt
```

Default schedule:

```text
BACKUP_INTERVAL_SECONDS=86400
BACKUP_RETENTION_DAYS=14
BACKUP_ON_START=true
```

Manual full-site backup:

```bash
docker compose -f docker-compose.prod.yml run --rm \
  --entrypoint /scripts/full-backup.sh backup
```

Full restore is guarded by `CONFIRM_RESTORE=YES` and should be performed while the app and scheduled backup service are stopped.

See [DISASTER_RECOVERY.md](DISASTER_RECOVERY.md) for full-site restore instructions. Database-only procedures remain documented in [BACKUP_RESTORE.md](BACKUP_RESTORE.md).

Important production note: the `db_backups` Docker volume protects recovery data on the same server. For server/disk-loss protection, copy full backup bundles to independent off-server storage.

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
- Docker / Docker Compose
- GitHub Actions
- GitHub Container Registry
- HTML + CSS

## Environment Setup

Copy `.env.example` to `.env` and configure:

```text
MYSQL_ROOT_PASSWORD
ADMIN_USERNAME
ADMIN_PASSWORD
SITE_URL
APP_PORT
APP_IMAGE
JAVA_OPTS
BACKUP_INTERVAL_SECONDS
BACKUP_RETENTION_DAYS
BACKUP_ON_START
```

Do not commit the real `.env` file.

## Local Docker Run

```bash
docker compose up --build
```

Open:

- Student website: http://localhost:8080
- Admin panel: http://localhost:8080/admin
- Health: http://localhost:8080/actuator/health

## Roadmap

- Off-server backup synchronization
- Cloud object storage
- Date-range analytics
- Mobile/PWA improvements
- Reverse-proxy HTTPS example
- Contribution/contact workflow

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
