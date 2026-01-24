# Rider Service — Spring Boot

Rider Service for a food delivery backend.  
This service manages **users**, **riders**, **rider onboarding**, **real-time location tracking**, and exposes a clean API contract via **Swagger (OpenAPI)**.

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA
- Hibernate
- Springdoc OpenAPI (Swagger)
- PostgreSQL
- Redis (for real-time location tracking)

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
    - Availability status
    - Real-time location (Redis)

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

---

## Rider Lifecycle & Availability

Riders move through a well-defined lifecycle based on availability and order assignment.

### Rider Status States

| Status | Meaning |
|--------|---------|
| OFFLINE | Rider is not available for orders |
| ONLINE | Rider is available to take orders |
| BUSY | Rider is currently assigned to an order |
| SUSPENDED | Rider is blocked (admin action) |

### Valid State Transitions
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

## Order Assignment APIs

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

## Rider Location Tracking (Redis-backed)

The Rider Service supports real-time rider location tracking to enable order assignment and live tracking.

### Design Principles

* Rider location is high-frequency, ephemeral data
* Only the latest known location is required
* Location updates are pushed by the rider client
* Location data is stored only in Redis (not in the DB)

This design avoids unnecessary database load and supports low-latency reads during order assignment.

### Location Storage Model

* **Storage:** Redis
* **Key format:**
```
rider:{riderId}:location
```

* **Value:**
```json
{
  "lat": 12.9352,
  "lon": 77.6245,
  "updatedAt": 1706100000000
}
```

* **TTL:** 5 minutes (Automatically expires if the rider stops sending updates)

If a location key is missing, the rider's location is treated as stale or unavailable.

---

## Rider Location APIs

### Update Rider Location

**PUT** `/api/riders/{riderId}/location`

This endpoint is called periodically (every 5–10 seconds) by the rider client (mobile app) to update the last-known location.

**Request**
```json
{
  "latitude": 12.9352,
  "longitude": 77.6245
}
```

**Rules**
* Rider must exist
* Rider must not be `OFFLINE`
* Location is overwritten in Redis
* TTL is refreshed on each update

---

### Get Rider Current Location

**GET** `/api/riders/{riderId}/location`

Fetches the last-known rider location from Redis.

**Response**
```json
{
  "riderId": 12,
  "latitude": 12.9352,
  "longitude": 77.6245,
  "updatedAt": 1706100000000
}
```

**Notes**
* Returns `404 Not Found` if location is stale or unavailable
* This endpoint is used by:
    * Order Service (for assignment)
    * Admin dashboards
    * Debugging / tracking

---

## Architectural Note: Redis-only for Now

Currently, rider location data is stored only in Redis and is not persisted to the database.

This is intentional.

### Why Redis-only?

* Location updates are frequent
* Location data changes constantly
* Historical precision is not required for live operations
* Overwriting a single key in Redis is extremely fast

This keeps:
* Database load low
* Assignment latency minimal
* System behavior easy to reason about

### Future Enhancement: Async DB Persistence (Planned)

In a future iteration, rider locations may be persisted asynchronously to the database at coarse intervals (e.g., every 10–15 minutes or on key lifecycle events).

#### Why async DB persistence is useful

Async persistence is not required for live operations, but it enables:

* 📊 **Analytics & heatmaps**
    * Rider density over time
    * Supply vs demand analysis
* 🛠️ **Debugging & incident analysis**
    * Investigating delayed or failed orders
* 🔄 **System recovery**
    * Restoring approximate state after Redis restarts
* 🤖 **ML / optimization (future)**
    * Training ETA or acceptance prediction models

**Importantly:**
* Async DB writes will not be on the hot path
* Redis remains the source of truth for real-time behavior
* Losing a few seconds of location data is acceptable

This separation keeps the system fast now, while leaving room for future growth.

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