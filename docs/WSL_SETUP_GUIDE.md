# Guia de Setup no WSL (Windows Subsystem for Linux)

Este guia completo vai ajudá-lo a configurar o ambiente de desenvolvimento Fantasy Analyzer no WSL.

## 📋 Índice

1. [Pré-requisitos](#pré-requisitos)
2. [Instalação do WSL](#instalação-do-wsl)
3. [Instalação do Docker no WSL](#instalação-do-docker-no-wsl)
4. [Instalação das Ferramentas de Desenvolvimento](#instalação-das-ferramentas-de-desenvolvimento)
5. [Clone e Setup do Projeto](#clone-e-setup-do-projeto)
6. [Rodando com Docker](#rodando-com-docker)
7. [Rodando sem Docker (Nativo)](#rodando-sem-docker-nativo)
8. [Configuração de IDEs](#configuração-de-ides)
9. [Troubleshooting](#troubleshooting)
10. [Dicas e Boas Práticas](#dicas-e-boas-práticas)

## 🚀 Pré-requisitos

- Windows 10 versão 2004+ ou Windows 11
- Conta de administrador
- Mínimo 8GB RAM (16GB recomendado)
- 20GB de espaço em disco livre

## 1️⃣ Instalação do WSL

### Passo 1: Habilitar WSL

Abra PowerShell como Administrador e execute:

```powershell
# Habilitar WSL e Virtual Machine Platform
wsl --install
```

Reinicie o computador após a instalação.

### Passo 2: Instalar Ubuntu

```powershell
# Instalar Ubuntu (recomendado)
wsl --install -d Ubuntu-22.04

# Ou listar distribuições disponíveis
wsl --list --online

# Verificar instalação
wsl --list --verbose
```

### Passo 3: Configurar Usuário Ubuntu

Na primeira inicialização do Ubuntu:
1. Defina seu username (ex: `devuser`)
2. Defina sua senha
3. Confirme a senha

### Passo 4: Atualizar Sistema

```bash
# Atualizar pacotes
sudo apt update && sudo apt upgrade -y

# Instalar ferramentas essenciais
sudo apt install -y \
    build-essential \
    curl \
    wget \
    git \
    ca-certificates \
    gnupg \
    lsb-release
```

## 🐳 Instalação do Docker no WSL

### Opção 1: Docker Desktop (Recomendado para iniciantes)

1. **Baixar Docker Desktop**
   - Download: https://www.docker.com/products/docker-desktop/

2. **Instalar Docker Desktop**
   - Execute o instalador
   - Aceite os termos
   - Aguarde a instalação

3. **Configurar WSL 2 Backend**
   - Abra Docker Desktop
   - Settings → General
   - ✅ Marque "Use the WSL 2 based engine"
   - Settings → Resources → WSL Integration
   - ✅ Marque "Enable integration with my default WSL distro"
   - ✅ Marque "Ubuntu-22.04"
   - Apply & Restart

4. **Verificar Instalação**
```bash
# No WSL Ubuntu
docker --version
docker-compose --version
docker run hello-world
```

### Opção 2: Docker Engine Nativo no WSL (Avançado)

```bash
# 1. Adicionar repositório Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 2. Instalar Docker
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 3. Adicionar usuário ao grupo docker
sudo usermod -aG docker $USER

# 4. Iniciar Docker
sudo service docker start

# 5. Habilitar Docker no boot
echo "sudo service docker start" >> ~/.bashrc

# 6. Verificar
docker --version
docker compose version
```

## 🛠️ Instalação das Ferramentas de Desenvolvimento

### Java 17

```bash
# Instalar OpenJDK 17
sudo apt install -y openjdk-17-jdk

# Verificar instalação
java -version
javac -version

# Configurar JAVA_HOME (adicionar ao ~/.bashrc)
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$PATH:$JAVA_HOME/bin' >> ~/.bashrc
source ~/.bashrc
```

### Maven

```bash
# Instalar Maven
sudo apt install -y maven

# Verificar instalação
mvn -version
```

### Node.js e npm

```bash
# Instalar Node.js 18 LTS via NVM (recomendado)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# Recarregar shell
source ~/.bashrc

# Instalar Node.js
nvm install 18
nvm use 18
nvm alias default 18

# Verificar instalação
node --version
npm --version
```

### MySQL Client (Opcional - para acesso direto ao banco)

```bash
# Instalar MySQL Client
sudo apt install -y mysql-client

# Verificar instalação
mysql --version
```

## 📦 Clone e Setup do Projeto

### Clonar Repositório

```bash
# Navegar para diretório de projetos
cd ~
mkdir projects
cd projects

# Clonar repositório
git clone https://github.com/seu-usuario/fantasy-analyzer.git
cd fantasy-analyzer

# Verificar estrutura
ls -la
```

### Configurar Variáveis de Ambiente

```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar com suas configurações
nano .env
# ou
vim .env
```

Configurações importantes no `.env`:
```env
MYSQL_ROOT_PASSWORD=seu_password_seguro
MYSQL_DATABASE=fantasy_analyzer
MYSQL_USER=fantasy_user
MYSQL_PASSWORD=seu_password_aqui
JWT_SECRET=sua-chave-jwt-super-secreta-min-256-bits
```

## 🐳 Rodando com Docker

### Desenvolvimento (Hot Reload)

```bash
# Subir todos os serviços em modo desenvolvimento
docker compose -f docker-compose.yml -f docker-compose.dev.yml up

# Ou em background
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Ver logs
docker compose logs -f

# Ver logs de um serviço específico
docker compose logs -f backend
```

### Produção

```bash
# Subir serviços em modo produção
docker compose up -d

# Verificar status
docker compose ps

# Parar serviços
docker compose down

# Parar e remover volumes (CUIDADO: deleta dados!)
docker compose down -v
```

### Comandos Úteis Docker

```bash
# Rebuild de um serviço específico
docker compose build backend

# Rebuild sem cache
docker compose build --no-cache

# Executar comando em container
docker compose exec backend bash

# Acessar MySQL
docker compose exec mysql mysql -u fantasy_user -p fantasy_analyzer

# Ver logs em tempo real
docker compose logs -f --tail=100

# Remover tudo (containers, networks, volumes)
docker compose down -v
docker system prune -a
```

## 💻 Rodando sem Docker (Nativo)

### Setup MySQL Local

```bash
# Instalar MySQL Server
sudo apt install -y mysql-server

# Iniciar MySQL
sudo service mysql start

# Configurar MySQL
sudo mysql_secure_installation

# Acessar MySQL
sudo mysql -u root -p

# Criar banco e usuário
CREATE DATABASE fantasy_analyzer;
CREATE USER 'fantasy_user'@'localhost' IDENTIFIED BY 'seu_password';
GRANT ALL PRIVILEGES ON fantasy_analyzer.* TO 'fantasy_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Rodar Backend

```bash
# Navegar para backend
cd ~/projects/fantasy-analyzer/backend

# Configurar application-dev.properties
nano src/main/resources/application-dev.properties

# Build e Run
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev

# Ou build JAR e executar
./mvnw clean package
java -jar target/fantasy-analyzer-*.jar --spring.profiles.active=dev
```

### Rodar Frontend (quando implementado)

```bash
# Navegar para web
cd ~/projects/fantasy-analyzer/web

# Instalar dependências
npm install

# Rodar em modo desenvolvimento
npm start
```

## 🔧 Configuração de IDEs

### IntelliJ IDEA

1. **Instalar IntelliJ no Windows**
   - Download: https://www.jetbrains.com/idea/download/

2. **Configurar WSL**
   - File → Settings → Build, Execution, Deployment → Build Tools → Maven
   - Maven home directory: Apontar para Maven no WSL
   - Settings → Tools → Terminal
   - Shell path: `wsl.exe`

3. **Abrir Projeto**
   - File → Open
   - Navegar para: `\\wsl$\Ubuntu-22.04\home\seu-usuario\projects\fantasy-analyzer`

4. **Configurar JDK**
   - File → Project Structure → Project
   - SDK: Add SDK → Add WSL JDK → Select /usr/lib/jvm/java-17-openjdk-amd64

### VS Code

```bash
# Instalar VS Code no Windows
# Download: https://code.visualstudio.com/

# Instalar extensão Remote - WSL no VS Code

# Abrir projeto no WSL via VS Code
cd ~/projects/fantasy-analyzer
code .
```

**Extensões Recomendadas**:
- Remote - WSL
- Extension Pack for Java
- Spring Boot Extension Pack
- Docker
- GitLens
- ESLint (para web)
- Prettier (para web)

### Configurar Git

```bash
# Configurar nome e email
git config --global user.name "Seu Nome"
git config --global user.email "seu.email@example.com"

# Configurar editor padrão
git config --global core.editor "vim"

# Configurar credenciais
git config --global credential.helper store

# SSH (recomendado)
ssh-keygen -t ed25519 -C "seu.email@example.com"
cat ~/.ssh/id_ed25519.pub
# Adicionar chave ao GitHub
```

## 🐛 Troubleshooting

### Problema: WSL não inicia

```powershell
# Verificar status
wsl --status

# Reiniciar WSL
wsl --shutdown
wsl

# Atualizar WSL
wsl --update
```

### Problema: Docker não funciona

```bash
# Verificar se Docker está rodando
sudo service docker status

# Iniciar Docker
sudo service docker start

# Verificar permissões
sudo usermod -aG docker $USER
# Reiniciar WSL
```

### Problema: Porta já em uso

```bash
# Verificar porta em uso
sudo lsof -i :8080

# Matar processo
sudo kill -9 <PID>

# Ou mudar porta no .env
BACKEND_PORT=8081
```

### Problema: MySQL connection refused

```bash
# Verificar se MySQL está rodando
docker compose ps
# ou (se nativo)
sudo service mysql status

# Ver logs do MySQL
docker compose logs mysql

# Conectar manualmente para testar
docker compose exec mysql mysql -u fantasy_user -p
```

### Problema: Baixa performance

```powershell
# No PowerShell (Windows)

# Aumentar memória WSL
# Criar arquivo .wslconfig em C:\Users\SeuUsuario\.wslconfig

[wsl2]
memory=8GB
processors=4
swap=2GB
```

### Problema: Arquivos Windows vs Linux

```bash
# SEMPRE trabalhe em /home/usuario/ (não em /mnt/c/)
# Melhor performance em filesystem Linux

# Mover projeto se estiver em /mnt/c/
mv /mnt/c/Users/SeuUsuario/projects/fantasy-analyzer ~/projects/
```

## 💡 Dicas e Boas Práticas

### Performance

1. **Use filesystem do Linux** (`/home/user/`) em vez de Windows (`/mnt/c/`)
2. **Configure .wslconfig** para mais memória se necessário
3. **Use Docker Desktop** para melhor integração
4. **Desabilite antivírus** no diretório WSL (ou adicione exceção)

### Desenvolvimento

```bash
# Criar alias úteis no ~/.bashrc
echo "alias dcu='docker compose up -d'" >> ~/.bashrc
echo "alias dcd='docker compose down'" >> ~/.bashrc
echo "alias dcl='docker compose logs -f'" >> ~/.bashrc
echo "alias fantasy='cd ~/projects/fantasy-analyzer'" >> ~/.bashrc
source ~/.bashrc

# Agora você pode usar:
# fantasy  → vai para o projeto
# dcu      → sobe os containers
# dcl      → vê os logs
```

### Backup

```bash
# Exportar distribuição WSL
wsl --export Ubuntu-22.04 D:\backups\ubuntu-backup.tar

# Importar distribuição
wsl --import Ubuntu-22.04-Restored D:\WSL D:\backups\ubuntu-backup.tar
```

### Limpeza

```bash
# Limpar Docker
docker system prune -a --volumes

# Limpar Maven cache
rm -rf ~/.m2/repository

# Limpar npm cache
npm cache clean --force
```

### Acessar Arquivos WSL do Windows

```
# Explorer do Windows
\\wsl$\Ubuntu-22.04\home\seu-usuario\projects

# Ou
\\wsl.localhost\Ubuntu-22.04\home\seu-usuario\projects
```

## 🔗 Recursos Adicionais

- [WSL Documentation](https://docs.microsoft.com/en-us/windows/wsl/)
- [Docker WSL Documentation](https://docs.docker.com/desktop/wsl/)
- [Spring Boot on WSL](https://spring.io/guides/gs/spring-boot/)
- [VS Code WSL](https://code.visualstudio.com/docs/remote/wsl)

## ✅ Checklist de Setup Completo

- [ ] WSL 2 instalado e atualizado
- [ ] Ubuntu 22.04 instalado
- [ ] Docker Desktop configurado com WSL 2
- [ ] Java 17 instalado
- [ ] Maven instalado
- [ ] Node.js 18+ instalado
- [ ] Git configurado
- [ ] Projeto clonado em `~/projects/`
- [ ] Arquivo `.env` configurado
- [ ] Docker Compose funcionando
- [ ] Backend acessível em `http://localhost:8080`
- [ ] MySQL acessível
- [ ] IDE configurada

## 🆘 Suporte

Se encontrar problemas:

1. Verifique a seção [Troubleshooting](#troubleshooting)
2. Consulte os logs: `docker compose logs`
3. Verifique configurações do `.env`
4. Abra uma issue no GitHub

---

**Última atualização**: 2025-01-13
**Testado em**: Windows 11, WSL 2, Ubuntu 22.04
