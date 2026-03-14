
CREATE TABLE users(
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name ENUM('ADMIN', 'AUTHOR', 'REVIEWER', 'READER') NOT NULL UNIQUE
);

CREATE TABLE user_roles(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY(user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY(role_id) REFERENCES roles(id)
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
    content TEXT,
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
    version_id INT NOT NULL,
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

CREATE TABLE auditlog(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    action VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auditlog_user
        FOREIGN KEY(user_id) REFERENCES users(id)
);

INSERT INTO roles(name)
VALUES
    ('ADMIN'),
    ('AUTHOR'),
    ('REVIEWER'),
    ('READER');

DELIMITER //

/* само одобрена версия може да стане активна */
CREATE TRIGGER trg_active_only_approved_insert 
BEFORE INSERT ON document_versions
FOR EACH ROW
BEGIN
    IF NEW.is_active = TRUE AND NEW.status <> 'approved' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Only approved versions can be active.';
    END IF;
END//

CREATE TRIGGER trg_active_only_approved_update
BEFORE UPDATE ON document_versions
FOR EACH ROW
BEGIN
    IF NEW.is_active = TRUE AND NEW.status <> 'approved' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Only approved versions can be active.';
    END IF;
END//

/* новите версии се създават от последната активна версия */
CREATE TRIGGER trg_parent_must_be_active 
BEFORE INSERT ON document_versions
FOR EACH ROW
BEGIN
    IF NEW.parent_version_id IS NOT NULL THEN
        IF NOT EXISTS (
            SELECT 1
            FROM document_versions
            WHERE id = NEW.parent_version_id
              AND document_id = NEW.document_id
              AND is_active = TRUE
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'New version must be created from the active version.';
        END IF;
    END IF;
END//

DELIMITER ;