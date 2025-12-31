# AinBondhu Project Status & TODO

## 1. Existing Features (DONE)

### Authentication & User Management
- [x] User Registration (Client, Lawyer)
- [x] JWT Authentication
- [x] Role-based Access Control (CLIENT, LAWYER, ADMIN)
- [x] Lawyer Profile (Status, Specialization, Location)

### Case Management (Lawyer)
- [x] Create Legal Case
- [x] View List of Cases (Pagination support)
- [x] View Case Details
- [x] Update Case (Status, Details)
- [x] Add Case Notes
- [x] Upload Case Documents
- [x] Download Case Documents

### Chat System
- [x] Real-time Messaging (WebSocket/STOMP)
- [x] Chat History (Persisted in DB)

### Questionnaire (Legal Assistant)
- [x] Categories and Scenarios
- [x] Question Flow

## 2. Planned Features (TODO) - High Priority

### Post-Case Completion Feedback (User Request)
- [x] **Rating & Review System**
    - Create `Review` entity linked to `LegalCase`, `Client`, and `Lawyer`.
    - Allow Clients to rate (1-5 stars) and comment on a Lawyer after a case is `CLOSED`.
    - API to submit reviews.
    - API to fetch reviews for a lawyer.
    - **Lawyer Profile Update**: Display average rating and review count on Lawyer profile.

### Lawyer Public Profile Enhancements
- [x] **Public View of Lawyer Profile**: Allow unauthenticated or client users to view lawyer details including:
    - Average Rating
    - Recent Reviews
    - Success Rate (derived from Closed cases as Total Cases Served)

## 3. Production-Grade Features (TODO) - Brainstorming

### Notification System
- [ ] **Push/In-App Notifications**
    - Notify Client when Case Status changes.
    - Notify Lawyer when a new Case Request is received.
    - Notify User when a new Message arrives.
    - Use Firebase Cloud Messaging (FCM) or similar.

### Payments & Monetization
- [ ] **Payment Gateway Integration** (SSLCommerz / Stripe / Bkash)
    - Consultation fees.
    - Retainer fees for cases.
    - Subscription model for Lawyers (Premium features).

### Appointment Scheduling
- [ ] **Booking System**
    - Lawyers set availability slots.
    - Clients book appointments (In-person or Video Call).
    - Integration with Google Calendar (Optional).

### Advanced Search & Discovery
- [ ] **Elasticsearch / Advanced Filtering**
    - Search lawyers by Name, Specialization, Location, Rating.
    - Filter by "Online Now".

### Admin Dashboard
- [ ] **Admin Features**
    - Verify Lawyer credentials (Bar License check).
    - moderate Reviews.
    - View System Analytics (Active users, Cases count).

### Security & Reliability
- [x] **Audit Logging**: Track who changed what (especially for Case files).
- [x] **Rate Limiting**: Prevent abuse of APIs.
- [ ] **Data Backup Strategy**: Automated backups for DB and File Storage.

### Infrastructure
- [ ] **Cloud Storage**: Move from local file storage to AWS S3 / MinIO.
- [ ] **CI/CD Pipeline**: Automated testing and deployment.
