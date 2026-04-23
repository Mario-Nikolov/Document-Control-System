# 📄 Document Control System

A full-stack system for managing documents, version control, reviews, and approvals.  
The backend is built with **Java + Spring Boot + MySQL**, and the frontend is built with **React (JavaScript)**.

---

## 📌 Overview

The **Document Control System** is designed to manage documents in an enterprise-like environment where version tracking, approval workflows, and audit logging are essential.

Each document can have multiple versions, which go through a lifecycle:

* Draft → Review → Approved/Rejected
* Only one version can be active at a time

---

## 🚀 Key Features

* 📁 Document creation and management
* 🔄 Full version control system
* 📝 Draft & publishing workflow
* ✅ Review system (Approve / Reject)
* 💬 Comments on document versions
* 🔐 Role-based access control
* 📊 Audit logging (who did what and when)
* 📄 Export documents to PDF
* 🔍 Text extraction from files (TXT, PDF, DOCX)
* 🆚 Version difference tracking

---

## 🌐 Frontend

The project also includes a frontend application built with **React (JavaScript)**.

### 📌 Frontend Features
- User authentication (JWT-based login)
- Document listing and viewing
- Display of document versions
- Rendering extracted text from files
- Interaction with backend API

---

### ⚙️ Running the Frontend

1. Navigate to the frontend folder:
```bash
cd frontend
2. Install dependencies:
npm install
3.Start the application:
npm run dev
4. Open in browser:
http://localhost:5173

```
## 🏗️ System Architecture

The application follows a **layered architecture**:

```
Controller → Service → Repository → Database
```

### 🔹 Layers Description

* **Controller (Web Layer)**

    * Handles HTTP requests
    * Returns DTO responses
    * Works with JWT authentication

* **Service Layer**

    * Business logic
    * Validation and permissions
    * Workflow handling

* **Repository Layer**

    * Uses Spring Data JPA
    * Communicates with database

* **Domain Layer**

    * Entity models (mapped to DB tables)

* **DTO & Mapper Layer**

    * Converts Entity ↔ Response/Request objects

---

## 🧱 Tech Stack

* - Java 17
* - Spring Boot
* - Spring Data JPA
* - Spring Security
* - JWT (Authentication)
* - Hibernate ORM
* - MySQL
* - Maven
* - Lombok
* - Apache PDFBox
* - Apache POI
* - OpenPDF
* - java-diff-utils**
* - React (JavaScript)
* - Vite
* - Fetch API

---

## 🗄️ Database Design

### 👤 User

* id
* username
* email
* password
* role_id

### 🛡️ Role

* ADMIN
* AUTHOR
* REVIEWER
* READER

### 📄 Document

* id
* title
* description
* created_by
* active_version_id
* created_at
* updated_at

### 📑 DocumentVersion

* id
* document_id
* version_number
* parent_version_id
* status
* is_active
* content (LONGBLOB)
* extension
* change_summary
* created_by
* created_at

### 💬 Comment

* id
* version_id
* author_id
* body
* created_at

### ✅ Review

* id
* version_id
* reviewer_id
* decision (APPROVED / REJECTED)
* comment
* reviewed_at

### 📜 AuditLog

* id
* user_id
* action
* entity_type
* entity_id
* details
* created_at

---

## 🔄 Document Lifecycle

```
DRAFT → IN_REVIEW → ACTIVE
           ↓
        REJECTED
```

### Rules:

* Only one version can be `ACTIVE`
* When new version is approved:

    * Old active → `OLD_VERSION`
    * New version → `ACTIVE`
* Rejected versions are not active

---

## 🔐 Authentication & Authorization

* Uses **JWT (JSON Web Token)**
* Every request requires:

```
Authorization: Bearer <token>
```

### Roles:

| Role     | Permissions             |
| -------- | ----------------------- |
| ADMIN    | Full access             |
| AUTHOR   | Create & edit documents |
| REVIEWER | Review versions         |
| READER   | Read-only access        |

---

## 📡 API Endpoints

### 🔑 Authentication

```
POST /auth/login
```

---

### 📄 Documents

```
POST   /documents
GET    /documents
GET    /documents/{id}
DELETE /documents/{id}
```

---

### 📑 Versions

```
POST /versions
GET  /documents/{id}/versions
GET  /documents/{id}/versions/active
GET  /documents/{id}/versions/latest
```

---

### ✅ Reviews

```
POST /reviews
```

---

### 💬 Comments

```
POST /comments
GET  /comments/version/{id}
```

---

### 📊 Audit Logs (Admin only)

```
GET /audit-logs
```

---

## 📦 File Handling

* Files are stored as **byte[] (LONGBLOB)** in the database
* Supported formats:

    * `.txt`
    * `.pdf`
    * `.docx`

### Text Extraction:

* TXT → Plain text
* PDF → Apache PDFBox
* DOCX → Apache POI

---

## 📄 PDF Export

* Extracts text from active version
* Generates PDF using OpenPDF
* Returns downloadable file

---

## 🆚 Version Comparison

* Uses **java-diff-utils**
* Compares two versions line-by-line
* Outputs:

    * Added lines
    * Removed lines
    * Changed lines

---

## ⚙️ Setup & Installation

### 1. Clone the repository

```bash
git clone https://github.com/your-repo/document-control-system.git
cd document-control-system
```

---

### 2. Configure MySQL

Create database:

```sql
CREATE DATABASE documentVersionSystem;
```

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/documentVersionSystem
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

---

### 3. Run the project

```bash
./mvnw spring-boot:run
```

---

### 4. Test with Postman

Add header:

```
Authorization: Bearer <your_token>
```

---

## 📁 Project Structure

```
src/main/java/com/logiclab/documentcontrolsystem
│
├── domain        → Entities
├── repository    → JPA Repositories
├── service       → Business Logic
├── web           → Controllers
├── dto           → Request/Response DTOs
├── mapper        → Mapping classes
├── config        → Security & Config
└── exception     → Custom Exceptions
```

---


## 🧠 Business Rules

Some important rules in the system are:

1. A document can have many versions
2. A document can have only one active version
3. A version must go through review before becoming active
4. Only authorized users can review or approve versions
5. Comments belong to a specific document version
6. Audit logs should track critical actions
7. File extension must be present for text extraction
8. Binary content must be processed according to its file type

---

## 📌 Future Improvements

* ☁️ File storage (AWS S3 / Cloud)
* 🔔 Notifications system
* 📊 Dashboard & analytics
* 🔍 Full-text search

---

## 👥 Team

Developed by **LogicLab**.

Repository:
- `Mario-Nikolov/Document-Control-System`

