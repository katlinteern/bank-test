import React, { useContext, useEffect } from 'react';
import InvestmentTableRow from '../InvestmentTableRow';
import { InvestmentContext } from '../../context/InvestmentContext';
import { getInvestmentsByUserId } from '../../services/ApiService';
import { NavLink } from 'react-router-dom';
import './InvestmentList.css';

export default function InvestmentList() {
  const { investments, updateInvestments } = useContext(InvestmentContext);

  useEffect(() => {
    async function fetchData() {
      try {
        const investments = await getInvestmentsByUserId();
        updateInvestments(investments);
      } catch (error) {
        console.error('Error fetching investments:', error);
      }
    }

    fetchData();
  }, []);

  return (
    <div>
      <div className="summary-container">
        <h2>Investment Summary</h2>
        <p>Your current investments have a total return of $XX,XXX.</p>
        <p>Manage your investments effectively: add, edit, or remove investments from the list below.</p>
      </div>

      {/* Wrapper for the button to align it to the right */}
      <div className="button-container">
        <NavLink className="btn btn-primary" to="/new">Add</NavLink>
      </div>

      {/* Add a title for the table */}
      <div className="table-title">
        <h2>Table</h2>
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
