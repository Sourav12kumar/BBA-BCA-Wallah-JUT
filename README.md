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
5. `docker compose config` validates the Compose file.
6. The Docker image is built to catch container-build failures.

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
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

Production Compose does not expose MySQL publicly and keeps database/uploads in persistent Docker volumes.

See [DEPLOYMENT.md](DEPLOYMENT.md) for deployment, update, logs and rollback instructions.

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
- Date-range analytics
- Mobile/PWA improvements
- Reverse-proxy HTTPS example
- Database backup automation
- Contribution/contact workflow

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
