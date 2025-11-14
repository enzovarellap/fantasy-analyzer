# Application Layer

This layer contains **use case implementations** and application services.

## Responsibilities

- Orchestrate domain logic
- Implement use case interfaces (from domain/ports/input)
- Use repository ports (from domain/ports/output)
- Handle DTOs (convert between domain and API models)
- Transaction management
- Application-level validation

## Structure

```
application/
├── service/       # Use case implementations
└── dto/          # Data Transfer Objects (Request/Response)
```

## Example

```java
@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse execute(CreateUserCommand command) {
        // Orchestrate use case logic
        User user = User.create(command.getEmail(), ...);
        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }
}
```
