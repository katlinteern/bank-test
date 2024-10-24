import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

jest.mock('./components/NavBar/NavBar', () => () => <div data-testid="navbar">Mock NavBar</div>);

describe('App Component', () => {
  test('renders NavBar and Portfolio components', () => {
    render(<App />); 

    expect(screen.getByTestId('navbar')).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /portfolio/i })).toBeInTheDocument();
  });
});
