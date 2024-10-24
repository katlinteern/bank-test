import React from 'react';
import './SummaryCard.css'; 

export default function SummaryCard({ icon, name, value }) {
  return (
    <div className="summary-card">
      <div className="card-icon">{icon}</div>
      <p className="card-name">{name}:</p>
      <p className="card-value"><strong>{value}</strong></p>
    </div>
  );
}
