# Implementation Plan - ShareToCare Platform Design

This plan outlines the design of the ShareToCare platform, which facilitates donations (Food, Clothes, Books, Games) from Donors to NGOs.

## User Review Required

> [!IMPORTANT]
> **Manual Verification**: NGOs will have a `PENDING` status upon registration until an Admin manually approves them. Only `APPROVED` NGOs will receive notifications for nearby donations.

> [!NOTE]
> **Spatial Querying**: For the 5km radius notification, we will need to implement a distance calculation (e.g., Haversine formula) or use a spatial database extension like PostGIS if the load is high.

## Proposed Design

### 1. Notification Management Strategy

To handle notifications via Firebase Cloud Messaging (FCM), we will implement the following flow:

1.  **Token Storage**: Add an `fcm_token` field to the `User` model. When an NGO logs in via the mobile/web app, the client-side app sends its unique FCM token to the backend to be stored.
2.  **Notification Trigger**: When a `Donation` is successfully saved, a `NotificationService` will be triggered asynchronously.
3.  **Geospatial Querying**: The service will query the database for all users who:
    *   Have the `NGO` or `BOTH` role.
    *   Have an `APPROVED` status.
    *   Are within a **5km radius** of the donation's latitude/longitude (using the Haversine formula).
4.  **Firebase Delivery**: The backend will use the **Firebase Admin SDK** to send a "Data Message" or "Notification" to the collected FCM tokens.
5.  **Claim Logic**: When an NGO clicks the notification, it opens the donation details. Once claimed, we can send a "Cancel/Claimed" notification to other nearby NGOs to remove the alert.

### 2. Entity Relationship Diagram (ERD) & Table Design

We will structure the database to support Users with multiple roles, NGO verification, and donation tracking.

#### Table: `users`
| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGINT (PK) | Unique identifier |
| `name` | VARCHAR | Full name |
| `email` | VARCHAR (Unique)| Login email |
| `password` | VARCHAR | Hashed password |
| `phone` | VARCHAR | Contact number |
| `role` | ENUM | DONOR, NGO, BOTH, ADMIN |
| `latitude` | DECIMAL(10,8) | Location Latitude |
| `longitude` | DECIMAL(11,8) | Location Longitude |
| `ngo_status` | ENUM | PENDING, APPROVED, REJECTED |
| `fcm_token` | VARCHAR | Firebase Cloud Messaging Token |

#### Table: `ngo_profiles`
| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGINT (PK) | Unique identifier |
| `user_id` | BIGINT (FK) | Reference to `users.id` |
| `license_number` | VARCHAR | Legal license identifier |
| `certificate_url` | VARCHAR | URL to the uploaded certificate document |
| `rejection_reason`| TEXT | Reason for rejection (if applicable) |

#### Table: `donations`
| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGINT (PK) | Unique identifier |
| `donor_id` | BIGINT (FK) | Reference to `users.id` |
| `category` | ENUM | FOOD, CLOTHES, BOOKS, GAMES |
| `details` | TEXT | Description and quantities |
| `latitude` | DECIMAL(10,8) | Donation pick-up latitude |
| `longitude` | DECIMAL(11,8) | Donation pick-up longitude |
| `status` | ENUM | AVAILABLE, CLAIMED, EXPIRED |
| `created_at` | TIMESTAMP | Creation time |

#### Table: `claims`
| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGINT (PK) | Unique identifier |
| `donation_id` | BIGINT (FK) | Reference to `donations.id` (Unique - one claim per donation) |
| `ngo_id` | BIGINT (FK) | Reference to `users.id` |
| `claimed_at` | TIMESTAMP | Time of claim |

---

### 2. UML Diagrams

I will provide high-level Mermaid diagrams within the final documentation for:
*   **Class Diagram**: Showing the structure of the entities and their associations.
*   **Use Case Diagram**: Detailing the interactions of Donors, NGOs, and Admins.
*   **Sequence Diagram**: Illustrating the donation -> notification -> claim flow.

## Open Questions

1.  **Document Storage**: Do you have a preferred storage for the NGO documents? (e.g., AWS S3, Firebase Storage, or local file system).
2.  **Notification Batching**: If many donations happen simultaneously, should notifications be individual or batched? (Defaulting to individual for real-time response).
3.  **Claim Window**: Should there be a time limit for a donation before it expires?

## Verification Plan

### Manual Verification
- Review the generated diagrams to ensure they meet all requirements.
- Verify that the table schema supports the 5km radius logic (latitude/longitude fields).
- Check that the `ngo_status` workflow is correctly captured.
