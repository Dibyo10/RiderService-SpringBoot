# Rider Service — Spring Boot

Rider Service for a food delivery backend.  
This service manages **users**, **riders**, and **rider onboarding**, and exposes a clean API contract via **Swagger (OpenAPI)**.

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA
- Hibernate
- Springdoc OpenAPI (Swagger)
- MySQL / PostgreSQL (configurable)

---

## Domain Overview

### User
- Represents a system user
- Can optionally become a Rider

### Rider
- One-to-One with User
- Stores rider-specific data like:
    - Rating
    - Driving License (DL as BLOB)

---

## API Documentation (Swagger)

Once the application is running:

```
http://localhost:8080/swagger-ui/index.html
```

This is the **source of truth** for all endpoints.

---

## Base URL

```
/api
```

---

## User APIs

### Create User
**POST** `/api/users`

```json
{
  "name": "Vimal",
  "email": "v@gmail.com",
  "password": "secret123",
  "phoneNumber": "7903632688"
}
```

### Get User by ID
**GET** `/api/users/{id}`

Returns user details (password is write-only).

---

## Rider APIs

### Create Rider Profile
**POST** `/api/riders`

```json
{
  "userId": 1,
  "dl": "ZRDzKQ=="
}
```

- `dl` must be Base64 encoded
- Converts an existing user into a rider

### Get Rider by User ID
**GET** `/api/riders/by-user/{userId}`

Fetches rider profile linked to a user.

## Rider Lifecycle & Availability (Phase 1)

Riders move through a well-defined lifecycle based on availability and order assignment.

## Rider Status States

| Status | Meaning |
|--------|---------|
| OFFLINE | Rider is not available for orders |
| ONLINE | Rider is available to take orders |
| BUSY | Rider is currently assigned to an order |
| SUSPENDED | Rider is blocked (admin action) |

## Valid State Transitions

```
OFFLINE → ONLINE
ONLINE  → BUSY
BUSY    → ONLINE
ONLINE  → OFFLINE
```

Invalid transitions are rejected by the API.

---

## Rider Availability APIs

### Update Rider Status

**PUT** `/api/riders/{riderId}/status`

Update the availability status of a rider.

**Request**

```json
{
  "status": "ONLINE"
}
```

**Rules**
- Invalid state transitions return `400 Bad Request`
- Suspended riders cannot change status

---

### Get Available Riders

**GET** `/api/riders/available`

Fetch riders who are currently available to take orders.

**Query Params**

| Param | Description | Default |
|-------|-------------|---------|
| limit | Max number of riders | 10 |

**Response**

```json
[
  {
    "riderId": 12,
    "rating": 4.5
  },
  {
    "riderId": 18,
    "rating": 4.2
  }
]
```

**Used by:**
- Order Service
- Admin dashboards
- Debugging

---

## Order Assignment APIs (Phase 1)

### Assign Rider to Order

**POST** `/api/riders/{riderId}/assign`

Assigns a rider to an order.

**Request**

```json
{
  "orderId": 991
}
```

**Rules**
- Rider must be `ONLINE`
- Rider status changes to `BUSY`

---

### Release Rider After Order Completion

**POST** `/api/riders/{riderId}/release`

Releases a rider after order completion or cancellation.

**Request**

```json
{
  "orderId": 991
}
```

**Rules**
- Rider must be `BUSY`
- Rider status changes back to `ONLINE`

---

## Notes

- Swagger reflects all available APIs
- Validation is enforced via annotations

---

## How to Run Locally

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```
