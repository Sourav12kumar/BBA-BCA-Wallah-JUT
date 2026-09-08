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
- Placement/internship cards with company, role, location, eligible batch, eligible courses, eligibility, deadline and apply link
- Latest placements and internships highlighted on the homepage

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

## Priority Content Flow

Featured resources appear before normal resources, while pinned notices appear before regular notices and in the Important Notices homepage section.

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
```

Do not commit the real `.env` file.

## Run with Docker

```bash
docker compose up --build
```

Open:

- Student website: http://localhost:8080
- Admin panel: http://localhost:8080/admin

## Recommended Admin Workflow

1. Add BBA/BCA subjects semester-wise.
2. Add syllabus, notes, PYQs and other academic resources.
3. Feature high-value resources.
4. Publish and pin urgent notices.
5. Add current placement and internship opportunities.
6. Enter clean course, batch and location values so filters remain useful.
7. Feature important career opportunities.
8. Review download analytics to identify popular material.

## Roadmap

- Cloud object storage
- Date-range analytics
- SEO metadata
- Mobile/PWA improvements
- CI/CD deployment
- Contribution/contact workflow

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
