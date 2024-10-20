// App.test.js

import { render, screen } from '@testing-library/react';
import App from './App';

test('renders NavBar component', () => {
  render(<App />);
  const navElement = screen.getByText('List');
  expect(navElement).toBeInTheDocument();
});



test('renders Investments component', () => {
  render(<App />);
});
