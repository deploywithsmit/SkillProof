-- ==============================================================================
-- SkillProof Database Migration V2: Production-Grade Seed Data
-- ==============================================================================

-- 1. Initial Users (All initial passwords default to Password@123; admin has Admin@12345)
-- Bcrypt hash for Password@123: $2a$10$wT8m9Mv63/zE6aTqTjDffuYIskZz.N25lX7rKj9gKj3a19bX2sF0a (will also be synced by bootstrapper)
INSERT INTO users (id, email, username, full_name, password_hash, role, status, failed_login_attempts, created_at, updated_at) VALUES
(1, 'admin@skillproof.io', 'admin', 'Alex Mercer', '$2a$10$wT8m9Mv63/zE6aTqTjDffuYIskZz.N25lX7rKj9gKj3a19bX2sF0a', 'ADMIN', 'ACTIVE', 0, NOW(), NOW()),
(2, 'sarah.chen@skillproof.io', 'sarah_chen', 'Sarah Chen', '$2a$10$wT8m9Mv63/zE6aTqTjDffuYIskZz.N25lX7rKj9gKj3a19bX2sF0a', 'MENTOR', 'ACTIVE', 0, NOW(), NOW()),
(3, 'marcus.vance@skillproof.io', 'marcus_vance', 'Marcus Vance', '$2a$10$wT8m9Mv63/zE6aTqTjDffuYIskZz.N25lX7rKj9gKj3a19bX2sF0a', 'MENTOR', 'ACTIVE', 0, NOW(), NOW()),
(4, 'devon.student@skillproof.io', 'devon_miles', 'Devon Miles', '$2a$10$wT8m9Mv63/zE6aTqTjDffuYIskZz.N25lX7rKj9gKj3a19bX2sF0a', 'STUDENT', 'ACTIVE', 0, NOW(), NOW()),
(5, 'priya.patel@skillproof.io', 'priya_codes', 'Priya Patel', '$2a$10$wT8m9Mv63/zE6aTqTjDffuYIskZz.N25lX7rKj9gKj3a19bX2sF0a', 'STUDENT', 'ACTIVE', 0, NOW(), NOW());

-- 2. Profile Settings
INSERT INTO profile_settings (user_id, headline, bio, technical_interests, github_url, linkedin_url, is_public, show_on_leaderboard, show_reviews) VALUES
(1, 'Lead Platform Administrator', 'Overseeing technical standards and skill verification protocols on SkillProof.', 'Cloud Architecture, Distributed Systems, Security', 'https://github.com/alex-skillproof', 'https://linkedin.com/in/alex-mercer-sp', TRUE, FALSE, TRUE),
(2, 'Staff Backend Engineer @ CloudScale', 'Passionate about clean architecture, concurrency, and mentoring upcoming engineers.', 'Java, Spring Boot, MySQL, Kafka, Microservices', 'https://github.com/sarahchen-dev', 'https://linkedin.com/in/sarah-chen-backend', TRUE, TRUE, TRUE),
(3, 'Principal Systems Architect', 'Over 12 years building production relational databases and resilient systems.', 'PostgreSQL, MySQL Internals, Linux, High Availability', 'https://github.com/marcusvance', 'https://linkedin.com/in/marcusvance-systems', TRUE, TRUE, TRUE),
(4, 'Computer Science Student & Systems Enthusiast', 'Focused on robust backend engineering, databases, and proving practical competencies through verifiable projects.', 'Java, Relational Databases, Linux, Git Internals', 'https://github.com/devonmiles', 'https://linkedin.com/in/devon-miles-engineer', TRUE, TRUE, TRUE),
(5, 'Full-Stack Developer in Training', 'Crafting accessible UI components and building solid REST APIs with Spring Boot.', 'HTML/CSS, JavaScript, Spring Boot, Responsive Design', 'https://github.com/priyapatel-tech', 'https://linkedin.com/in/priya-patel-swe', TRUE, TRUE, TRUE);

-- 3. Technical Skills
INSERT INTO skills (id, slug, name, category, difficulty, description, learning_objectives, estimated_hours, icon_name) VALUES
(1, 'java-fundamentals', 'Java Fundamentals', 'Programming', 'BEGINNER', 
 'Core object-oriented principles, memory model, exception handling, collections framework, and clean code conventions.',
 'Master OOP design principles; Understand JVM stack vs heap; Effectively use Java Collections (List, Set, Map); Implement safe error handling and checked exceptions.',
 15, 'code-2'),

(2, 'sql-relational-design', 'SQL & Relational Database Design', 'Databases', 'BEGINNER',
 'Entity-relationship modeling, 3NF normalization, foreign key integrity, multi-table joins, transactions, and indexing strategies.',
 'Design normalized relational schemas; Write high-performance JOIN and GROUP BY queries; Understand ACID transaction semantics; Prevent SQL injection through prepared statements.',
 14, 'database'),

(3, 'git-github', 'Git & GitHub Workflows', 'Version Control', 'BEGINNER',
 'Distributed version control fundamentals, atomic commits, interactive rebase, branch management, merge conflict resolution, and PR etiquette.',
 'Execute clean branching strategies (feature branches, hotfixes); Resolve complex merge conflicts safely; Use rebase and squashing for clean history; Leverage GitHub PRs and reviews.',
 8, 'git-branch'),

(4, 'linux-fundamentals', 'Linux Fundamentals & Shell Scripting', 'Operating Systems', 'BEGINNER',
 'Unix philosophy, file system navigation, permissions and ownership, process inspection, pipeline composition, and POSIX shell scripts.',
 'Navigate and manage Linux directories and permissions (chmod, chown); Inspect and control running processes (ps, kill, top); Write robust Bash scripts with error traps and arguments.',
 12, 'terminal'),

(5, 'html-css', 'Modern HTML5 & Semantic CSS', 'Web Development', 'BEGINNER',
 'Semantic markup, accessibility (a11y), flexbox and CSS grid layouts, CSS custom properties, and responsive design systems without frameworks.',
 'Structure documents with accessible semantic elements; Master Flexbox and CSS Grid layouts; Implement responsive viewports with CSS variables and media queries; Build high-contrast accessible components.',
 10, 'layout'),

(6, 'javascript-fundamentals', 'JavaScript Fundamentals & Async ES6+', 'Web Development', 'INTERMEDIATE',
 'Modern ECMAScript standards, event loop, closures, promises, async/await, DOM manipulation, and resilient HTTP client communication.',
 'Write modular ES6+ JavaScript; Work comfortably with async/await and Fetch API; Handle network errors gracefully; Manipulate the DOM without external dependencies.',
 16, 'file-code'),

(7, 'docker-fundamentals', 'Docker & Containerization', 'DevOps', 'INTERMEDIATE',
 'Container isolation concepts, writing multi-stage Dockerfiles, image optimization, volume persistence, and multi-container composition.',
 'Build lightweight, secure container images; Use multi-stage builds to minimize attack surface; Configure persistent data volumes; Coordinate services with Docker Compose.',
 14, 'box'),

(8, 'rest-api-development', 'REST API Development with Spring Boot', 'Programming', 'INTERMEDIATE',
 'Stateless REST architecture, HTTP method semantics, DTO mapping, input validation, structured error responses, and service-layer separation.',
 'Design RESTful resource hierarchies and status codes; Implement Jakarta Bean Validation with descriptive errors; Enforce strict DTO separation from JPA entities; Secure endpoints with role checks.',
 20, 'server'),

(9, 'spring-boot-architecture', 'Spring Boot Enterprise Architecture', 'Programming', 'ADVANCED',
 'Domain-driven design boundaries, transactional concurrency controls, caching strategies, audit logging, and resilient external service integration.',
 'Implement clean hexagonal or layered architectures; Configure pessimistic and optimistic locking for high concurrency; Build comprehensive integration test suites with MockMvc and Testcontainers.',
 25, 'layers');

-- 4. Skill Prerequisites (Relational DAG)
INSERT INTO skill_prerequisites (skill_id, prerequisite_id) VALUES
-- REST API requires Java Fundamentals and SQL Design
(8, 1),
(8, 2),
-- JavaScript requires HTML/CSS
(6, 5),
-- Docker requires Linux Fundamentals and Git
(7, 4),
(7, 3),
-- Enterprise Spring Boot requires REST API Development
(9, 8);

-- 5. Practical Challenges
INSERT INTO challenges (id, slug, skill_id, title, difficulty, points, estimated_minutes, summary, description, instructions, submission_requirements, rubric_json, sample_input_output) VALUES
(1, 'console-expense-tracker', 1, 'Build a Console-Based Expense Tracker', 'BEGINNER', 50, 75,
 'Develop a modular CLI application in Java to track personal expenses with category categorization, summary statistics, and file persistence.',
 'Demonstrate mastery of Java core concepts, OOP separation of concerns, the Collections framework, and clean input validation by constructing an interactive expense manager.',
 '1. Create an Expense model with id, title, amount, category, and timestamp.\n2. Implement an ExpenseService with methods: addExpense, listExpenses, filterByCategory, and calculateSummary.\n3. Implement a repository layer that persists data to a local CSV or text file.\n4. Handle invalid user inputs gracefully without application crashes.\n5. Write unit tests covering business calculations.',
 'Provide a link to a public GitHub repository with a clear README.md, source code under src/main/java, and JUnit test cases under src/test/java. Optional supporting text explanation or screenshot attachment.',
 '[{"criterion":"OOP Design & Architecture","maxPoints":15,"guidance":"Clean separation between model, service, repository, and CLI interface."},{"criterion":"Collections & Data Structures","maxPoints":10,"guidance":"Proper use of List, Map, or Stream API for filtering and summary calculations."},{"criterion":"Error Handling & Validation","maxPoints":15,"guidance":"No uncaught exceptions, positive amounts only, graceful recovery on malformed input."},{"criterion":"Code Quality & Tests","maxPoints":10,"guidance":"Meaningful naming, comments where appropriate, and at least 3 passing JUnit tests."}]',
 'Input:\n> add 45.50 Groceries "Weekly vegetables"\n> summary\n\nOutput:\nTotal Expenses: $45.50 across 1 record.\nCategories: [Groceries: $45.50 (100%)]'),

(2, 'student-record-manager', 1, 'Student Record Manager with File Storage', 'BEGINNER', 60, 90,
 'Create an object-oriented Java application managing student records, grade calculations, and GPA ranking.',
 'Showcase Java object relationships, custom exceptions, sorting mechanisms with Comparable/Comparator, and file I/O operations.',
 '1. Model Student and CourseGrade entities.\n2. Implement GPA calculation algorithms based on credit hours.\n3. Implement sorting by GPA descending and student ID ascending.\n4. Persist and restore student records to/from disk.\n5. Implement custom validation for course grades (0.0 - 4.0 scale).',
 'GitHub repository containing the complete Java project with build file (Maven/Gradle) and instructions to compile and run.',
 '[{"criterion":"Data Modeling","maxPoints":15,"guidance":"Proper encapsulation, immutability where appropriate, equals/hashCode."},{"criterion":"Sorting & Algorithms","maxPoints":15,"guidance":"Clean implementation of Comparator and sorting logic."},{"criterion":"File Persistence & Safety","maxPoints":15,"guidance":"Safe resource handling with try-with-resources; atomic file writes."},{"criterion":"Testing","maxPoints":15,"guidance":"Automated tests verifying calculation accuracy."}]',
 'Sample Output:\nTop Students:\n1. Elena Vasquez (ID: 1042) - GPA: 3.92 (Credits: 45)\n2. Marcus Kim (ID: 1018) - GPA: 3.85 (Credits: 60)'),

(3, 'ecommerce-relational-schema', 2, 'Design a Normalized E-Commerce Schema', 'BEGINNER', 75, 90,
 'Design and script a third-normal-form (3NF) relational database schema for an online marketplace with inventory and orders.',
 'Apply rigorous normalization rules to design tables for users, products, categories, orders, order items, inventory ledger, and payments.',
 '1. Draft an Entity-Relationship design conforming to 3NF.\n2. Write DDL scripts with explicit PRIMARY KEY, FOREIGN KEY, and UNIQUE constraints.\n3. Include appropriate INDEX definitions for high-frequency search queries.\n4. Write sample DML queries demonstrating a customer checkout join and monthly sales aggregation.\n5. Provide an explanation of normalization choices.',
 'DDL and DML SQL files, plus a clear Markdown explanation of table design rationale and indexing decisions.',
 '[{"criterion":"Relational Normalization","maxPoints":25,"guidance":"Strict adherence to 3NF with no transitive or partial dependencies."},{"criterion":"Integrity Constraints & Indexing","maxPoints":20,"guidance":"Appropriate cascade rules, not-null constraints, and indexes on foreign keys."},{"criterion":"Complex Query Formulation","maxPoints":15,"guidance":"Accurate multi-table joins and aggregation query syntax."},{"criterion":"Documentation & Rationale","maxPoints":15,"guidance":"Well-reasoned trade-offs for performance and normalization."}]',
 'Query:\nSELECT p.name, SUM(oi.quantity) as sold\nFROM order_items oi\nJOIN products p ON oi.product_id = p.id\nGROUP BY p.id ORDER BY sold DESC LIMIT 5;'),

(4, 'transaction-inventory-operations', 2, 'Write Transaction-Safe Inventory Operations', 'INTERMEDIATE', 80, 80,
 'Implement SQL stored procedures or transactional Java operations that safely manage concurrent inventory decrements.',
 'Prove understanding of database isolation levels, pessimistic vs optimistic locking, and rollback handling during stockout conditions.',
 '1. Create a schema with inventory items and order reservations.\n2. Write a transactional routine that checks available stock and decrements safely.\n3. Ensure rollback occurs if stock is insufficient or if payment fails.\n4. Demonstrate prevention of overselling under concurrent execution.',
 'SQL script or Java application repository demonstrating transactional boundaries, deadlock avoidance, and concurrency tests.',
 '[{"criterion":"ACID Compliance","maxPoints":25,"guidance":"Transaction rollback verified when stock is insufficient."},{"criterion":"Concurrency Control","maxPoints":25,"guidance":"Use of SELECT ... FOR UPDATE or version column to prevent race conditions."},{"criterion":"Edge Case Handling","maxPoints":15,"guidance":"Handles negative stock attempts, zero stock, and deadlocks."},{"criterion":"Evidence & Testing","maxPoints":15,"guidance":"Clear demonstration or test case proving safety under concurrency."}]',
 'Expected Result: Concurrent orders for the last 1 item result in exactly 1 success and 1 rollback with OutOfStockException.'),

(5, 'git-branching-rebase', 3, 'Git Branching, Rebase, and Conflict Resolution', 'BEGINNER', 45, 60,
 'Submit evidence of executing an interactive rebase, squashing commits, and resolving a simulated multi-contributor merge conflict.',
 'Demonstrate professional Git hygiene by structuring feature branches, squashing work-in-progress commits, and rebasing on top of an updated main branch.',
 '1. Create a Git repository with an initial commit.\n2. Create two divergent feature branches modifying the same lines of code.\n3. Interactively rebase one branch onto main, squashing 3 experimental commits into 1 descriptive commit.\n4. Resolve the resulting merge conflict with careful diff verification.\n5. Submit a link to the published GitHub repository showing clean git log graph.',
 'Public GitHub repository URL with commit history showing branches, squashed commits, and resolved merge.',
 '[{"criterion":"Commit History Cleanliness","maxPoints":15,"guidance":"Meaningful imperative commit messages, no merge commit clutter on rebased branch."},{"criterion":"Conflict Resolution Accuracy","maxPoints":15,"guidance":"Correct syntax preserved from both conflicting branches without syntax errors."},{"criterion":"Branch Strategy","maxPoints":15,"guidance":"Evidence of clear feature branch separation."}]',
 'Git log graph verification:\n* feat(auth): add secure email otp verification\n* Initial repository setup'),

(6, 'linux-automation-script', 4, 'Practical Command-Line Linux Automation', 'BEGINNER', 55, 60,
 'Write a POSIX-compliant Bash automation script to monitor disk space, archive rotating log files, and report system health.',
 'Show proficiency with standard Linux utilities (grep, awk, sed, find, tar, systemctl) and defensive shell scripting practices.',
 '1. Write a script `syshealth.sh` with `set -euo pipefail`.\n2. Accept arguments for target log directory and disk threshold percentage.\n3. Archive logs older than 7 days into a compressed .tar.gz bundle.\n4. Output formatted health status to stdout and optionally send an alert webhook or log.',
 'GitHub repository containing the Bash script with execution permissions, documentation, and a test run log.',
 '[{"criterion":"Defensive Shell Scripting","maxPoints":20,"guidance":"Uses set -euo pipefail, quotes variables, checks command existence."},{"criterion":"Utility Mastery","maxPoints":20,"guidance":"Appropriate usage of find, tar, df, and awk without unnecessary subshells."},{"criterion":"Documentation & Portability","maxPoints":15,"guidance":"Runs on standard Debian/Ubuntu and Alpine distributions."}]',
 'Sample Run:\n$ ./syshealth.sh /var/log 80\n[OK] Disk usage: 62% (Threshold: 80%)\n[INFO] Archived 14 log files into /var/log/archives/2026-10-19.tar.gz'),

(7, 'responsive-design-system', 5, 'Component Library with Modern Semantic CSS', 'BEGINNER', 65, 90,
 'Build a lightweight, accessible UI component kit using semantic HTML5 and vanilla CSS custom properties (variables) with dark/light themes.',
 'Demonstrate mastery of modern CSS layout techniques (Grid, Flexbox), accessibility guidelines (WCAG AA), responsive breakpoints, and tokenized design systems without CSS frameworks.',
 '1. Define CSS custom properties for colors, spacing, typography, and elevations.\n2. Create components: button variants (primary, secondary, danger), card, modal, form input, and responsive data table.\n3. Implement a dark/light mode toggle with smooth CSS transitions.\n4. Ensure 100% responsive behavior from 320px mobile to 1920px desktop viewports.\n5. Verify keyboard focus rings and color contrast compliance.',
 'GitHub Pages / Repository link or uploaded ZIP archive containing the HTML demo file and modular CSS stylesheets.',
 '[{"criterion":"Design Token System","maxPoints":20,"guidance":"Organized CSS variables for palette, typography scale, and shadows."},{"criterion":"Responsive Layout & Grid","maxPoints":20,"guidance":"No horizontal scrollbars on mobile; fluid layout transitions."},{"criterion":"Accessibility & Focus States","maxPoints":15,"guidance":"Visible focus indicators, proper ARIA labels, semantic landmark elements."},{"criterion":"Code Modularity","maxPoints":10,"guidance":"Clean class naming conventions and modular stylesheet organization."}]',
 'Verification: Tested at 375px, 768px, and 1280px widths with dark mode persistence.'),

(8, 'containerize-java-app', 7, 'Containerize a Production Java Application', 'INTERMEDIATE', 80, 75,
 'Author an optimized, secure multi-stage Dockerfile for a Spring Boot application and coordinate it with MySQL using Docker Compose.',
 'Prove knowledge of non-root container users, multi-stage caching, JVM memory tuning in containers, and health check definitions.',
 '1. Create a multi-stage Dockerfile with eclipse-temurin or openjdk.\n2. Use a dedicated build stage to cache dependencies.\n3. Run the runtime container as a non-privileged user (e.g., appuser, UID 10001).\n4. Configure JVM container-aware memory limits (-XX:MaxRAMPercentage=75.0).\n5. Write a docker-compose.yml file linking the application with a MySQL database and health checks.',
 'Repository URL or archive containing Dockerfile, docker-compose.yml, and application source code.',
 '[{"criterion":"Multi-Stage Optimization","maxPoints":25,"guidance":"Separates build tools from runtime; image size minimized."},{"criterion":"Container Security","maxPoints":25,"guidance":"Runs as non-root user, no unnecessary packages installed."},{"criterion":"Compose Coordination & Health Checks","maxPoints":20,"guidance":"Health checks verify database readiness before application starts."},{"criterion":"JVM Configuration","maxPoints":10,"guidance":"Explicit container memory limits configured."}]',
 'Dockerfile size check: Final runtime image under 280MB vs 800MB unoptimized.'),

(9, 'secure-restful-api', 8, 'Production-Ready Secure REST API', 'INTERMEDIATE', 100, 120,
 'Design and implement a complete RESTful API with Spring Boot featuring DTO validation, error structures, pagination, and role-based access control.',
 'Demonstrate mastery of production backend engineering practices: controller-service-repository patterns, global exception handling, pagination, and security.',
 '1. Implement endpoints for a domain resource (e.g. Courses, Projects, or Tasks).\n2. Enforce input validation using Jakarta annotations (@NotBlank, @Size, @Min).\n3. Implement a GlobalExceptionHandler returning a standardized RFC-7807 problem detail response.\n4. Support page, size, and sort query parameters.\n5. Protect write operations with role-based authorization.',
 'GitHub repository containing Spring Boot application with integration tests validating controller endpoints.',
 '[{"criterion":"RESTful Semantics & API Design","maxPoints":25,"guidance":"Correct HTTP verbs, status codes (201, 204, 400, 404, 422), and resource URIs."},{"criterion":"Input Validation & Error Responses","maxPoints":25,"guidance":"Standardized error payload with field-level constraint violations."},{"criterion":"Pagination & Query Handling","maxPoints":25,"guidance":"Spring Data Pageable integration with page metadata in responses."},{"criterion":"Security & Authorization","maxPoints":25,"guidance":"Access controls enforced at endpoint or service level."}]',
 'Response for 400 Bad Request:\n{\n  "status": 400,\n  "error": "Validation Failed",\n  "errors": [{"field": "title", "message": "Title cannot be blank"}]\n}');

-- 6. Challenge Prerequisites
INSERT INTO challenge_prerequisites (challenge_id, prerequisite_skill_id) VALUES
(1, 1), -- expense tracker requires Java
(2, 1), -- student manager requires Java
(3, 2), -- ecommerce schema requires SQL
(4, 2), -- transaction inventory requires SQL
(5, 3), -- git branching requires Git
(6, 4), -- linux automation requires Linux
(7, 5), -- responsive design requires HTML/CSS
(8, 7), -- containerize requires Docker
(8, 4), -- containerize requires Linux
(9, 8); -- secure REST API requires REST API Development

-- 7. User Skills (Demonstrating progression for Devon and Priya)
INSERT INTO user_skills (user_id, skill_id, status, progress_percent, started_at, completed_at) VALUES
(4, 1, 'VERIFIED', 100, DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 3, 'VERIFIED', 100, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, 2, 'IN_PROGRESS', 65, DATE_SUB(NOW(), INTERVAL 7 DAY), NULL),
(4, 8, 'IN_PROGRESS', 25, DATE_SUB(NOW(), INTERVAL 3 DAY), NULL),
(5, 5, 'VERIFIED', 100, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
(5, 6, 'IN_PROGRESS', 50, DATE_SUB(NOW(), INTERVAL 10 DAY), NULL);

-- 8. Submissions (Realistic history)
INSERT INTO submissions (id, challenge_id, user_id, status, repo_url, project_url, explanation, assigned_mentor_id, submitted_at, created_at, updated_at) VALUES
(1, 1, 4, 'APPROVED', 'https://github.com/devonmiles/java-expense-tracker', 'https://devonmiles.github.io/java-expense-tracker', 
 'Implemented a full console expense tracker featuring CSV persistence, category aggregation via Streams API, and comprehensive JUnit 5 tests.', 
 2, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),

(2, 5, 4, 'APPROVED', 'https://github.com/devonmiles/git-workflow-evidence', NULL, 
 'Completed interactive rebase of 4 experimental commits into a single conventional commit and cleanly resolved simulated merge conflict in main branch.', 
 3, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),

(3, 3, 4, 'UNDER_REVIEW', 'https://github.com/devonmiles/ecommerce-relational-design', NULL, 
 'Drafted complete 3NF schema for marketplace with orders, inventory, and order items. Added indexes on frequently joined foreign keys.', 
 2, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),

(4, 7, 5, 'APPROVED', 'https://github.com/priyapatel-tech/semantic-css-system', 'https://priyapatel-tech.github.io/semantic-css-system', 
 'Created an accessible design token system with CSS custom properties, responsive flexbox/grid layouts, and keyboard focus states.', 
 2, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY));

-- 9. Submission Reviews
INSERT INTO submission_reviews (id, submission_id, reviewer_id, decision, total_score, general_feedback, improvement_suggestions, is_public, reviewed_at) VALUES
(1, 1, 2, 'APPROVED', 48, 
 'Outstanding implementation. The separation between the CLI user interaction and the ExpenseService business logic is clean and follows SOLID principles. Exception handling is robust.', 
 'Consider using an embedded H2 database or SQLite instead of raw CSV persistence for future scalability.', 
 TRUE, DATE_SUB(NOW(), INTERVAL 10 DAY)),

(2, 2, 3, 'APPROVED', 44, 
 'Flawless demonstration of interactive rebase and commit history hygiene. The conflict resolution kept both features intact without regression.', 
 'Keep practicing signed commits with GPG keys for additional security proof in enterprise settings.', 
 TRUE, DATE_SUB(NOW(), INTERVAL 8 DAY)),

(4, 4, 2, 'APPROVED', 62, 
 'Impressive attention to accessibility and semantic markup. The color contrast ratios in both light and dark themes satisfy WCAG AA standards.', 
 'Consider adding fluid typography using CSS clamp() for even smoother scaling across intermediate tablet viewports.', 
 TRUE, DATE_SUB(NOW(), INTERVAL 15 DAY));

-- 10. Review Scores (Rubric Breakdown)
INSERT INTO review_scores (review_id, criterion_name, score, max_score, comments) VALUES
(1, 'OOP Design & Architecture', 15, 15, 'Excellent domain modeling and single-responsibility services.'),
(1, 'Collections & Data Structures', 10, 10, 'Stream API used effectively for summaries.'),
(1, 'Error Handling & Validation', 14, 15, 'Great validation; could add custom runtime exception.'),
(1, 'Code Quality & Tests', 9, 10, '4 solid unit tests covering calculation edge cases.'),

(2, 'Commit History Cleanliness', 15, 15, 'Clear imperative messages, squashed cleanly.'),
(2, 'Conflict Resolution Accuracy', 15, 15, 'Preserved logic correctly without losing imports.'),
(2, 'Branch Strategy', 14, 15, 'Well-structured feature branch and rebase target.'),

(4, 'Design Token System', 20, 20, 'Well-organized color, spacing, and typography tokens.'),
(4, 'Responsive Layout & Grid', 19, 20, 'Very responsive across all breakpoints.'),
(4, 'Accessibility & Focus States', 14, 15, 'Focus rings clearly visible and keyboard accessible.'),
(4, 'Code Modularity', 9, 10, 'Clean BEM-inspired naming convention.');

-- 11. Verified Skills
INSERT INTO verified_skills (user_id, skill_id, verified_by_submission_id, reviewer_id, verification_badge_code, verified_at) VALUES
(4, 1, 1, 2, 'VP-JAVA-9842A1', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 3, 2, 3, 'VP-GIT-4419B2', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(5, 5, 4, 2, 'VP-HTML-7731C3', DATE_SUB(NOW(), INTERVAL 15 DAY));

-- 12. Notifications
INSERT INTO notifications (user_id, title, message, type, link, is_read, created_at) VALUES
(4, 'Skill Verified: Java Fundamentals', 'Congratulations! Your submission for Console Expense Tracker was approved by Sarah Chen with a score of 48/50.', 'SKILL_VERIFIED', '/verified.html', TRUE, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 'Skill Verified: Git & GitHub', 'Your evidence for Git Branching & Rebase was verified by Marcus Vance with a score of 44/45.', 'SKILL_VERIFIED', '/verified.html', TRUE, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, 'Submission Under Review', 'Your submission for Design a Normalized E-Commerce Schema has been assigned to Sarah Chen for evaluation.', 'MENTOR_ASSIGNED', '/submissions.html', FALSE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 'Recommended Next Challenge', 'Since you verified Java and Git, you are now eligible for "REST API Development with Spring Boot"!', 'CHALLENGE_RECOMMENDATION', '/challenges.html', FALSE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 'Skill Verified: Modern HTML5 & CSS', 'Your responsive component library was verified by Sarah Chen with a score of 62/65.', 'SKILL_VERIFIED', '/verified.html', TRUE, DATE_SUB(NOW(), INTERVAL 15 DAY));

-- 13. User Activity
INSERT INTO user_activity (user_id, action_type, description, reference_id, created_at) VALUES
(4, 'SUBMISSION_APPROVED', 'Earned verified skill badge for Java Fundamentals (Score: 48/50)', 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 'SUBMISSION_APPROVED', 'Earned verified skill badge for Git & GitHub (Score: 44/45)', 2, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, 'CHALLENGE_SUBMITTED', 'Submitted evidence for Design a Normalized E-Commerce Schema', 3, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 'SUBMISSION_APPROVED', 'Earned verified skill badge for Modern HTML5 & Semantic CSS (Score: 62/65)', 4, DATE_SUB(NOW(), INTERVAL 15 DAY));
