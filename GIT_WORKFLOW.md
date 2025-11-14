# Git Workflow - Estratégia de Branching

Este documento define a estratégia de branching Git Flow para o projeto Fantasy Analyzer.

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Branches Principais](#branches-principais)
3. [Branches de Suporte](#branches-de-suporte)
4. [Fluxo de Trabalho](#fluxo-de-trabalho)
5. [Convenções de Nomenclatura](#convenções-de-nomenclatura)
6. [Exemplos Práticos](#exemplos-práticos)
7. [Regras e Boas Práticas](#regras-e-boas-práticas)

## 🎯 Visão Geral

O projeto utiliza **Git Flow**, um modelo de branching robusto que define uma estrutura rigorosa de branches para facilitar o desenvolvimento em equipe e releases organizados.

### Por que Git Flow?

- ✅ **Organização clara** de features, releases e hotfixes
- ✅ **Processo de release** bem definido
- ✅ **Facilita code review** e integração contínua
- ✅ **Suporte a múltiplas versões** em produção
- ✅ **Histórico limpo** e rastreável

## 🌳 Branches Principais

### `main` (ou `master`)
- **Propósito**: Código em produção
- **Proteção**: ✅ Branch protegida
- **Merge**: Apenas de `release/*` e `hotfix/*`
- **Tag**: Toda merge recebe uma tag de versão (ex: `v1.0.0`)

```bash
# Estado: sempre estável e pronto para produção
# Commits: apenas merges de release ou hotfix
# Deploy: automático para produção (via CI/CD)
```

### `develop`
- **Propósito**: Branch de integração para desenvolvimento
- **Proteção**: ✅ Branch protegida
- **Merge**: De `feature/*`, `release/*` e `hotfix/*`
- **Estado**: Deve sempre estar funcional (passar nos testes)

```bash
# Estado: última versão de desenvolvimento
# Commits: merges de features concluídas
# Deploy: automático para ambiente de staging/dev
```

## 🔧 Branches de Suporte

### Feature Branches - `feature/*`

**Propósito**: Desenvolvimento de novas funcionalidades

**Criação**:
```bash
# Criar a partir de develop
git checkout develop
git pull origin develop
git checkout -b feature/nome-da-feature
```

**Nomenclatura**:
- `feature/user-authentication`
- `feature/sleeper-api-integration`
- `feature/player-statistics-dashboard`
- `feature/ISSUE-123-league-scoring`

**Workflow**:
```bash
# 1. Desenvolver a feature
git add .
git commit -m "feat: implement user authentication"

# 2. Manter atualizado com develop
git checkout develop
git pull origin develop
git checkout feature/nome-da-feature
git merge develop

# 3. Push para remote
git push origin feature/nome-da-feature

# 4. Criar Pull Request para develop
# (via GitHub interface)

# 5. Após merge, deletar branch
git branch -d feature/nome-da-feature
git push origin --delete feature/nome-da-feature
```

### Release Branches - `release/*`

**Propósito**: Preparação para uma nova release em produção

**Criação**:
```bash
# Criar a partir de develop quando features estão prontas
git checkout develop
git pull origin develop
git checkout -b release/v1.2.0
```

**Nomenclatura**:
- `release/v1.0.0`
- `release/v1.2.0`
- `release/v2.0.0-beta`

**Workflow**:
```bash
# 1. Ajustes finais (version bumps, changelog, bug fixes)
git add .
git commit -m "chore: bump version to 1.2.0"

# 2. Merge para main
git checkout main
git pull origin main
git merge --no-ff release/v1.2.0
git tag -a v1.2.0 -m "Release version 1.2.0"
git push origin main --tags

# 3. Merge de volta para develop
git checkout develop
git merge --no-ff release/v1.2.0
git push origin develop

# 4. Deletar branch
git branch -d release/v1.2.0
git push origin --delete release/v1.2.0
```

### Hotfix Branches - `hotfix/*`

**Propósito**: Correção urgente em produção

**Criação**:
```bash
# Criar a partir de main
git checkout main
git pull origin main
git checkout -b hotfix/v1.2.1-critical-bug
```

**Nomenclatura**:
- `hotfix/v1.2.1-security-patch`
- `hotfix/v1.0.1-login-fix`
- `hotfix/ISSUE-456-payment-bug`

**Workflow**:
```bash
# 1. Aplicar fix
git add .
git commit -m "fix: resolve critical authentication bug"

# 2. Bump version
git commit -m "chore: bump version to 1.2.1"

# 3. Merge para main
git checkout main
git merge --no-ff hotfix/v1.2.1-critical-bug
git tag -a v1.2.1 -m "Hotfix version 1.2.1"
git push origin main --tags

# 4. Merge para develop
git checkout develop
git merge --no-ff hotfix/v1.2.1-critical-bug
git push origin develop

# 5. Deletar branch
git branch -d hotfix/v1.2.1-critical-bug
git push origin --delete hotfix/v1.2.1-critical-bug
```

## 🎨 Fluxo de Trabalho

### Desenvolvimento de Nova Feature

```
develop
  ↓
feature/new-feature
  ↓ (commits)
  ↓ (pull request)
  ↓ (code review)
  ↓ (CI/CD passa)
  ↓ (merge)
develop
```

### Criação de Release

```
develop (com features prontas)
  ↓
release/v1.2.0
  ↓ (ajustes finais)
  ↓ (testes de aceitação)
  ├──→ main (tag v1.2.0) → PRODUÇÃO
  └──→ develop (sync)
```

### Hotfix Urgente

```
main (bug em produção!)
  ↓
hotfix/v1.2.1-fix
  ↓ (fix aplicado)
  ├──→ main (tag v1.2.1) → PRODUÇÃO
  └──→ develop (sync)
```

## 📝 Convenções de Nomenclatura

### Branches

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| Feature | `feature/descricao-kebab-case` | `feature/user-authentication` |
| Feature (com issue) | `feature/ISSUE-123-descricao` | `feature/ISSUE-456-add-scoring` |
| Release | `release/vX.Y.Z` | `release/v1.2.0` |
| Hotfix | `hotfix/vX.Y.Z-descricao` | `hotfix/v1.0.1-login-fix` |
| Bugfix | `bugfix/descricao` | `bugfix/fix-null-pointer` |
| Docs | `docs/descricao` | `docs/update-api-docs` |

### Commits (Conventional Commits)

```bash
# Formato
<tipo>(<escopo opcional>): <descrição>

# Tipos
feat:     # Nova funcionalidade
fix:      # Correção de bug
docs:     # Documentação
style:    # Formatação, ponto e vírgula, etc
refactor: # Refatoração de código
perf:     # Melhoria de performance
test:     # Testes
chore:    # Tarefas de build, configs, etc
ci:       # CI/CD changes
```

**Exemplos**:
```bash
feat(auth): add JWT token validation
fix(api): resolve null pointer in user service
docs(readme): update setup instructions
refactor(service): extract validation logic
test(auth): add integration tests for login
chore(deps): update Spring Boot to 3.2.0
ci(github): add automated deployment workflow
```

### Tags de Versão (Semantic Versioning)

```
vMAJOR.MINOR.PATCH

v1.0.0    # Release inicial
v1.1.0    # Nova feature (minor)
v1.1.1    # Bug fix (patch)
v2.0.0    # Breaking change (major)
```

**Quando incrementar**:
- **MAJOR**: Mudanças incompatíveis na API
- **MINOR**: Nova funcionalidade compatível
- **PATCH**: Correção de bugs compatível

## 💡 Exemplos Práticos

### Exemplo 1: Adicionar Autenticação de Usuário

```bash
# 1. Criar feature branch
git checkout develop
git pull origin develop
git checkout -b feature/user-authentication

# 2. Desenvolver e commitar
git add src/main/java/com/fantasyfootball/fantasy_analyzer/config/SecurityConfig.java
git commit -m "feat(auth): add Spring Security configuration"

git add src/main/java/com/fantasyfootball/fantasy_analyzer/service/AuthService.java
git commit -m "feat(auth): implement authentication service"

git add src/test/java/com/fantasyfootball/fantasy_analyzer/service/AuthServiceTest.java
git commit -m "test(auth): add authentication service tests"

# 3. Push e criar PR
git push origin feature/user-authentication
# Criar Pull Request no GitHub: feature/user-authentication → develop

# 4. Após aprovação e merge
git checkout develop
git pull origin develop
git branch -d feature/user-authentication
```

### Exemplo 2: Release v1.0.0

```bash
# 1. Criar release branch
git checkout develop
git pull origin develop
git checkout -b release/v1.0.0

# 2. Preparar release
# Atualizar pom.xml version
git add pom.xml
git commit -m "chore(release): bump version to 1.0.0"

# Atualizar CHANGELOG.md
git add CHANGELOG.md
git commit -m "docs(changelog): update for v1.0.0"

# 3. Merge para main
git checkout main
git pull origin main
git merge --no-ff release/v1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags

# 4. Merge para develop
git checkout develop
git merge --no-ff release/v1.0.0
git push origin develop

# 5. Cleanup
git branch -d release/v1.0.0
git push origin --delete release/v1.0.0
```

### Exemplo 3: Hotfix Crítico

```bash
# 1. Bug encontrado em produção (main)
git checkout main
git pull origin main
git checkout -b hotfix/v1.0.1-authentication-bypass

# 2. Aplicar fix
git add src/main/java/com/fantasyfootball/fantasy_analyzer/config/SecurityConfig.java
git commit -m "fix(security): resolve authentication bypass vulnerability"

# 3. Bump version
# Atualizar pom.xml
git add pom.xml
git commit -m "chore(hotfix): bump version to 1.0.1"

# 4. Merge para main
git checkout main
git merge --no-ff hotfix/v1.0.1-authentication-bypass
git tag -a v1.0.1 -m "Hotfix version 1.0.1 - Security patch"
git push origin main --tags

# 5. Merge para develop
git checkout develop
git merge --no-ff hotfix/v1.0.1-authentication-bypass
git push origin develop

# 6. Cleanup
git branch -d hotfix/v1.0.1-authentication-bypass
git push origin --delete hotfix/v1.0.1-authentication-bypass
```

## 📏 Regras e Boas Práticas

### Proteção de Branches

**Branches protegidas**: `main`, `develop`

Configurações obrigatórias (GitHub):
- ✅ Require pull request reviews (1+ aprovação)
- ✅ Require status checks to pass (CI deve passar)
- ✅ Require branches to be up to date
- ✅ Include administrators
- ✅ Restrict who can push (ninguém push direto)

### Pull Requests

**Checklist antes de criar PR**:
- [ ] Código segue as guidelines do projeto
- [ ] Todos os testes passam
- [ ] Código está formatado corretamente
- [ ] Commits seguem Conventional Commits
- [ ] Branch está atualizada com develop
- [ ] Descrição clara do PR

**Template de PR**:
```markdown
## Descrição
[Descrição clara da mudança]

## Tipo de Mudança
- [ ] Nova feature
- [ ] Bug fix
- [ ] Breaking change
- [ ] Documentação

## Como Testar
1. ...
2. ...

## Checklist
- [ ] Testes adicionados/atualizados
- [ ] Documentação atualizada
- [ ] CI passa
```

### Commits

**Boas práticas**:
- ✅ Commits pequenos e focados
- ✅ Mensagens descritivas
- ✅ Usar Conventional Commits
- ✅ Um commit = uma mudança lógica
- ❌ Não commitar código quebrado
- ❌ Não commitar secrets/credentials

### Merge

**Estratégia**:
- `main` e `develop`: Sempre usar `--no-ff` (criar merge commit)
- Features: Squash merge (opcional, depende do tamanho)

**Razão para --no-ff**:
```bash
# Preserva histórico da branch
git merge --no-ff feature/new-feature

# Cria merge commit mesmo quando fast-forward é possível
# Facilita reverter features inteiras
git revert -m 1 <merge-commit>
```

### Sync e Atualização

```bash
# Manter feature atualizada com develop
git checkout feature/my-feature
git fetch origin
git merge origin/develop

# Ou usando rebase (para histórico linear)
git rebase origin/develop
```

### Limpeza

```bash
# Deletar branches locais já merged
git branch --merged | grep -v "\*" | grep -v "main" | grep -v "develop" | xargs -n 1 git branch -d

# Ver branches remotas deletadas
git remote prune origin --dry-run

# Limpar referências remotas
git remote prune origin
```

## 🔄 Integração com CI/CD

### Triggers de CI

```yaml
# feature/* → develop
- Build e testes automatizados
- Code quality checks
- Security scans

# release/* → main
- Todos os checks acima
- Testes de integração
- Build de produção
- Deploy para staging

# main (após merge)
- Deploy para produção
- Criar release notes
- Notificações
```

## 📊 Diagrama Visual

```
main     ─────●────────────●──────────●─────────→
              │            │          │
              │   v1.0.0   │ v1.1.0   │ v1.1.1
              │            │          │
develop  ───┬─┴─●──●──●──┬─┴─●──●──┬─┴──●──●────→
            │            │         │
feature/a   └──●──●──●──┘          │
                                   │
feature/b                          └──●──●──●───

hotfix/1                              ●──┐
                                      └──┴──→
```

## 📚 Referências

- [Git Flow Original (Vincent Driessen)](https://nvie.com/posts/a-successful-git-branching-model/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
- [GitHub Flow](https://docs.github.com/en/get-started/quickstart/github-flow)

## ❓ FAQ

**Q: Posso commitar direto na develop?**
A: Não. Sempre use feature branches e Pull Requests.

**Q: Quando criar uma release branch?**
A: Quando develop tem todas as features planejadas para a próxima versão.

**Q: E se eu encontrar um bug durante uma release?**
A: Corrija na release branch. Ela será mergeada tanto para main quanto develop.

**Q: Posso ter múltiplas release branches?**
A: Não é recomendado. Termine uma release antes de iniciar outra.

**Q: Como faço rollback de uma release?**
A: Crie um hotfix branch a partir de main com a correção ou reversão.

---

**Última atualização**: 2025-01-13
**Revisão**: Trimestral
