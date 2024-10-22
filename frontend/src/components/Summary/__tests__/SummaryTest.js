import { render, screen, waitFor, act } from '@testing-library/react';
import Summary from '../Summary';
import { getUserInvestmentSummary } from '../../../services/ApiService';

jest.mock('../../../services/ApiService');

describe('Summary', () => {
  beforeAll(() => {
    // Mock console.error globally for all tests in this suite
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterAll(() => {
    // Restore the original console.error after all tests in this suite
    console.error.mockRestore();
  });

  beforeEach(() => {
    jest.resetAllMocks(); // Ensure that all mocks are reset before each test
  });

  test('renders summary cards when data is fetched', async () => {
    const mockSummary = {
      totalValue: 5000,
      profitability: 10,
      numberOfInvestments: 5,
    };
    getUserInvestmentSummary.mockResolvedValueOnce(mockSummary);

    await act(async () => {
      render(<Summary />);
    });

    // Wait for the summary data to load and be displayed
    await waitFor(() => {
      expect(screen.getByText(/total value/i)).toBeInTheDocument();
      expect(screen.getByText(/profitability/i)).toBeInTheDocument();
      expect(screen.getByText(/number of investments/i)).toBeInTheDocument();
    });
  });

  test('handles API errors gracefully', async () => {
    getUserInvestmentSummary.mockRejectedValueOnce(new Error('API Error'));

    await act(async () => {
      render(<Summary />);
    });

    await waitFor(() => {
      expect(screen.getByText(/summary/i)).toBeInTheDocument();
      // Check that the loading spinner has disappeared
      expect(screen.queryByRole('status')).not.toBeInTheDocument();
      // Check that cards are rendered (but no value)
      expect(screen.queryByText(/total value/i)).toBeInTheDocument();
      expect(screen.queryByText(/profitability/i)).toBeInTheDocument();
      expect(screen.queryByText(/number of investments/i)).toBeInTheDocument();
    });
  });
});
