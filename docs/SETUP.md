# Setup do Projeto Fantasy Analyzer

## Pré-requisitos

### 1. Instalar Java 17

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk
```

Verificar instalação:
```bash
java -version
```

Se houver múltiplas versões, configure o Java 17 como padrão:
```bash
sudo update-alternatives --config java
```

### 2. Iniciar MySQL

```bash
sudo service mysql start
```

### 3. Criar banco de dados e usuário

```bash
sudo mysql -u root -p
```

Dentro do MySQL, execute:
```sql
CREATE DATABASE IF NOT EXISTS fantasy_analyzer;
CREATE USER IF NOT EXISTS 'spring'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON fantasy_analyzer.* TO 'spring'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

## Estrutura de Autenticação JWT

O projeto implementa autenticação JWT com os seguintes componentes:

### Entidades
- **Usuario** (`com.fantasyfootball.model.entity.Usuario`): Entidade JPA com id, nome, email e password

### DTOs
- **LoginRequest**: Email e password para login
- **RegisterRequest**: Nome, email e password para registro
- **AuthResponse**: Token JWT e dados do usuário

### Configuração
- **JwtUtil**: Utilidades para geração e validação de tokens JWT
- **JwtAuthenticationFilter**: Filtro para interceptar requisições e validar tokens
- **SecurityConfig**: Configuração do Spring Security
- **CustomUserDetailsService**: Serviço customizado para carregar usuários

### Serviços e Controllers
- **AuthService**: Lógica de negócio para login e registro
- **AuthController**: Endpoints REST para autenticação

## Executar o projeto

### Build
```bash
./mvnw clean install
```

### Executar
```bash
./mvnw spring-boot:run
```

## Testar a API

### Registrar novo usuário
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teste Usuario",
    "email": "teste@email.com",
    "password": "senha123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "password": "senha123"
  }'
```

### Testar endpoint protegido
```bash
curl -X GET http://localhost:8080/api/auth/test \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## Configurações

As configurações estão em `src/main/resources/application.properties`:

- **Banco de dados**: fantasy_analyzer
- **Usuário**: spring
- **Senha**: password
- **JWT Secret**: Chave secreta para assinar tokens
- **JWT Expiration**: 86400000ms (24 horas)

## Estrutura do Projeto

```
src/main/java/com/fantasyfootball/
├── fantasy_analyzer/
│   ├── FantasyAnalyzerApplication.java
│   ├── config/
│   │   ├── JwtUtil.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── AuthResponse.java
│   │   ├── LoginRequest.java
│   │   └── RegisterRequest.java
│   └── service/
│       ├── AuthService.java
│       └── CustomUserDetailsService.java
├── model/entity/
│   └── Usuario.java
└── repository/
    └── UsuarioRepository.java
```
