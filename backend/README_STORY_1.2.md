# Story 1.2 - Configuração do Spring Boot com Arquitetura em Camadas

## ✅ Tasks Completadas

### 1.2.1 ✅ Criar projeto Spring Boot com módulos essenciais

**Dependências Adicionadas:**
- ✅ Spring Boot Starter Web
- ✅ Spring Boot Starter Data JPA
- ✅ Spring Boot Starter Security
- ✅ Spring Boot Starter Validation
- ✅ **Spring Boot Starter Actuator** (Health checks, métricas)
- ✅ **SpringDoc OpenAPI** (Swagger UI)
- ✅ **MapStruct** (Mapeamento DTOs)
- ✅ MySQL Connector + H2 (test)
- ✅ JWT (JJWT)
- ✅ Lombok

### 1.2.2 ✅ Aplicar arquitetura hexagonal (ports/adapters)

**Estrutura Criada:**

```
src/main/java/com/fantasyfootball/fantasy_analyzer/
├── domain/                    # Camada de Domínio (Núcleo)
│   ├── model/                 # Entities, Value Objects
│   └── ports/                 # Interfaces (contratos)
│       ├── input/             # Use Cases
│       └── output/            # Repositories, External Services
│
├── application/               # Camada de Aplicação
│   ├── service/               # Implementação Use Cases
│   ├── usecase/               # Use Cases específicos
│   └── dto/                   # Data Transfer Objects
│
├── infrastructure/            # Camada de Infraestrutura
│   ├── persistence/           # Implementações JPA
│   ├── external/              # APIs externas
│   └── security/              # Configs de segurança
│
├── controller/                # API REST
│
├── common/                    # Código compartilhado
│   ├── exception/             # Exceções customizadas
│   ├── response/              # Padrões de resposta
│   └── validation/            # Validações
│
└── config/                    # Configurações Spring
```

**Componentes Implementados:**

#### Exception Handling
- `FantasyAnalyzerException` - Base exception
- `ResourceNotFoundException`
- `BusinessException`
- `ValidationException`
- `UnauthorizedException`
- `GlobalExceptionHandler` - Exception handler global

#### Response Pattern
- `ApiResponse<T>` - Wrapper padrão para todas as respostas
- `ErrorDetails` - Detalhes de erro padronizados

### 1.2.3 ✅ Configurar logging (SLF4J + Logback)

**Arquivo:** `src/main/resources/logback-spring.xml`

**Features:**
- ✅ Console appender com cores
- ✅ File appender com rolling policy
- ✅ Error file separado
- ✅ Configuração por profile (dev, test, prod)
- ✅ Logs estruturados

**Níveis de Log por Profile:**

**Development:**
```
com.fantasyfootball.fantasy_analyzer: DEBUG
org.springframework.web: DEBUG
org.springframework.security: DEBUG
org.hibernate.SQL: DEBUG
```

**Production:**
```
com.fantasyfootball.fantasy_analyzer: INFO
org.springframework: WARN
org.hibernate: WARN
```

**Test:**
```
com.fantasyfootball.fantasy_analyzer: DEBUG
org.springframework: WARN
```

### 1.2.4 ✅ Configurar variáveis de ambiente (profiles)

**Arquivos Criados:**

1. **`application.yml`** - Configuração base
2. **`application-dev.yml`** - Development
3. **`application-test.yml`** - Testing (H2 in-memory)
4. **`application-prod.yml`** - Production
5. **`.env.example`** - Template de variáveis

**Variáveis de Ambiente:**

```bash
# Database
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=fantasy_analyzer
MYSQL_USERNAME=fantasy_user
MYSQL_PASSWORD=***

# JWT
JWT_SECRET=***
JWT_EXPIRATION=86400000

# Profile
SPRING_PROFILES_ACTIVE=dev
```

**Features por Profile:**

| Feature | Dev | Test | Prod |
|---------|-----|------|------|
| show-sql | ✅ | ❌ | ❌ |
| ddl-auto | update | create-drop | validate |
| Database | MySQL | H2 (memory) | MySQL |
| Logs detalhados | ✅ | ❌ | ❌ |
| DevTools | ✅ | ❌ | ❌ |
| Actuator all | ✅ | ❌ | ❌ |
| Swagger UI | ✅ | ❌ | ⚠️ |

### 1.2.5 ✅ Adicionar Swagger/OpenAPI

**Arquivo:** `config/OpenApiConfig.java`

**Acesso:**
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

**Features:**
- ✅ JWT Authentication configurado
- ✅ Servers (dev/prod) configurados
- ✅ Metadata (título, versão, contato)
- ✅ Security scheme (Bearer token)
- ✅ Filtragem por paths (`/api/**`)

**Exemplo de Uso no Controller:**

```java
@Tag(name = "Authentication", description = "Authentication endpoints")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Operation(
        summary = "Login",
        description = "Authenticate user and get JWT token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) {
        // ...
    }
}
```

## 📊 Arquitetura Implementada

### Dependency Flow

```
HTTP Request
    ↓
Controller (Presentation)
    ↓
Use Case Interface (Domain Port - Input)
    ↓
Service Implementation (Application)
    ↓
Repository Port (Domain Port - Output)
    ↓
Repository Adapter (Infrastructure)
    ↓
JPA Repository
    ↓
Database
```

### Example Flow: Create User

```
POST /api/users
    ↓
UserController.create()
    ↓
CreateUserUseCase.execute()
    ↓
CreateUserService (implements CreateUserUseCase)
    ↓
UserRepositoryPort.save()
    ↓
JpaUserRepositoryAdapter (implements UserRepositoryPort)
    ↓
UserJpaRepository (extends JpaRepository)
    ↓
MySQL Database
```

## 🧪 Health Checks & Actuator

**Endpoints Disponíveis:**

```bash
# Health check
GET /actuator/health

# Info
GET /actuator/info

# Metrics
GET /actuator/metrics

# Prometheus (para monitoring)
GET /actuator/prometheus
```

**Response Example:**

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## 📝 Como Usar

### 1. Configurar Environment

```bash
cd backend
cp src/main/resources/.env.example .env
# Editar .env com suas credenciais
```

### 2. Rodar Aplicação

```bash
# Development
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev

# Production
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod

# Test
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=test
```

### 3. Acessar Documentação

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health**: http://localhost:8080/actuator/health

### 4. Testar API

```bash
# Health check
curl http://localhost:8080/actuator/health

# Login (exemplo)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

## 📚 Documentação Criada

1. **`HEXAGONAL_ARCHITECTURE.md`** - Guia completo da arquitetura
2. **`domain/ports/README.md`** - Explicação de Ports
3. **`backend/README_STORY_1.2.md`** - Este arquivo

## ✨ Features Adicionais

### MapStruct Integration

Configurado para mapeamento automático entre:
- Domain Models ↔ DTOs
- DTOs ↔ Entities

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
    User toDomain(UserEntity entity);
    UserEntity toEntity(User user);
}
```

### Global Exception Handling

Todas as exceções retornam formato padronizado:

```json
{
  "success": false,
  "message": "User not found with id: '123'",
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "User not found with id: '123'"
  },
  "timestamp": "2025-01-13T10:30:00"
}
```

### API Response Pattern

Sucesso:
```json
{
  "success": true,
  "data": { ... },
  "timestamp": "2025-01-13T10:30:00"
}
```

Erro:
```json
{
  "success": false,
  "message": "Validation failed",
  "error": {
    "code": "VALIDATION_ERROR",
    "validationErrors": {
      "email": "must be a valid email",
      "password": "size must be between 6 and 50"
    }
  },
  "timestamp": "2025-01-13T10:30:00"
}
```

## 🎯 Próximos Passos

Para implementar um novo feature:

1. Definir Domain Model em `domain/model/`
2. Criar Input Port em `domain/ports/input/`
3. Criar Output Ports em `domain/ports/output/`
4. Implementar Service em `application/service/`
5. Criar DTOs em `application/dto/`
6. Implementar Adapters em `infrastructure/`
7. Criar Controller
8. Adicionar testes
9. Documentar com OpenAPI annotations

## 📋 Checklist de Qualidade

- ✅ Arquitetura hexagonal implementada
- ✅ Logging configurado por perfis
- ✅ Exception handling global
- ✅ Swagger/OpenAPI documentado
- ✅ Health checks ativos
- ✅ Profiles (dev/test/prod) configurados
- ✅ Environment variables setup
- ✅ Response pattern padronizado
- ✅ MapStruct configurado
- ✅ Actuator habilitado

---

**Story 1.2 - COMPLETA** ✅
