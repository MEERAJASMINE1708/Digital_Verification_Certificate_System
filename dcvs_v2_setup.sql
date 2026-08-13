-- ============================================================
--  DCVS v2 — MySQL Setup Script
--  Run ONCE in MySQL Workbench before starting the app
-- ============================================================

CREATE DATABASE IF NOT EXISTS dcvs
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'dcvs_user'@'localhost'
  IDENTIFIED BY 'Dcvs@2026#Secure';

GRANT ALL PRIVILEGES ON dcvs.* TO 'dcvs_user'@'localhost';
FLUSH PRIVILEGES;

USE dcvs;

-- Users
CREATE TABLE IF NOT EXISTS users (
    user_id         INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    hashed_password VARCHAR(64)  NOT NULL,
    role            ENUM('ADMIN','ISSUER','VERIFIER') NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE
);

-- Courses (NEW)
CREATE TABLE IF NOT EXISTS courses (
    course_id    INT AUTO_INCREMENT PRIMARY KEY,
    course_name  VARCHAR(200) NOT NULL UNIQUE,
    category     VARCHAR(100) NOT NULL,
    description  TEXT,
    duration     VARCHAR(50),
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   DATETIME NOT NULL
);

-- Certificates
CREATE TABLE IF NOT EXISTS certificates (
    cert_id        VARCHAR(36)  PRIMARY KEY,
    recipient_name VARCHAR(200) NOT NULL,
    recipient_id   VARCHAR(100) NOT NULL,
    course_id      INT,
    course         VARCHAR(200) NOT NULL,
    issue_date     DATE         NOT NULL,
    expiry_date    DATE         NOT NULL,
    signature      TEXT         NOT NULL,
    cert_hash      VARCHAR(64)  NOT NULL,
    status         ENUM('ACTIVE','REVOKED','EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    issued_by      VARCHAR(100) NOT NULL,
    org_name       VARCHAR(200) NOT NULL DEFAULT 'DCVS Institute of Technology',
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE SET NULL
);

-- Audit logs
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id    INT AUTO_INCREMENT PRIMARY KEY,
    action    VARCHAR(100) NOT NULL,
    actor     VARCHAR(100) NOT NULL,
    target_id VARCHAR(100),
    details   VARCHAR(500),
    timestamp DATETIME NOT NULL
);

-- Seed admin (password: admin123)
INSERT IGNORE INTO users (username, hashed_password, role, active)
VALUES ('admin',
  '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
  'ADMIN', TRUE);

-- Seed default courses
INSERT IGNORE INTO courses (course_name, category, description, duration, active, created_at) VALUES
('Data Science',                'AI & Data',       'Comprehensive data analysis, statistics, and visualization techniques',  '6 Months',  TRUE, NOW()),
('Machine Learning',            'AI & Data',       'Supervised, unsupervised learning algorithms and model evaluation',       '6 Months',  TRUE, NOW()),
('Artificial Intelligence',     'AI & Data',       'AI fundamentals, neural networks, NLP and computer vision',              '8 Months',  TRUE, NOW()),
('Full Stack Web Development',  'Web Dev',         'React, Node.js, databases, REST APIs and cloud deployment',              '9 Months',  TRUE, NOW()),
('Python Programming',          'Programming',     'Python fundamentals, OOP, libraries and scripting',                      '3 Months',  TRUE, NOW()),
('Java Programming',            'Programming',     'Core Java, OOP, collections, JDBC and Spring basics',                   '4 Months',  TRUE, NOW()),
('Cloud Computing',             'Infrastructure',  'AWS, Azure, GCP fundamentals, deployment and DevOps',                    '5 Months',  TRUE, NOW()),
('Cyber Security',              'Security',        'Network security, ethical hacking, cryptography and compliance',         '6 Months',  TRUE, NOW()),
('Data Analytics',              'AI & Data',       'Business analytics, Power BI, Tableau and SQL reporting',                '4 Months',  TRUE, NOW()),
('Deep Learning',               'AI & Data',       'CNNs, RNNs, transformers and large language model fundamentals',         '6 Months',  TRUE, NOW());

-- Verify
SHOW TABLES;
SELECT * FROM courses;
SELECT * FROM users;
