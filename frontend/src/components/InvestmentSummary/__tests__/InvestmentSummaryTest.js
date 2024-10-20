import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import InvestmentSummary from '../InvestmentSummary'; 
import { getUserInvestmentSummary } from '../../../services/ApiService';

jest.mock('../../../services/ApiService', () => ({
  getUserInvestmentSummary: jest.fn(),
}));

describe('InvestmentSummary', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders loading state initially', () => {
    render(<InvestmentSummary />);
  
    expect(screen.getByText(/summary/i)).toBeInTheDocument();
    expect(screen.getByRole('status')).toBeInTheDocument(); 
  });

  it('renders summary cards when data is fetched', async () => {
    const mockSummary = {
      totalValue: 10000,
      profitability: 15,
      numberOfInvestments: 5,
    };

    getUserInvestmentSummary.mockResolvedValueOnce(mockSummary);

    render(<InvestmentSummary />);

    await waitFor(() => expect(screen.getByText(/current value/i)).toBeInTheDocument());

    expect(screen.getByText(/current value/i)).toBeInTheDocument();
    expect(screen.getByText(/10000 €/i)).toBeInTheDocument();
    expect(screen.getByText(/profitability/i)).toBeInTheDocument();
    expect(screen.getByText(/15 %/i)).toBeInTheDocument();
    expect(screen.getByText(/number of investments/i)).toBeInTheDocument();
    expect(screen.getByText('5', { selector: 'strong' })).toBeInTheDocument(); 
  });

  it('handles API errors gracefully', async () => {
    getUserInvestmentSummary.mockRejectedValueOnce(new Error('Failed to fetch'));

    render(<InvestmentSummary />);

    await waitFor(() => expect(screen.getByText(/summary/i)).toBeInTheDocument());

    expect(screen.queryByText(/current value/i)).not.toBeInTheDocument();
    expect(screen.queryByText(/profitability/i)).not.toBeInTheDocument();
    expect(screen.queryByText(/number of investments/i)).not.toBeInTheDocument();
  });
});
