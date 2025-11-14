# Integração com API do Sleeper

Este documento descreve como usar os endpoints de integração com a API do Sleeper implementados neste projeto.

## Base URL

Todos os endpoints estão disponíveis sob o prefixo `/api/sleeper` e **requerem autenticação JWT**.

## Endpoints Disponíveis

### 1. Buscar Usuário

**GET** `/api/sleeper/user/{usernameOrId}`

Retorna informações de um usuário do Sleeper.

**Parâmetros:**
- `usernameOrId` (path): Username ou User ID do Sleeper

**Exemplo:**
```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0ZUBlbWFpbC5jb20iLCJpYXQiOjE3NjMwMDQ3NzYsImV4cCI6MTc2MzA5MTE3Nn0.dXMtHbr3KYSocDGp6PmKtsGi88BHuXX0iuB62V0UwtY" \
  http://localhost:8080/api/sleeper/user/flashyyy
```

**Resposta:**
```json
{
  "user_id": "123456789",
  "username": "username123",
  "display_name": "Display Name",
  "avatar": "avatar_id",
  "is_bot": false
}
```

---

### 2. Buscar Ligas de um Usuário

**GET** `/api/sleeper/user/{userId}/leagues/{sport}/{season}`

Retorna todas as ligas de um usuário em uma temporada específica.

**Parâmetros:**
- `userId` (path): ID do usuário no Sleeper
- `sport` (path): Esporte (ex: `nfl`, `nba`, `lcs`)
- `season` (path): Ano da temporada (ex: `2024`)

**Exemplo:**
```bash
curl -H "Authorization: Bearer {seu_token_jwt}" \
  http://localhost:8080/api/sleeper/user/123456789/leagues/nfl/2024
```

**Resposta:**
```json
[
  {
    "league_id": "987654321",
    "name": "Nome da Liga",
    "season": "2024",
    "sport": "nfl",
    "status": "in_season",
    "total_rosters": 12,
    "settings": {...},
    "scoring_settings": {...},
    "roster_positions": ["QB", "RB", "WR", ...]
  }
]
```

---

### 3. Buscar Informações da Liga

**GET** `/api/sleeper/league/{leagueId}`

Retorna informações detalhadas de uma liga.

**Parâmetros:**
- `leagueId` (path): ID da liga

**Exemplo:**
```bash
curl -H "Authorization: Bearer {seu_token_jwt}" \
  http://localhost:8080/api/sleeper/league/987654321
```

---

### 4. Buscar Rosters da Liga

**GET** `/api/sleeper/league/{leagueId}/rosters`

Retorna todos os rosters (times) de uma liga.

**Parâmetros:**
- `leagueId` (path): ID da liga

**Exemplo:**
```bash
curl -H "Authorization: Bearer {seu_token_jwt}" \
  http://localhost:8080/api/sleeper/league/987654321/rosters
```

**Resposta:**
```json
[
  {
    "roster_id": 1,
    "owner_id": "123456789",
    "players": ["4034", "4035", "4036"],
    "starters": ["4034", "4035"],
    "reserve": ["4036"],
    "settings": {...},
    "league_id": "987654321"
  }
]
```

---

### 5. Buscar Usuários da Liga

**GET** `/api/sleeper/league/{leagueId}/users`

Retorna todos os usuários participantes de uma liga.

**Parâmetros:**
- `leagueId` (path): ID da liga

**Exemplo:**
```bash
curl -H "Authorization: Bearer {seu_token_jwt}" \
  http://localhost:8080/api/sleeper/league/987654321/users
```

---

### 6. Buscar Matchups da Semana

**GET** `/api/sleeper/league/{leagueId}/matchups/{week}`

Retorna os confrontos (matchups) de uma semana específica.

**Parâmetros:**
- `leagueId` (path): ID da liga
- `week` (path): Número da semana (1-18 para NFL regular season)

**Exemplo:**
```bash
curl -H "Authorization: Bearer {seu_token_jwt}" \
  http://localhost:8080/api/sleeper/league/987654321/matchups/1
```

**Resposta:**
```json
[
  {
    "roster_id": 1,
    "matchup_id": 1,
    "points": 125.5,
    "custom_points": 125.5,
    "starters": ["4034", "4035"],
    "starters_points": [15.5, 20.0],
    "players": ["4034", "4035", "4036"],
    "players_points": {
      "4034": 15.5,
      "4035": 20.0,
      "4036": 0.0
    }
  }
]
```

---

### 7. Buscar Todos os Jogadores

**GET** `/api/sleeper/players/{sport}`

Retorna todos os jogadores disponíveis no Sleeper para um esporte.

**⚠️ ATENÇÃO:** Este endpoint retorna uma quantidade muito grande de dados (10MB+). Use com cuidado.

**Parâmetros:**
- `sport` (path): Esporte (ex: `nfl`, `nba`)

**Exemplo:**
```bash
curl -H "Authorization: Bearer {seu_token_jwt}" \
  http://localhost:8080/api/sleeper/players/nfl
```

**Resposta:**
```json
{
  "4034": {
    "player_id": "4034",
    "first_name": "Patrick",
    "last_name": "Mahomes",
    "full_name": "Patrick Mahomes",
    "position": "QB",
    "team": "KC",
    "number": 15,
    "age": 28,
    "status": "Active",
    "fantasy_positions": ["QB"],
    "years_exp": 7
  },
  ...
}
```

---

### 8. Buscar Jogadores em Trending

**GET** `/api/sleeper/players/{sport}/trending/{type}?lookbackHours={hours}&limit={limit}`

Retorna jogadores que estão em alta (mais adicionados ou dropados).

**Parâmetros:**
- `sport` (path): Esporte (ex: `nfl`, `nba`)
- `type` (path): Tipo de trending - `add` (adicionados) ou `drop` (dropados)
- `lookbackHours` (query, opcional): Quantas horas analisar (padrão: 24)
- `limit` (query, opcional): Limite de resultados (padrão: 25)

**Exemplo:**
```bash
curl -H "Authorization: Bearer {seu_token_jwt}" \
  "http://localhost:8080/api/sleeper/players/nfl/trending/add?lookbackHours=48&limit=10"
```

**Resposta:**
```json
[
  {
    "player_id": "4034",
    "count": 1523
  },
  {
    "player_id": "4035",
    "count": 1456
  }
]
```

---

## Autenticação

Todos os endpoints requerem autenticação JWT. Você deve:

1. Primeiro fazer login no endpoint `/api/auth/login` ou registrar-se em `/api/auth/register`
2. Usar o token JWT retornado no header `Authorization: Bearer {token}` em todas as requisições

**Exemplo de Login:**
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "seu@email.com", "senha": "suaSenha"}'

# Resposta
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer"
}

# Usar o token nas requisições
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  http://localhost:8080/api/sleeper/user/username123
```

---

## Rate Limiting

A API do Sleeper tem limite de **1000 requisições por minuto**. O serviço implementado não possui cache, então seja consciente ao fazer requisições em larga escala.

---

## Arquitetura da Implementação

### Estrutura de Pacotes

```
src/main/java/com/fantasyfootball/fantasy_analyzer/
├── config/
│   └── RestTemplateConfig.java          # Configuração do RestTemplate
├── controller/
│   └── SleeperController.java           # Endpoints REST
├── service/
│   └── SleeperApiService.java           # Lógica de integração com API
└── model/dto/sleeper/
    ├── SleeperUser.java                 # DTO de usuário
    ├── SleeperLeague.java               # DTO de liga
    ├── SleeperRoster.java               # DTO de roster
    ├── SleeperMatchup.java              # DTO de matchup
    └── SleeperPlayer.java               # DTO de jogador
```

### Tecnologias Utilizadas

- **Spring Boot RestTemplate**: Para fazer as chamadas HTTP à API do Sleeper
- **Lombok**: Para reduzir boilerplate nos DTOs
- **Jackson**: Para deserialização JSON automática
- **SLF4J**: Para logging das operações

---

## Tratamento de Erros

Todas as exceções da API do Sleeper são capturadas e re-lançadas como `RuntimeException` com mensagens descritivas. Os logs incluem detalhes sobre os erros para facilitar debugging.

---

## Próximos Passos (Sugestões)

1. **Implementar Cache**: Adicionar Redis ou cache em memória para reduzir chamadas à API
2. **Rate Limiting**: Implementar controle de rate limiting local
3. **Exception Handling Global**: Criar `@ControllerAdvice` para tratamento centralizado de erros
4. **Validação de Parâmetros**: Adicionar validações nos parâmetros dos endpoints
5. **Testes**: Criar testes unitários e de integração
6. **Documentação Swagger**: Adicionar SpringDoc OpenAPI para documentação interativa
