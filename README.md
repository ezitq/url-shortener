# URL Shortener

Simple and lightweight URL shortener service built with Java and Spring Boot.  
Allows creating short links with optional custom aliases and expiration time.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data MongoDB
- MongoDB
- Lombok
- Gradle
- Docker

## Features

- Create short URLs
- Custom aliases support
- Optional link expiration
- Click counter
- Redirect by short code
- Proper error handling with meaningful HTTP status codes
- Request validation

## Requirements

- Java 21+
- Docker & Docker Compose
- Gradle (or use the wrapper)

## How to Run

### 1. Start MongoDB

```bash
docker-compose up -d
