# Domain Ports

This directory contains **interfaces** that define the contracts for the domain layer.

## Structure

```
ports/
├── input/      # Use case interfaces (driven by application layer)
└── output/     # Repository/External service interfaces (driven by infrastructure)
```

## Hexagonal Architecture (Ports & Adapters)

### Ports
- **Input Ports (Primary/Driving)**: Use cases that the application offers
- **Output Ports (Secondary/Driven)**: Services that the application needs

### Example:

**Input Port** (Use Case):
```java
public interface CreateUserUseCase {
    UserResponse execute(CreateUserCommand command);
}
```

**Output Port** (Repository):
```java
public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
}
```

## Benefits

1. **Dependency Inversion**: Core business logic doesn't depend on external frameworks
2. **Testability**: Easy to mock dependencies
3. **Flexibility**: Can change infrastructure without touching business logic
4. **Clear Boundaries**: Well-defined responsibilities
