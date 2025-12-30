# AinBondhu - Backend Architecture Document

## 1. System Overview
**AinBondhu** is a mobile application that connects citizens with legal professionals (lawyers) based on their location and specific legal needs. The platform allows users (clients) to report issues via a guided questionnaire, finds nearby lawyers, and facilitates real-time communication. The entire application content is in Bangla.

The backend is built using **Spring Boot**, providing a robust, scalable, and secure API for the Flutter mobile app.

## 2. Technology Stack

| Component | Technology | Description |
|-----------|------------|-------------|
| **Language** | Java 17+ / Kotlin | Core backend language. |
| **Framework** | Spring Boot 3.x | Main application framework. |
| **Database** | PostgreSQL | Relational database with strong JSON & geospatial support. |
| **Geospatial** | PostGIS | PostgreSQL extension for location-based queries (finding nearby lawyers). |
| **Caching** | Redis | Caching frequently accessed data (metadata, questions) and session management. |
| **Messaging/Chat** | WebSocket (STOMP) | Real-time bi-directional communication for chat. |
| **Authentication** | Spring Security + JWT | Stateless authentication. |
| **OTP Service** | Firebase Auth / Twilio | Phone number verification. |
| **Storage** | AWS S3 / MinIO | Storing documents (licenses, user files). |
| **Search** | Hibernate Search / Elasticsearch | (Optional) Advanced text search for lawyers/cases. |

## 3. System Architecture
We will follow a **Modular Monolith** architecture. This allows for clear separation of concerns while keeping deployment simple initially. It can be easily broken into microservices later if scaling requires.

### High-Level Layers
1.  **API Layer (Controllers):** Handles HTTP requests, validation, and serialization.
2.  **Service Layer (Business Logic):** Contains the core logic for matching, scenarios, and verification.
3.  **Data Access Layer (Repositories):** Interacts with PostgreSQL/PostGIS.
4.  **Integration Layer:** Handles 3rd party APIs (SMS, Cloud Storage).

## 4. Core Modules

### 4.1. Authentication & User Management
*   **Features:**
    *   Login/Register via Phone Number + OTP.
    *   Role-based access: `ROLE_CLIENT`, `ROLE_LAWYER`, `ROLE_ADMIN`.
    *   Profile management (Name, Photo, Address).
*   **Flow:**
    1.  App sends Phone No -> Backend requests OTP provider.
    2.  User enters OTP -> Backend verifies -> Issues JWT.

### 4.2. Lawyer Management
*   **Features:**
    *   Registration with specific details (Education, Experience, Pricing, Lawyer Level).
    *   **Bar License Verification:** Upload license photo/number -> Admin verification (manual or OCR integrated).
    *   Status: `PENDING`, `VERIFIED`, `REJECTED`.
    *   Availability toggle (Online/Offline for "Uber-like" requests).

### 4.3. Client & Case Scenarios
*   **Features:**
    *   **Scenario Selection:** Dynamic list of categories (Divorce, GD, Tax, Domestic Violence).
    *   **Questionnaire Engine:** Based on category, ask sequential questions to narrow down the problem.
    *   *Localization:* All questions and options stored in Bangla.
*   **Data Structure:**
    *   `Category` -> `Question` -> `Options` -> `NextQuestion`.

### 4.4. Location & Matching Service
*   **Features:**
    *   **Geospatial Search:** "Find lawyers within X km radius of Client".
    *   **Matching Algorithm:** Filters based on:
        *   Location (Distance).
        *   Specialization (Matches Case Category).
        *   Availability (Online status).
        *   Pricing/Level.
*   **Client View:** Map interface showing lawyer pins.
*   **Lawyer View:** List of nearby case requests (Uber-style feed).

### 4.5. Connection & Chat
*   **Features:**
    *   **Request/Accept:** Client requests lawyer OR Lawyer accepts open case.
    *   **Temporary Chat:** Once connected, a chat session opens.
    *   **Offline Agreement:** Option to "Close Case" or "Take Offline".
    *   **Review/Rating:** Post-interaction rating.
*   **Tech:** Spring Boot WebSocket with STOMP protocol. Chat history stored in DB.

## 5. Database Schema Design (Conceptual)

### Key Entities

**1. Users**
*   `id`: UUID
*   `phone_number`: String (Unique)
*   `role`: Enum (CLIENT, LAWYER)
*   `full_name_bn`: String (Bangla Name)
*   `location`: Geometry(Point)

**2. Lawyers (extends Users)**
*   `user_id`: FK
*   `bar_license_number`: String
*   `verification_status`: Enum
*   `specializations`: List<String>
*   `hourly_rate`: Decimal
*   `is_online`: Boolean

**3. Cases (Case Requests)**
*   `id`: UUID
*   `client_id`: FK
*   `category`: String (e.g., "Divorce")
*   `description`: Text (Generated from questionnaire)
*   `location`: Geometry(Point)
*   `status`: Enum (OPEN, ASSIGNED, CLOSED)

**4. Connections**
*   `id`: UUID
*   `case_id`: FK
*   `lawyer_id`: FK
*   `created_at`: Timestamp
*   `is_active`: Boolean

**5. ChatMessages**
*   `id`: Long
*   `connection_id`: FK
*   `sender_id`: FK
*   `content`: Text
*   `timestamp`: Timestamp

## 6. API Endpoints (Preview)

### Auth
*   `POST /api/v1/auth/send-otp`
*   `POST /api/v1/auth/verify-otp` -> returns JWT

### Client
*   `GET /api/v1/scenarios` -> List categories (Bangla)
*   `POST /api/v1/cases/calculate-need` -> Submit questionnaire answers
*   `GET /api/v1/lawyers/nearby?lat=...&lon=...&category=...`

### Lawyer
*   `POST /api/v1/lawyer/register` -> Upload License
*   `GET /api/v1/lawyer/feed` -> Open cases nearby

### Chat
*   `WS /ws/chat` -> WebSocket endpoint

## 7. Security & Compliance
*   **Data Privacy:** Sensitive legal data must be encrypted at rest if possible.
*   **SSL/TLS:** All API traffic over HTTPS.
*   **Validation:** Strict input validation to prevent injection.

## 8. Localization
*   The backend will serve dynamic content (Categories, Questions, Error Messages) in Bangla.
*   Database tables for configuration data will use UTF-8 encoding.
