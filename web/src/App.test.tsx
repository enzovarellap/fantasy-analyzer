import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('renders Fantasy Analyzer title', () => {
  render(<App />);
  const titleElement = screen.getByText(/Fantasy Analyzer/i);
  expect(titleElement).toBeInTheDocument();
});

test('renders coming soon message', () => {
  render(<App />);
  const comingSoonElement = screen.getByText(/Coming Soon/i);
  expect(comingSoonElement).toBeInTheDocument();
});
