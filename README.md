# SkillProof — Practical Skill Verification & Evidence Platform

> **"Prove what you can build."**

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Java](https://img.shields.io/badge/Java-21-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)]()
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)]()
[![Security](https://img.shields.io/badge/Security-Spring%20Security%206-red.svg)]()
[![License](https://img.shields.io/badge/License-MIT-blue.svg)]()

SkillProof is a developer verification platform built for engineers and students to demonstrate genuine technical competencies through real-world challenges, code artifacts (GitHub repositories, live deployments, solution archives), rubric-based mentor evaluations, and cryptographically verifiable skill badges.

---

## 1. System Architecture & Tech Stack

SkillProof is engineered with an intentional, resilient architectural foundation:

### Backend
- **Java 21 (LTS)** & **Spring Boot 3.3.4**
- **Spring Security 6**: Stateful session management with secure `HttpOnly` session cookies, `CookieCsrfTokenRepository` CSRF protection, and role-based access control (`STUDENT`, `MENTOR`, `ADMIN`).
- **Spring Data JPA & Hibernate 6**: Optimized relational mappings, transactional boundaries, query indexes, and cascade integrity.
- **Jakarta Mail**: Genuine transactional SMTP delivery for cryptographically secure 6-digit Email One-Time Passwords (OTP).
- **Flyway Migrations**: Automated version-controlled database schema migrations (`V1__init_schema.sql`) and seed data (`V2__seed_data.sql`).
- **JUnit 5 & Mockito**: Full unit, integration, and security test coverage.

### Frontend
- **Zero Frontend Frameworks**: Pure HTML5, modern CSS3, and ES6+ JavaScript.
- **Design Token System (`/css/tokens.css`)**: Cohesive CSS custom properties for spacing, typography scale, radii, and elevations.
- **Dual-Theme Support**: Dark mode (default) and Light mode with local persistence.
- **Lucide Icon Engine (`/js/icons.js`)**: Crisp embedded SVG icons with zero layout shift.
- **Robust API Client (`/js/api.js`)**: Automatic CSRF token synchronization, unified session error interceptor, and dynamic responsive sidebar navigation.

### Database
- **MySQL 8.0**: Normalized schema spanning 14 tables, complete with foreign keys, composite unique constraints, cascade rules, and query indexes.
- **H2 In-Memory (Test Profile)**: Fast, hermetic test execution with MySQL syntax emulation.

---

## 2. Relational Schema Blueprint

```
 users (id, email, username, password_hash, role, status, email_verified, ...)
   ├── profile_settings (user_id, headline, bio, github_url, is_public, show_on_leaderboard, ...)
   ├── otp_verifications (id, email, otp_hash, purpose, expires_at, attempts, consumed, ...)
   ├── user_skills (id, user_id, skill_id, status, progress_percent, started_at, completed_at)
   ├── submissions (id, challenge_id, student_id, repo_url, demo_url, file_path, status, ...)
   │     └── submission_reviews (id, submission_id, mentor_id, total_score, decision, feedback, ...)
   │           └── review_scores (id, review_id, criterion_name, score, max_score, comments)
   ├── verified_skills (id, user_id, skill_id, badge_code, reviewer_id, verified_at, ...)
   ├── notifications (id, user_id, title, message, type, is_read, ...)
   └── user_activity (id, user_id, activity_type, description, ...)

 skills (id, name, slug, category, difficulty, estimated_hours, icon_name, ...)
   ├── skill_prerequisites (skill_id, prerequisite_skill_id)  <-- Directed Acyclic Graph (DAG)
   └── challenges (id, skill_id, title, difficulty, points, rubric_json, ...)
         └── challenge_prerequisites (challenge_id, prerequisite_challenge_id)
```

---

## 3. Core Engine Implementations

### A. Skill Dependency Engine (`SkillDependencyEngine.java`)
- **Cycle-Detection Algorithm**: Uses Depth-First Search (DFS) with 3-color graph state tracking (`UNVISITED`, `VISITING`, `VISITED`) to prevent circular dependencies in the skill curriculum.
- **Prerequisite Eligibility**: Evaluates whether a student has completed all required parent skills before unlocking advanced challenges.
- **Rule-Based Recommendations**: Generates explainable skill suggestions based on current progress, next-in-path prerequisites, and popular foundational skills.

### B. Server-Authoritative Email OTP Authentication (`OtpService.java`)
- **Cryptographic Generation**: 6-digit codes generated using `SecureRandom`.
- **One-Way BCrypt Storage**: The plain OTP is never stored in the database. Only its BCrypt hash is persisted alongside purpose, expiration timestamp, attempt count, and client IP.
- **Authoritative Expiration & Throttling**: 5-minute strict server-side TTL, 60-second resend cooldown, 5-attempt brute-force threshold, and account lockout protection.
- **Timing Attack Mitigation**: Constant-time verification comparisons.

### C. Transparent Leaderboard Algorithm (`LeaderboardService.java`)
- **Formula**:
  $$\text{Total Points} = \sum (\text{Deduplicated Challenge Points}) + (\text{Verified Skills} \times 50)$$
- **Privacy & Opt-Out**: Students can opt out of the public leaderboard via Settings at any time while retaining all credentials.

---

## 4. Pre-configured Seed Accounts

The system automatically seeds realistic users with BCrypt-hashed passwords on first launch:

| Role | Name | Email | Password | Pre-seeded Badges |
| :--- | :--- | :--- | :--- | :--- |
| **ADMIN** | Alex Mercer | `admin@skillproof.io` | `Admin@12345` | System Administrator |
| **MENTOR** | Sarah Chen | `sarah.chen@skillproof.io` | `Password@123` | Evaluates Java, Git, Web |
| **MENTOR** | Marcus Vance | `marcus.vance@skillproof.io` | `Password@123` | Evaluates Linux, Docker, DB |
| **STUDENT** | Devon Student | `devon.student@skillproof.io` | `Password@123` | `VP-JAVA-9842A1`, `VP-GIT-4419B2` |
| **STUDENT** | Priya Patel | `priya.patel@skillproof.io` | `Password@123` | `VP-HTML-7731C3` |

---

## 5. Screen Inventory & Application Pages

- **Public Landing Page** (`/index.html`): Hero section, live interactive student profile preview, "How It Works" workflow, verified competencies catalogue, FAQ, and call-to-actions.
- **Authentication Center** (`/auth.html`): Multi-step registration, 6-digit OTP input with automatic paste distribution, login OTP requests, password login fallback, and password reset.
- **Student Dashboard** (`/dashboard.html`): Personalized real-time metrics (pursued skills, passed challenges, pending submissions, verified credentials), skill progress bars, recommendations, and recent activity.
- **Skills Catalogue** (`/skills.html`): Categorized curriculum, difficulty filtering, search, and prerequisite inspection modal.
- **Practical Challenges** (`/challenges.html`): Real-world project briefs, rubrics inspection, and submission modal (supporting GitHub URLs, live deployment URLs, and zipped source code archives up to 5MB).
- **My Submissions** (`/submissions.html`): Historical submission table with review statuses, evidence links, and mentor rubric score breakdown.
- **Mentor Review Workspace** (`/mentor.html`): Restricted to mentors/admins. Review queue filterable by status, evidence viewer, criterion-by-criterion rubric scoring inputs, and decision dispatcher (`APPROVED`, `CHANGES_REQUESTED`, `REJECTED`).
- **Verified Credentials** (`/verified.html`): Earned badge showcase, cryptographic badge lookup, and shareable high-resolution certificate modal with copyable verification links.
- **Leaderboard** (`/leaderboard.html`): Transparent ranking table, top-3 medal cards, average score indicators, and formula modal.
- **Developer Profile** (`/profile.html?user=username`): Publicly shareable student portfolio displaying verified badges, approved code repositories, and mentor feedback.
- **Account Settings** (`/settings.html`): Personal headline, bio, external profile URLs, leaderboard opt-out toggle, and password reset OTP triggers.

---

## 6. REST API Reference

All API responses follow the standard `ApiResponse<T>` envelope:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2026-10-19T20:25:00Z"
}
```

### Key Endpoints

| Category | Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | `GET` | `/api/v1/auth/csrf` | Public | Synchronize CSRF token |
| | `POST` | `/api/v1/auth/register` | Public | Register new user in pending state |
| | `POST` | `/api/v1/auth/verify-email` | Public | Verify registration OTP and activate session |
| | `POST` | `/api/v1/auth/request-login-otp` | Public | Dispatch 6-digit login code via email |
| | `POST` | `/api/v1/auth/verify-login-otp` | Public | Authenticate via OTP code |
| | `POST` | `/api/v1/auth/password-login` | Public | Authenticate via password |
| | `POST` | `/api/v1/auth/forgot-password` | Public | Request password reset OTP |
| | `POST` | `/api/v1/auth/reset-password` | Public | Reset password using OTP |
| | `GET` | `/api/v1/auth/me` | Authenticated | Get current authenticated user details |
| **Skills** | `GET` | `/api/v1/skills` | Public | List skills with search, category & difficulty filters |
| | `GET` | `/api/v1/skills/{slug}` | Public | Get skill details and prerequisite graph |
| | `POST` | `/api/v1/skills/{skillId}/pursue`| Student | Enroll in a skill track |
| **Challenges** | `GET` | `/api/v1/challenges` | Public | Browse technical challenges |
| | `GET` | `/api/v1/challenges/{id}` | Public | Get challenge details and scoring rubric |
| **Submissions**| `POST`| `/api/v1/submissions` | Student | Submit project evidence (URLs & file upload) |
| | `GET` | `/api/v1/submissions/mine` | Student | List user's submissions and evaluations |
| **Reviews** | `GET` | `/api/v1/mentor/submissions` | Mentor/Admin| Review queue with status filters |
| | `POST`| `/api/v1/mentor/submissions/{id}/reviews`| Mentor/Admin| Submit rubric scores and decision |
| **Badges** | `GET` | `/api/v1/verified-skills/mine` | Student | Get user's verified credentials |
| | `GET` | `/api/v1/verified-skills/badge/{code}`| Public | Public badge verification |
| **Leaderboard**| `GET` | `/api/v1/leaderboard` | Public | Retrieve community rankings |
| | `GET` | `/api/v1/leaderboard/formula` | Public | Scoring formula and policy |
| **Profiles** | `GET` | `/api/v1/profiles/me` | Authenticated | Get current user's full profile |
| | `PATCH`| `/api/v1/profiles/me` | Authenticated | Update bio, headline, links & privacy |
| | `GET` | `/api/v1/profiles/{username}`| Public | Public profile portfolio |

---

## 7. Running the Application

### Option A: Local Development (Maven & Host Database)

#### 1. Prerequisites
- **Java 21+** installed (`java -version`)
- **Maven 3.9+** installed (`mvn -version`)
- **MySQL 8.0+** running locally on port 3306

#### 2. Configure Environment
Copy `.env.example` or adjust `application.yml` / environment variables:
```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/skillproof_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="your_password"
```

*(Optional: Run Mailpit locally to catch outgoing OTP emails)*:
```bash
docker run -d -p 1025:1025 -p 8025:8025 axllent/mailpit
```

#### 3. Run the Backend & Serve Frontend
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.
Flyway will automatically create all tables and populate the initial seed dataset.

---

### Option B: Complete Containerized Stack (Docker Compose)

Launch the full stack — MySQL 8, Mailpit (SMTP + Web UI), and SkillProof app:

```bash
docker compose up --build
```

Access services:
- **SkillProof Web Application**: [http://localhost:8080](http://localhost:8080)
- **Mailpit Web Inbox** (inspect OTP emails sent by the system): [http://localhost:8025](http://localhost:8025)
- **MySQL Database**: `localhost:3307` (mapped to container port 3306 to prevent conflict with local MySQL on 3306)

---

## 8. Test Execution

SkillProof includes comprehensive tests for:
- Core services (Auth, OTP, Dependency Engine, Submissions, Reviews, Leaderboard)
- Skill prerequisite cycle detection and graph traversal algorithms
- Spring Security authentication and session endpoints

Run all tests via Maven:
```bash
mvn clean test
```

To build a standalone production JAR:
```bash
mvn clean package
```

The resulting runnable artifact will be located at `target/skillproof-1.0.0.jar`.
Run it directly with:
```bash
java -jar target/skillproof-1.0.0.jar
```
