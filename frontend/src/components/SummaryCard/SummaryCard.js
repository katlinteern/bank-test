import React from 'react';
import './SummaryCard.css'; // Separate CSS for card styling

export default function SummaryCard({ icon, name, value }) {
  return (
    <div className="summary-card">
      <div className="card-icon">{icon}</div>
      <p>{name}: <strong>{value}</strong></p>
    </div>
  );
}
