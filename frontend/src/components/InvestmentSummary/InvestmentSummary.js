import React, { useEffect, useState } from 'react';
import { getPortfolioProfitablity } from '../../services/ApiService';
import './InvestmentSummary.css';

export default function InvestmentSummary() {
  const [portfolio, setPortfolio] = useState(null);

  useEffect(() => { 
      async function fetchData() {
        try {
          const portfolio = await getPortfolioProfitablity();
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
          <div className="summary-card">
            <div className="card-icon">&#128181;</div> {/* Dollar sign icon */}
            <p>Portfolio value: <strong>{portfolio.totalValue}</strong></p>
          </div>
          <div className="summary-card">
            <div className="card-icon">&#128176;</div> {/* Money bag icon */}
            <p>Total profitability: <strong>{portfolio.totalProfitability}</strong></p>
          </div>
          <div className="summary-card">
            <div className="card-icon">&#128200;</div> {/* Bar chart icon */}
            <p>Profitability percentage: <strong>{portfolio.profitabilityPercentage}%</strong></p>
          </div>
          <div className="summary-card">
            <div className="card-icon">&#128202;</div> {/* List icon */}
            <p>Number of investments: <strong>{portfolio.numberOfInvestments}</strong></p>
          </div>
        </div>
      </div>
    </div>
  );
}
