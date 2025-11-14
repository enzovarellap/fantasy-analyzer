# Infrastructure Layer

This layer contains **adapters** that implement domain ports.

## Responsibilities

- Implement repository ports (database access)
- Implement external service ports (API clients)
- Security configurations
- Framework-specific code

## Structure

```
infrastructure/
├── persistence/    # JPA repositories, database adapters
├── external/       # External API clients (Sleeper API, etc.)
└── security/       # Security configurations, JWT, etc.
```

## Example - Repository Adapter

```java
@Repository
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

## Example - External Service Adapter

```java
@Service
@RequiredArgsConstructor
public class SleeperApiAdapter implements SleeperServicePort {

    private final RestTemplate restTemplate;

    @Override
    public SleeperUser getUser(String username) {
        String url = SLEEPER_API_URL + "/user/" + username;
        return restTemplate.getForObject(url, SleeperUser.class);
    }
}
```
