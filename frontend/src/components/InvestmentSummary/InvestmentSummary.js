import React, { useEffect, useState } from 'react';
import { getPortfolioProfitablity } from '../../services/ApiService';
import './InvestmentSummary.css';

export default function InvestmentSummary() {
  const [ portfolio, setPortfolio ] = useState(null);

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
    return <div>Loading...</div>;
  }

  return (
    <div>
      <div className="summary-container">
        <h2>Investment Summary</h2>
        {/* Safely access totalProfitability */}
        <p>Your current investments have a total return of {portfolio.data?.totalProfitability}</p>
        <p>Manage your investments effectively: add, edit, or remove investments from the list below.</p>
      </div>
    </div>
  );
}
