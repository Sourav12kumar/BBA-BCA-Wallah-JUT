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
- Syllabus
- Previous Year Questions (PYQs)
- Sample/model questions
- Notes and e-books
- Important questions
- Search and filters
- Notices and university updates
- Direct file downloads
- External resource links supported
- Per-resource download counter
- Most Downloaded Resources section
- Featured Resources section
- Pinned Important Notices section
- Featured resources automatically appear first in resource lists
- Pinned notices automatically appear first in notice lists

## Admin Features

- Protected admin login
- Add/edit/delete subjects
- Add/edit/delete notices
- Add/edit/delete academic resources
- Assign each resource to course, semester and subject
- Upload files directly from the admin panel
- Optional external Google Drive / OneDrive / public URL fallback
- Supported uploads: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX and TXT
- 20 MB maximum file size per upload
- Uploaded files are stored outside Git and served through `/files/{filename}`
- Docker volume keeps uploaded resources across container restarts
- Mark any academic resource as **Featured**
- Pin/unpin important notices from the notice form
- Total download count on the dashboard
- Download count for every resource
- Most-downloaded resource ranking
- Featured/Pinned status visible in admin tables

## Priority Content Flow

### Featured Resources

An administrator can mark a resource as **Featured** from the resource form. Featured resources:

- appear in a dedicated homepage section,
- appear before normal resources in resource lists,
- appear first inside subject resource categories,
- display a FEATURED badge to students.

### Pinned Notices

An administrator can mark a notice as **Pinned**. Pinned notices:

- appear in the Important Notices homepage section,
- appear before normal notices,
- display a PINNED badge,
- remain visually highlighted on the notices page.

This is useful for exam dates, admit-card updates, registration deadlines, result notices and urgent JUT announcements.

## Download Analytics Flow

All student download/open actions use:

```text
/resource/{resourceId}/download
```

The application increments the resource's download counter in MySQL and then redirects the student to the locally uploaded file or external resource URL. This means both local files and Drive/OneDrive/public links are tracked consistently.

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

Copy the sample environment file:

```bash
cp .env.example .env
```

On Windows PowerShell you can instead create `.env` manually from `.env.example`.

Set strong values for:

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

1. Add all BBA/BCA subjects semester-wise.
2. Add syllabus, notes, PYQs and other resources to the correct subject.
3. Mark high-value material as **Featured** when needed.
4. Publish notices and pin urgent updates.
5. Students navigate **Course → Semester → Subject → Resource Category**.
6. Use download analytics to identify the resources students use most.

## Roadmap

- Cloud object storage
- Placement and internship modules
- Date-range analytics
- Pagination
- SEO metadata
- Mobile/PWA improvements
- CI/CD deployment
- Contribution/contact workflow

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
