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
- Dedicated **Placements** page
- Dedicated **Internships** page
- Combined `/opportunities` career browser
- Opportunity filters by **type, course, batch, location and keyword**
- Pagination for academic resources and career opportunities
- Sorting for resources by **Featured, Newest, Most Downloaded, Title A-Z**
- Sorting for opportunities by **Featured, Newest, Deadline Soonest**
- Shareable public detail page for every resource and career opportunity
- Web Share / Copy Link actions
- Dynamic sitemap and social metadata

## Admin Features

- Protected admin login
- Add/edit/delete subjects, notices and academic resources
- Upload PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX and TXT files
- 20 MB maximum file size per upload
- Optional Google Drive / OneDrive / public URL fallback
- Mark academic resources as Featured
- Pin important notices
- Download analytics and most-downloaded ranking
- Add/edit/delete placement and internship opportunities
- Opportunity type, company, role, location, batch, course, eligibility, deadline, apply URL and description
- Featured career opportunities
- Placement and internship counts on admin dashboard

## SEO & Shareable Pages

Active resources are available at:

```text
/resource/{id}
```

Active placements/internships are available at:

```text
/opportunity/{id}
```

The pages include unique title/description metadata, canonical URLs, Open Graph metadata, Twitter summary metadata, native sharing and Copy Link fallback.

Dynamic sitemap:

```text
/sitemap.xml
```

Set the production URL with:

```text
SITE_URL=https://your-domain.example
```

## CI/CD & Deployment Readiness

The repository includes GitHub Actions workflows for automated build validation and container publishing.

### CI workflow

On pushes and pull requests to `main`:

1. Java 21 is configured.
2. Maven dependencies are cached.
3. Spring Boot tests run with an isolated H2 database.
4. The deployable JAR is built and saved as a workflow artifact.
5. Local and production Compose configurations are validated.
6. The application Docker image is built.
7. The database-backup Docker image is built.
8. Backup/restore shell scripts receive syntax checks.

Workflow file:

```text
.github/workflows/ci.yml
```

### Container publishing

On pushes to `main` and version tags such as `v1.0.0`, GitHub Actions publishes a production Docker image to GitHub Container Registry (GHCR).

Image:

```text
ghcr.io/sourav12kumar/bba-bca-wallah-jut:latest
```

It also publishes commit-SHA tags that can be used for exact deployments and rollback.

Workflow file:

```text
.github/workflows/publish-image.yml
```

### Health checks

Spring Boot Actuator exposes:

```text
/actuator/health
```

Docker checks application health automatically. MySQL also has a readiness check, and the application waits until MySQL is healthy before starting.

### Production Compose

For server deployment using the published image:

```bash
docker compose -f docker-compose.prod.yml pull app
docker compose -f docker-compose.prod.yml up -d --build
```

Production Compose does not expose MySQL publicly and keeps database/uploads/backups in persistent Docker volumes.

See [DEPLOYMENT.md](DEPLOYMENT.md) for deployment, update, logs and rollback instructions.

## Database Backup & Restore

Production includes a dedicated MySQL backup container.

Default behavior:

```text
BACKUP_INTERVAL_SECONDS=86400
BACKUP_RETENTION_DAYS=14
BACKUP_ON_START=true
```

This creates a compressed SQL backup every 24 hours, keeps 14 days of backups, and creates one backup when the backup service starts.

Backups are stored in the persistent `db_backups` Docker volume.

Manual backup:

```bash
docker compose -f docker-compose.prod.yml run --rm --entrypoint /scripts/backup-db.sh backup
```

Restore requires an explicit backup filename and `CONFIRM_RESTORE=YES` to reduce accidental destructive restores.

See [BACKUP_RESTORE.md](BACKUP_RESTORE.md) for full backup, restore, retention, and recovery instructions.

Important: SQL backups protect MySQL data. Uploaded documents remain in the separate `resource_uploads` volume and should also be backed up for complete disaster recovery.

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

- Cloud object storage
- Full uploaded-file backup/export
- Date-range analytics
- Mobile/PWA improvements
- Reverse-proxy HTTPS example
- Contribution/contact workflow

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
