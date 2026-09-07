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
- Most Downloaded Resources section on the homepage

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
- Total download count on the dashboard
- Download count for every resource
- Most-downloaded resource ranking

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

## Admin Resource Upload Flow

1. Log in to `/admin`.
2. Add the required subject first.
3. Click **Add Resource**.
4. Choose course, semester, subject and resource type.
5. Upload a PDF/document directly, or provide an external file URL.
6. Save the resource.
7. Students can open/download it from the subject dashboard without logging in.
8. Downloads automatically appear in the admin analytics.

When a new uploaded file replaces an older locally uploaded file, the previous stored file is removed automatically. Deleting a resource also removes its associated local uploaded file when applicable.

## Local Storage Design

By default, uploaded files are stored in:

```text
uploads/
```

The path can be changed with:

```text
UPLOAD_DIR
```

In Docker, files are persisted using the `resource_uploads` volume mounted at `/app/uploads`.

For production deployment, the storage service is intentionally isolated so it can later be replaced with S3, Cloudinary or another object-storage provider.

## Recommended Admin Workflow

1. Add all BBA/BCA subjects semester-wise.
2. Add syllabus, notes, PYQs and other resources to the correct subject.
3. Publish notices and updates.
4. Students navigate **Course → Semester → Subject → Resource Category**.
5. Use download analytics to identify the resources students use most.

## Roadmap

- Cloud object storage
- Placement and internship modules
- Featured/pinned resources
- Date-range analytics
- Pagination
- SEO metadata
- Mobile/PWA improvements
- CI/CD deployment
- Contribution/contact workflow

## Disclaimer

This is an independent student project and is not the official website of Jharkhand University of Technology.
