import React from 'react';
import { render, screen } from '@testing-library/react';
import SummaryCard from '../SummaryCard';

describe('SummaryCard', () => {
  it('renders correctly with given props', () => {
    const icon = <span role="img" aria-label="icon">✨</span>;
    const name = "Total Sales";
    const value = "$5000";

    render(<SummaryCard icon={icon} name={name} value={value} />);

    // Check if the icon is rendered
    expect(screen.getByRole('img', { name: /icon/i })).toBeInTheDocument();

    // Check if the name is rendered
    expect(screen.getByText(/total sales/i)).toBeInTheDocument();

    // Check if the value is rendered
    expect(screen.getByText(/5000/i)).toBeInTheDocument();
  });
});
