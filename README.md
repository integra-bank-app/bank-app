# Bank App for Integra Project

**Bank App for Integra Project** is a modern, full-stack web application developed as part of the Integra initiative.  
Its primary purpose is to simulate and manage core banking operations—including user management, account handling, deposits, transactions, and investments—using a secure, scalable, and extensible architecture.

The application is built with Java (Spring Boot) for the backend and TypeScript (React) for the frontend, featuring:
- robust JWT-based authentication and authorization,
- internationalization (i18n) for multilingual support,
- RESTful APIs documented with OpenAPI/Swagger,
- automated database schema migrations (Flyway),
- and a responsive UI for both users and admins.

## Table of Contents

- [Features](#features)
- [Architecture Overview](#architecture-overview)
- [Security: Login & Registration](#security-login--registration)
- [Internationalization (i18n)](#internationalization-i18n)
- [Database Migration](#database-migration)
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

---

## Architecture Overview

The Bank App for Integra Project is built as a modular, full-stack web application, following modern best practices for scalability, maintainability, and security.

- **Backend:**  
  Developed in Java using Spring Boot, the backend serves as the core of the application, handling business logic, data storage, security, and API exposure.  
  It offers RESTful endpoints for all core banking operations (user management, account operations, transactions, deposits, investments, etc.).  
  Key components include:
    - **Spring Security:** Ensures robust authentication (JWT) and role-based authorization.
    - **Database Layer:** Uses JPA/Hibernate for ORM, and Flyway for automated schema migrations.
    - **OpenAPI/Swagger:** Provides interactive API documentation for developers.
    - **Internationalization:** Supports multi-language error and response messages.

- **Frontend:**  
  Built with React and TypeScript, the frontend provides a responsive, user-friendly interface for both regular users and admins.  
  Key components:
    - **React Router & State Management:** Efficient navigation and dynamic data handling.
    - **react-i18next:** Enables multi-language support throughout the user interface.
    - **API Integration:** Communicates securely with the backend via JWT-protected API calls.
    - **UI Components:** Uses modern component libraries for a clean and accessible design.

- **Inter-service Communication:**  
  The frontend consumes backend APIs via REST calls, using JWT tokens for secure communication.

- **Deployment:**  
  Both backend and frontend are containerized with Docker, making it easy to deploy and scale in various environments.

This architecture ensures that the application is extensible (easy to add new modules), secure (modern authentication and authorization), and user-centric (responsive, localized UI).

---

## Security: Login & Registration

- **JWT Authentication:** All API endpoints require a valid JWT token in the `Authorization` header.
- **Registration:** Users can sign up with email and password (securely hashed).
- **Password Reset:** Secure flows for forgotten passwords.
- **Role-based Access:** Endpoints are protected according to user roles (`user`, `admin`).

---

## Internationalization (i18n)

- **Frontend:** Uses [react-i18next](https://react.i18next.com/) for UI translations.
- **Backend:** Localized error and response messages; supports language preference via the `Accept-Language` header.

---

## Database Migration

- **Tool:** [Flyway](https://flywaydb.org/) is used for database schema migrations.
- **Workflow:** On backend startup, Flyway automatically applies migrations from `src/main/resources/db/migration`.
- **Configuration:** Database connection via the `DB_URL` environment variable in `application.properties`.

---

## OpenAPI Documentation

- **Swagger UI:** Interactive API docs available at `/swagger-ui.html` on the backend after startup.
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
- `POST /api/branches/{branchId}/collect-taxes-and-fees` – Collect taxes and fees for branch

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

---

## Screenshots
<!-- Add screenshots here if available -->
