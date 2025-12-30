# Case Management System Implementation Plan

## 1. Introduction
The Case Management System is a new module designed to empower lawyers using the AinBondhu platform to manage their legal cases efficiently. This system will allow lawyers to create digital records of their cases, track their progress, manage clients, and maintain a timeline of case-related events and notes.

## 2. Objectives
- Enable lawyers to create and maintain digital case files.
- Provide a structured way to track case details, including court information and hearing dates.
- Facilitate the recording of case notes and updates.
- Link cases to specific clients within the AinBondhu ecosystem.

## 3. Scope
The initial implementation will focus on the core backend infrastructure required to support case management.
- Database schema updates (New Entities).
- REST API development for case CRUD operations.
- Service layer logic for business rules.
- Security integration to ensure data privacy.

## 4. Domain Modeling

### 4.1 New Entities

#### `LegalCase`
Represents a legal case file managed by a lawyer.
- **Table Name**: `legal_cases`
- **Attributes**:
    - `id` (UUID, PK)
    - `caseNumber` (String, unique per court/year ideally, but flexible here)
    - `title` (String, e.g., "Rahim vs State")
    - `description` (String, Text)
    - `status` (Enum: `OPEN`, `IN_PROGRESS`, `HEARING_SCHEDULED`, `CLOSED`, `ARCHIVED`)
    - `filingDate` (LocalDate)
    - `nextHearingDate` (LocalDateTime)
    - `courtName` (String)
    - `createdAt` (LocalDateTime)
    - `updatedAt` (LocalDateTime)
- **Relationships**:
    - `ManyToOne` `Lawyer` (The lawyer managing the case)
    - `ManyToOne` `User` (The Client, optional if the client is not on the app, but recommended)

#### `CaseNote`
Represents an update, note, or event in the case timeline.
- **Table Name**: `case_notes`
- **Attributes**:
    - `id` (UUID, PK)
    - `title` (String)
    - `content` (String, Text)
    - `noteDate` (LocalDateTime, defaults to now)
    - `attachmentUrl` (String, optional link to a document/image)
- **Relationships**:
    - `ManyToOne` `LegalCase`

### 4.2 Enums
- `CaseStatus`: `OPEN`, `IN_PROGRESS`, `HEARING_SCHEDULED`, `CLOSED`, `ARCHIVED`

## 5. API Design
Base Path: `/api/v1/cases` (or `/api/lawyer/cases` depending on existing convention)

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| POST | `/` | Create a new legal case | Lawyer |
| GET | `/` | Get all cases for the authenticated lawyer | Lawyer |
| GET | `/{id}` | Get specific case details | Lawyer (Owner) |
| PUT | `/{id}` | Update case details (status, hearing date) | Lawyer (Owner) |
| POST | `/{id}/notes` | Add a note to a case | Lawyer (Owner) |
| GET | `/{id}/notes` | Get timeline/notes for a case | Lawyer (Owner) |

## 6. Implementation Steps

### Step 1: Domain Layer Implementation
- Create `CaseStatus` enum.
- Create `LegalCase` entity with JPA annotations.
- Create `CaseNote` entity with JPA annotations.
- Define Repositories: `LegalCaseRepository`, `CaseNoteRepository`.

### Step 2: DTO Creation
- `LegalCaseRequestDTO`: For creating/updating cases.
- `LegalCaseResponseDTO`: For viewing case details.
- `CaseNoteRequestDTO`: For adding notes.
- `CaseNoteResponseDTO`: For viewing notes.

### Step 3: Service Layer
- `CaseManagementService`:
    - `createCase(Lawyer lawyer, LegalCaseRequestDTO request)`
    - `getLawyerCases(Lawyer lawyer)`
    - `getCaseDetails(UUID caseId, Lawyer lawyer)`
    - `updateCase(UUID caseId, Lawyer lawyer, LegalCaseRequestDTO request)`
    - `addNote(UUID caseId, Lawyer lawyer, CaseNoteRequestDTO note)`

### Step 4: Controller Layer
- `CaseManagementController`: Implement the REST endpoints defined in section 5.
- Apply Security checks (ensure `Principal` is a `LAWYER`).

### Step 5: Testing & Validation
- Unit tests for Service layer.
- Integration tests for Repositories.
- API testing.

## 7. Security Considerations
- **Authentication**: JWT Token must be present.
- **Authorization**: Endpoints restricted to `ROLE_LAWYER`.
- **Data Ownership**: Service layer must verify that the `LegalCase` belongs to the requesting `Lawyer` before allowing read/write operations (IDOR prevention).
