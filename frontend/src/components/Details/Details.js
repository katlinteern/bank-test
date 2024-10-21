import React from 'react';
import './Details.css';

export default function Details({ investments }) {
  return (
    <div>
      <div className="table-title">
        <h2>Details</h2>
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
          {investments.map(({ id, name, totalValue, profitability }) => (
            <tr key={id}>
              <td>{name}</td>
              <td>{totalValue} €</td>
              <td>{profitability} %</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
