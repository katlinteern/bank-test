import React, { createContext, useState } from 'react';

export const InvestmentContext = createContext();

export const InvestmentListProvider = ({ children }) => {
  const [investments, setInvestments] = useState([]);
  const [investment] = useState({});

  const updateInvestments = (investments) => {
    setInvestments(investments)
  }

  const addInvestment = (investment) => {
    setInvestments([...investments, investment]);
  }

  return (
    <InvestmentContext.Provider value={{ investments, investment, addInvestment, updateInvestments }}>
      {children}
    </InvestmentContext.Provider>
  );
}