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
