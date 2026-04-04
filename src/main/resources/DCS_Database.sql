SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS version_comments;
DROP TABLE IF EXISTS version_reviews;
DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS document_versions;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE roles(
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      name ENUM('ADMIN', 'AUTHOR', 'REVIEWER', 'READER') NOT NULL UNIQUE
);

CREATE TABLE users(
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE documents(
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_by INT NOT NULL,
    active_version_id INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_documents_user
        FOREIGN KEY(created_by) REFERENCES users(id)
);

CREATE TABLE document_versions(
    id INT AUTO_INCREMENT PRIMARY KEY,
    document_id INT NOT NULL,
    version_number INT NOT NULL,
    parent_version_id INT NULL,
    status ENUM('DRAFT', 'IN_REVIEW', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'DRAFT',
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_by INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content LONGBLOB NOT NULL,
    extension VARCHAR(20),
    change_summary TEXT,
    CONSTRAINT uq_document_version UNIQUE(document_id, version_number),
    CONSTRAINT fk_versions_document
        FOREIGN KEY(document_id) REFERENCES documents(id),
    CONSTRAINT fk_versions_parent
        FOREIGN KEY(parent_version_id) REFERENCES document_versions(id),
    CONSTRAINT fk_versions_user
        FOREIGN KEY(created_by) REFERENCES users(id)
);

ALTER TABLE documents
    ADD CONSTRAINT fk_documents_active_version
        FOREIGN KEY(active_version_id) REFERENCES document_versions(id);

CREATE TABLE version_reviews(
    id INT AUTO_INCREMENT PRIMARY KEY,
    version_id INT NOT NULL UNIQUE,
    reviewer_id INT NOT NULL,
    decision ENUM('APPROVED', 'REJECTED') NOT NULL,
    comment TEXT,
    reviewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_version
        FOREIGN KEY(version_id) REFERENCES document_versions(id),
    CONSTRAINT fk_reviews_user
        FOREIGN KEY(reviewer_id) REFERENCES users(id)
);

CREATE TABLE version_comments(
    id INT AUTO_INCREMENT PRIMARY KEY,
    version_id INT NOT NULL,
    author_id INT NOT NULL,
    body TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_version
        FOREIGN KEY(version_id) REFERENCES document_versions(id),
    CONSTRAINT fk_comments_user
        FOREIGN KEY(author_id) REFERENCES users(id)
);

CREATE TABLE audit_logs(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    action ENUM('CREATE', 'UPDATE', 'DELETE', 'APPROVE', 'REJECT', 'ACTIVATE', 'DEACTIVATE', 'LOGIN') NOT NULL,
    entity_type ENUM('DOCUMENT', 'DOCUMENT_VERSION', 'COMMENT', 'USER', 'VERSION_REVIEW') NOT NULL,
    entity_id INT NOT NULL,
    details TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user
        FOREIGN KEY(user_id) REFERENCES users(id)
);

ALTER TABLE audit_logs
    DROP FOREIGN KEY fk_audit_logs_user;

ALTER TABLE audit_logs
    MODIFY user_id INT NULL;

ALTER TABLE audit_logs
    ADD CONSTRAINT fk_audit_logs_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE SET NULL;