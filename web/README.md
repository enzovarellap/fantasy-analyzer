# Fantasy Analyzer - Web Frontend

React + TypeScript web application for Fantasy Football analysis.

## 🛠️ Technology Stack

- **Framework**: React 18
- **Language**: TypeScript
- **Build Tool**: Create React App
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **Testing**: Jest + React Testing Library
- **Linting**: ESLint + Prettier

## 📁 Project Structure

```
web/
├── public/              # Static assets
│   ├── index.html
│   └── manifest.json
├── src/
│   ├── components/      # Reusable UI components
│   ├── pages/           # Page components
│   ├── services/        # API services
│   ├── hooks/           # Custom React hooks
│   ├── utils/           # Utility functions
│   ├── types/           # TypeScript types/interfaces
│   ├── App.tsx          # Main App component
│   ├── index.tsx        # Entry point
│   └── index.css        # Global styles
├── package.json
├── tsconfig.json
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Node.js 18+
- npm 9+ or yarn

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm start
```

The app will open at `http://localhost:3000`

## 📜 Available Scripts

### Development

```bash
# Start dev server with hot reload
npm start
```

### Build

```bash
# Build for production
npm run build

# The build folder will contain optimized production build
```

### Testing

```bash
# Run tests in watch mode
npm test

# Run tests with coverage
npm run test:ci
```

### Code Quality

```bash
# Run ESLint
npm run lint

# Fix ESLint issues
npm run lint:fix

# Format code with Prettier
npm run format

# Type check
npm run type-check
```

## 🔧 Configuration

### Environment Variables

Create `.env.local` file:

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENV=development
```

### API Configuration

API base URL is configured in `src/services/api.ts`:

```typescript
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';
```

## 🧪 Testing

### Unit Tests

```bash
npm test
```

### Coverage Report

```bash
npm run test:ci
```

Coverage reports will be in `coverage/` directory.

## 📦 Building

### Development Build

```bash
npm run build
```

### Production Build

Set environment variables and build:

```bash
REACT_APP_API_URL=https://api.fantasy-analyzer.com/api npm run build
```

## 🐳 Docker

### Build Docker Image

```bash
# From project root
docker build -t fantasy-analyzer-web ./web
```

### Run Container

```bash
docker run -p 3000:3000 fantasy-analyzer-web
```

## 📝 Code Style

This project follows:
- **React** best practices
- **TypeScript** strict mode
- **ESLint** for linting
- **Prettier** for formatting

### Import Organization

```typescript
// 1. External imports
import React from 'react';
import { useNavigate } from 'react-router-dom';

// 2. Internal imports (with aliases)
import { Button } from '@components/Button';
import { useAuth } from '@hooks/useAuth';

// 3. Relative imports
import './styles.css';
```

## 🎨 Component Guidelines

### Function Components

```typescript
import React from 'react';

interface Props {
  title: string;
  onSubmit: () => void;
}

export const MyComponent: React.FC<Props> = ({ title, onSubmit }) => {
  return (
    <div>
      <h1>{title}</h1>
      <button onClick={onSubmit}>Submit</button>
    </div>
  );
};
```

### Custom Hooks

```typescript
import { useState, useEffect } from 'react';

export const useApi = <T,>(url: string) => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    // Fetch logic
  }, [url]);

  return { data, loading, error };
};
```

## 🔗 API Integration

Example service:

```typescript
// src/services/auth.service.ts
import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL;

export const authService = {
  login: async (email: string, password: string) => {
    const response = await axios.post(`${API_URL}/auth/login`, {
      email,
      password,
    });
    return response.data;
  },

  register: async (email: string, password: string, name: string) => {
    const response = await axios.post(`${API_URL}/auth/register`, {
      email,
      password,
      nome: name,
    });
    return response.data;
  },
};
```

## 🐛 Troubleshooting

### Port already in use

```bash
# Find process using port 3000
lsof -i :3000

# Kill process
kill -9 <PID>

# Or use different port
PORT=3001 npm start
```

### Module not found

```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

### TypeScript errors

```bash
# Check types
npm run type-check

# Clear TypeScript cache
rm -rf node_modules/.cache
```

## 📚 Resources

- [React Documentation](https://react.dev/)
- [TypeScript Documentation](https://www.typescriptlang.org/docs/)
- [Create React App](https://create-react-app.dev/)
- [React Router](https://reactrouter.com/)
- [Axios Documentation](https://axios-http.com/)

## 🤝 Contributing

Follow the coding standards defined in:
- [Architecture Guide](../docs/ARCHITECTURE_GUIDE.md)
- [Git Workflow](../GIT_WORKFLOW.md)

## 📄 License

[Add license information]
