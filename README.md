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

## Download Analytics

Every tracked resource download now records a lightweight timestamped download event while preserving the existing lifetime counter.

Admin analytics page:

```text
/admin/analytics
```

It provides:

- downloads today
- downloads in the last 7 days
- downloads in the last 30 days
- custom start/end date reporting
- selected-range total
- lifetime total
- top 10 downloaded resources in the selected date range

Date-range analytics starts recording history from the deployment of this feature onward; existing lifetime download counts are not rewritten or guessed.

## SEO & Shareable Pages

Resources: `/resource/{id}`

Career opportunities: `/opportunity/{id}`

Dynamic sitemap: `/sitemap.xml`

Configure production canonical URLs with:

```text
SITE_URL=https://your-domain.example
```

## CI/CD & Deployment Readiness

GitHub Actions validates Java 21 builds, H2-backed Spring Boot tests, local/production Compose files, the application image, the local recovery image, the remote-backup image, and backup/restore/sync shell scripts.

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

The scheduled backup service creates synchronized backups and a combined recovery bundle.

Default local schedule:

```text
BACKUP_INTERVAL_SECONDS=86400
BACKUP_RETENTION_DAYS=14
BACKUP_ON_START=true
```

See [DISASTER_RECOVERY.md](DISASTER_RECOVERY.md), [BACKUP_RESTORE.md](BACKUP_RESTORE.md), and [REMOTE_BACKUP.md](REMOTE_BACKUP.md) for recovery and off-server backup procedures.

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

- Cloud object storage for primary resource files
- Mobile/PWA improvements
- Reverse-proxy HTTPS production example
- Contribution/contact workflow
- Final end-to-end production smoke test and release hardening

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
