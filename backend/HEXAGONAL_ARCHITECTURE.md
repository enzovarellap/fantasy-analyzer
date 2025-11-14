# Arquitetura Hexagonal - Fantasy Analyzer

Este projeto utiliza **Arquitetura Hexagonal** (Ports & Adapters) combinada com princípios de **Clean Architecture** e **DDD**.

## 📐 Estrutura de Camadas

```
backend/src/main/java/com/fantasyfootball/fantasy_analyzer/
├── domain/                    # Camada de Domínio (Núcleo)
│   ├── model/                 # Entities, Value Objects, Aggregates
│   └── ports/                 # Interfaces (contratos)
│       ├── input/             # Use Cases (o que a aplicação faz)
│       └── output/            # Repositories, External Services (o que a aplicação precisa)
│
├── application/               # Camada de Aplicação (Casos de Uso)
│   ├── service/               # Implementação dos Use Cases
│   ├── usecase/               # Use Case específicos
│   └── dto/                   # Data Transfer Objects
│
├── infrastructure/            # Camada de Infraestrutura (Adapters)
│   ├── persistence/           # Implementações de Repositories (JPA, etc.)
│   ├── external/              # Clientes de APIs externas
│   └── security/              # Configurações de segurança
│
├── controller/                # Camada de Apresentação (API REST)
│   └── (controllers REST)
│
├── common/                    # Código compartilhado
│   ├── exception/             # Exceções customizadas
│   ├── response/              # Padrões de resposta
│   └── validation/            # Validações comuns
│
└── config/                    # Configurações Spring
```

## 🔄 Fluxo de uma Requisição

```
1. HTTP Request
   ↓
2. Controller (Presentation Layer)
   - Recebe request
   - Valida entrada básica
   - Chama Use Case Interface
   ↓
3. Use Case Service (Application Layer)
   - Implementa lógica de caso de uso
   - Orquestra chamadas ao domínio
   - Usa Output Ports (interfaces)
   ↓
4. Domain Logic (Domain Layer)
   - Aplica regras de negócio
   - Valida invariantes
   - Retorna resultado
   ↓
5. Output Adapters (Infrastructure Layer)
   - Repository: Persiste dados (JPA)
   - External Service: Chama APIs externas
   ↓
6. Response
   - Constrói DTO
   - Retorna ao Controller
   - Controller retorna HTTP Response
```

## 🎯 Princípios

### 1. Dependency Rule

```
Presentation → Application → Domain ← Infrastructure
```

**Regras:**
- **Domain** não depende de nada (núcleo puro)
- **Application** depende apenas de **Domain**
- **Infrastructure** depende de **Domain** (implementa ports)
- **Controller** depende de **Application**

### 2. Ports & Adapters

#### Input Ports (Primary/Driving)
Definem o que a aplicação **oferece**:

```java
// domain/ports/input/CreateUserUseCase.java
public interface CreateUserUseCase {
    UserResponse execute(CreateUserCommand command);
}

// application/service/CreateUserService.java
@Service
public class CreateUserService implements CreateUserUseCase {
    private final UserRepositoryPort userRepository;

    @Override
    public UserResponse execute(CreateUserCommand command) {
        // Lógica do caso de uso
    }
}
```

#### Output Ports (Secondary/Driven)
Definem o que a aplicação **precisa**:

```java
// domain/ports/output/UserRepositoryPort.java
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
}

// infrastructure/persistence/JpaUserRepository.java
@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        // Implementação JPA
    }
}
```

## 📝 Exemplo Completo: Criar Usuário

### 1. Controller (API)

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(
        @Valid @RequestBody CreateUserRequest request
    ) {
        CreateUserCommand command = CreateUserCommand.from(request);
        UserResponse response = createUserUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
```

### 2. Use Case Interface (Domain Port - Input)

```java
package com.fantasyfootball.fantasy_analyzer.domain.ports.input;

public interface CreateUserUseCase {
    UserResponse execute(CreateUserCommand command);
}
```

### 3. Use Case Implementation (Application Service)

```java
package com.fantasyfootball.fantasy_analyzer.application.service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse execute(CreateUserCommand command) {
        log.debug("Creating user with email: {}", command.getEmail());

        // Validação de regra de negócio
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new BusinessException("User already exists");
        }

        // Criação da entidade de domínio
        User user = User.create(
            command.getEmail(),
            passwordEncoder.encode(command.getPassword()),
            command.getName()
        );

        // Persistência através do port
        User savedUser = userRepository.save(user);

        log.info("User created successfully: {}", savedUser.getId());

        return UserResponse.from(savedUser);
    }
}
```

### 4. Repository Port (Domain Port - Output)

```java
package com.fantasyfootball.fantasy_analyzer.domain.ports.output;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void delete(User user);
}
```

### 5. Repository Adapter (Infrastructure)

```java
package com.fantasyfootball.fantasy_analyzer.infrastructure.persistence;

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

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
```

### 6. JPA Repository (Infrastructure)

```java
package com.fantasyfootball.fantasy_analyzer.infrastructure.persistence;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### 7. Domain Model

```java
package com.fantasyfootball.fantasy_analyzer.domain.model;

@Getter
@Builder
public class User {
    private Long id;
    private String email;
    private String password;
    private String name;
    private LocalDateTime createdAt;

    // Factory method
    public static User create(String email, String encodedPassword, String name) {
        return User.builder()
            .email(email)
            .password(encodedPassword)
            .name(name)
            .createdAt(LocalDateTime.now())
            .build();
    }

    // Business logic methods
    public void updateProfile(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new ValidationException("Name cannot be empty");
        }
        this.name = newName;
    }
}
```

## 🧪 Testabilidade

### Teste de Use Case (Application Layer)

```java
@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserService service;

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserCommand command = new CreateUserCommand(
            "test@example.com", "password123", "Test User"
        );

        when(userRepository.existsByEmail(command.getEmail()))
            .thenReturn(false);
        when(passwordEncoder.encode(command.getPassword()))
            .thenReturn("encoded");
        when(userRepository.save(any(User.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // When
        UserResponse response = service.execute(command);

        // Then
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }
}
```

## 📋 Checklist para Novos Features

Ao implementar uma nova funcionalidade:

- [ ] 1. Definir **Domain Model** (entities, value objects)
- [ ] 2. Criar **Input Port** (use case interface) em `domain/ports/input/`
- [ ] 3. Criar **Output Ports** (repository, external service) em `domain/ports/output/`
- [ ] 4. Implementar **Use Case** em `application/service/`
- [ ] 5. Criar **DTOs** em `application/dto/`
- [ ] 6. Implementar **Adapters** em `infrastructure/`
- [ ] 7. Criar **Controller** para expor API REST
- [ ] 8. Escrever **Testes** (unitários e integração)
- [ ] 9. Documentar com **OpenAPI** annotations

## 🎨 Benefícios da Arquitetura

1. **Independência de Frameworks**: Core não depende de Spring, JPA, etc.
2. **Testabilidade**: Fácil mockar dependências
3. **Manutenibilidade**: Mudanças isoladas
4. **Flexibilidade**: Trocar DB ou frameworks sem alterar core
5. **Clareza**: Separação clara de responsabilidades
6. **Escalabilidade**: Fácil adicionar novos use cases

## 📚 Referências

- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)
