import React, { useEffect, useState } from 'react';
import { getUserInvestmentSummary } from '../../services/ApiService';
import SummaryCard from '../SummaryCard/SummaryCard'; // Import new SummaryCard component
import './InvestmentSummary.css';

export default function InvestmentSummary() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const summary = await getUserInvestmentSummary();
        setSummary(summary);
      } catch (error) {
        console.error('Error fetching investments:', error);
      } finally {
        setLoading(false);
      }
    }

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="summary-container">
        <h2 className="summary-title">Summary</h2>
        <div className="loading-spinner-container">
          <div className="loading-spinner" role="status"></div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="summary-container">
        <h2 className="summary-title">Summary</h2>
        <div className="summary-cards">
          <SummaryCard 
            icon="&#128181;" 
            name="Current value" 
            value={summary.totalValue ? `${summary.totalValue} €` : ''}  
          />
          <SummaryCard 
            icon="&#128200;" 
            name="Profitability" 
            value={summary.profitability? `${summary.profitability} %` : ''} 
          />
          <SummaryCard 
            icon="&#128202;" 
            name="Number of investments" 
            value={summary.numberOfInvestments} 
          />
        </div>
      </div>
    </div>
  );
}
