import React from 'react';
import InvestmentSummary from './InvestmentSummary/InvestmentSummary'; 
import InvestmentList from './InvestmentList/InvestmentList'; 

const Investments = () => {
  return (
    <div>
      <InvestmentSummary /> 
      <InvestmentList /> 
    </div>
  );
};

export default Investments;
