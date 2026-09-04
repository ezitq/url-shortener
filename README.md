# URL Shortener

Simple and lightweight URL shortener service built with Java and Spring Boot.

Allows creating short links with optional custom aliases and expiration time.

---

## 🛠 Tech Stack

* **Java 21**
* **Spring Boot**
* **Spring Data MongoDB**
* **MongoDB**
* **Lombok**
* **Gradle**
* **Docker**

---

## ✨ Features

* Create short URLs
* Custom aliases support
* Optional link expiration
* Click counter
* Redirect by short code
* Proper error handling with meaningful HTTP status codes
* Request validation

---

## 📋 Requirements

* **Java 21+**
* **Docker & Docker Compose**
* **Gradle** (або Gradle Wrapper `./gradlew`)

---

## 🚀 How to Run

### 1. Start MongoDB
```bash
docker-compose up -d

### 2. Run the application
./gradlew bootRun
The service will start on http://localhost:8080.

##📡 API Reference
Create short URL
POST /api/v1/urls

Request Body:
{
  "originalUrl": "[https://example.com/some/very/long/url](https://example.com/some/very/long/url)",
  "customAlias": "my-link",
  "expirationDays": 30
}
Field,Required,Description
originalUrl,Yes,Original long URL
customAlias,No,Custom short code (3–30 characters)
expirationDays,No,Number of days until the link expires

Example Response (201 Created):
{
  "originalUrl": "[https://example.com/some/very/long/url](https://example.com/some/very/long/url)",
  "shortUrl": "http://localhost:8080/my-link",
  "shortCode": "my-link",
  "createdAt": "2026-09-04T19:00:00",
  "expiresAt": "2026-10-04T19:00:00"
}

Redirect
GET /{shortCode}

Example:
GET http://localhost:8080/my-link
Returns 302 Found and redirects immediately to the original URL.

Status,Meaning
400 Bad Request,Validation error
404 Not Found,Short URL not found
409 Conflict,Alias already exists
410 Gone,Short URL has expired
500 Internal Server Error,Unexpected server error

## 📂 Project Structure
src/main/java/com/bohdan/urlshortener/
├── controller/          # REST controllers
├── dto/                 # Request/Response objects
├── exception/           # Custom exceptions + Global handler
├── repository/          # MongoDB repositories
├── service/             # Business logic
└── util/                # Utility classes (Base62 generator)

📌 Future Improvements
[ ] Click statistics endpoint

[ ] Redis caching for hot redirects

[ ] Authentication & User accounts

[ ] CI/CD pipeline with GitHub Actions

[ ] Comprehensive unit & integration tests
