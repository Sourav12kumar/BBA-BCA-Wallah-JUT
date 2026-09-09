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
- Shareable public detail page for every resource
- Shareable public detail page for every placement/internship opportunity
- Web Share / Copy Link actions on detail pages

## SEO & Shareable Pages

Each active resource has a public page:

```text
/resource/{id}
```

Each active placement or internship has a public page:

```text
/opportunity/{id}
```

These detail pages include:

- unique page title
- meta description
- canonical URL
- Open Graph title, description and URL
- Twitter summary metadata
- native Web Share support where available
- Copy Link fallback

A dynamic sitemap is available at:

```text
/sitemap.xml
```

The production base URL is configured through:

```text
SITE_URL=https://your-domain.example
```

For local development it defaults to `http://localhost:8080`.

## Admin Features

- Protected admin login
- Add/edit/delete subjects, notices and academic resources
- Upload PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX and TXT files
- 20 MB maximum file size per upload
- Optional Google Drive / OneDrive / public URL fallback
- Mark academic resources as Featured
- Pin important notices
- Download analytics and most-downloaded ranking
- Add/edit/delete **Placement and Internship opportunities**
- Opportunity type: Placement or Internship
- Manage company/organization, role, location, eligible batch, courses, eligibility, deadline, application URL and description
- Mark important career opportunities as Featured
- Placement and internship counts on admin dashboard

## Placement & Internship Module

Students can browse:

```text
/placements
/internships
/opportunities
```

### Career Opportunity Filters

```text
Keyword
Type: Placement / Internship
Course
Batch
Location
Sort: Featured / Newest / Deadline Soonest
```

Filter options are generated dynamically from active opportunity data.

## Pagination & Sorting

Academic resources and career opportunities are shown **12 items per page** so the UI remains usable as the database grows.

### Resource sort options

```text
Featured First
Newest First
Most Downloaded
Title A-Z
```

### Opportunity sort options

```text
Featured First
Newest First
Deadline Soonest
```

Filters and sort choices are preserved while navigating between pages.

## Download Analytics Flow

All student download/open actions use:

```text
/resource/{resourceId}/download
```

The application increments the resource's download counter in MySQL and then redirects the student to the local file or external resource URL.

## Tech Stack

- Java 21
- Spring Boot
- Spring MVC
- Thymeleaf
- Spring Data JPA
- Spring Security
- MySQL
- Docker / Docker Compose
- HTML + CSS

## Environment Setup

Copy `.env.example` to `.env` and set strong values for:

```text
MYSQL_ROOT_PASSWORD
ADMIN_USERNAME
ADMIN_PASSWORD
SITE_URL
```

Do not commit the real `.env` file.

## Run with Docker

```bash
docker compose up --build
```

Open:

- Student website: http://localhost:8080
- Admin panel: http://localhost:8080/admin

## Roadmap

- Cloud object storage
- Date-range analytics
- Mobile/PWA improvements
- CI/CD deployment
- Contribution/contact workflow

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
