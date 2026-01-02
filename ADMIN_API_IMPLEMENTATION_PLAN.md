# Admin API Implementation Plan

This document lists the required Admin APIs to fully implement the Admin Dashboard and management features.

## 1. User Management
APIs to manage all users (Clients, Lawyers, Admins).

- [ ] `GET /api/v1/admin/users`
    - **Description:** List all users with pagination.
    - **Filters:** Role (CLIENT, LAWYER, ADMIN), Status (Enabled/Disabled).
    - **Response:** Page<UserDTO>

- [ ] `GET /api/v1/admin/users/{id}`
    - **Description:** Get detailed information of a specific user.
    - **Response:** UserDetailsDTO

- [ ] `PATCH /api/v1/admin/users/{id}/status`
    - **Description:** Ban (disable) or Unban (enable) a user.
    - **Body:** `{ "enabled": boolean }`
    - **Response:** 200 OK

## 2. Lawyer Verification & Management
APIs specifically for managing Lawyer verification workflows.

- [ ] `GET /api/v1/admin/lawyers/pending`
    - **Description:** List all lawyers with `verificationStatus = PENDING`.
    - **Response:** Page<LawyerDTO>

- [ ] `GET /api/v1/admin/lawyers/{id}`
    - **Description:** Get lawyer details including documents (Bar License) for verification.
    - **Response:** LawyerDetailsDTO

- [ ] `PATCH /api/v1/admin/lawyers/{id}/verify`
    - **Description:** Approve lawyer verification (`verificationStatus = VERIFIED`).
    - **Response:** 200 OK (Existing, but check for improvements)

- [ ] `PATCH /api/v1/admin/lawyers/{id}/reject`
    - **Description:** Reject lawyer verification (`verificationStatus = REJECTED`).
    - **Body:** `{ "reason": "Invalid license number..." }` (Optional: requires DB update to store reason)
    - **Response:** 200 OK

## 3. Review Moderation
APIs to moderate user reviews.

- [ ] `GET /api/v1/admin/reviews`
    - **Description:** List all reviews with pagination.
    - **Filters:** By Lawyer ID, Date range.
    - **Response:** Page<ReviewDTO>

- [ ] `DELETE /api/v1/admin/reviews/{id}`
    - **Description:** Delete a review (e.g., if it violates terms).
    - **Response:** 204 No Content

## 4. System Analytics
APIs for the Admin Dashboard overview.

- [ ] `GET /api/v1/admin/analytics/summary`
    - **Description:** Get high-level statistics.
    - **Response:**
      ```json
      {
        "totalUsers": 100,
        "activeUsers": 50,
        "totalCases": 200,
        "activeCases": 20,
        "totalRevenue": 0.0
      }
      ```

## 5. Audit Logs
APIs to view system activity logs.

- [x] `GET /api/v1/admin/audit-logs`
    - **Description:** View system audit logs.
    - **Response:** Page<AuditLog> (Existing)

## 6. Infrastructure & Refactoring
- [ ] Refactor `AdminController` to use `AdminService` instead of calling Repositories directly.
- [ ] Add DTOs for Admin responses to avoid exposing internal entities.
