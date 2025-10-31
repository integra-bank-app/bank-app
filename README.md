# Bank App for Integra Project

**Bank App for Integra Project** is a modern, full-stack web application. 
Its primary purpose is to simulate and manage core banking operations—including user management, account handling, deposits, transactions, investments, and compliance processes—using a secure, scalable, and extensible architecture.

The application is built with Java (Spring Boot) for the backend and TypeScript (React) for the frontend, featuring:
- Robust JWT-based authentication and authorization
- Internationalization (i18n) for multilingual support
- RESTful APIs documented with OpenAPI/Swagger
- Automated database schema migrations (Flyway)
- Integration with ANAF (Romanian tax authority) for simulating tax and fee collection
- Real-time notifications (frontend and backend events)
- Responsive UI for both users and admins

## Table of Contents

- [Features](#features)
- [Architecture Overview](#architecture-overview)
- [Security: Login & Registration](#security-login--registration)
- [Internationalization (i18n)](#internationalization-i18n)
- [Database Migration](#database-migration)
- [Notifications](#notifications)
- [ANAF Integration](#anaf-integration)
- [OpenAPI Documentation](#openapi-documentation)
- [API Endpoints](#api-endpoints)
- [API Request/Response Examples](#api-requestresponse-examples)
- [Backend](#backend)
- [Frontend](#frontend)
- [Roadmap](#roadmap)
- [Screenshots](#screenshots)

---

## Features

- **Secure Authentication:** JWT-based login, registration, password management, and role-based access (user/admin)
- **Internationalization (i18n):** Multi-language support for frontend and backend responses
- **Account Management:** Create, update, and delete multiple accounts
- **Transaction History:** Filter and export statements
- **Fund Transfers:** Internal and external transfers, scheduled payments
- **Admin Dashboard:** User and account management, audit logs
- **RESTful API:** Integration-ready endpoints, documented with OpenAPI/Swagger
- **Database Migration:** Automated DB migrations for schema updates (Flyway)
- **ANAF Integration:** Simulates interaction with ANAF for tax and fee collection at branch level
- **Notifications:** Real-time alerts/updates for users (transaction confirmations, account changes, salary requests)

---

## Architecture Overview

Bank App for Integra Project is built as a modular, full-stack web application, following best practices for scalability, maintainability, and security.

- **Backend:**  
  - Java (Spring Boot), JPA/Hibernate ORM, Flyway migrations
  - Exposes RESTful endpoints for all banking operations (users, accounts, transactions, deposits, investments, taxes).
  - **Spring Security:** Robust JWT authentication and role-based authorization.
  - **Internationalization:** Multi-language error/response messages.
  - **ANAF Simulation:** Branches collect taxes and fees via endpoints simulating communication with ANAF.
  - **Notifications:** Publishes events for actions (e.g., transaction complete, salary requested). Can be extended to use email, SMS, or push notifications.
  - **Swagger/OpenAPI:** Interactive documentation for all endpoints.

- **Frontend:**  
  - TypeScript (React), react-i18next (i18n), modern UI component libraries
  - Responsive user/admin interface for all banking features
  - **Notifications:** Displays alerts for important events (e.g., transfer success, deposit creation)
  - **API Integration:** Uses JWT to securely access backend endpoints

- **Deployment:**  
  Both backend and frontend are containerized with Docker, supporting easy local development and production scaling.

---

## Security: Login & Registration

- **JWT Authentication:** All API endpoints require a valid JWT token in the `Authorization` header.
- **Registration:** Users sign up with email and password (securely hashed).
- **Password Reset:** Secure flows for forgotten passwords.
- **Role-based Access Control:** Endpoints protected for `user` and `admin` roles.

---

## Internationalization (i18n)

- **Frontend:** Uses [react-i18next](https://react.i18next.com/) for UI translations.
- **Backend:** Localized error and response messages, `Accept-Language` header supported.

---

## Database Migration

- **Tool:** [Flyway](https://flywaydb.org/) is used for database schema migrations.
- **Workflow:** Migrations applied automatically from `src/main/resources/db/migration` on backend startup.
- **Configuration:** Database via `DB_URL` in `application.properties`.

---

## Notifications

- **Transaction alerts:** Users receive confirmations for transfers, deposits, withdrawals.
- **Account updates:** Changes to user/account info trigger notifications.
- **Salary requests:** Admin actions or salary disbursements notify users.
- **Branch operations:** Tax/fee collections (ANAF simulation) can notify branch managers.
- **Tech:** Notifications are displayed in the frontend UI; backend can be extended for email, SMS, or push.

---

## ANAF Integration

- **Purpose:** Simulates communication with ANAF (Agenția Națională de Administrare Fiscală) for tax and fee collection.
- **Endpoints:** Branches can collect taxes/fees, and export related data.
- **Extensibility:** Designed for real ANAF API integration in future roadmap.

---

## OpenAPI Documentation

- **Swagger UI:** Interactive docs at `/swagger-ui.html` after backend starts.
- **OpenAPI Spec:** Available at `/v3/api-docs` (JSON).

---

## API Endpoints

All endpoints are available under the `/api` prefix and require JWT authentication unless otherwise stated.

### User Management
- `POST /api/auth/login` – Login (JWT)
- `POST /api/auth/register` – Register new user
- `GET /api/users/{userId}` – Get user details
- `POST /api/users` – Create a new user (admin only)
- `GET /api/users` – List users (paginated)
- `POST /api/users/{userId}/balance` – Add funds to user's account
- `GET /api/users/{userId}/balance` – Get user's total balance
- `POST /api/users/transfer?fromUserId={id}&toUserId={id}&amount={sum}` – Transfer funds between users
- `GET /api/users/{userId}/accounts` – List user's accounts
- `GET /api/users/{userId}/accounts/{accountId}` – Get balance for a user's account
- `GET /api/users/{userId}/deposits` – List user's deposits
- `POST /api/users/{userId}/requestSalary` – Request salary (admin/user)

### Branch Management
- `GET /api/branches/{branchId}/users?page={page}&size={size}` – List users for a branch (paginated)
- `POST /api/branches/{branchId}/collect-taxes-and-fees` – Collect taxes and fees for branch (simulated ANAF integration)

### Deposits
- `GET /api/deposits/{userId}` – List deposits for a user
- `POST /api/deposits/{userId}` – Create a new deposit for user
- `GET /api/deposits/export` – Export deposits (admin only)
- `POST /api/deposits/import` – Bulk import deposits (admin only)

### Transactions
- `GET /api/accounts/{accountId}/transactions` – List transactions for an account
- `POST /api/accounts/{accountId}/transfer` – Transfer funds between accounts

### Investments
- `POST /api/users/{userId}/investments` – Create an investment for user
- `GET /api/users/{userId}/investments/{investmentId}` – Get investment details

> For full API details, see the [OpenAPI/Swagger documentation](http://localhost:8080/swagger-ui.html) after starting the backend.

---

## API Request/Response Examples

### Login

**Request**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "yourPassword"
}
```

**Response**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR..."
}
```

---

### Get User Details

**Request**
```http
GET /api/users/5e2a1d9b-3c2a-4fd8-bb4e-2d5a9f2b5e2a
Authorization: Bearer <JWT_TOKEN>
```

**Response**
```json
{
  "id": "5e2a1d9b-3c2a-4fd8-bb4e-2d5a9f2b5e2a",
  "firstName": "John",
  "middleName": "A.",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "role": "USER",
  "branchId": "a2b3c4d5-e6f7-4g8h-9i0j-1k2l3m4n5o6p"
}
```

---

### Transfer Funds

**Request**
```http
POST /api/users/transfer?fromUserId=abc123&toUserId=def456&amount=100.00
Authorization: Bearer <JWT_TOKEN>
```

**Response**
```json
{
  "newBalance": 900.00
}
```

---

### List Deposits

**Request**
```http
GET /api/deposits/5e2a1d9b-3c2a-4fd8-bb4e-2d5a9f2b5e2a
Authorization: Bearer <JWT_TOKEN>
```

**Response**
```json
[
  {
    "id": "d1e2f3a4-b5c6-7d8e-9f0a-1b2c3d4e5f6g",
    "interest_rate": 1.5,
    "amount": 1000.00
  }
]
```

---

### Simulate ANAF Tax Collection

**Request**
```http
POST /api/branches/{branchId}/collect-taxes-and-fees
Authorization: Bearer <JWT_TOKEN>
```

**Response**
```json
{
  "collectedAmount": 3200.00
}
```

---

### Notification Example

**Scenario:**  
A user requests a salary payout. Upon completion, the system sends a notification (visible in the UI, or potentially via email/push).

**Notification**
```json
{
  "type": "salaryRequest",
  "message": "Your salary request has been processed.",
  "timestamp": "2025-10-31T06:40:00Z"
}
```

---

## Backend

- **Tech:** Java (Spring Boot), Mustache, Flyway, OpenAPI/Swagger
- **Location:** `backend/`
- **Run:**
  ```bash
  cd backend
  ./gradlew bootRun
  ```
- **Config:**
  - Edit `src/main/resources/application.properties`
  - Environment variables:
    - `DB_URL` – Database connection URL
    - `JWT_SECRET` – JWT key
    - `PORT` – Server port
  - Migrations: Place migration scripts in `src/main/resources/db/migration`

---

## Frontend

- **Tech:** TypeScript (React), react-i18next (i18n)
- **Location:** `frontend/`
- **Run:**
  ```bash
  cd frontend
  npm install
  npm run dev
  ```
- **Config:**
  - Edit `.env`
  - Example:
    ```
    REACT_APP_API_URL=http://localhost:8080/api
    REACT_APP_DEFAULT_LANGUAGE=en
    ```
  - i18n: Add translation files in `frontend/src/locales/`

---

## Roadmap

### Planned Features & Improvements

- Multi-factor authentication (MFA)
- Scheduled/recurring transactions
- Enhanced admin dashboard with analytics
- Mobile app support
- Role-based UI customization
- More comprehensive test coverage
- Integration with external financial APIs
- Advanced transaction filtering/search
- Accessibility improvements for frontend
- Performance and scalability enhancements
- Real ANAF API integration for production scenarios
- Push/email/SMS notification channels

---

## Screenshots
