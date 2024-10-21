import React from 'react';
import { render, screen } from '@testing-library/react'; // Ensure screen is imported
import App from './App';

describe('Index', () => {
  beforeEach(() => {
    // Create a div element and append it to the body
    const root = document.createElement('div');
    root.setAttribute('id', 'root');
    document.body.appendChild(root);
  });

  afterEach(() => {
    // Clean up after each test
    const root = document.getElementById('root');
    if (root) {
      document.body.removeChild(root);
    }
  });

  test('renders App component without crashing', () => {
    // Render the App component
    render(<App />);
    
    // Check if the portfolio header is rendered
    expect(screen.getByRole('heading', { name: /portfolio/i })).toBeInTheDocument();
  });
});
