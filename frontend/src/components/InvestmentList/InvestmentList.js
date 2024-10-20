import React, { useContext, useEffect } from 'react';
import InvestmentTableRow from '../InvestmentTableRow';
import { InvestmentContext } from '../../context/InvestmentContext';
import { getUserInvestments } from '../../services/ApiService';
import { NavLink } from 'react-router-dom';
import './InvestmentList.css';

export default function InvestmentList() {
  const { investments, updateInvestments } = useContext(InvestmentContext);

  useEffect(() => {
    if (investments.length === 0) {  
      async function fetchData() {
        try {
          const investments = await getUserInvestments();
          updateInvestments(investments);
        } catch (error) {
          console.error('Error fetching investments:', error);
        }
      }

      fetchData();
    }
  }, [updateInvestments]);  

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
            <InvestmentTableRow key={id} {...investment} />
          ))}
        </tbody>
      </table>
    </div>
  );
}
