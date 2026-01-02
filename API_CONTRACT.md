# AinBondhu API Contract

This document outlines the API endpoints for the AinBondhu backend application. It is intended to guide the implementation of the Flutter frontend.

**Base URL:** `http://<server-host>/api/v1` (unless otherwise noted)

## 1. Authentication
**Base Path:** `/api/v1/auth`

### 1.1. Register Client
*   **Method:** `POST`
*   **Path:** `/register/client`
*   **Description:** Registers a new client user.
*   **Request Body:** `RegisterRequest`
    ```json
    {
      "phoneNumber": "string",
      "fullNameBn": "string",
      "latitude": 0.0,
      "longitude": 0.0
    }
    ```
*   **Response:** `UserDto`
    ```json
    {
      "id": "uuid-string",
      "phoneNumber": "string",
      "fullNameBn": "string",
      "role": "CLIENT"
    }
    ```

### 1.2. Register Lawyer
*   **Method:** `POST`
*   **Path:** `/register/lawyer`
*   **Description:** Registers a new lawyer user.
*   **Request Body:** `RegisterRequest`
    ```json
    {
      "phoneNumber": "string",
      "fullNameBn": "string",
      "latitude": 0.0,
      "longitude": 0.0,
      "barLicenseNumber": "string",
      "educationalBackground": "string",
      "experienceYears": 0
    }
    ```
*   **Response:** `LawyerDto`
    ```json
    {
      "id": "uuid-string",
      "phoneNumber": "string",
      "fullNameBn": "string",
      "barLicenseNumber": "string",
      "verificationStatus": "PENDING",
      "isOnline": false,
      "distance": 0.0,
      "averageRating": null,
      "totalReviews": 0,
      "totalCasesServed": 0
    }
    ```

### 1.3. Login
*   **Method:** `POST`
*   **Path:** `/login`
*   **Description:** Authenticates a user.
*   **Request Body:** `LoginRequest`
    ```json
    {
      "phoneNumber": "string"
    }
    ```
*   **Response:** `LoginResponse`
    ```json
    {
      "token": "jwt-token-string",
      "type": "Bearer",
      "role": "CLIENT|LAWYER|ADMIN"
    }
    ```

---

## 2. Lawyer Discovery
**Base Path:** `/api/v1/lawyers`

### 2.1. Get Nearby Lawyers
*   **Method:** `GET`
*   **Path:** `/nearby`
*   **Query Parameters:**
    *   `lat` (double): Client's latitude.
    *   `lon` (double): Client's longitude.
    *   `radius` (double, optional): Search radius in meters (default: 5000).
*   **Response:** List of `LawyerDto`
    ```json
    [
      {
        "id": "uuid-string",
        "phoneNumber": "string",
        "fullNameBn": "string",
        "barLicenseNumber": "string",
        "verificationStatus": "VERIFIED",
        "isOnline": true,
        "distance": 1200.5,
        "averageRating": 4.5,
        "totalReviews": 10,
        "totalCasesServed": 5
      }
    ]
    ```

### 2.2. Get Lawyer Details
*   **Method:** `GET`
*   **Path:** `/{id}`
*   **Path Parameters:**
    *   `id` (uuid): The Lawyer's ID.
*   **Response:** `LawyerDto` (same structure as above)

### 2.3. Update Online Status
*   **Method:** `PATCH`
*   **Path:** `/{id}/status`
*   **Path Parameters:**
    *   `id` (uuid): The Lawyer's ID.
*   **Query Parameters:**
    *   `isOnline` (boolean): `true` or `false`.
*   **Response:** `200 OK` (Empty body)

---

## 3. Case Requests (Client/Public)
**Base Path:** `/api/v1/cases`

### 3.1. Request a Lawyer
*   **Method:** `POST`
*   **Path:** `/`
*   **Query Parameters:**
    *   `clientId` (string): ID of the client.
    *   `lawyerId` (string): ID of the lawyer being requested.
*   **Response:** `CaseRequest`
    ```json
    {
      "id": "uuid-string",
      "clientId": "string",
      "lawyerId": "string",
      "status": "REQUESTED",
      "createdAt": "timestamp"
    }
    ```

### 3.2. Accept Case Request
*   **Method:** `PATCH`
*   **Path:** `/{id}/accept`
*   **Path Parameters:**
    *   `id` (uuid): The Case Request ID.
*   **Response:** `200 OK` (Empty body)

---

## 4. Case Management (Lawyer Restricted)
**Base Path:** `/api/lawyer/cases`
**Authorization:** Requires Bearer Token with `LAWYER` role.

### 4.1. Create Legal Case
*   **Method:** `POST`
*   **Path:** `/`
*   **Request Body:** `LegalCaseRequestDTO`
    ```json
    {
      "caseNumber": "string",
      "title": "string",
      "description": "string",
      "status": "OPEN",
      "filingDate": "YYYY-MM-DD",
      "nextHearingDate": "YYYY-MM-DDTHH:mm:ss",
      "courtName": "string",
      "clientId": "uuid-string"
    }
    ```
    *Status values: OPEN, IN_PROGRESS, HEARING_SCHEDULED, CLOSED, ARCHIVED*
*   **Response:** `LegalCaseResponseDTO`
    ```json
    {
      "id": "uuid-string",
      "caseNumber": "string",
      "title": "string",
      "description": "string",
      "status": "OPEN",
      "filingDate": "YYYY-MM-DD",
      "nextHearingDate": "YYYY-MM-DDTHH:mm:ss",
      "courtName": "string",
      "createdAt": "timestamp",
      "updatedAt": "timestamp",
      "lawyerId": "uuid-string",
      "clientId": "uuid-string",
      "clientName": "string"
    }
    ```

### 4.2. Get All Cases
*   **Method:** `GET`
*   **Path:** `/`
*   **Query Parameters:** `page`, `size` (standard pagination).
*   **Response:** `Page<LegalCaseResponseDTO>` (Paginated list)

### 4.3. Get Case Details
*   **Method:** `GET`
*   **Path:** `/{id}`
*   **Path Parameters:** `id` (uuid).
*   **Response:** `LegalCaseResponseDTO`

### 4.4. Update Case
*   **Method:** `PUT`
*   **Path:** `/{id}`
*   **Path Parameters:** `id` (uuid).
*   **Request Body:** `LegalCaseRequestDTO`
*   **Response:** `LegalCaseResponseDTO`

### 4.5. Add Case Note
*   **Method:** `POST`
*   **Path:** `/{id}/notes`
*   **Path Parameters:** `id` (uuid) of the case.
*   **Request Body:** `CaseNoteRequestDTO`
    ```json
    {
      "title": "string",
      "content": "string",
      "noteDate": "YYYY-MM-DDTHH:mm:ss",
      "attachmentUrl": "string"
    }
    ```
*   **Response:** `CaseNoteResponseDTO`
    ```json
    {
      "id": "uuid-string",
      "title": "string",
      "content": "string",
      "noteDate": "timestamp",
      "attachmentUrl": "string",
      "legalCaseId": "uuid-string"
    }
    ```

### 4.6. Get Case Notes
*   **Method:** `GET`
*   **Path:** `/{id}/notes`
*   **Response:** `List<CaseNoteResponseDTO>`

### 4.7. Upload Document
*   **Method:** `POST`
*   **Path:** `/{id}/documents`
*   **Content-Type:** `multipart/form-data`
*   **Form Data:**
    *   `file`: The file object.
*   **Response:** `CaseDocumentResponseDTO`
    ```json
    {
      "id": "uuid-string",
      "fileName": "string",
      "fileUrl": "string",
      "fileType": "string",
      "uploadedAt": "timestamp",
      "legalCaseId": "uuid-string"
    }
    ```

### 4.8. Get Documents
*   **Method:** `GET`
*   **Path:** `/{id}/documents`
*   **Response:** `List<CaseDocumentResponseDTO>`

### 4.9. Download Document
*   **Method:** `GET`
*   **Path:** `/documents/download/{fileName}`
*   **Response:** Binary file stream.

---

## 5. Reviews
**Base Path:** `/api/v1/reviews`

### 5.1. Create Review (Client Only)
*   **Method:** `POST`
*   **Path:** `/`
*   **Request Body:** `ReviewDto`
    ```json
    {
      "rating": 5, // 1-5
      "comment": "string",
      "lawyerId": "uuid-string",
      "caseId": "uuid-string"
    }
    ```
*   **Response:** `ReviewDto`

### 5.2. Get Reviews for Lawyer
*   **Method:** `GET`
*   **Path:** `/lawyer/{lawyerId}`
*   **Response:** `List<ReviewDto>`

---

## 6. Scenarios (Questionnaire)
**Base Path:** `/api/v1/scenarios`

### 6.1. Get Categories
*   **Method:** `GET`
*   **Path:** `/`
*   **Response:** List of `CategoryDto`
    ```json
    [
      {
        "id": "string",
        "nameBn": "string",
        "descriptionBn": "string"
      }
    ]
    ```

### 6.2. Get Questions
*   **Method:** `GET`
*   **Path:** `/{categoryId}/questions`
*   **Response:** List of `QuestionDto`
    ```json
    [
      {
        "id": "string",
        "textBn": "string",
        "order": 1,
        "options": ["string", "string"]
      }
    ]
    ```

---

## 7. Chat (Real-time & History)

### 7.1. WebSocket Connection
*   **Endpoint:** `/ws`
*   **Type:** STOMP over WebSocket
*   **Subscribe:** `/user/queue/messages` (To receive private messages)
*   **Send Destination:** `/app/chat`
*   **Payload:** `ChatMessageDto`
    ```json
    {
      "senderId": "string",
      "receiverId": "string",
      "content": "string",
      "timestamp": "string"
    }
    ```

### 7.2. Get Chat History (REST)
*   **Method:** `GET`
*   **Path:** `/api/v1/chat/history`
*   **Query Parameters:**
    *   `userId1` (string)
    *   `userId2` (string)
*   **Response:** `List<ChatMessageDto>`

---

## 8. Admin
**Base Path:** `/api/v1/admin`

### 8.1. Get Audit Logs
*   **Method:** `GET`
*   **Path:** `/audit-logs`
*   **Query Parameters:** `page`, `size`.
*   **Response:** `Page<AuditLog>`
    ```json
    {
        "content": [
            {
                "id": "uuid",
                "entityName": "string",
                "entityId": "string",
                "action": "string",
                "changedBy": "string",
                "changeTime": "timestamp",
                "details": "string"
            }
        ]
        ...
    }
    ```

### 8.2. Verify Lawyer
*   **Method:** `PATCH`
*   **Path:** `/lawyers/{id}/verify`
*   **Path Parameters:** `id` (uuid).
*   **Response:** `200 OK`
