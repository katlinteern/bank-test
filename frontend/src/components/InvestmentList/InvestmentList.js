import React, { useState, useEffect } from 'react';
import { getUserInvestments } from '../../services/ApiService';
import './InvestmentList.css';

export default function InvestmentList() {
  const [investments, setInvestments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null); 

  useEffect(() => {
    async function fetchData() {
      try {
        const investments = await getUserInvestments();
        setInvestments(investments);
      } catch (error) {
        console.error('Error fetching investments:', error);
        setError('No investments found'); 
      } finally {
        setLoading(false);
      }
    }

    fetchData();
  }, []);

  if (loading) {
    return (
      <div>
        <div className="table-title">
          <h2>Investments</h2>
        </div>
        <div className="table-container">
          <div className="loading-spinner-container">
            <div className="loading-spinner" role="status"></div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="table-title">
        <h2>Investments</h2>
        <div className="error-message">{error}</div> 
      </div>
    );
  }

  return (
    <div>
      <div className="table-title">
        <h2>Investments</h2>
      </div>

      <table className="table table-striped">
        <thead>
          <tr>
            <th scope="col">Name</th>
            <th scope="col">Total value</th>
            <th scope="col">Profitability</th>
          </tr>
        </thead>
        <tbody>
          {investments.map(({ id, ...investment }) => (
            <tr key={id}>
              <td>{investment.name}</td>
              <td>{investment.totalValue} €</td>
              <td>{investment.profitability} %</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
