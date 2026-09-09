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

GitHub Actions validates Java 21 builds, H2-backed Spring Boot tests, local/production Compose files, the application image, the local recovery image, the remote-backup image, and all backup/restore/sync shell scripts.

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

Default local schedule:

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

## Off-Server Backup Synchronization

A separate opt-in `remote-backup` service copies local recovery artifacts to private S3-compatible object storage. This can be used with AWS S3, Cloudflare R2, Backblaze B2 S3 API, MinIO and similar providers.

The Java application and MySQL service do not receive the object-storage credentials.

Default remote-sync settings:

```text
S3_PREFIX=bba-bca-wallah
REMOTE_SYNC_INTERVAL_SECONDS=3600
REMOTE_SYNC_ON_START=true
REMOTE_RETENTION_DAYS=30
```

Enable it after configuring a private bucket and credentials:

```bash
docker compose -f docker-compose.prod.yml --profile remote-backup up -d --build
```

Remote recovery bundles can be downloaded back into the local backup volume and then restored using the existing guarded full-restore flow.

See [REMOTE_BACKUP.md](REMOTE_BACKUP.md) for setup, provider-neutral configuration, manual synchronization, remote download and security guidance.

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
- S3-compatible off-server backup storage
- HTML + CSS

## Environment Setup

Copy `.env.example` to `.env` and configure the application/database values first. To enable remote backups, also configure:

```text
S3_ENDPOINT
S3_BUCKET
S3_PREFIX
S3_ACCESS_KEY
S3_SECRET_KEY
REMOTE_SYNC_INTERVAL_SECONDS
REMOTE_SYNC_ON_START
REMOTE_RETENTION_DAYS
```

Never commit the real `.env` file or storage credentials.

## Local Docker Run

```bash
docker compose up --build
```

Open:

- Student website: http://localhost:8080
- Admin panel: http://localhost:8080/admin
- Health: http://localhost:8080/actuator/health

## Roadmap

- Cloud object storage for primary resource files
- Date-range analytics
- Mobile/PWA improvements
- Reverse-proxy HTTPS example
- Contribution/contact workflow

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
