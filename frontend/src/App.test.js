import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

// Mock the NavBar component
jest.mock('./components/NavBar/NavBar', () => () => <div data-testid="navbar">Mock NavBar</div>);

// Test Suite for the App component
describe('App Component', () => {
  test('renders NavBar and Portfolio components', () => {
    render(<App />); // Render App directly without MemoryRouter

    // Check if NavBar is rendered
    expect(screen.getByTestId('navbar')).toBeInTheDocument();

    // Check if the Portfolio component is rendered by default
    expect(screen.getByRole('heading', { name: /portfolio/i })).toBeInTheDocument();
  });
});
