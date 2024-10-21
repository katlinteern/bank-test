import { render, screen } from '@testing-library/react';
import NavBar from '../NavBar';

describe('NavBar Component', () => {
  beforeEach(() => {
    render(<NavBar />);
  });

  test('renders navigation links correctly', () => {
    // Check that the Summary link is rendered
    const summaryLink = screen.getByText(/summary/i);
    expect(summaryLink).toBeInTheDocument();
    // Check that it has the correct role
    expect(summaryLink).toHaveAttribute('class', 'nav-link');

    // Check that the Details link is rendered
    const detailsLink = screen.getByText(/details/i);
    expect(detailsLink).toBeInTheDocument();
    expect(detailsLink).toHaveAttribute('class', 'nav-link');

    // Check that the Overview link is rendered
    const overviewLink = screen.getByText(/overview/i);
    expect(overviewLink).toBeInTheDocument();
    expect(overviewLink).toHaveAttribute('class', 'nav-link');
  });
});
