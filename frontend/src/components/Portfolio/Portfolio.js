import React from 'react';
import InvestmentSummary from '../InvestmentSummary/InvestmentSummary';
import InvestmentList from '../InvestmentList/InvestmentList';

const Portfolio = () => {
  return (
    <div>
      <div id="summary">
        <InvestmentSummary />
      </div>
      <div id="list">
        <InvestmentList />
      </div>
    </div>
  );
};

export default Portfolio;
