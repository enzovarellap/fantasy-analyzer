# Guia Docker - Fantasy Analyzer

Guia completo para rodar o Fantasy Analyzer usando Docker e Docker Compose.

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Pré-requisitos](#pré-requisitos)
3. [Quick Start](#quick-start)
4. [Arquitetura Docker](#arquitetura-docker)
5. [Desenvolvimento](#desenvolvimento)
6. [Produção](#produção)
7. [Comandos Úteis](#comandos-úteis)
8. [Troubleshooting](#troubleshooting)

## 🎯 Visão Geral

Este projeto usa Docker para:
- ✅ **Consistência**: Ambiente idêntico em dev e produção
- ✅ **Isolamento**: Cada serviço roda em seu próprio container
- ✅ **Simplicidade**: Um comando para subir tudo
- ✅ **Portabilidade**: Funciona em qualquer sistema com Docker

### Serviços Disponíveis

| Serviço | Container | Porta | Descrição |
|---------|-----------|-------|-----------|
| Backend | `fantasy-analyzer-backend` | 8080 | Spring Boot API |
| MySQL | `fantasy-analyzer-mysql` | 3306 | Banco de dados |
| Web | `fantasy-analyzer-web` | 3000 | React Frontend (futuro) |

## 🚀 Pré-requisitos

### Docker Desktop (Recomendado)
- [Docker Desktop para Windows](https://docs.docker.com/desktop/install/windows-install/)
- [Docker Desktop para Mac](https://docs.docker.com/desktop/install/mac-install/)
- [Docker Desktop para Linux](https://docs.docker.com/desktop/install/linux-install/)

### Ou Docker Engine + Docker Compose
```bash
# Linux
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-compose-plugin
```

### Verificar Instalação
```bash
docker --version          # Docker version 24.0.0+
docker compose version    # Docker Compose version v2.20.0+
```

## ⚡ Quick Start

### 1. Clonar Repositório

```bash
git clone https://github.com/seu-usuario/fantasy-analyzer.git
cd fantasy-analyzer
```

### 2. Configurar Variáveis de Ambiente

```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar configurações (IMPORTANTE!)
nano .env
# ou
vim .env
```

**IMPORTANTE**: Altere pelo menos estas variáveis:
```env
MYSQL_ROOT_PASSWORD=sua_senha_root_forte
MYSQL_PASSWORD=sua_senha_user_forte
JWT_SECRET=sua-chave-jwt-super-secreta-minimo-256-bits
```

### 3. Subir Aplicação

```bash
# Desenvolvimento
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Produção
docker compose up -d
```

### 4. Acessar

- **Backend API**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **MySQL**: localhost:3306

### 5. Verificar Status

```bash
docker compose ps
```

Deve mostrar:
```
NAME                          STATUS         PORTS
fantasy-analyzer-backend      Up (healthy)   0.0.0.0:8080->8080/tcp
fantasy-analyzer-mysql        Up (healthy)   0.0.0.0:3306->3306/tcp
```

## 🏗️ Arquitetura Docker

### Estrutura de Arquivos

```
fantasy-analyzer/
├── docker-compose.yml           # Configuração principal
├── docker-compose.dev.yml       # Overrides para desenvolvimento
├── .env.example                 # Template de variáveis
├── .env                         # Suas variáveis (gitignored)
├── backend/
│   ├── Dockerfile              # Build produção (multi-stage)
│   ├── Dockerfile.dev          # Build desenvolvimento
│   └── .dockerignore           # Arquivos ignorados no build
└── web/
    ├── Dockerfile              # Build React (futuro)
    └── .dockerignore
```

### Dockerfile do Backend (Multi-Stage)

```dockerfile
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
# ... compila aplicação

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
# ... roda aplicação

# Benefícios:
# - Imagem final pequena (apenas JRE)
# - Segurança (sem ferramentas de build)
# - Performance (otimizado para runtime)
```

### Networks e Volumes

```yaml
networks:
  fantasy-network:     # Comunicação entre containers
    driver: bridge

volumes:
  mysql_data:          # Persistência dos dados MySQL
    driver: local
```

## 💻 Desenvolvimento

### Modo Desenvolvimento (Hot Reload)

```bash
# Subir com hot reload
docker compose -f docker-compose.yml -f docker-compose.dev.yml up

# Em background
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

**Recursos em dev**:
- ✅ Spring DevTools habilitado
- ✅ Hot reload de código
- ✅ Logs detalhados
- ✅ Debug remoto na porta 5005
- ✅ SQL logging habilitado

### Debug Remoto

Configure sua IDE para conectar em `localhost:5005`

**IntelliJ IDEA**:
1. Run → Edit Configurations
2. Add → Remote JVM Debug
3. Host: localhost, Port: 5005
4. Apply e Debug

**VS Code** (launch.json):
```json
{
  "type": "java",
  "name": "Attach to Docker",
  "request": "attach",
  "hostName": "localhost",
  "port": 5005
}
```

### Ver Logs

```bash
# Todos os serviços
docker compose logs -f

# Apenas backend
docker compose logs -f backend

# Últimas 100 linhas
docker compose logs -f --tail=100 backend

# Com timestamps
docker compose logs -f -t backend
```

### Rebuild Após Mudanças

```bash
# Rebuild do backend
docker compose build backend

# Rebuild sem cache
docker compose build --no-cache backend

# Rebuild e restart
docker compose up -d --build backend
```

### Executar Comandos no Container

```bash
# Abrir shell no backend
docker compose exec backend sh

# Executar Maven command
docker compose exec backend ./mvnw test

# Acessar MySQL
docker compose exec mysql mysql -u fantasy_user -p fantasy_analyzer

# Ver processos Java
docker compose exec backend jps -v
```

### Desenvolvimento Local do Source

Com o `docker-compose.dev.yml`, o código fonte está montado:
```yaml
volumes:
  - ./backend/src:/app/src  # Mudanças refletidas automaticamente
```

## 🚀 Produção

### Build de Produção

```bash
# Build otimizado
docker compose build --no-cache

# Subir em produção
docker compose up -d
```

**Diferenças de produção**:
- ✅ Imagem otimizada (multi-stage build)
- ✅ JRE apenas (sem JDK)
- ✅ Non-root user
- ✅ Health checks
- ✅ Resource limits (opcional)
- ✅ Restart policies

### Configurações de Produção

Adicione ao `docker-compose.yml`:

```yaml
backend:
  deploy:
    resources:
      limits:
        cpus: '2'
        memory: 2G
      reservations:
        cpus: '1'
        memory: 1G
  restart: unless-stopped
  logging:
    driver: "json-file"
    options:
      max-size: "10m"
      max-file: "3"
```

### Secrets e Segurança

**NUNCA** commite o `.env` com dados reais!

```bash
# .gitignore já inclui
.env
.env.local
.env.*.local
```

**Para produção**, use:
- Docker Secrets
- AWS Secrets Manager
- HashiCorp Vault
- Variáveis de ambiente do host

### Backup do Banco

```bash
# Backup
docker compose exec mysql mysqldump \
  -u fantasy_user -p fantasy_analyzer > backup.sql

# Restore
docker compose exec -T mysql mysql \
  -u fantasy_user -p fantasy_analyzer < backup.sql

# Backup de volume
docker run --rm \
  -v fantasy-analyzer_mysql_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/mysql-data-backup.tar.gz /data
```

## 🛠️ Comandos Úteis

### Gerenciamento Básico

```bash
# Subir todos os serviços
docker compose up -d

# Parar todos os serviços
docker compose down

# Parar e remover volumes (CUIDADO!)
docker compose down -v

# Restart de um serviço
docker compose restart backend

# Pausar serviços
docker compose pause

# Resumir serviços
docker compose unpause
```

### Monitoramento

```bash
# Status dos containers
docker compose ps

# Ver processos
docker compose top

# Estatísticas de uso (CPU, RAM, I/O)
docker stats

# Health checks
docker inspect fantasy-analyzer-backend | grep -A 10 Health
```

### Limpeza

```bash
# Remover containers parados
docker compose rm

# Remover imagens não utilizadas
docker image prune -a

# Limpeza geral (cuidado!)
docker system prune -a --volumes

# Ver espaço usado
docker system df
```

### Escalar Serviços

```bash
# Escalar backend (múltiplas instâncias)
docker compose up -d --scale backend=3

# Nota: Requer load balancer configurado
```

## 🐛 Troubleshooting

### Problema: Container não inicia

```bash
# Ver logs de erro
docker compose logs backend

# Ver eventos do container
docker events --filter container=fantasy-analyzer-backend

# Inspecionar container
docker inspect fantasy-analyzer-backend
```

### Problema: Porta em uso

```bash
# Verificar porta
sudo lsof -i :8080
# ou
netstat -tulpn | grep 8080

# Alterar porta no .env
BACKEND_PORT=8081
```

### Problema: MySQL connection refused

```bash
# Verificar se MySQL está healthy
docker compose ps

# Ver logs do MySQL
docker compose logs mysql

# Testar conexão
docker compose exec mysql mysql -u root -p

# Verificar variáveis
docker compose exec mysql env | grep MYSQL
```

### Problema: Build falha

```bash
# Limpar cache do Docker
docker builder prune

# Rebuild sem cache
docker compose build --no-cache

# Verificar .dockerignore
cat backend/.dockerignore
```

### Problema: Lentidão

```bash
# Verificar recursos
docker stats

# Aumentar memória no Docker Desktop
# Settings → Resources → Advanced → Memory: 4GB+

# Limpar volumes não usados
docker volume prune
```

### Problema: Mudanças não refletem

```bash
# Verificar se está usando docker-compose.dev.yml
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Forçar rebuild
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build

# Verificar volumes montados
docker compose exec backend ls -la /app/src
```

## 📊 Health Checks

### Backend Health Check

```bash
# Via curl
curl http://localhost:8080/actuator/health

# Resposta esperada:
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

### MySQL Health Check

```bash
# Via Docker
docker compose exec mysql mysqladmin ping -h localhost -u root -p

# Resposta: mysqld is alive
```

## 🔧 Customização

### Adicionar Novo Serviço

```yaml
# docker-compose.yml
services:
  redis:
    image: redis:7-alpine
    container_name: fantasy-analyzer-redis
    ports:
      - "6379:6379"
    networks:
      - fantasy-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
```

### Variáveis de Ambiente Adicionais

```yaml
# docker-compose.yml
backend:
  environment:
    - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}
    - LOGGING_LEVEL_ROOT=${LOGGING_LEVEL:-INFO}
    - CUSTOM_PROPERTY=${CUSTOM_VALUE}
```

## 📚 Recursos

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [Best Practices for Writing Dockerfiles](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)

## ✅ Checklist

- [ ] Docker instalado e rodando
- [ ] .env configurado com valores seguros
- [ ] Containers subiram com sucesso
- [ ] Health checks passando
- [ ] Backend acessível em http://localhost:8080
- [ ] MySQL acessível
- [ ] Logs sem erros
- [ ] Hot reload funcionando (dev)

---

**Última atualização**: 2025-01-13
