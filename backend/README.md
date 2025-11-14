# Fantasy Analyzer - Backend API

Spring Boot REST API for Fantasy Football analysis with Sleeper API integration.

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: MySQL 8.0+
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security + JWT
- **API Client**: RestTemplate
- **Development**: Spring Boot DevTools, Lombok

## 📁 Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/fantasyfootball/fantasy_analyzer/
│   │   │   ├── config/           # Security, JWT, RestTemplate configs
│   │   │   ├── controller/       # REST API endpoints
│   │   │   ├── service/          # Business logic
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── model/
│   │   │   │   ├── entity/       # JPA entities
│   │   │   │   └── dto/          # Data Transfer Objects
│   │   │   └── FantasyAnalyzerApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   └── test/
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java 17 (JDK 17)
- MySQL 8.0+
- Maven 3.6+ (or use included wrapper)

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE fantasy_analyzer;
CREATE USER 'fantasy_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON fantasy_analyzer.* TO 'fantasy_user'@'localhost';
FLUSH PRIVILEGES;
```

2. Update `src/main/resources/application-dev.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fantasy_analyzer
spring.datasource.username=fantasy_user
spring.datasource.password=your_password
```

### Build and Run

```bash
# Build the project
./mvnw clean install

# Run the application (dev profile)
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev

# Run tests
./mvnw test

# Package as JAR
./mvnw package
```

The API will be available at `http://localhost:8080`

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Sleeper Integration
- `GET /api/sleeper/user/{username}` - Get Sleeper user
- `GET /api/sleeper/user/{userId}/leagues/{season}` - Get user's leagues
- `GET /api/sleeper/league/{leagueId}/rosters` - Get league rosters
- `GET /api/sleeper/league/{leagueId}/matchups/{week}` - Get matchups for week
- `GET /api/sleeper/players/nfl` - Get all NFL players

## 🔑 Environment Variables

Create `.env` file or set environment variables:

```properties
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=fantasy_analyzer
MYSQL_USERNAME=fantasy_user
MYSQL_PASSWORD=your_password
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthControllerTest

# Run specific test method
./mvnw test -Dtest=AuthControllerTest#testRegister

# Run tests with coverage
./mvnw test jacoco:report
```

## 📝 Development Guidelines

Please follow the coding standards and architecture principles defined in:
- [Architecture Guide](../docs/ARCHITECTURE_GUIDE.md)
- [Claude Development Guide](../docs/CLAUDE.md)

Key principles:
- **SOLID** principles
- **Clean Code** practices
- **Domain Driven Design** (DDD)
- **Layered Architecture**: Controller → Service → Repository → Entity

## 🔧 Configuration Profiles

- `default`: Default configuration
- `dev`: Development profile with detailed logging
- `prod`: Production profile (to be configured)

Activate profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

## 📦 Dependencies

Key dependencies:
- `spring-boot-starter-web` - REST API
- `spring-boot-starter-data-jpa` - Database access
- `spring-boot-starter-security` - Security and authentication
- `spring-boot-starter-validation` - Input validation
- `mysql-connector-j` - MySQL driver
- `jjwt` - JWT token handling
- `lombok` - Boilerplate reduction

## 🐛 Troubleshooting

### Application won't start
- Verify Java 17 is installed: `java -version`
- Check MySQL is running: `mysql -u root -p`
- Verify database credentials in `application-dev.properties`

### Tests failing
- Ensure MySQL is accessible
- Check test database configuration

### Build errors
- Clean Maven cache: `./mvnw clean`
- Update dependencies: `./mvnw dependency:resolve`

## 🔗 Related Documentation

- [Sleeper API Documentation](https://docs.sleeper.com/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
