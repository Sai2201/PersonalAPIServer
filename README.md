# Modern Personal API Server

A local-first personal API server to manage structured data like notes, bookmarks, todos, and more.

## Features (MVP)
- PUT/GET JSON objects by key
- In-memory store (will evolve to file-backed, versioned storage)
- REST API endpoints for basic interaction
- OpenAPI (Swagger UI) integration

## Stack
- Java 17
- Spring Boot
- Maven

## Getting Started
```bash
mvn spring-boot:run
```

Access the API at:
- `POST /api/store/{key}` with JSON body
- `GET /api/store/{key}` to retrieve
- `GET /api/store/` to list all keys

Swagger UI (after starting server):
- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Next Goals
- Add JSON file-based persistence
- Add versioning for stored objects
- Implement CLI access
- Expand OpenAPI definitions for all endpoints