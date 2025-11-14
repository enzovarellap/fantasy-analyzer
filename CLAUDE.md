# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Fantasy Analyzer is a Spring Boot 3.5.7 application for fantasy football analysis. The project uses:
- Java 17
- Maven for build management
- Spring Boot with Spring Data JPA, Spring Security, Spring Web, and Validation
- MySQL database (runtime dependency)
- Lombok for reducing boilerplate code
- Spring Boot DevTools for development

**Important**: The original package name 'com.fantasyfootball.fantasy-analyzer' is invalid - this project uses 'com.fantasyfootball.fantasy_analyzer' instead (with underscore).

## Build and Run Commands

### Build the project
```bash
./mvnw clean install
```

### Run the application
```bash
./mvnw spring-boot:run
```

### Run tests
```bash
./mvnw test
```

### Run a single test class
```bash
./mvnw test -Dtest=ClassName
```

### Run a single test method
```bash
./mvnw test -Dtest=ClassName#methodName
```

### Package the application
```bash
./mvnw package
```

### Skip tests during build
```bash
./mvnw clean install -DskipTests
```

## Architecture

### Technology Stack
- **Framework**: Spring Boot 3.5.7 with Spring MVC for RESTful web services
- **Security**: Spring Security for authentication and authorization
- **Data Access**: Spring Data JPA with MySQL database
- **Validation**: Spring Boot Validation for input validation
- **Development**: Spring Boot DevTools for automatic restart and live reload

### Project Structure
```
src/
├── main/
│   ├── java/com/fantasyfootball/fantasy_analyzer/
│   │   └── FantasyAnalyzerApplication.java (main application class)
│   └── resources/
│       ├── application.properties (configuration)
│       ├── static/ (static resources)
│       └── templates/ (view templates)
└── test/
    └── java/com/fantasyfootball/fantasy_analyzer/
```

### Key Configuration
- **Package Structure**: All code should be under `com.fantasyfootball.fantasy_analyzer` package
- **Lombok**: Annotation processing is configured for Lombok support. Use Lombok annotations (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor, etc.) to reduce boilerplate
- **Database**: MySQL connector is available as runtime dependency - configure connection details in `application.properties`

### Expected Architecture (when implemented)
This is a Spring Boot application following standard layered architecture:
1. **Controller Layer**: REST endpoints for handling HTTP requests
2. **Service Layer**: Business logic and transaction management
3. **Repository Layer**: Data access using Spring Data JPA repositories
4. **Model/Entity Layer**: JPA entities representing database tables
5. **DTO Layer**: Data Transfer Objects for API requests/responses
6. **Security Configuration**: Spring Security setup for authentication/authorization

## Database Configuration

The application is configured to use MySQL. Update `src/main/resources/application.properties` with:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fantasy_analyzer
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Development Notes

- Maven wrapper (`mvnw`) is included - no need to install Maven separately
- Spring Boot DevTools enables automatic restart on code changes during development
- Lombok requires annotation processing support in your IDE
- The application uses Spring Boot 3.x which requires Java 17 minimum
