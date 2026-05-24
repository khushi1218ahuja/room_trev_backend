# RoomTrev Backend

A RESTful API backend for **RoomTrev**, a room booking platform built with Spring Boot 3, Spring Security, JWT authentication, and PostgreSQL.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2.5 |
| Language | Java 19 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Utilities | Lombok, Hypersistence Utils |
| Testing | JUnit 5, Spring Security Test, H2 (in-memory) |

---

## Project Structure

```
roomtrev-spring/
├── src/
│   ├── main/
│   │   ├── java/com/roomtrev/
│   │   │   ├── config/          # Security configuration
│   │   │   ├── controller/      # REST controllers (Auth, Room, Booking, Review, Admin, Stats)
│   │   │   ├── dto/             # Request/Response DTOs
│   │   │   ├── entity/          # JPA entities (User, Room, Booking, Review)
│   │   │   ├── repository/      # Spring Data JPA repositories
│   │   │   ├── security/        # JWT filter, JwtUtil, UserDetailsService
│   │   │   ├── service/         # Business logic
│   │   │   └── RoomTrevApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/roomtrev/
│           ├── security/        # JWT unit tests
│           └── service/         # Service unit tests
└── pom.xml
```

---

## Prerequisites

Make sure the following are installed on your machine:

- **Java 19+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **PostgreSQL 14+** — [Download](https://www.postgresql.org/download/)

---

## Database Setup

1. Start PostgreSQL and open a terminal (`psql`):

```sql
CREATE DATABASE staybnb;
CREATE USER postgres WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE staybnb TO postgres;
```

2. The app uses `spring.jpa.hibernate.ddl-auto=none`, so you need to create the schema manually. Run the following SQL:

```sql
-- Enums
CREATE TYPE role AS ENUM ('GUEST', 'HOST', 'ADMIN');
CREATE TYPE room_type AS ENUM ('ENTIRE', 'PRIVATE', 'SHARED');
CREATE TYPE booking_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED');

-- Users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role role DEFAULT 'GUEST',
    phone TEXT,
    address TEXT,
    profile_picture TEXT,
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT now()
);

-- Rooms
CREATE TABLE rooms (
    id SERIAL PRIMARY KEY,
    host_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    location TEXT NOT NULL,
    city TEXT NOT NULL,
    type room_type NOT NULL,
    price_per_night DOUBLE PRECISION NOT NULL,
    amenities TEXT[] DEFAULT '{}',
    images TEXT[] DEFAULT '{}',
    is_available BOOLEAN DEFAULT true,
    avg_rating DOUBLE PRECISION,
    review_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT now()
);

-- Bookings
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    room_id INTEGER REFERENCES rooms(id),
    guest_id INTEGER REFERENCES users(id),
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    guest_count INTEGER NOT NULL,
    total_price DOUBLE PRECISION NOT NULL,
    status booking_status DEFAULT 'PENDING',
    payment_status TEXT,
    transaction_id TEXT,
    created_at TIMESTAMP DEFAULT now()
);

-- Reviews
CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    room_id INTEGER REFERENCES rooms(id),
    user_id INTEGER REFERENCES users(id),
    rating INTEGER NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);
```

---

## Environment Variables

Set the following environment variables before running the app. You can export them in your shell or create an `.env` file and source it.

| Variable | Description | Default |
|----------|-------------|---------|
| `PGHOST` | PostgreSQL host | `localhost` |
| `PGPORT` | PostgreSQL port | `5432` |
| `PGDATABASE` | Database name | `staybnb` |
| `PGUSER` | Database user | `postgres` |
| `PGPASSWORD` | Database password | _(empty)_ |
| `JWT_SECRET` | Secret key for signing JWTs (min 32 chars) | _(fallback default)_ |
| `PORT` | Port the server listens on | `8081` |

Export example (Linux/macOS):

```bash
export PGHOST=localhost
export PGPORT=5432
export PGDATABASE=staybnb
export PGUSER=postgres
export PGPASSWORD=your_password
export JWT_SECRET=your_super_secret_key_minimum_32_characters_long
export PORT=8081
```

---

## Running Locally

### 1. Clone the repository

```bash
git clone https://github.com/khushi1218ahuja/room_trev_backend.git
cd room_trev_backend
```

### 2. Set environment variables

Follow the [Environment Variables](#environment-variables) section above.

### 3. Build the project

```bash
mvn clean install -DskipTests
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8081`

---

## Running Tests

```bash
mvn test
```

Tests use an in-memory H2 database and do not require PostgreSQL to be running.

---

## API Endpoints

### Auth
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login and receive JWT token |

### Rooms
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/rooms` | Public | List all available rooms |
| GET | `/api/rooms/{id}` | Public | Get room details |
| POST | `/api/rooms` | Host | Create a new room listing |
| PUT | `/api/rooms/{id}` | Host | Update a room listing |
| DELETE | `/api/rooms/{id}` | Host | Delete a room listing |

### Bookings
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/bookings` | Guest | Create a booking |
| GET | `/api/bookings/my` | Guest | Get current user's bookings |
| PUT | `/api/bookings/{id}/cancel` | Guest | Cancel a booking |

### Reviews
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/reviews` | Guest | Submit a review |
| GET | `/api/reviews/room/{roomId}` | Public | Get reviews for a room |

### Admin
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/admin/users` | Admin | List all users |
| DELETE | `/api/admin/users/{id}` | Admin | Delete a user |
| GET | `/api/admin/rooms` | Admin | List all rooms |
| GET | `/api/stats` | Admin | Platform statistics |

### Health
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/actuator/health` | Public | App health check |

---

## Authentication

The API uses **Bearer token (JWT)** authentication.

1. Register or login to receive a JWT token
2. Include the token in subsequent requests:

```
Authorization: Bearer <your_jwt_token>
```

Tokens are valid for **7 days** (604800000 ms).

---

## License

This project is open-source and available under the [MIT License](LICENSE).
