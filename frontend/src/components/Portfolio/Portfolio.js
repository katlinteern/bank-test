import React, { useState, useEffect } from 'react';
import { getUserInvestments } from '../../services/ApiService';
import Summary from '../Summary/Summary';
import Details from '../Details/Details';
import Overview from '../Overview/Overview';

const Portfolio = () => {
  const [investments, setInvestments] = useState([]);

  useEffect(() => {
    async function fetchData() {
      try {
        const investments = await getUserInvestments();
        setInvestments(investments);  
      } catch (error) {
        console.error('Error fetching investments:', error);
      }
    }

    fetchData();
  }, []);

  return (
    <div>
      <div id="summary">
        <Summary />
      </div>

      {investments.length > 0 && (
        <>
          <div id="details">
            <Details investments={investments} />
          </div>
          <div id="chart">
            <Overview investments={investments} />
          </div>
        </>
      )}
    </div>
  );
};

export default Portfolio;
