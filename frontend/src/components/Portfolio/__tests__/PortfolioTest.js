import React from 'react';
import { render, screen, waitFor, act } from '@testing-library/react';
import Portfolio from '../Portfolio';
import { getUserInvestments } from '../../../services/ApiService'; // Ensure correct path

// Mock the child components to avoid rendering their full implementations
jest.mock('../../Summary/Summary', () => () => <div data-testid="summary">Mock Summary</div>);
jest.mock('../../Overview/Overview', () => () => <div data-testid="overview">Mock Overview</div>);
jest.mock('../../Details/Details', () => () => <div data-testid="details">Mock Details</div>);

// Mock the API service
jest.mock('../../../services/ApiService');

describe('Portfolio Component', () => {
  const mockInvestments = [
    { id: 1, name: 'Investment A', totalValue: 1000, profitability: 10 },
    { id: 2, name: 'Investment B', totalValue: 2000, profitability: 15 },
  ];

  beforeEach(() => {
    getUserInvestments.mockClear();

    // Mock console.error to avoid cluttering the test output
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    // Restore console.error after each test
    console.error.mockRestore();
  });

  test('renders Summary component', async () => {
    getUserInvestments.mockResolvedValue([]);

    await act(async () => {
      render(<Portfolio />);
    });

    // Check if the summary section is rendered
    expect(screen.getByTestId('summary')).toBeInTheDocument();
  });

  test('renders Overview and Details components when investments data is fetched', async () => {
    getUserInvestments.mockResolvedValue(mockInvestments);

    await act(async () => {
      render(<Portfolio />);
    });

    // Wait for investments to be fetched and state to update
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

    // Wait to ensure no investments were fetched
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

    // Wait for the error to be caught and handled
    await waitFor(() => {
      expect(getUserInvestments).toHaveBeenCalledTimes(1);
      expect(screen.queryByTestId('overview')).not.toBeInTheDocument();
      expect(screen.queryByTestId('details')).not.toBeInTheDocument();
    });
  });
});
