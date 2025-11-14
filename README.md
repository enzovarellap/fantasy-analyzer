# Fantasy Analyzer

Fantasy Football Analysis Platform integrating with Sleeper API.

## 🏗️ Monorepo Structure

This project uses a monorepo structure to organize all related codebases:

```
fantasy-analyzer/
├── backend/          # Spring Boot API (Java 17)
├── web/              # React web application
├── mobile/           # React Native mobile app (future)
├── docs/             # Project documentation
├── .github/          # GitHub Actions workflows
└── README.md         # This file
```

## 📦 Projects

### Backend
- **Technology**: Spring Boot 3.5.7, Java 17
- **Database**: MySQL
- **Authentication**: JWT
- **Documentation**: [Backend README](./backend/README.md)

### Web
- **Technology**: React + TypeScript
- **Documentation**: [Web README](./web/README.md)

### Mobile
- **Status**: Planned for future development

## 🚀 Quick Start

### Prerequisites
- Java 17
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+ (or use included Maven wrapper)

### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```

### Web Setup
```bash
cd web
npm install
npm start
```

## 📚 Documentation

- [Architecture Guide](./docs/ARCHITECTURE_GUIDE.md) - Detailed architecture and design principles
- [Claude Development Guide](./docs/CLAUDE.md) - Guide for Claude Code development
- [Setup Instructions](./docs/SETUP.md) - Detailed setup instructions
- [Sleeper API Integration](./docs/SLEEPER_API_INTEGRATION.md) - Sleeper API integration guide
- [Git Branching Strategy](./GIT_WORKFLOW.md) - Git Flow branching model

## 🔄 CI/CD

This project uses GitHub Actions for continuous integration:
- **Backend CI**: Automated build and test on every push
- **Web CI**: Automated build and test on every push
- **Pull Request Checks**: Automated validation before merge

## 🤝 Contributing

1. Create a feature branch from `develop`
2. Make your changes following the coding standards in [Architecture Guide](./docs/ARCHITECTURE_GUIDE.md)
3. Ensure all tests pass
4. Submit a pull request to `develop`

See [Git Workflow](./GIT_WORKFLOW.md) for detailed branching strategy.

## 📝 License

[Add license information]

## 👥 Team

[Add team information]
