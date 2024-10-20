import React, { useState, useEffect } from 'react';
import { getUserInvestments } from '../../services/ApiService';
import './InvestmentList.css';

export default function InvestmentList() {
  const [investments, setInvestments] = useState([]);

  useEffect(() => {
    if (investments.length === 0) {
      async function fetchData() {
        try {
          const investments = await getUserInvestments();
          setInvestments(investments);
        } catch (error) {
          console.error('Error fetching investments:', error);
        }
      }

      fetchData();
    }
  }, []);

  return (
    <div>
      <div className="table-title">
        <h2>Investments</h2>
      </div>

      <table className="table table-striped">
        <thead>
          <tr>
            <th scope="col">Title</th>
            <th scope="col">Price</th>
            <th scope="col">Quantity</th>
          </tr>
        </thead>
        <tbody>
          {investments.map(({ id, ...investment }) => (
            <tr>
              <td>{investment.name}</td>
              <td>{investment.totalValue}</td>
              <td>{investment.profitability} %</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
