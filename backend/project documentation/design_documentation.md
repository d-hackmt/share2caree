# ShareToCare Design Documentation

This document provides the architectural design for the ShareToCare platform, detailing the data structures and system interactions.

## 1. Entity Relationship Diagram (ERD)

The following diagram illustrates the relationships between Users, NGO Profiles, Donations, and Claims.

```mermaid
erDiagram
    USER ||--o| NGO_PROFILE : "has"
    USER ||--o{ DONATION : "donates"
    USER ||--o{ CLAIM : "claims"
    DONATION ||--o| CLAIM : "result in"
    
    USER {
        bigint id PK
        string name
        string email
        string password
        string phone
        enum role
        decimal latitude
        decimal longitude
        enum ngo_status
        string fcm_token
    }
    
    NGO_PROFILE {
        bigint id PK
        bigint user_id FK
        string license_number
        string certificate_url
        text rejection_reason
    }
    
    DONATION {
        bigint id PK
        bigint donor_id FK
        enum category
        text details
        decimal latitude
        decimal longitude
        enum status
        timestamp created_at
    }
    
    CLAIM {
        bigint id PK
        bigint donation_id FK
        bigint ngo_id FK
        timestamp claimed_at
    }
```

## 2. Table Design Schema

### Table: `users`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY | Unique ID |
| `name` | VARCHAR(255) | NOT NULL | Full name |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Login credential |
| `password` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `phone` | VARCHAR(20) | | Contact number |
| `role` | ENUM | DONOR, NGO, BOTH, ADMIN | System role |
| `latitude` | DECIMAL(10,8) | | Address latitude |
| `longitude` | DECIMAL(11,8) | | Address longitude |
| `ngo_status` | ENUM | PENDING, APPROVED, REJECTED | Verification status |
| `fcm_token` | VARCHAR(255) | | For Firebase Notifications |

### Table: `ngo_profiles`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY | Unique ID |
| `user_id` | BIGINT | FOREIGN KEY (users.id) | Link to user |
| `license_number` | VARCHAR(100) | | Gov. registered license |
| `certificate_url` | VARCHAR(500) | | Storage URL for doc |
| `rejection_reason`| TEXT | | Reason if rejected |

### Table: `donations`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY | Unique ID |
| `donor_id` | BIGINT | FOREIGN KEY (users.id) | Who donated? |
| `category` | ENUM | FOOD, CLOTHES, BOOKS, GAMES | Category |
| `details` | TEXT | | Item description |
| `latitude` | DECIMAL(10,8) | | Pick-up latitude |
| `longitude` | DECIMAL(11,8) | | Pick-up longitude |
| `status` | ENUM | AVAILABLE, CLAIMED, EXPIRED | Current state |
| `created_at` | TIMESTAMP | DEFAULT NOW() | Time of donation |

### Table: `claims`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY | Unique ID |
| `donation_id` | BIGINT | FOREIGN KEY (donations.id) | Which donation? |
| `ngo_id` | BIGINT | FOREIGN KEY (users.id) | Which NGO? |
| `claimed_at` | TIMESTAMP | DEFAULT NOW() | Time of claim |

---

## 3. UML Diagrams

### A. Use Case Diagram
Describes the interactions between actors (Donor, NGO, Admin) and the system.

```mermaid
graph TD
    Donor((Donor))
    NGO((NGO))
    Admin((Admin))
    
    subgraph "ShareToCare System"
        UC1(Register/Login)
        UC2(Donate Items)
        UC3(Upload NGO Docs)
        UC4(Approve/Reject NGO)
        UC5(View Nearby Donations)
        UC6(Claim Donation)
        UC7(Receive Firebase Notification)
    end
    
    Donor --> UC1
    Donor --> UC2
    
    NGO --> UC1
    NGO --> UC3
    NGO --> UC5
    NGO --> UC6
    NGO -.-> UC7
    
    Admin --> UC4
    
    UC2 -- "Trigger Location Logic" --> UC7
```

### B. Class Diagram
Illustrates the structure of the system by showing its classes, their attributes, and relationships.

```mermaid
classDiagram
    class User {
        +Long id
        +String name
        +String email
        +String password
        +String phone
        +Role role
        +Double latitude
        +Double longitude
        +NGOStatus ngoStatus
        +String fcmToken
        +register()
        +login()
    }
    class NGOProfile {
        +Long id
        +String licenseNumber
        +String certificateUrl
        +String rejectionReason
        +uploadDocs()
    }
    class Donation {
        +Long id
        +Category category
        +String details
        +Double latitude
        +Double longitude
        +Status status
        +createDonation()
    }
    class Claim {
        +Long id
        +LocalDateTime claimedAt
        +claimItems()
    }
    
    User "1" -- "0..1" NGOProfile : profile
    User "1" -- "0..*" Donation : donates
    User "1" -- "0..*" Claim : claims
    Donation "1" -- "0..1" Claim : results in
```

### C. Sequence Diagram (Donation & Notification Flow)
Visualizes the step-by-step process of a donation event.

```mermaid
sequenceDiagram
    participant D as Donor
    participant B as Backend
    participant DB as Database
    participant F as Firebase
    participant N as Approved NGO
    
    D->>B: Submit Donation (Food, Location)
    B->>DB: Save Donation (AVAILABLE)
    B->>DB: Find Approved NGOs within 5km
    DB-->>B: List of NGOs
    B->>F: Trigger Notifications
    F->>N: "New Donation in your area!"
    N->>B: Claim Donation Request
    B->>DB: Update Status to CLAIMED
    B-->>N: Success Receipt
```
