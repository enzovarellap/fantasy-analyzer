# Architecture & Development Guide
## Fantasy Analyzer - Guia de Arquitetura e Desenvolvimento

**Versão:** 1.0
**Última atualização:** 2025-01-13

---

## Índice

1. [Visão Geral](#1-visão-geral)
2. [Princípios Fundamentais](#2-princípios-fundamentais)
   - [SOLID](#21-princípios-solid)
   - [Clean Code](#22-clean-code)
3. [Domain Driven Design (DDD)](#3-domain-driven-design-ddd)
4. [Arquitetura do Projeto](#4-arquitetura-do-projeto)
5. [Estrutura de Camadas](#5-estrutura-de-camadas)
6. [Padrões de Design](#6-padrões-de-design)
7. [Convenções de Código](#7-convenções-de-código)
8. [Guia para Iniciantes](#8-guia-para-iniciantes)
9. [Exemplos Práticos](#9-exemplos-práticos)
10. [Decisões de Arquitetura](#10-decisões-de-arquitetura)

---

## 1. Visão Geral

### 1.1 Sobre o Projeto

Fantasy Analyzer é uma aplicação Spring Boot para análise de fantasy football que integra com a API do Sleeper. O projeto foi arquitetado seguindo os princípios de **Clean Architecture**, **Domain Driven Design (DDD)**, **SOLID** e **Clean Code**.

### 1.2 Stack Tecnológica

```
Backend Framework:    Spring Boot 3.5.7
Linguagem:           Java 17
Build Tool:          Maven
Database:            MySQL
ORM:                 Spring Data JPA (Hibernate)
Security:            Spring Security + JWT
Logging:             SLF4J + Logback
Development:         Spring Boot DevTools, Lombok
```

### 1.3 Objetivo do Guia

Este documento serve como:
- **Referência arquitetural** para toda a equipe
- **Guia de boas práticas** de desenvolvimento
- **Material educacional** para desenvolvedores iniciantes
- **Documentação de decisões** arquiteturais

---

## 2. Princípios Fundamentais

### 2.1 Princípios SOLID

SOLID é um acrônimo para cinco princípios de design orientado a objetos que tornam o software mais compreensível, flexível e mantível.

#### 2.1.1 Single Responsibility Principle (SRP)

**Definição:** Uma classe deve ter apenas uma razão para mudar, ou seja, deve ter apenas uma responsabilidade.

**Aplicação no Projeto:**

```java
// ❌ MAU EXEMPLO - Classe com múltiplas responsabilidades
public class UserService {
    public void createUser(User user) { }
    public void sendEmail(User user) { }
    public void generateReport(User user) { }
    public void validateUser(User user) { }
}

// ✅ BOM EXEMPLO - Responsabilidades separadas
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserValidator userValidator;

    public User createUser(RegisterRequest request) {
        userValidator.validate(request);
        User user = userRepository.save(toEntity(request));
        emailService.sendWelcomeEmail(user);
        return user;
    }
}

public class EmailService {
    public void sendWelcomeEmail(User user) { }
}

public class UserValidator {
    public void validate(RegisterRequest request) { }
}
```

**No nosso projeto:**
- `AuthService` - apenas autenticação
- `SleeperApiService` - apenas integração com API externa
- `CustomUserDetailsService` - apenas carregamento de usuários para autenticação

#### 2.1.2 Open/Closed Principle (OCP)

**Definição:** Entidades de software devem estar abertas para extensão, mas fechadas para modificação.

**Aplicação no Projeto:**

```java
// ✅ BOM EXEMPLO - Usando Strategy Pattern
public interface AuthenticationStrategy {
    AuthResponse authenticate(LoginRequest request);
}

public class JwtAuthenticationStrategy implements AuthenticationStrategy {
    @Override
    public AuthResponse authenticate(LoginRequest request) {
        // Implementação JWT
    }
}

public class OAuth2AuthenticationStrategy implements AuthenticationStrategy {
    @Override
    public AuthResponse authenticate(LoginRequest request) {
        // Implementação OAuth2
    }
}

// Service pode ser extendido sem modificação
public class AuthService {
    private final AuthenticationStrategy strategy;

    public AuthResponse login(LoginRequest request) {
        return strategy.authenticate(request);
    }
}
```

**Benefícios:**
- Adicionar novos tipos de autenticação sem alterar código existente
- Facilita testes unitários (mock de strategies)
- Reduz risco de bugs em código já testado

#### 2.1.3 Liskov Substitution Principle (LSP)

**Definição:** Objetos de uma superclasse devem poder ser substituídos por objetos de suas subclasses sem quebrar a aplicação.

**Aplicação no Projeto:**

```java
// ✅ BOM EXEMPLO
public interface DataRepository<T> {
    T save(T entity);
    Optional<T> findById(Long id);
    void delete(T entity);
}

// Todas as implementações podem substituir a interface
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Completamente substituível
    Optional<Usuario> findByEmail(String email);
}
```

**Violação comum a evitar:**

```java
// ❌ MAU EXEMPLO - Violação de LSP
public class ReadOnlyRepository implements DataRepository {
    @Override
    public void save(Object entity) {
        throw new UnsupportedOperationException("Read-only!");
    }
}
```

#### 2.1.4 Interface Segregation Principle (ISP)

**Definição:** Clientes não devem ser forçados a depender de interfaces que não utilizam.

**Aplicação no Projeto:**

```java
// ❌ MAU EXEMPLO - Interface "gordinha"
public interface UserOperations {
    void create();
    void read();
    void update();
    void delete();
    void sendEmail();
    void generateReport();
    void exportToPdf();
}

// ✅ BOM EXEMPLO - Interfaces segregadas
public interface UserCrudOperations {
    User create(RegisterRequest request);
    User findById(Long id);
    User update(Long id, UpdateRequest request);
    void delete(Long id);
}

public interface UserNotificationOperations {
    void sendEmail(User user, String message);
}

public interface UserReportOperations {
    Report generateReport(User user);
    byte[] exportToPdf(Report report);
}
```

**No nosso projeto:**
- Repositories específicos por entidade
- Services focados em domínios específicos
- DTOs segregados por caso de uso

#### 2.1.5 Dependency Inversion Principle (DIP)

**Definição:** Módulos de alto nível não devem depender de módulos de baixo nível. Ambos devem depender de abstrações.

**Aplicação no Projeto:**

```java
// ✅ BOM EXEMPLO - Inversão de dependência
public class AuthService {
    // Dependemos de abstrações, não de implementações concretas
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
}

// Spring injeta as implementações automaticamente
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Implementação concreta
    }
}
```

**Benefícios:**
- Facilita testes (podemos injetar mocks)
- Permite trocar implementações facilmente
- Reduz acoplamento entre módulos

---

### 2.2 Clean Code

#### 2.2.1 Nomes Significativos

**Princípio:** O nome deve revelar a intenção.

```java
// ❌ MAU
int d; // tempo decorrido em dias
List<int[]> list1;

// ✅ BOM
int elapsedTimeInDays;
List<Player> activePlayers;
```

**Regras para nomes:**
1. **Classes/Interfaces:** Substantivos (User, PlayerService, LeagueRepository)
2. **Métodos:** Verbos (save, calculateScore, validateEmail)
3. **Booleanos:** Predicados (isActive, hasPermission, canEdit)
4. **Constantes:** UPPER_SNAKE_CASE (MAX_RETRIES, DEFAULT_TIMEOUT)

#### 2.2.2 Funções Pequenas

**Princípio:** Funções devem fazer apenas uma coisa e fazê-la bem.

```java
// ❌ MAU EXEMPLO - Função muito longa
public AuthResponse register(RegisterRequest request) {
    // Validação
    if (request.getEmail() == null || request.getEmail().isEmpty()) {
        throw new ValidationException("Email required");
    }
    if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        throw new ValidationException("Invalid email");
    }
    if (request.getPassword() == null || request.getPassword().length() < 6) {
        throw new ValidationException("Password must be at least 6 characters");
    }

    // Verificar se existe
    if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new BusinessException("User already exists");
    }

    // Criar usuário
    Usuario usuario = new Usuario();
    usuario.setEmail(request.getEmail());
    usuario.setPassword(passwordEncoder.encode(request.getPassword()));
    usuario.setNome(request.getNome());
    usuario.setCreatedAt(LocalDateTime.now());

    // Salvar
    usuario = usuarioRepository.save(usuario);

    // Gerar token
    String token = jwtUtil.generateToken(usuario.getEmail());

    return new AuthResponse(token, usuario.getEmail());
}

// ✅ BOM EXEMPLO - Funções pequenas e focadas
public AuthResponse register(RegisterRequest request) {
    validateRegistrationRequest(request);
    checkUserNotExists(request.getEmail());

    Usuario usuario = createUsuario(request);
    Usuario savedUsuario = usuarioRepository.save(usuario);

    return buildAuthResponse(savedUsuario);
}

private void validateRegistrationRequest(RegisterRequest request) {
    if (!isValidEmail(request.getEmail())) {
        throw new ValidationException("Invalid email format");
    }
    if (!isValidPassword(request.getPassword())) {
        throw new ValidationException("Password must be at least 6 characters");
    }
}

private void checkUserNotExists(String email) {
    usuarioRepository.findByEmail(email)
        .ifPresent(u -> {
            throw new BusinessException("User already exists");
        });
}

private Usuario createUsuario(RegisterRequest request) {
    return Usuario.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .nome(request.getNome())
        .createdAt(LocalDateTime.now())
        .build();
}

private AuthResponse buildAuthResponse(Usuario usuario) {
    String token = jwtUtil.generateToken(usuario.getEmail());
    return new AuthResponse(token, usuario.getEmail());
}
```

**Regras:**
- Máximo 20 linhas por função (idealmente 5-10)
- Máximo 3 parâmetros (use DTOs para mais)
- Um nível de abstração por função

#### 2.2.3 Comentários

**Princípio:** O código deve ser auto-explicativo. Comentários são necessários apenas quando o código não consegue se explicar.

```java
// ❌ MAU - Comentário óbvio
// Retorna o usuário
public User getUser() {
    return user;
}

// ❌ MAU - Código que precisa de comentário para ser entendido
// Verifica se o usuário tem permissão (tipo 1 ou 2)
if (user.getType() == 1 || user.getType() == 2) {
    // permitir acesso
}

// ✅ BOM - Código auto-explicativo
public boolean hasAdministrativePermission(Usuario usuario) {
    return usuario.isAdmin() || usuario.isModerator();
}

// ✅ BOM - Comentário explicando o "porquê", não o "o quê"
/**
 * A Sleeper API tem rate limit de 1000 requisições por minuto.
 * Implementamos retry com backoff exponencial para evitar erros 429.
 */
@Retryable(
    value = RateLimitException.class,
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2)
)
public SleeperUser fetchUser(String username) {
    // implementação
}
```

**Quando usar comentários:**
- Documentação de API pública (JavaDoc)
- Explicação de decisões não-óbvias
- TODO/FIXME temporários (com issue associada)
- Avisos sobre consequências

#### 2.2.4 Tratamento de Erros

**Princípio:** Use exceções em vez de códigos de retorno.

```java
// ❌ MAU EXEMPLO
public int saveUser(User user) {
    if (user == null) return -1;
    if (user.getEmail() == null) return -2;
    if (userExists(user.getEmail())) return -3;
    // ...
    return 1; // sucesso
}

// ✅ BOM EXEMPLO
public Usuario saveUser(RegisterRequest request) {
    Objects.requireNonNull(request, "Request cannot be null");

    validateEmail(request.getEmail());

    if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new UserAlreadyExistsException(
            "User with email " + request.getEmail() + " already exists"
        );
    }

    return usuarioRepository.save(toEntity(request));
}
```

**Hierarquia de exceções no projeto:**

```java
// Exception base do domínio
public class FantasyAnalyzerException extends RuntimeException {
    public FantasyAnalyzerException(String message) {
        super(message);
    }
}

// Exceções específicas
public class UserAlreadyExistsException extends FantasyAnalyzerException { }
public class InvalidCredentialsException extends FantasyAnalyzerException { }
public class SleeperApiException extends FantasyAnalyzerException { }
public class ResourceNotFoundException extends FantasyAnalyzerException { }
```

#### 2.2.5 Formatação

**Princípio:** Código bem formatado comunica profissionalismo.

```java
// ✅ Formatação consistente
public class AuthService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = findUsuarioByEmail(request.getEmail());
        validatePassword(request.getPassword(), usuario.getPassword());

        String token = jwtUtil.generateToken(usuario.getEmail());

        return AuthResponse.builder()
            .token(token)
            .email(usuario.getEmail())
            .build();
    }

    private Usuario findUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }
}
```

**Regras de formatação:**
- Linha em branco entre métodos
- Constantes no topo da classe
- Ordem: constantes → campos → construtores → métodos públicos → métodos privados
- Máximo 120 caracteres por linha
- 4 espaços de indentação

---

## 3. Domain Driven Design (DDD)

### 3.1 Conceitos Fundamentais

Domain Driven Design é uma abordagem de desenvolvimento de software que coloca o **domínio do negócio** no centro do processo de desenvolvimento.

#### 3.1.1 Ubiquitous Language (Linguagem Ubíqua)

**Definição:** Linguagem comum entre desenvolvedores e especialistas do domínio.

**Aplicação no projeto Fantasy Football:**

```
Termos do Domínio:
- League (Liga): Competição de fantasy football
- Roster (Escalação): Time de um usuário em uma liga
- Player (Jogador): Atleta da NFL
- Matchup (Confronto): Jogo entre dois rosters
- Points (Pontos): Pontuação de um jogador/roster
- Draft: Processo de seleção de jogadores
- Waiver: Sistema de reivindicação de jogadores
- Trade: Troca de jogadores entre usuários
```

**No código:**

```java
// ✅ Usando linguagem do domínio
public class League {
    private String name;
    private LeagueSettings settings;
    private List<Roster> rosters;
    private DraftStatus draftStatus;
}

// ❌ Não use termos técnicos genéricos
public class Entity1 {
    private String field1;
    private List<Entity2> list1;
}
```

#### 3.1.2 Bounded Contexts (Contextos Delimitados)

**Definição:** Limites explícitos dentro dos quais um modelo de domínio é definido e aplicável.

**Contextos no Fantasy Analyzer:**

```
┌─────────────────────────────────────────────────────────┐
│                   Fantasy Analyzer                      │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌──────────────────┐  ┌──────────────────┐            │
│  │   User Context   │  │  League Context  │            │
│  │                  │  │                  │            │
│  │ - Authentication │  │ - League         │            │
│  │ - Authorization  │  │ - Roster         │            │
│  │ - User Profile   │  │ - Matchup        │            │
│  └──────────────────┘  └──────────────────┘            │
│                                                         │
│  ┌──────────────────┐  ┌──────────────────┐            │
│  │  Player Context  │  │ Analysis Context │            │
│  │                  │  │                  │            │
│  │ - NFL Players    │  │ - Statistics     │            │
│  │ - Stats          │  │ - Projections    │            │
│  │ - Performance    │  │ - Rankings       │            │
│  └──────────────────┘  └──────────────────┘            │
│                                                         │
│  ┌──────────────────┐                                  │
│  │ Integration Ctx  │                                  │
│  │                  │                                  │
│  │ - Sleeper API    │                                  │
│  │ - External Data  │                                  │
│  └──────────────────┘                                  │
└─────────────────────────────────────────────────────────┘
```

**Estrutura de pacotes refletindo bounded contexts:**

```
com.fantasyfootball.fantasy_analyzer/
├── user/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── api/
├── league/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── api/
├── player/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── api/
└── integration/
    └── sleeper/
```

#### 3.1.3 Entities (Entidades)

**Definição:** Objetos que têm identidade única que persiste ao longo do tempo.

```java
// ✅ Entidade - tem identidade (ID)
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identidade única

    private String email;
    private String nome;

    // Dois usuários são iguais se têm o mesmo ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

#### 3.1.4 Value Objects (Objetos de Valor)

**Definição:** Objetos sem identidade conceitual, definidos apenas por seus atributos.

```java
// ✅ Value Object - não tem identidade, é imutável
@Embeddable
public class Email {

    private final String value;

    public Email(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public String getValue() {
        return value;
    }

    // Dois emails são iguais se têm o mesmo valor
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

// Uso no Entity
@Entity
public class Usuario {
    @Embedded
    private Email email;
}
```

**Outros Value Objects úteis:**

```java
@Embeddable
public class PlayerStats {
    private Integer passingYards;
    private Integer rushingYards;
    private Integer touchdowns;

    public Double calculateFantasyPoints() {
        return (passingYards * 0.04) +
               (rushingYards * 0.1) +
               (touchdowns * 6.0);
    }
}

@Embeddable
public class LeagueSettings {
    private Integer teamSize;
    private ScoringType scoringType;
    private Integer playoffTeams;
}
```

#### 3.1.5 Aggregates (Agregados)

**Definição:** Cluster de objetos de domínio tratados como uma única unidade.

```java
// ✅ Aggregate Root
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Aggregate gerencia seus próprios membros
    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Roster> rosters = new ArrayList<>();

    @Embedded
    private LeagueSettings settings;

    // Métodos de negócio que mantêm invariantes
    public void addRoster(Roster roster) {
        if (rosters.size() >= settings.getMaxTeams()) {
            throw new LeagueFullException("League is full");
        }
        rosters.add(roster);
        roster.setLeague(this);
    }

    public void removeRoster(Roster roster) {
        if (hasStartedSeason()) {
            throw new IllegalStateException("Cannot remove roster after season started");
        }
        rosters.remove(roster);
        roster.setLeague(null);
    }

    // Invariantes do agregado
    private boolean hasStartedSeason() {
        return rosters.stream()
            .anyMatch(r -> r.hasPlayedGames());
    }
}
```

**Regras para Aggregates:**
1. **Acesso externo** apenas pela raiz (Aggregate Root)
2. **Transações** não devem cruzar limites de agregados
3. **Invariantes** são mantidas pela raiz
4. **Referências externas** apenas para a raiz

#### 3.1.6 Repositories (Repositórios)

**Definição:** Abstração para acesso a agregados, simulando uma coleção.

```java
// ✅ Repository - interface no domínio
public interface LeagueRepository {
    League findById(Long id);
    List<League> findByUserId(Long userId);
    League save(League league);
    void delete(League league);
}

// Implementação na camada de infraestrutura
public interface JpaLeagueRepository
    extends JpaRepository<League, Long>, LeagueRepository {

    @Query("SELECT l FROM League l JOIN l.rosters r WHERE r.userId = :userId")
    List<League> findByUserId(@Param("userId") Long userId);
}
```

#### 3.1.7 Domain Services (Serviços de Domínio)

**Definição:** Operações de domínio que não pertencem naturalmente a uma entidade ou value object.

```java
// ✅ Domain Service - lógica de negócio sem estado
@Service
public class MatchupScoringService {

    public MatchupResult calculateMatchupResult(
        Roster roster1,
        Roster roster2,
        Week week
    ) {
        Double score1 = calculateRosterScore(roster1, week);
        Double score2 = calculateRosterScore(roster2, week);

        return MatchupResult.builder()
            .roster1(roster1)
            .roster2(roster2)
            .score1(score1)
            .score2(score2)
            .winner(score1 > score2 ? roster1 : roster2)
            .build();
    }

    private Double calculateRosterScore(Roster roster, Week week) {
        return roster.getActivePlayersForWeek(week)
            .stream()
            .mapToDouble(player -> player.getStats(week).calculateFantasyPoints())
            .sum();
    }
}
```

#### 3.1.8 Domain Events (Eventos de Domínio)

**Definição:** Algo que aconteceu no domínio que é importante para o negócio.

```java
// ✅ Domain Event
@Getter
public class PlayerTradedEvent {
    private final Long playerId;
    private final Long fromRosterId;
    private final Long toRosterId;
    private final LocalDateTime occurredAt;

    public PlayerTradedEvent(Long playerId, Long fromRosterId, Long toRosterId) {
        this.playerId = playerId;
        this.fromRosterId = fromRosterId;
        this.toRosterId = toRosterId;
        this.occurredAt = LocalDateTime.now();
    }
}

// Publisher
@Service
public class TradeService {

    private final ApplicationEventPublisher eventPublisher;

    public void executeTrade(Trade trade) {
        // Validações e execução
        trade.execute();

        // Publicar evento
        trade.getPlayers().forEach(player ->
            eventPublisher.publishEvent(
                new PlayerTradedEvent(
                    player.getId(),
                    trade.getFromRoster().getId(),
                    trade.getToRoster().getId()
                )
            )
        );
    }
}

// Listener
@Component
public class PlayerTradedEventListener {

    @EventListener
    public void handlePlayerTraded(PlayerTradedEvent event) {
        // Atualizar estatísticas
        // Enviar notificações
        // Atualizar cache
        log.info("Player {} traded from roster {} to roster {}",
            event.getPlayerId(),
            event.getFromRosterId(),
            event.getToRosterId()
        );
    }
}
```

### 3.2 Camadas DDD

```
┌─────────────────────────────────────────────┐
│         Presentation Layer (API)            │
│  Controllers, DTOs, Request/Response        │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│        Application Layer (Use Cases)        │
│  Application Services, DTOs, Validation     │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│          Domain Layer (Core)                │
│  Entities, Value Objects, Domain Services   │
│  Aggregates, Domain Events, Repositories    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│       Infrastructure Layer (Details)        │
│  JPA Implementations, External APIs,        │
│  Database, Security, Configuration          │
└─────────────────────────────────────────────┘
```

---

## 4. Arquitetura do Projeto

### 4.1 Clean Architecture

O projeto segue os princípios da Clean Architecture (Arquitetura Limpa) de Robert C. Martin:

```
┌───────────────────────────────────────────────────┐
│                 External Interfaces               │
│  (Web, Database, External APIs)                   │
└───────────────────────────────────────────────────┘
                      ↓
┌───────────────────────────────────────────────────┐
│              Interface Adapters                   │
│  (Controllers, Presenters, Gateways)              │
└───────────────────────────────────────────────────┘
                      ↓
┌───────────────────────────────────────────────────┐
│              Application Business Rules           │
│  (Use Cases, Application Services)                │
└───────────────────────────────────────────────────┘
                      ↓
┌───────────────────────────────────────────────────┐
│           Enterprise Business Rules               │
│  (Entities, Value Objects, Domain Services)       │
└───────────────────────────────────────────────────┘
```

**Regra de dependência:**
- Camadas externas dependem de camadas internas
- Camadas internas NUNCA dependem de camadas externas

### 4.2 Estrutura Hexagonal (Ports & Adapters)

```
                  ┌─────────────────┐
                  │   Controllers   │
                  │   (Adapters)    │
                  └────────┬────────┘
                           │
              ┌────────────▼────────────┐
              │    Application Layer    │
              │       (Ports)           │
              └────────────┬────────────┘
                           │
              ┌────────────▼────────────┐
              │     Domain Layer        │
              │    (Business Logic)     │
              └────────────┬────────────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
    ┌────▼─────┐    ┌─────▼─────┐    ┌─────▼─────┐
    │ Database │    │  External │    │  Security │
    │ Adapter  │    │    APIs   │    │  Adapter  │
    └──────────┘    └───────────┘    └───────────┘
```

---

## 5. Estrutura de Camadas

### 5.1 Camada de Domínio (Domain Layer)

**Localização:** `com.fantasyfootball.fantasy_analyzer.model.entity`

**Responsabilidade:** Lógica de negócio pura, independente de frameworks.

**Componentes:**
- Entities (Entidades JPA)
- Value Objects
- Domain Services
- Domain Events
- Repository Interfaces

**Exemplo:**

```java
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nome;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Métodos de negócio
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public void activate() {
        if (this.active) {
            throw new IllegalStateException("User is already active");
        }
        this.active = true;
    }
}
```

### 5.2 Camada de Aplicação (Application Layer)

**Localização:** `com.fantasyfootball.fantasy_analyzer.service`

**Responsabilidade:** Orquestrar casos de uso, coordenar domínio e infraestrutura.

**Componentes:**
- Application Services
- Use Cases
- DTOs de Application
- Validações de negócio

**Exemplo:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        // Validação
        validateRegistrationRequest(request);

        // Verificar duplicação
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        // Criar entidade
        Usuario usuario = Usuario.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .nome(request.getNome())
            .role(UserRole.USER)
            .createdAt(LocalDateTime.now())
            .build();

        // Persistir
        usuario = usuarioRepository.save(usuario);

        // Gerar token
        String token = jwtUtil.generateToken(usuario.getEmail());

        log.info("User registered successfully: {}", usuario.getEmail());

        return new AuthResponse(token, usuario.getEmail());
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        // Validações específicas
    }
}
```

### 5.3 Camada de Apresentação (Presentation Layer)

**Localização:** `com.fantasyfootball.fantasy_analyzer.controller`

**Responsabilidade:** Interface com o mundo externo (REST API).

**Componentes:**
- Controllers
- Request/Response DTOs
- Exception Handlers
- Validação de entrada

**Exemplo:**

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
        @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Registration request received for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request
    ) {
        log.info("Login request received for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
```

### 5.4 Camada de Infraestrutura (Infrastructure Layer)

**Localização:**
- `com.fantasyfootball.fantasy_analyzer.repository`
- `com.fantasyfootball.fantasy_analyzer.config`

**Responsabilidade:** Detalhes técnicos, integrações externas.

**Componentes:**
- Repository Implementations (JPA)
- Configurações (Security, Database)
- Adaptadores para APIs externas
- Filters e Interceptors

**Exemplo:**

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.role = :role")
    List<Usuario> findByRole(@Param("role") UserRole role);
}
```

### 5.5 Fluxo de uma Requisição

```
1. HTTP Request
   ↓
2. Controller (Presentation)
   - Recebe request
   - Valida dados de entrada (@Valid)
   - Chama Application Service
   ↓
3. Application Service (Application)
   - Orquestra caso de uso
   - Chama Domain Services
   - Usa Repositories
   ↓
4. Domain Service/Entity (Domain)
   - Executa lógica de negócio
   - Mantém invariantes
   ↓
5. Repository (Infrastructure)
   - Persiste/busca dados
   - Acessa banco de dados
   ↓
6. Application Service
   - Constrói DTO de resposta
   ↓
7. Controller
   - Retorna ResponseEntity
   ↓
8. HTTP Response
```

---

## 6. Padrões de Design

### 6.1 Repository Pattern

**Propósito:** Abstração do acesso a dados.

```java
// Interface no domínio
public interface UsuarioRepository {
    Optional<Usuario> findByEmail(String email);
    Usuario save(Usuario usuario);
}

// Implementação usando Spring Data JPA
public interface JpaUsuarioRepository
    extends JpaRepository<Usuario, Long>, UsuarioRepository {
    // Spring Data gera implementação automaticamente
}
```

### 6.2 DTO Pattern

**Propósito:** Transferência de dados entre camadas, evitando exposição de entidades.

```java
// Request DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Name is required")
    private String nome;
}

// Response DTO
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
}
```

### 6.3 Builder Pattern

**Propósito:** Construção de objetos complexos de forma fluente.

```java
Usuario usuario = Usuario.builder()
    .email("user@example.com")
    .password(encodedPassword)
    .nome("John Doe")
    .role(UserRole.USER)
    .createdAt(LocalDateTime.now())
    .build();
```

### 6.4 Strategy Pattern

**Propósito:** Encapsular algoritmos intercambiáveis.

```java
// Interface Strategy
public interface ScoringStrategy {
    Double calculatePoints(PlayerStats stats);
}

// Implementações
public class StandardScoring implements ScoringStrategy {
    @Override
    public Double calculatePoints(PlayerStats stats) {
        return stats.getTouchdowns() * 6.0 +
               stats.getYards() * 0.1;
    }
}

public class PPRScoring implements ScoringStrategy {
    @Override
    public Double calculatePoints(PlayerStats stats) {
        return stats.getTouchdowns() * 6.0 +
               stats.getYards() * 0.1 +
               stats.getReceptions() * 1.0; // Point Per Reception
    }
}

// Uso
@Service
public class PlayerScoringService {
    public Double calculateScore(Player player, ScoringStrategy strategy) {
        return strategy.calculatePoints(player.getStats());
    }
}
```

### 6.5 Factory Pattern

**Propósito:** Criação de objetos sem expor lógica de criação.

```java
@Component
public class LeagueFactory {

    public League createStandardLeague(String name, Usuario commissioner) {
        return League.builder()
            .name(name)
            .commissioner(commissioner)
            .settings(createStandardSettings())
            .status(LeagueStatus.DRAFT)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public League createDynastyLeague(String name, Usuario commissioner) {
        return League.builder()
            .name(name)
            .commissioner(commissioner)
            .settings(createDynastySettings())
            .status(LeagueStatus.DRAFT)
            .createdAt(LocalDateTime.now())
            .build();
    }

    private LeagueSettings createStandardSettings() {
        return LeagueSettings.builder()
            .teamSize(10)
            .scoringType(ScoringType.STANDARD)
            .playoffTeams(4)
            .build();
    }

    private LeagueSettings createDynastySettings() {
        return LeagueSettings.builder()
            .teamSize(12)
            .scoringType(ScoringType.PPR)
            .playoffTeams(6)
            .keeperPlayers(5)
            .build();
    }
}
```

### 6.6 Dependency Injection

**Propósito:** Inversão de controle, facilita testes e manutenção.

```java
// ✅ Constructor Injection (preferível)
@Service
@RequiredArgsConstructor // Lombok gera construtor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
}

// ❌ Field Injection (evitar)
@Service
public class AuthService {
    @Autowired
    private UsuarioRepository usuarioRepository;
}
```

---

## 7. Convenções de Código

### 7.1 Nomenclatura de Pacotes

```
com.fantasyfootball.fantasy_analyzer/
├── config/              # Configurações (Security, Database, etc)
├── controller/          # REST Controllers
├── service/             # Application Services
├── repository/          # Data Access (Repositories)
├── model/
│   ├── entity/         # Domain Entities (JPA)
│   └── dto/            # Data Transfer Objects
│       ├── sleeper/    # DTOs específicos da API Sleeper
│       └── ...
├── exception/          # Custom Exceptions
├── util/               # Utilities
└── FantasyAnalyzerApplication.java
```

### 7.2 Nomenclatura de Classes

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| Entity | Substantivo | Usuario, League, Player |
| Controller | *Controller | AuthController, LeagueController |
| Service | *Service | AuthService, SleeperApiService |
| Repository | *Repository | UsuarioRepository |
| DTO Request | *Request | RegisterRequest, LoginRequest |
| DTO Response | *Response | AuthResponse, LeagueResponse |
| Exception | *Exception | UserNotFoundException |
| Config | *Config | SecurityConfig, JwtConfig |
| Util | *Util | JwtUtil, DateUtil |

### 7.3 Nomenclatura de Métodos

```java
// Controllers
@GetMapping     - findAll(), findById(), search()
@PostMapping    - create(), register()
@PutMapping     - update(), replace()
@PatchMapping   - partialUpdate()
@DeleteMapping  - delete(), remove()

// Services
public User createUser()
public User updateUser()
public void deleteUser()
public User findUserById()
public List<User> findAllUsers()
public boolean existsUserByEmail()

// Repositories
Optional<Usuario> findByEmail(String email)
List<Usuario> findByRole(UserRole role)
boolean existsByEmail(String email)
```

### 7.4 Anotações

**Ordem das anotações:**

```java
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    // ...
}
```

**Anotações comuns:**

```java
// Lombok
@Data                  // Gera getters, setters, toString, equals, hashCode
@Getter / @Setter      // Apenas getters/setters
@NoArgsConstructor     // Construtor sem argumentos
@AllArgsConstructor    // Construtor com todos argumentos
@RequiredArgsConstructor // Construtor com campos final
@Builder               // Padrão Builder
@Slf4j                 // Logger

// Spring
@Service               // Service layer
@RestController        // Controller layer
@Repository            // Repository layer
@Component             // Generic component
@Configuration         // Configuration class

// JPA
@Entity                // JPA Entity
@Table                 // Define nome da tabela
@Id                    // Primary key
@GeneratedValue        // Auto-increment
@Column                // Column definition
@OneToMany             // Relationship
@ManyToOne             // Relationship
@ManyToMany            // Relationship

// Validation
@Valid                 // Trigger validation
@NotNull               // Cannot be null
@NotBlank              // Cannot be null or empty
@Email                 // Valid email
@Size                  // Size constraints
@Min / @Max            // Numeric constraints
```

### 7.5 Configuração de Logs

```java
@Slf4j
@Service
public class AuthService {

    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());

        try {
            // lógica
            log.info("User logged in successfully: {}", request.getEmail());
            return response;
        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }
}
```

**Níveis de log:**
- `TRACE`: Informações muito detalhadas
- `DEBUG`: Informações de debug
- `INFO`: Informações gerais (operações bem-sucedidas)
- `WARN`: Avisos (situações inesperadas mas tratadas)
- `ERROR`: Erros (exceções, falhas)

---

## 8. Guia para Iniciantes

### 8.1 Entendendo Spring Boot

**O que é Spring Boot?**

Spring Boot é um framework que simplifica a criação de aplicações Java enterprise. Ele oferece:
- **Auto-configuração**: Configura automaticamente componentes baseado nas dependências
- **Starter Dependencies**: Grupos de dependências pré-configuradas
- **Embedded Server**: Servidor web embutido (Tomcat)
- **Production-ready**: Métricas, health checks, etc.

**Como funciona?**

```java
@SpringBootApplication // ← Anotação mágica
public class FantasyAnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FantasyAnalyzerApplication.class, args);
    }
}
```

`@SpringBootApplication` é um "atalho" para:
- `@Configuration`: Classe de configuração
- `@EnableAutoConfiguration`: Ativa auto-configuração
- `@ComponentScan`: Escaneia componentes no pacote

### 8.2 Injeção de Dependências

**O que é?**

Spring gerencia a criação e ciclo de vida dos objetos (beans).

```java
// Spring cria e injeta automaticamente
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repository; // ← Spring injeta isso

    public void doSomething() {
        repository.findAll(); // já está pronto para usar!
    }
}
```

**Como funciona?**

1. Spring escaneia classes com `@Component`, `@Service`, `@Repository`, `@Controller`
2. Cria instâncias (beans)
3. Injeta dependências automaticamente

### 8.3 Criando um Endpoint REST

**Passo a passo completo:**

```java
// 1. Criar DTO de Request
@Data
public class CreatePlayerRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String position;
}

// 2. Criar DTO de Response
@Data
@AllArgsConstructor
public class PlayerResponse {
    private Long id;
    private String name;
    private String position;
}

// 3. Criar Entity
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String position;
}

// 4. Criar Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByPosition(String position);
}

// 5. Criar Service
@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerResponse createPlayer(CreatePlayerRequest request) {
        Player player = Player.builder()
            .name(request.getName())
            .position(request.getPosition())
            .build();

        player = playerRepository.save(player);

        return new PlayerResponse(
            player.getId(),
            player.getName(),
            player.getPosition()
        );
    }

    public List<PlayerResponse> findAll() {
        return playerRepository.findAll()
            .stream()
            .map(p -> new PlayerResponse(p.getId(), p.getName(), p.getPosition()))
            .toList();
    }
}

// 6. Criar Controller
@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(
        @Valid @RequestBody CreatePlayerRequest request
    ) {
        PlayerResponse response = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        List<PlayerResponse> players = playerService.findAll();
        return ResponseEntity.ok(players);
    }
}
```

### 8.4 Testando com cURL

```bash
# Criar player
curl -X POST http://localhost:8080/api/players \
  -H "Content-Type: application/json" \
  -d '{"name": "Patrick Mahomes", "position": "QB"}'

# Listar players
curl http://localhost:8080/api/players
```

### 8.5 Entendendo JPA/Hibernate

**O que é?**

JPA (Java Persistence API) é uma especificação para mapeamento objeto-relacional (ORM).
Hibernate é a implementação que Spring Boot usa por padrão.

**Exemplo:**

```java
@Entity  // ← Esta classe vira uma tabela
@Table(name = "players")
public class Player {

    @Id  // ← Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← Auto-increment
    private Long id;

    @Column(nullable = false)  // ← Coluna NOT NULL
    private String name;

    private String position;  // ← Nome da coluna = nome do campo
}
```

**SQL Gerado:**

```sql
CREATE TABLE players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(255)
);
```

### 8.6 Relacionamentos JPA

```java
// One-to-Many
@Entity
public class League {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    private List<Roster> rosters = new ArrayList<>();
}

@Entity
public class Roster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;
}
```

### 8.7 Transações

**O que são?**

Transações garantem que operações no banco aconteçam atomicamente (tudo ou nada).

```java
@Service
public class TransferService {

    @Transactional  // ← Se algo falhar, tudo é revertido
    public void transferPlayer(Long playerId, Long fromRoster, Long toRoster) {
        // Remove do roster antigo
        rosterService.removePlayer(fromRoster, playerId);

        // Adiciona ao novo roster
        rosterService.addPlayer(toRoster, playerId);

        // Se qualquer operação falhar, ambas são revertidas
    }
}
```

### 8.8 Exception Handling

```java
// Criar exceção customizada
public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(Long id) {
        super("Player not found with id: " + id);
    }
}

// Global Exception Handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlayerNotFound(
        PlayerNotFoundException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex
    ) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .toList();

        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            errors.toString(),
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }
}
```

---

## 9. Exemplos Práticos

### 9.1 CRUD Completo

Vamos criar um CRUD completo para **Teams** (Times de fantasy).

**1. Entity:**

```java
@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Usuario owner;

    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

**2. DTOs:**

```java
// Request
@Data
public class CreateTeamRequest {
    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "League ID is required")
    private Long leagueId;
}

@Data
public class UpdateTeamRequest {
    @NotBlank(message = "Team name is required")
    private String name;
}

// Response
@Data
@Builder
public class TeamResponse {
    private Long id;
    private String name;
    private String ownerName;
    private String leagueName;
    private LocalDateTime createdAt;

    public static TeamResponse from(Team team) {
        return TeamResponse.builder()
            .id(team.getId())
            .name(team.getName())
            .ownerName(team.getOwner().getNome())
            .leagueName(team.getLeague().getName())
            .createdAt(team.getCreatedAt())
            .build();
    }
}
```

**3. Repository:**

```java
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByOwnerId(Long ownerId);
    List<Team> findByLeagueId(Long leagueId);
    Optional<Team> findByNameAndLeagueId(String name, Long leagueId);
}
```

**4. Service:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final UsuarioRepository usuarioRepository;
    private final LeagueRepository leagueRepository;

    @Transactional
    public TeamResponse createTeam(CreateTeamRequest request, Long ownerId) {
        log.info("Creating team '{}' for user {}", request.getName(), ownerId);

        // Validar usuário existe
        Usuario owner = usuarioRepository.findById(ownerId)
            .orElseThrow(() -> new UserNotFoundException(ownerId));

        // Validar liga existe
        League league = leagueRepository.findById(request.getLeagueId())
            .orElseThrow(() -> new LeagueNotFoundException(request.getLeagueId()));

        // Verificar nome duplicado na mesma liga
        teamRepository.findByNameAndLeagueId(request.getName(), request.getLeagueId())
            .ifPresent(t -> {
                throw new TeamAlreadyExistsException(request.getName());
            });

        // Criar time
        Team team = Team.builder()
            .name(request.getName())
            .owner(owner)
            .league(league)
            .createdAt(LocalDateTime.now())
            .build();

        team = teamRepository.save(team);

        log.info("Team created with ID: {}", team.getId());

        return TeamResponse.from(team);
    }

    @Transactional(readOnly = true)
    public TeamResponse findById(Long id) {
        Team team = teamRepository.findById(id)
            .orElseThrow(() -> new TeamNotFoundException(id));
        return TeamResponse.from(team);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> findByOwner(Long ownerId) {
        return teamRepository.findByOwnerId(ownerId)
            .stream()
            .map(TeamResponse::from)
            .toList();
    }

    @Transactional
    public TeamResponse updateTeam(Long id, UpdateTeamRequest request, Long ownerId) {
        Team team = teamRepository.findById(id)
            .orElseThrow(() -> new TeamNotFoundException(id));

        // Verificar ownership
        if (!team.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("You don't own this team");
        }

        team.setName(request.getName());
        team = teamRepository.save(team);

        return TeamResponse.from(team);
    }

    @Transactional
    public void deleteTeam(Long id, Long ownerId) {
        Team team = teamRepository.findById(id)
            .orElseThrow(() -> new TeamNotFoundException(id));

        // Verificar ownership
        if (!team.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("You don't own this team");
        }

        teamRepository.delete(team);
    }
}
```

**5. Controller:**

```java
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
        @Valid @RequestBody CreateTeamRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long ownerId = getUserId(userDetails);
        TeamResponse response = teamService.createTeam(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) {
        TeamResponse response = teamService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-teams")
    public ResponseEntity<List<TeamResponse>> getMyTeams(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long ownerId = getUserId(userDetails);
        List<TeamResponse> teams = teamService.findByOwner(ownerId);
        return ResponseEntity.ok(teams);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
        @PathVariable Long id,
        @Valid @RequestBody UpdateTeamRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long ownerId = getUserId(userDetails);
        TeamResponse response = teamService.updateTeam(id, request, ownerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long ownerId = getUserId(userDetails);
        teamService.deleteTeam(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(UserDetails userDetails) {
        // Implementação para extrair ID do usuário autenticado
        return 1L; // placeholder
    }
}
```

### 9.2 Integração com API Externa

Exemplo real do projeto: integração com Sleeper API.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class SleeperApiService {

    private static final String BASE_URL = "https://api.sleeper.app/v1";

    private final RestTemplate restTemplate;

    public SleeperUser getUser(String username) {
        String url = BASE_URL + "/user/" + username;

        try {
            log.debug("Fetching Sleeper user: {}", username);
            SleeperUser user = restTemplate.getForObject(url, SleeperUser.class);
            log.info("Successfully fetched user: {}", username);
            return user;
        } catch (HttpClientErrorException.NotFound e) {
            throw new SleeperUserNotFoundException(username);
        } catch (Exception e) {
            log.error("Error fetching Sleeper user: {}", username, e);
            throw new SleeperApiException("Failed to fetch user", e);
        }
    }

    public List<SleeperLeague> getUserLeagues(String userId, String season) {
        String url = BASE_URL + "/user/" + userId + "/leagues/nfl/" + season;

        try {
            log.debug("Fetching leagues for user {} season {}", userId, season);
            SleeperLeague[] leagues = restTemplate.getForObject(url, SleeperLeague[].class);
            return Arrays.asList(leagues != null ? leagues : new SleeperLeague[0]);
        } catch (Exception e) {
            log.error("Error fetching leagues", e);
            throw new SleeperApiException("Failed to fetch leagues", e);
        }
    }
}
```

---

## 10. Decisões de Arquitetura

### 10.1 Por que Spring Boot?

**Decisão:** Usar Spring Boot como framework principal.

**Razões:**
1. **Produtividade**: Auto-configuração reduz boilerplate
2. **Ecossistema**: Grande comunidade, muitas bibliotecas
3. **Enterprise-ready**: Suporte a transações, segurança, cache
4. **Testabilidade**: Excelente suporte a testes
5. **Documentação**: Extensa e bem mantida

**Trade-offs:**
- **Prós**: Rápido desenvolvimento, padrões bem estabelecidos
- **Contras**: "Mágica" pode ser confusa para iniciantes, overhead para apps muito simples

### 10.2 Por que JPA/Hibernate?

**Decisão:** Usar JPA com Hibernate para persistência.

**Razões:**
1. **Abstração**: Não precisamos escrever SQL para operações comuns
2. **Portabilidade**: Fácil trocar de banco de dados
3. **Lazy Loading**: Carregamento otimizado de relações
4. **Caching**: Cache de segundo nível integrado

**Trade-offs:**
- **Prós**: Produtividade, menos SQL manual
- **Contras**: Queries complexas podem ser difíceis, N+1 problem

### 10.3 Por que JWT para Autenticação?

**Decisão:** Usar JWT (JSON Web Tokens) para autenticação stateless.

**Razões:**
1. **Stateless**: Não precisa armazenar sessões no servidor
2. **Escalável**: Fácil escalar horizontalmente
3. **Mobile-friendly**: Funciona bem com apps mobile
4. **Cross-domain**: Pode ser usado entre diferentes domínios

**Trade-offs:**
- **Prós**: Escalabilidade, simplicidade
- **Contras**: Tokens não podem ser revogados facilmente, tamanho maior que session ID

### 10.4 Por que DTOs em vez de expor Entities?

**Decisão:** Sempre usar DTOs para requests/responses, nunca expor entidades diretamente.

**Razões:**
1. **Segurança**: Evita exposição de dados sensíveis (password, etc)
2. **Desacoplamento**: API independente do modelo de dados
3. **Flexibilidade**: Diferentes representações para diferentes casos de uso
4. **Versionamento**: Facilita evolução da API

**Exemplo:**

```java
// ❌ NUNCA faça isso
@GetMapping("/{id}")
public Usuario getUser(@PathVariable Long id) {
    return usuarioRepository.findById(id); // Expõe password!
}

// ✅ Use DTOs
@GetMapping("/{id}")
public UserResponse getUser(@PathVariable Long id) {
    Usuario usuario = usuarioRepository.findById(id);
    return UserResponse.from(usuario); // Apenas dados necessários
}
```

### 10.5 Por que Lombok?

**Decisão:** Usar Lombok para reduzir boilerplate.

**Razões:**
1. **Menos código**: `@Data` substitui getters/setters/toString/equals/hashCode
2. **Manutenibilidade**: Adicionar campo não requer atualizar métodos
3. **Legibilidade**: Código mais limpo e focado no que importa

**Trade-offs:**
- **Prós**: Menos código, mais legibilidade
- **Contras**: Requer plugin na IDE, "mágica" que pode confundir

### 10.6 Por que RestTemplate e não WebClient?

**Decisão atual:** Usar RestTemplate para chamadas HTTP.

**Razões:**
1. **Simplicidade**: API síncrona é mais fácil para iniciantes
2. **Suficiente**: Para nossas necessidades atuais, RestTemplate é adequado

**Consideração futura:**
WebClient (reativo) seria melhor para:
- Alta concorrência
- Chamadas não-bloqueantes
- Reactive programming

### 10.7 Estrutura de Pacotes

**Decisão:** Organização por camada técnica (controller, service, repository).

**Alternativa:** Organização por feature/módulo.

```
// Organização atual (por camada)
com.fantasyfootball.fantasy_analyzer/
├── controller/
├── service/
├── repository/
└── model/

// Alternativa (por feature) - recomendada para projetos grandes
com.fantasyfootball.fantasy_analyzer/
├── user/
│   ├── UserController
│   ├── UserService
│   ├── UserRepository
│   └── Usuario
├── league/
│   ├── LeagueController
│   ├── LeagueService
│   └── ...
```

**Para este projeto:** Camadas técnicas funcionam bem por ser de tamanho médio.
**Para projetos grandes:** Considere migrar para organização por feature.

---

## Conclusão

Este guia é um documento vivo e deve ser atualizado conforme o projeto evolui. Todos os desenvolvedores são encorajados a:

1. **Seguir** os princípios e padrões definidos
2. **Questionar** decisões quando apropriado
3. **Propor** melhorias via pull requests
4. **Educar** novos membros usando este documento

**Referências adicionais:**

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Clean Code - Robert C. Martin](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)
- [Domain-Driven Design - Eric Evans](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215)
- [Effective Java - Joshua Bloch](https://www.amazon.com/Effective-Java-Joshua-Bloch/dp/0134685997)

---

**Última revisão:** 2025-01-13
**Próxima revisão:** 2025-04-13
