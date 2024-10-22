import React from 'react';
import { render, screen } from '@testing-library/react';
import SummaryCard from '../SummaryCard';

describe('SummaryCard Component', () => {
  const icon = '💰';
  const name = 'Total value';
  const value = '5000 €';

  test('renders with provided props', () => {
    render(<SummaryCard icon={icon} name={name} value={value} />);

    // Check if the icon is rendered
    expect(screen.getByText(icon)).toBeInTheDocument();
    // Check if the name is rendered
    expect(screen.getByText(`${name}:`)).toBeInTheDocument();
    // Check if the value is rendered
    expect(screen.getByText(value)).toBeInTheDocument();
  });

  test('renders with empty value', () => {
    render(<SummaryCard icon={icon} name={name} value="" />);

    // Check if the icon is rendered
    expect(screen.getByText(icon)).toBeInTheDocument();
    // Check if the name is rendered
    expect(screen.getByText(`${name}:`)).toBeInTheDocument();
    // Check that the value is not rendered when it is empty
    expect(screen.queryByText(value)).not.toBeInTheDocument();
  });
});
