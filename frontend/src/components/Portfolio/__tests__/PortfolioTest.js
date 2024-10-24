import React from 'react';
import { render, screen, waitFor, act } from '@testing-library/react';
import Portfolio from '../Portfolio';
import { getUserInvestments } from '../../../services/ApiService'; 

jest.mock('../../Summary/Summary', () => () => <div data-testid="summary">Mock Summary</div>);
jest.mock('../../Overview/Overview', () => () => <div data-testid="overview">Mock Overview</div>);
jest.mock('../../Details/Details', () => () => <div data-testid="details">Mock Details</div>);

jest.mock('../../../services/ApiService');

describe('Portfolio Component', () => {
  const mockInvestments = [
    { id: 1, name: 'Investment A', totalValue: 1000, profitability: 10 },
    { id: 2, name: 'Investment B', totalValue: 2000, profitability: 15 },
  ];

  beforeEach(() => {
    getUserInvestments.mockClear();

    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    console.error.mockRestore();
  });

  test('renders Summary component', async () => {
    getUserInvestments.mockResolvedValue([]);

    await act(async () => {
      render(<Portfolio />);
    });

    expect(screen.getByTestId('summary')).toBeInTheDocument();
  });

  test('renders Overview and Details components when investments data is fetched', async () => {
    getUserInvestments.mockResolvedValue(mockInvestments);

    await act(async () => {
      render(<Portfolio />);
    });

    await waitFor(() => {
      expect(getUserInvestments).toHaveBeenCalledTimes(1);
      expect(screen.getByTestId('overview')).toBeInTheDocument();
      expect(screen.getByTestId('details')).toBeInTheDocument();
    });
  });

  test('does not render Overview and Details when no investments are fetched', async () => {
    getUserInvestments.mockResolvedValue([]);

    await act(async () => {
      render(<Portfolio />);
    });

    await waitFor(() => {
      expect(getUserInvestments).toHaveBeenCalledTimes(1);
      expect(screen.queryByTestId('overview')).not.toBeInTheDocument();
      expect(screen.queryByTestId('details')).not.toBeInTheDocument();
    });
  });

  test('handles API error gracefully', async () => {
    getUserInvestments.mockRejectedValue(new Error('API Error'));

    await act(async () => {
      render(<Portfolio />);
    });

    await waitFor(() => {
      expect(getUserInvestments).toHaveBeenCalledTimes(1);
      expect(screen.queryByTestId('overview')).not.toBeInTheDocument();
      expect(screen.queryByTestId('details')).not.toBeInTheDocument();
    });
  });
});
