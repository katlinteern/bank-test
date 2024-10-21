import React from 'react';
import { render, screen } from '@testing-library/react';
import Overview from '../Overview';

jest.mock('react-chartjs-2', () => ({
  Bar: () => <div data-testid="mock-chart">Mock Bar Chart</div>,
}));

describe('Overview Component', () => {
  const investmentsMock = [
    { id: 1, name: 'Investment A', totalValue: 1000, profitability: 10 },
    { id: 2, name: 'Investment B', totalValue: 2000, profitability: 15 },
  ];

  test('renders Overview component correctly', () => {
    render(<Overview investments={investmentsMock} />);

    // Check if the title is rendered
    expect(screen.getByText(/Overview/i)).toBeInTheDocument();
    
    // Check if the mock chart is rendered
    expect(screen.getByTestId('mock-chart')).toBeInTheDocument();
  });
});
