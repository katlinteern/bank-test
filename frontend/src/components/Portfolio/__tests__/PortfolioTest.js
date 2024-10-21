import React from 'react';
import { render, screen } from '@testing-library/react';
import Portfolio from '../Portfolio';

jest.mock('../../Summary/Summary', () => () => <div>Summary</div>);
jest.mock('../../Details/Details', () => () => <div>Investments</div>);

describe('Portfolio', () => {
  it('renders Summary and Details', () => {
    render(<Portfolio />);

    expect(screen.getByText(/summary/i)).toBeInTheDocument();
    expect(screen.getByText(/investments/i)).toBeInTheDocument();
  });
});
