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
- BBA and BCA course selection
- Semester 1–6 navigation
- Subject-wise dashboard
- Syllabus, notes, PYQs, sample questions and important questions
- E-books and other downloadable resources
- Search and course/semester/resource-type filters
- Notices and university updates
- Mobile-friendly interface

## Admin Features
- Protected `/admin` login
- Add/edit/delete subjects
- Add/edit/delete academic resources
- Assign resources to course, semester and subject
- Add/edit/delete notices
- Dashboard counters for subjects, resources and notices

## Tech Stack
- Java 21
- Spring Boot 3
- Spring MVC + Thymeleaf
- Spring Data JPA
- Spring Security
- MySQL
- Docker / Docker Compose
- HTML + CSS

## Run with Docker

Create a `.env` file locally:

```env
MYSQL_ROOT_PASSWORD=your_mysql_password
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your_secure_admin_password
```

Then run:

```bash
docker compose up --build
```

Open:
- Website: `http://localhost:8080`
- Admin: `http://localhost:8080/admin`

## Recommended Content Workflow
1. Add BBA/BCA subjects semester-wise from the admin dashboard.
2. Add resources and assign each item to its correct subject.
3. Add notices and academic updates.
4. Students browse **Course → Semester → Subject → Resource Category**.

## Roadmap
- Direct PDF/file upload
- Cloud storage integration
- Dedicated placement/internship/career modules
- Download counters
- Featured resources and pinned notices
- Admin analytics
- Pagination and advanced search
- SEO + sitemap
- PWA/mobile improvements
- CI/CD and cloud deployment

## Disclaimer
This is an independent student project and is **not the official website of Jharkhand University of Technology**.
