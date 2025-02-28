# Book Inventory Management API

## Overview
The **Book Inventory Management API** is a RESTful service that allows users to manage books, track inventory levels, and process purchases. It supports CRUD operations, stock management, and search functionalities.

## Tech Stack
- **Spring Boot** (REST API Development)
- **Spring Data JPA** (Database Interaction)
- **H2/MySQL** (Database)
- **SLF4J + Logback** (Logging)

## Setup Instructions
1. Clone the repository:
   ```sh
   git clone git clone --branch feature-branch-name --single-branch https://github.com/Amaninreal/BookInventoryManagementSystem.git
   cd BookInventoryManagementSystem
   ```
2. Configure the database connection in `application.properties`.
3. Build and run the application:
   ```sh
   mvn spring-boot:run
   ```
4. The API will be available at `http://localhost:8080/inventory`.

## API Endpoints

### 1. **Book Management**
| Method  | Endpoint                 | Description                     |
|---------|--------------------------|---------------------------------|
| GET     | `/books`                  | Retrieve all books             |
| GET     | `/books/{isbn}`           | Retrieve a book by ISBN        |
| GET     | `/books/search?title=xyz` | Search books by title          |
| POST    | `/books`                  | Add a new book                 |
| PUT     | `/books/{isbn}`           | Update book details by ISBN    |
| PATCH   | `/books/{isbn}`           | Partially update book details  |
| DELETE  | `/books/{isbn}`           | Delete a book by ISBN          |

### 2. **Stock Management**
| Method | Endpoint                 | Description                      |
|--------|--------------------------|----------------------------------|
| POST   | `/books/{isbn}/purchase` | Purchase a book (decrease stock) |
| GET    | `/books/{isbn}/stock`    | Retrieve a book stock            |

### 3. **Error Handling**
- Returns `404 Not Found` if a book does not exist.
- Returns `400 Bad Request` if stock is insufficient for a purchase.

## Logging
- **INFO**: Logs successful operations (retrieval, purchase, restock, deletion).
- **WARN**: Logs failed operations (insufficient stock, book not found).
