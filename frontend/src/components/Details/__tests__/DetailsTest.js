import React from 'react';
import { render, screen } from '@testing-library/react';
import Details from '../Details';

describe('Details Component', () => {
  const investmentsMock = [
    { id: 1, name: 'Investment A', totalValue: 1000, profitability: 10 },
    { id: 2, name: 'Investment B', totalValue: 2000, profitability: 15 },
  ];

  test('renders Details component correctly', () => {
    render(<Details investments={investmentsMock} />);

    // Check if the title is rendered
    expect(screen.getByText(/Details/i)).toBeInTheDocument();

    // Check if the table headers are rendered correctly
    expect(screen.getByText(/Name/i)).toBeInTheDocument();
    expect(screen.getByText(/Total value/i)).toBeInTheDocument();
    expect(screen.getByText(/Profitability/i)).toBeInTheDocument();
  });

  test('renders investment data correctly', () => {
    render(<Details investments={investmentsMock} />);

    // Check if each investment is rendered in the table
    expect(screen.getByText(/Investment A/i)).toBeInTheDocument();
    expect(screen.getByText(/1000 €/i)).toBeInTheDocument();
    expect(screen.getByText(/10 %/i)).toBeInTheDocument();

    expect(screen.getByText(/Investment B/i)).toBeInTheDocument();
    expect(screen.getByText(/2000 €/i)).toBeInTheDocument();
    expect(screen.getByText(/15 %/i)).toBeInTheDocument();
  });

});
