import React from 'react';
import { render, screen } from '@testing-library/react';
import Portfolio from '../Portfolio';

jest.mock('../../InvestmentSummary/InvestmentSummary', () => () => <div>Summary</div>);
jest.mock('../../InvestmentList/InvestmentList', () => () => <div>Investments</div>);

describe('Portfolio', () => {
  it('renders InvestmentSummary and InvestmentList', () => {
    render(<Portfolio />);

    expect(screen.getByText(/summary/i)).toBeInTheDocument();
    expect(screen.getByText(/investments/i)).toBeInTheDocument();
  });
});
