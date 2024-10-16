import React, { useEffect, useState } from 'react';
import { getUserInvestmentSummary } from '../../services/ApiService';
import SummaryCard from '../SummaryCard/SummaryCard'; // Import new SummaryCard component
import './InvestmentSummary.css';

export default function InvestmentSummary() {
  const [portfolio, setPortfolio] = useState(null);

  useEffect(() => { 
      async function fetchData() {
        try {
          const portfolio = await getUserInvestmentSummary();
          setPortfolio(portfolio);
        } catch (error) {
          console.error('Error fetching investments:', error);
        }
      }

      fetchData();
  }, []);  

  if (!portfolio) {
    return (
      <div className="summary-container">
        <h2 className="summary-title">Investment Summary</h2>
        <div className="loading-spinner-container">
          <div className="loading-spinner"></div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="summary-container">
        <h2 className="summary-title">Investment Summary</h2>
        <div className="summary-cards">
          {/* Use SummaryCard component for each card */}
          <SummaryCard 
            icon="&#128181;" 
            name="Portfolio value" 
            value={portfolio.totalValue} 
          />
          <SummaryCard 
            icon="&#128176;" 
            name="Total profit" 
            value={portfolio.totalProfit} 
          />
          <SummaryCard 
            icon="&#128200;" 
            name="Profit percentage" 
            value={`${portfolio.profitPercentage}%`} 
          />
          <SummaryCard 
            icon="&#128202;" 
            name="Number of investments" 
            value={portfolio.numberOfInvestments} 
          />
        </div>
      </div>
    </div>
  );
}
