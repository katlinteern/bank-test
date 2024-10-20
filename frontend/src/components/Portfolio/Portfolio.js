import React from 'react';
import InvestmentSummary from '../InvestmentSummary/InvestmentSummary'; 
import InvestmentList from '../InvestmentList/InvestmentList'; 

const Portfolio = () => {
  return (
    <div>
      <InvestmentSummary /> 
      <InvestmentList /> 
    </div>
  );
};

export default Portfolio;
