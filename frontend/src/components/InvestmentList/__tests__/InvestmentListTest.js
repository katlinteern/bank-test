import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import InvestmentList from '../InvestmentList';
import { getUserInvestments } from '../../../services/ApiService';

jest.mock('../../../services/ApiService');

describe('InvestmentList Component', () => {
  test('renders loading spinner while fetching data', () => {
    getUserInvestments.mockResolvedValueOnce([]); // Mock empty data

    render(<InvestmentList />);
    
    // Check for the loading spinner
    const loadingSpinner = screen.getByRole('status');
    expect(loadingSpinner).toBeInTheDocument();
    
    // Check for the title
    const title = screen.getByText(/Investments/i);
    expect(title).toBeInTheDocument();
  });

  test('renders investment table after data is fetched', async () => {
    const mockInvestments = [
      { id: 1, name: 'Investment 1', totalValue: 1000, profitability: 10 },
      { id: 2, name: 'Investment 2', totalValue: 2000, profitability: 20 },
    ];
    
    getUserInvestments.mockResolvedValueOnce(mockInvestments); 

    render(<InvestmentList />);
    
    await waitFor(() => {
      expect(screen.queryByRole('status')).not.toBeInTheDocument(); 
    });

    const investment1 = screen.getByText(/Investment 1/i);
    const investment2 = screen.getByText(/Investment 2/i);
    expect(investment1).toBeInTheDocument();
    expect(investment2).toBeInTheDocument();
    
    expect(screen.getByText('1000 €')).toBeInTheDocument();
    expect(screen.getByText('10 %')).toBeInTheDocument();
    expect(screen.getByText('2000 €')).toBeInTheDocument();
    expect(screen.getByText('20 %')).toBeInTheDocument();
  });

  test('handles error while fetching investments', async () => {
    getUserInvestments.mockRejectedValueOnce(new Error('Failed to fetch'));
  
    render(<InvestmentList />);
    
    await waitFor(() => {
      expect(screen.queryByRole('status')).not.toBeInTheDocument(); 
    });
  
    const errorMessage = screen.getByText(/No investments found/i);
    expect(errorMessage).toBeInTheDocument();
    
    const table = screen.queryByRole('table');
    expect(table).not.toBeInTheDocument();
  });
  
});
