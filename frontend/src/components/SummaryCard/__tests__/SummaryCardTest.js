import React from 'react';
import { render, screen } from '@testing-library/react';
import SummaryCard from '../SummaryCard';

describe('SummaryCard Component', () => {
  const icon = '💰';
  const name = 'Total value';
  const value = '5000 €';

  test('renders with provided props', () => {
    render(<SummaryCard icon={icon} name={name} value={value} />);

    expect(screen.getByText(icon)).toBeInTheDocument();
    expect(screen.getByText(`${name}:`)).toBeInTheDocument();
    expect(screen.getByText(value)).toBeInTheDocument();
  });

  test('renders with empty value', () => {
    render(<SummaryCard icon={icon} name={name} value="" />);

    expect(screen.getByText(icon)).toBeInTheDocument();
    expect(screen.getByText(`${name}:`)).toBeInTheDocument();
    expect(screen.queryByText(value)).not.toBeInTheDocument();
  });
});
